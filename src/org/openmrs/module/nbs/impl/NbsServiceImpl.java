package org.openmrs.module.nbs.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.dss.DssElement;
import org.openmrs.module.dss.DssManager;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.nbs.NbsParameterHandler;
import org.openmrs.module.nbs.Percentile;
import org.openmrs.module.nbs.db.NbsDAO;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7Export;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMap;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMapType;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportStatus;
import org.openmrs.module.nbs.hibernateBeans.OldRule;
import org.openmrs.module.nbs.hibernateBeans.Study;
import org.openmrs.module.nbs.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.nbs.xmlBeans.Field;
import org.openmrs.module.nbs.xmlBeans.LanguageAnswers;
import org.openmrs.module.nbs.xmlBeans.PWSPromptAnswerErrs;
import org.openmrs.module.nbs.xmlBeans.PWSPromptAnswers;
import org.openmrs.module.nbs.xmlBeans.StatsConfig;

public class NbsServiceImpl implements NbsService
{

	private Log log = LogFactory.getLog(this.getClass());

	private NbsDAO dao;

	/**
	 * 
	 */
	public NbsServiceImpl()
	{
	}

	/**
	 * @return
	 */
	public NbsDAO getNbsDAO()
	{
		return this.dao;
	}

	/**
	 * @param dao
	 */
	public void setNbsDAO(NbsDAO dao)
	{
		this.dao = dao;
	}

	/**
	 * @should testPSFConsume
	 * @should testPWSConsume
	 */
	public void consume(InputStream input, Patient patient,
			Integer encounterId,FormInstance formInstance,
			Integer sessionId,
			List<FormField> fieldsToConsume,
			Integer locationTagId)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		try
		{
			DssService dssService = Context
					.getService(DssService.class);
			ATDService atdService = Context
					.getService(ATDService.class);
			ParameterHandler parameterHandler = new NbsParameterHandler();

			// check that the medical record number in the xml file and the medical
			// record number of the patient match
			String patientMedRecNumber = patient.getPatientIdentifier()
					.getIdentifier();
			String xmlMedRecNumber = null;
			String xmlMedRecNumber2 = null;
			LogicService logicService = Context.getLogicService();

			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService
					.getLogicDataSource("xml");
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource
					.getParsedFile(formInstance);

			Integer formInstanceId = formInstance.getFormInstanceId();

			if (fieldMap == null)
			{
				try
				{
					formInstance = xmlDatasource.parse(input,
							formInstance,locationTagId);
					fieldMap = xmlDatasource.getParsedFile(formInstance);

				} catch (Exception e1)
				{
					ATDError atdError = new ATDError("Error","XML Parsing", 
							" Error parsing XML file to be consumed. Form Instance Id = " + formInstanceId
							, Util.getStackTrace(e1), new Date(),sessionId);
					atdService.saveError(atdError);
					return;
				}
			}

			startTime = System.currentTimeMillis();


			Integer formId = formInstance.getFormId();
			Integer locationId = formInstance.getLocationId();
			String medRecNumberTag = org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId, "medRecNumberTag",locationTagId,locationId);

			//MRN
			if (medRecNumberTag!=null&&fieldMap.get(medRecNumberTag) != null)
			{
				xmlMedRecNumber = fieldMap.get(medRecNumberTag).getValue();
			}
			
			
			//Compare form MRNs to patient medical record number
			if (!Util.extractIntFromString(patientMedRecNumber).equalsIgnoreCase(
					Util.extractIntFromString(xmlMedRecNumber)))
			{
				//Compare patient MRN to MRN bar code from back of form.
				if (xmlMedRecNumber2 == null || !Util.extractIntFromString(patientMedRecNumber)
							.equalsIgnoreCase( Util.extractIntFromString(xmlMedRecNumber2))){
					ATDError noMatch = new ATDError("Fatal", "MRN Validity", "Patient MRN" 
							+ " does not match any form MRN bar codes (front or back) " 
							,"\r\n Form instance id: "  + formInstanceId 
							+ "\r\n Patient MRN: " + patientMedRecNumber
							+ " \r\n MRN barcode front: " + xmlMedRecNumber + "\r\n MRN barcode back: "
							+ xmlMedRecNumber2, new Date(), sessionId);
					atdService.saveError(noMatch);
				    return;
					
				} 
				//Patient MRN does not match MRN bar code on front of form, but does match 
				// MRN bar code on back of form.
				ATDError warning = new ATDError("Warning", "MRN Validity", "Patient MRN matches" 
							+ " MRN bar code from back of form only. " 
							,"Form instance id: "  + formInstanceId 
							+ "\r\n Patient MRN: " + patientMedRecNumber
							+ " \r\n MRN barcode front: " + xmlMedRecNumber + "\r\n MRN barcode back: " 
							+ xmlMedRecNumber2, new Date(), sessionId);
				atdService.saveError(warning);
				
				
			}

			startTime = System.currentTimeMillis();
			// make sure storeObs gets loaded before running consume
			// rules
			dssService.loadRule("CREATE_JIT",false);
			dssService.loadRule("storeObs",false);
			dssService.loadRule("ScoreJit", false);

			startTime = System.currentTimeMillis();
			//only consume the question fields for one side of the PSF
			HashMap<String,Field> languageFieldsToConsume = 
				saveAnswers(fieldMap, formInstance,encounterId,patient);
			FormService formService = Context.getFormService();
			Form databaseForm = formService.getForm(formId);
			TeleformTranslator translator = new TeleformTranslator();

			if (fieldsToConsume == null)
			{
				fieldsToConsume = new ArrayList<FormField>();

				for (FormField currField : databaseForm.getOrderedFormFields())
				{
					FormField parentField = currField.getParent();
					String fieldName = currField.getField().getName();
					FieldType currFieldType = currField.getField()
							.getFieldType();
					// only process export fields
					if (currFieldType != null
							&& currFieldType.equals(translator
									.getFieldType("Export Field")))
					{
						if (parentField != null)
						{
							PatientATD patientATD = atdService.getPatientATD(
									formInstance, parentField.getField()
											.getFieldId());

							if (patientATD != null)
							{
								if (databaseForm.getName().equals("PSF"))
								{
									// consume only one side of questions for
									// PSF
									if (languageFieldsToConsume
											.get(fieldName)!=null)
									{
										fieldsToConsume.add(currField);
									}
								} else
								{
									fieldsToConsume.add(currField);
								}
							} else
							{
								// consume all other fields with parents
								fieldsToConsume.add(currField);
							}
						} else
						{
							// consume all other fields
							fieldsToConsume.add(currField);
						}
					}
				}
			}
			log.info("nbsService.consume: Fields to consume: "+
				(System.currentTimeMillis()-startTime));
			
			startTime = System.currentTimeMillis();
			atdService.consume(input, formInstance, patient, encounterId,
					 null, null, parameterHandler,
					 fieldsToConsume,locationTagId,sessionId);
			log.info("nbsService.consume: Time of atdService.consume: "+
				(System.currentTimeMillis()-startTime));
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
	}

	private void populateFieldNameArrays(
			HashMap<String, HashMap<String, Field>> languages,
			ArrayList<String> pwsAnswerChoices,
			ArrayList<String> pwsAnswerChoiceErr)
	{
		AdministrationService adminService = Context.getAdministrationService();
		StatsConfig statsConfig = null;
		String statsConfigFile = adminService
				.getGlobalProperty("nbs.statsConfigFile");

		if(statsConfigFile == null){
			log.error("Could not find statsConfigFile. Please set global property nbs.statsConfigFile.");
			return;
		}
		try
		{
			InputStream input = new FileInputStream(statsConfigFile);
			statsConfig = (StatsConfig) XMLUtil.deserializeXML(
					StatsConfig.class, input);
		} catch (IOException e1)
		{
			log.error(e1.getMessage());
			log.error(Util.getStackTrace(e1));
			return;
		}

		// process prompt answers
		PWSPromptAnswers pwsPromptAnswers = statsConfig.getPwsPromptAnswers();

		if (pwsPromptAnswers != null)
		{
			ArrayList<org.openmrs.module.nbs.xmlBeans.Field> fields = pwsPromptAnswers
					.getFields();

			if (fields != null)
			{
				for (org.openmrs.module.nbs.xmlBeans.Field currField : fields)
				{
					if (currField.getId() != null)
					{
						pwsAnswerChoices.add(currField.getId());
					}
				}
			}
		}

		// process prompt answer errs
		PWSPromptAnswerErrs pwsPromptAnswerErrs = statsConfig
				.getPwsPromptAnswerErrs();

		if (pwsPromptAnswerErrs != null)
		{
			ArrayList<org.openmrs.module.nbs.xmlBeans.Field> fields = pwsPromptAnswerErrs
					.getFields();

			if (fields != null)
			{
				for (org.openmrs.module.nbs.xmlBeans.Field currField : fields)
				{
					if (currField.getId() != null)
					{
						pwsAnswerChoiceErr.add(currField.getId());
					}
				}
			}
		}

		LanguageAnswers languageAnswers = statsConfig.getLanguageAnswers();
		org.openmrs.module.nbs.util.Util.populateFieldNameArrays(languages, languageAnswers);
	}

	private HashMap<String, Field> saveAnswers(
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap,
			FormInstance formInstance, int encounterId, Patient patient)
	{
		ATDService atdService = Context
				.getService(ATDService.class);
		TeleformTranslator translator = new TeleformTranslator();
		FormService formService = Context.getFormService();
		Integer formId = formInstance.getFormId();
		Form databaseForm = formService.getForm(formId);
		if (databaseForm == null)
		{
			log.error("Could not consume teleform export xml because form "
					+ formId + " does not exist in the database");
			return null;
		}

		ArrayList<String> pwsAnswerChoices = new ArrayList<String>();
		ArrayList<String> pwsAnswerChoiceErr = new ArrayList<String>();

		HashMap<String, HashMap<String, Field>> languageToFieldnames = new HashMap<String, HashMap<String, Field>>();

		this.populateFieldNameArrays(languageToFieldnames, pwsAnswerChoices,
				pwsAnswerChoiceErr);

		HashMap<String, Integer> languageToNumAnswers = new HashMap<String, Integer>();
		HashMap<String, HashMap<Integer, String>> languageToAnswers = new HashMap<String, HashMap<Integer, String>>();

		Rule providerNameRule = new Rule();
		providerNameRule.setTokenName("providerName");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("encounterId", encounterId);
		providerNameRule.setParameters(parameters);

		for (FormField currField : databaseForm.getFormFields())
		{
			FieldType currFieldType = currField.getField().getFieldType();
			// only process export fields
			if (currFieldType != null
					&& currFieldType.equals(translator
							.getFieldType("Export Field")))
			{
				String fieldName = currField.getField().getName();
				String answer = null;
				if (fieldMap.get(fieldName) != null)
				{
					answer = fieldMap.get(fieldName).getValue();
				}
				FormField parentField = currField.getParent();

				// if parent field is not null look at parent
				// field for rule to execute
				if (parentField != null)
				{
					PatientATD patientATD = atdService.getPatientATD(
							formInstance,
							parentField.getField().getFieldId());

					if (patientATD != null)
					{
						Rule rule = patientATD.getRule();

						if (answer == null || answer.length() == 0)
						{
							answer = "NoAnswer";
						}
						Integer ruleId = rule.getRuleId();

						String dsstype = databaseForm.getName();

						if (dsstype.equalsIgnoreCase("PSF"))
						{
							for (String currLanguage : languageToFieldnames
									.keySet())
							{
								HashMap<String, Field> currLanguageArray = languageToFieldnames
										.get(currLanguage);
								HashMap<Integer, String> answers = languageToAnswers
										.get(currLanguage);

								if (answers == null)
								{
									answers = new HashMap<Integer, String>();
									languageToAnswers
											.put(currLanguage, answers);
								}
						
								if (currLanguageArray.get(currField
										.getField().getName())!= null)
								{
									answers.put(ruleId, answer);
									if (!answer.equalsIgnoreCase("NoAnswer"))
									{
										if (languageToNumAnswers
												.get(currLanguage) == null)
										{
											languageToNumAnswers.put(
													currLanguage, 0);
										}

										languageToNumAnswers.put(currLanguage,
												languageToNumAnswers
														.get(currLanguage) + 1);
									}
								}
							}
						}

						if (dsstype.equalsIgnoreCase("PWS"))
						{
							Integer formInstanceId = formInstance.getFormInstanceId();
							Integer locationId = formInstance.getLocationId();
							List<Statistics> statistics = this.getNbsDAO()
									.getStatByIdAndRule(formInstanceId,
											ruleId,dsstype,locationId);

							if (statistics != null)
							{
								if (!answer.equalsIgnoreCase("NoAnswer"))
								{
									answer = formatAnswer(answer);
								}
								for (Statistics stat : statistics)
								{
									if (pwsAnswerChoices.contains(currField
											.getField().getName()))
									{
										stat.setAnswer(answer);
									}
									if (pwsAnswerChoiceErr.contains(currField
											.getField().getName()))
									{
										stat.setAnswerErr(answer);
									}

									this.updateStatistics(stat);
								}
							}
						}
					}

				}
			}
		}

		int maxNumAnswers = -1;
		String maxLanguage = null;
		HashMap<Integer, String> maxAnswers = null;

		for (String language : languageToNumAnswers.keySet())
		{
			Integer compareNum = languageToNumAnswers.get(language);

			if (compareNum != null && compareNum > maxNumAnswers)
			{
				maxNumAnswers = compareNum;
				maxLanguage = language;
				maxAnswers = languageToAnswers.get(language);
			}
		}

		String languageResponse = "English";
		if (maxNumAnswers > 0)
		{
			languageResponse = maxLanguage;
			HashMap<Integer, String> answers = maxAnswers;

			for (Integer currRuleId : answers.keySet())
			{
				String answer = answers.get(currRuleId);
				Integer formInstanceId = formInstance.getFormInstanceId();
				Integer locationId = formInstance.getLocationId();
				List<Statistics> statistics = this.getNbsDAO()
						.getStatByIdAndRule(formInstanceId, currRuleId, "PSF",locationId);
				if (statistics != null)
				{
					for (Statistics stat : statistics)
					{
						stat.setAnswer(answer);
						stat.setLanguageResponse(languageResponse);

						this.updateStatistics(stat);
					}
				}
			}
		}
		
		String formName = formService.getForm(formId).getName();
		//save language response to preferred language
		//language is determined by maximum number of answers
		//selected for a language on the PSF
		if (languageResponse != null&&formName.equals("PSF")) {
			ObsService obsService = Context.getObsService();
			Obs obs = new Obs();
			String conceptName = "preferred_language";
			ConceptService conceptService = Context.getConceptService();
			Concept currConcept = conceptService.getConceptByName(conceptName);
			Concept languageConcept = conceptService.getConceptByName(languageResponse);
			if (currConcept == null || languageConcept == null) {
				log
					.error("Could not save preferred language for concept: " + conceptName + " and language: "
				        + languageResponse);
			} else {
				obs.setValueCoded(languageConcept);
				
				EncounterService encounterService = Context.getService(EncounterService.class);
				Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
				
				Location location = encounter.getLocation();
				
				obs.setPerson(patient);
				obs.setConcept(currConcept);
				obs.setLocation(location);
				obs.setEncounter(encounter);
				obs.setObsDatetime(new Date());
				obsService.saveObs(obs, null);
			}
		}
		if (maxNumAnswers > 0){
			return languageToFieldnames.get(maxLanguage);
		}else{
			return languageToFieldnames.get("English");
		}
	}

	private String formatAnswer(String answer)
	{
		final int NUM_ANSWERS = 6;
		String[] answers = new String[NUM_ANSWERS];

		// initialize all answers to N
		for (int i = 0; i < answers.length; i++)
		{
			answers[i] = "X";
		}

		char[] characters = answer.toCharArray();

		// convert the answer string from positional digits (1,2,3,4,5,6)
		// to Y
		for (int i = 0; i < characters.length; i++)
		{
			try
			{
				int intAnswer = Character.getNumericValue(characters[i]);
				int answerPos = intAnswer - 1;

				if (answerPos >= 0 && answerPos < answers.length)
				{
					answers[answerPos] = "Y";
				}
			} catch (Exception e)
			{
				log.error(e.getMessage());
				log.error(Util.getStackTrace(e));
			}
		}

		String newAnswer = "";

		for (String currAnswer : answers)
		{
			newAnswer += currAnswer;
		}

		return newAnswer;
	}

	/**
	 * @should testPSFProduce
	 * @should testPWSProduce
	 */
	public void produce(OutputStream output, PatientState state,
			Patient patient, Integer encounterId, String dssType,
			int maxDssElements,Integer sessionId)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();

		DssService dssService = Context
				.getService(DssService.class);
		ATDService atdService = Context
				.getService(ATDService.class);

		DssManager dssManager = new DssManager(patient);
		dssManager.setMaxDssElementsByType(dssType, maxDssElements);
		HashMap<String, Object> baseParameters = new HashMap<String, Object>();
		startTime = System.currentTimeMillis();
		try {
	        dssService.loadRule("CREATE_JIT",false);
	        dssService.loadRule("storeObs",false);
	        dssService.loadRule("ScoreJit", false);
	        dssService.loadRule("CAH", false);
        }
        catch (Exception e) {
	        log.error("load rule failed", e);
        }
		startTime = System.currentTimeMillis();

		FormInstance formInstance = state.getFormInstance();
		atdService.produce(patient, formInstance, output, dssManager,
				encounterId, baseParameters, null,state.getLocationTagId(),sessionId);
		startTime = System.currentTimeMillis();
		Integer formInstanceId = formInstance.getFormInstanceId();
		Integer locationId = formInstance.getLocationId();
		this.saveStats(patient, formInstanceId, dssManager, encounterId,state.getLocationTagId(),locationId);
	}
	
	public void produce(OutputStream output, PatientState state,
			Patient patient, Integer encounterId, String dssType,
			int maxDssElements,Integer sessionId, HashMap<String, Object> parameters)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();

		DssService dssService = Context
				.getService(DssService.class);
		ATDService atdService = Context
				.getService(ATDService.class);

		DssManager dssManager = new DssManager(patient);
		dssManager.setMaxDssElementsByType(dssType, maxDssElements);
		HashMap<String, Object> baseParameters = new HashMap<String, Object>();
		startTime = System.currentTimeMillis();
		try {
	        dssService.loadRule("CREATE_JIT",false);
	        dssService.loadRule("storeObs",false);
	        dssService.loadRule("ScoreJit", false);
	        dssService.loadRule("CAH", false);
        }
        catch (Exception e) {
	        log.error("load rule failed", e);
        }
		startTime = System.currentTimeMillis();
		String providerFirstName = (String) parameters.get("providerFirstName");
		baseParameters.put("providerFirstName", providerFirstName);
		String providerLastName = (String) parameters.get("providerLastName");
		baseParameters.put("providerLastName", providerLastName);
		String providerId = (String) parameters.get("provider_id");
		baseParameters.put("providerId", providerId);
		FormInstance formInstance = state.getFormInstance();
		atdService.produce(patient, formInstance, output, dssManager,
				encounterId, baseParameters, null,state.getLocationTagId(),sessionId);
		startTime = System.currentTimeMillis();
		Integer formInstanceId = formInstance.getFormInstanceId();
		Integer locationId = formInstance.getLocationId();
		this.saveStats(patient, formInstanceId, dssManager, encounterId,state.getLocationTagId(),locationId);
	}

	private void saveStats(Patient patient, Integer formInstanceId,
			DssManager dssManager, Integer encounterId, 
			Integer locationTagId,Integer locationId)
	{
		HashMap<String, ArrayList<DssElement>> dssElementsByType = dssManager
				.getDssElementsByType();
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService
				.getEncounter(encounterId);
		String type = null;

		if (dssElementsByType == null)
		{
			return;
		}
		Iterator<String> iter = dssElementsByType.keySet().iterator();
		ArrayList<DssElement> dssElements = null;

		while (iter.hasNext())
		{
			type = iter.next();
			dssElements = dssElementsByType.get(type);
			for (int i = 0; i < dssElements.size(); i++)
			{
				DssElement currDssElement = dssElements.get(i);

					this.addStatistics(patient, currDssElement, formInstanceId, i,
							encounter, type,locationTagId,locationId);
				}
			}
		}

	public void updateStatistics(Statistics statistics)
	{
		getNbsDAO().updateStatistics(statistics);
	}
	
	public void createStatistics(Statistics statistics)
	{
		getNbsDAO().addStatistics(statistics);
	}

	private void addStatistics(Patient patient, DssElement currDssElement,
			Integer formInstanceId, int questionPosition, Encounter encounter,
			String formName,Integer locationTagId,Integer locationId)
	{
			DssService dssService = Context
					.getService(DssService.class);
			Integer ruleId = currDssElement.getRuleId();
			Rule rule = dssService.getRule(ruleId);

			Statistics statistics = new Statistics();
			statistics.setAgeAtVisit(org.openmrs.module.nbs.util.Util
					.adjustAgeUnits(patient.getBirthdate(), null));
			statistics.setPriority(rule.getPriority());
			statistics.setFormInstanceId(formInstanceId);
			statistics.setLocationTagId(locationTagId);
			statistics.setPosition(questionPosition + 1);

			statistics.setRuleId(ruleId);
			statistics.setPatientId(patient.getPatientId());
			statistics.setFormName(formName);
			statistics.setEncounterId(encounter.getEncounterId());
			statistics.setLocationId(locationId);

			getNbsDAO().addStatistics(statistics);
	}

	public List<Study> getActiveStudies()
	{
		return getNbsDAO().getActiveStudies();
	}

	public List<Statistics> getStatByFormInstance(int formInstanceId,String formName, 
			Integer locationId)
	{
		return getNbsDAO().getStatByFormInstance(formInstanceId,formName, locationId);
	}

	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName)
	{
		return getNbsDAO().getStudyAttributeValue(study, studyAttributeName);
	}

	public List<Statistics> getStatByIdAndRule(int formInstanceId,int ruleId,String formName, 
			Integer locationId)	{
		return getNbsDAO().getStatByIdAndRule(formInstanceId,ruleId,formName,locationId);
	}

	public List<OldRule> getAllOldRules()
	{
		return getNbsDAO().getAllOldRules();
	}

	public List<String> getInsCategories(){
		return getNbsDAO().getInsCategories();
	}
	

	public String getObsvNameByObsvId(String obsvId){
		return getNbsDAO().getObsvNameByObsvId(obsvId);
	}
	
	public String getInsCategoryByCarrier(String carrierCode){
		return getNbsDAO().getInsCategoryByCarrier(carrierCode);
	}
	
	public String getInsCategoryBySMS(String smsCode){
		return getNbsDAO().getInsCategoryBySMS(smsCode);
	}
	
	public String getInsCategoryByInsCode(String insCode){
		return getNbsDAO().getInsCategoryByInsCode(insCode);
	}
	
	
	public PatientState getPrevProducePatientState(Integer sessionId, Integer patientStateId ){
		ATDService atdService = Context.getService(ATDService.class);
		return atdService.getPrevPatientStateByAction(sessionId, patientStateId,"PRODUCE FORM INSTANCE");
	}
	
	
		public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName){
			return getNbsDAO().getStatsByEncounterForm(encounterId, formName);
		}

		public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName){
			return getNbsDAO().getStatsByEncounterFormNotPrioritized(encounterId, formName);
		}
	
		
		public NbsHL7Export insertEncounterToHL7ExportQueue(NbsHL7Export export) {
			getNbsDAO().insertEncounterToHL7ExportQueue(export);
			return export;
		}

		public List<NbsHL7Export> getPendingHL7Exports() {
			return getNbsDAO().getPendingHL7Exports();
			
		}

		public void saveNbsHL7Export(NbsHL7Export export) {

			getNbsDAO().saveNbsHL7Export(export);
			
		}
	
		public List<NbsHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId) {
			return getNbsDAO().getPendingHL7ExportsByEncounterId(encounterId);
		}
		
		
		public NbsHL7Export getNextPendingHL7Export(String resend, String resendNoAck) {
			return getNbsDAO().getNextPendingHL7Export(resend, resendNoAck);
			
		}

	
	
	
		public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
				Integer locationTagId,Integer locationId){
			return getNbsDAO().getReprintRescanStatesByEncounter(encounterId, optionalDateRestriction, locationTagId,locationId);
		}
		
		public List<String> getPrinterStations(Location location){
			String locationTagAttributeName = "ActivePrinterLocation";
			ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);

			Set<LocationTag> tags = location.getTags();
			List<String>  stationNames = new ArrayList<String>();
			for (LocationTag tag : tags){
				LocationTagAttributeValue locationTagAttributeValue = 
					chirdlUtilService.getLocationTagAttributeValue(tag
						.getLocationTagId(), locationTagAttributeName,location.getLocationId());
				if (locationTagAttributeValue != null)
				{
					String activePrinterLocationString = locationTagAttributeValue
							.getValue();
					//only display active printer locations
					if (activePrinterLocationString.equalsIgnoreCase("true"))
					{
						stationNames.add(tag.getTag());
					}
				}
				
			}
			Collections.sort(stationNames);
			
			return stationNames;
		}
		
		
		public void  saveHL7ExportMap (NbsHL7ExportMap map){
			
			getNbsDAO().saveHL7ExportMap(map);
			
		}
		
		public NbsHL7ExportMap getNbsExportMapByQueueId(Integer queue_id, NbsHL7ExportMapType mapType){
			return getNbsDAO().getNbsExportMapByQueueId(queue_id, mapType);
		}
		
		public NbsHL7ExportStatus getNbsExportStatusByName (String name){
			return getNbsDAO().getNbsExportStatusByName(name);
		}
		
		public NbsHL7ExportStatus getNbsExportStatusById (Integer id){
			return getNbsDAO().getNbsExportStatusById( id);
		}	
		
		public void  saveHL7ExportMapType (NbsHL7ExportMapType map){
			getNbsDAO().saveHL7ExportMapType(map);
		}
		
		public NbsHL7ExportMapType getHL7ExportMapTypeByName (String name)
		{
			return getNbsDAO().getHL7ExportMapTypeByName(name);
		}
		public String getNPI(String firstName, String lastName){
			String npi = "";
			
			//Until we have a source for npi, set to random number
			//Need to check if duplicates in npi source.
			Random generator = new Random(111111118);
			int n = generator.nextInt(9);
			npi = String.valueOf(n);
			//
			return npi;
			
		}
		
		
}