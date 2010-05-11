package org.openmrs.module.nbsmodule.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.nbsmodule.NBSModuleResponse;
import org.openmrs.module.nbsmodule.NBSParameterHandler;
import org.openmrs.module.nbsmodule.NBSService;
import org.openmrs.module.nbsmodule.db.NBSModuleDAO;
import org.openmrs.module.nbsmodule.util.Util;
import org.openmrs.module.nbsmodule.util.Base64;
import org.openmrs.module.sockethl7listener.HL7MessageConstructor;

/**
 * Newborn Screen services
 * 
 * @author Vibha Anand
 * @vesrion 1.0
 */
public class NBSServiceImpl implements NBSService {

	private Log log = LogFactory.getLog(this.getClass());
	private NBSModuleDAO dao;
	private FormService formService; 
	private DssService dss;
	
	
	public NBSServiceImpl() {
		log.error("**********NBSService Constructor called!!!!!!!! ");
	}
	
	private NBSModuleDAO getNBSModuleDAO() {
		return dao;
	}
	
		
	public void setNBSModuleDAO(NBSModuleDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * Creates a new helloWorldResponse record
	 * 
	 * @param helloWorldResponse to be created
	 * @throws APIException
	 */
	public void createNBSModuleResponse(NBSModuleResponse alertResponse) throws APIException {
		getNBSModuleDAO().createNBSModuleResponse(alertResponse);
	}

	/**
	 * Get helloWorldResponse by internal identifier
	 * 
	 * @param helloWorldResponseId internal helloWorldResponse identifier
	 * @return helloWorldResponse with given internal identifier
	 * @throws APIException
	 */
	public NBSModuleResponse getNBSModuleResponse(String pid ) throws APIException {
		String patientID = null;
		int index = pid.indexOf("=");
		
		if (index == -1){
			patientID = pid;
		}
		else {	
			patientID = pid.split("=")[1].split("&")[0];
		}
	
		NBSModuleResponse response = new NBSModuleResponse();
		
		try {
	//		response.setResponse(dss.runRules(patientID));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		return response;
	}

	
	
	/**
	 * Update helloWorldResponse 
	 * 
	 * @param helloWorldResponse to be updated
	 * @throws APIException
	 */
	public void updateNBSModuleResponse(NBSModuleResponse helloWorldResponse) throws APIException {
		getNBSModuleDAO().updateNBSModuleResponse(helloWorldResponse);
	}
	
	/**
	 * Get helloWorldResponses
	 * 
	 * @return helloWorldResponse list
	 * @throws APIException
	 */
	public List<NBSModuleResponse> getResponses() throws APIException {
		return getNBSModuleDAO().getResponses();
	}
	
	public void writeNBSModuleResponse(Patient p, Encounter enc) throws APIException {
		try {
					
			
			dss = (DssService) Context.getService(DssService.class);
			formService = (FormService) Context.getService(FormService.class);
			
			
			//PatientService pSvc = Context.getPatientService();
			//Patient thisPatient = pSvc.getPatient(p.getPatientId());
			
							
			//EncounterService encSvc= Context.getEncounterService();
			//ObsService obsSvc = Context.getObsService();
						
			//Set<Obs> obsSet = encSvc.getEncounter(enc.getEncounterId()).getObs();
			
			
			List<org.openmrs.module.dss.hibernateBeans.Rule> rules = dss.getRules(new Rule(), true, true, null);
						
			for (Rule r : rules)
			{
			    String ruleType = r.getRuleType();
			    String tokenName = r.getTokenName();
			    
				if (ruleType.isEmpty() || tokenName.isEmpty()) {
					continue;
				}
				
				this.produce(tokenName, ruleType, p, enc);
			} 
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			
		}
	}
	
	
	
	/**
	 * produce the xml required for the form
	 * 
	 * @param output
	 * @param state
	 * @param patient
	 * @param encounterId
	 * @throws Exception
	 */
	public void produce(String tokenName, String formName, Patient patient,Encounter enc)throws Exception
	{
		
		ATDService atdService = Context.getService(ATDService.class);
		List<Form> forms = formService.getForms(formName, false);
		AdministrationService adminService = Context.getAdministrationService();
		String rulePackagePrefix = adminService.getGlobalProperty("dss.rulePackagePrefix");
		List<Session> sessions = atdService.getSessionsByEncounter(enc.getEncounterId());
		Session session = null;
		if (sessions.size()>0){
			session = sessions.get(0);
		}
		if (forms.isEmpty())
		{
			//TODO: Throw exception
			return;
		}
		int formId = forms.get(0).getFormId();

		// Run specific 
		Result result;
		result = atdService.evaluateRule(tokenName,
				patient, new HashMap<String,Object>(), null);
		
		if(result == null || !result.exists())
		{	
			return;
		}
		
		String mergeDirectory = null;
		FormAttributeValue val = atdService.getFormAttributeValue(formId, "defaultMergeDirectory", 1,1 );
		if (val != null) {
			mergeDirectory = val.getValue(); 
		}
		
		Map<String, Object> parameters = null;
		parameters = new HashMap<String, Object>();
		String rule = "";
		String ruleFieldname = "";
		
		String resultStr = result.get(0).toString(); // result = rule.rulename:field1,value of field2:field2
		if(resultStr.indexOf("rule") != -1)
		{
			StringTokenizer tokenizer = new StringTokenizer(resultStr,",");
			ArrayList<String> ruleTokens = new ArrayList<String>();
					
			while(tokenizer.hasMoreTokens())
			{
				ruleTokens.add(tokenizer.nextToken().trim());
			}
			
			
			
			for (String ruleToken : ruleTokens)
			{
				if(ruleToken.startsWith("rule."))
				{
					tokenizer  = new StringTokenizer(ruleToken.substring(5,ruleToken.length()),":");
					if(tokenizer.hasMoreTokens())
					{
						rule = tokenizer.nextToken().trim();
						ruleFieldname = tokenizer.nextToken().trim();
					}
				}
				else {
					tokenizer  = new StringTokenizer(ruleToken.substring(0,ruleToken.length()),":");
					while(tokenizer.hasMoreTokens())
					{
						String value = tokenizer.nextToken().trim();
						String fieldname = tokenizer.nextToken().trim();
						parameters.put(fieldname, value);

					}
						
				}	
			}
			if(!rule.isEmpty())
			{
				result.clear();
				result = atdService.evaluateRule(rule,
						patient, new HashMap<String,Object>(), "org.openmrs.module.nbsmodule.rule.");
			}
			for(int i = 0; i < result.size(); i++)
			{
				NBSModuleResponse response = new NBSModuleResponse();
				Integer providerId = enc.getProvider().getUserId();
				response.setPatient(patient);
				
				if(ruleFieldname.equalsIgnoreCase("provider"))
				{
					response.setProvider(result.get(i).toString());
					response.setProviderId(providerId);
				}
				else {
					response.setProvider(enc.getProvider().toString());
					response.setProviderId(enc.getProvider().getUserId());
				}
				
				
				response.setDateCreated(new Date());
				response.setFormId(formId);
				
				List<NBSModuleResponse> existingAlerts = getNBSAlerts(patient, formId, 
						providerId, enc, new Date(), null);
				//Don't send to physician multiple times per day.
				if (existingAlerts.size() > 0){
					return;
				}
					
				FormInstance formInstance = atdService.addFormInstance(formId, enc.getLocation().getLocationId() );
				if (formInstance != null){
					Integer formInstanceId = formInstance.getFormInstanceId();
				
					String mergeFilename = mergeDirectory + "//" + formInstance + ".xml";
										
					OutputStream output = new FileOutputStream(mergeFilename);
					parameters.put(ruleFieldname, result.get(i).toString());
				
					
					if (atdService.produce(patient, formInstance, output, 
							enc.getEncounterId(),
							parameters,
							rulePackagePrefix,
							false ,1 ,
							session.getSessionId()))
					{
						response.setEncounter(enc);
						response.setMrn(patient.getPatientIdentifier().getIdentifier());
						response.setFormInstanceId(formInstance.getFormInstanceId());
						Integer status = getAlertStatusIdByName("waiting_for_export");
						if (status == null){
							status = 1;
						}
						response.setStatus(status);
						createNBSModuleResponse(response);
					}

				}
				
				TeleformFileMonitor.addToPendingStatesWithFilename(formInstance, mergeDirectory 
						+ formInstance.getFormInstanceId() +" .19");
				
			}
		}
		else
		{
			NBSModuleResponse response = new NBSModuleResponse();
			response.setPatient(patient);
			response.setProviderId(enc.getProvider().getUserId());
			response.setDateCreated(new Date());
			response.setFormId(formId);
			
			
			
			List<NBSModuleResponse> existingAlerts = getNBSAlerts(patient, formId, 
					enc.getProvider().getUserId(), enc,  new Date(), null);
			//Don't send to physician multiple times per day.
			if (existingAlerts.size() > 0){
				return;
			}
				
			FormInstance formInstance = atdService.addFormInstance(formId, enc.getLocation().getLocationId());
			Integer formInstanceId = formInstance.getFormInstanceId();
			
			String mergeFilename = mergeDirectory + "//" + formInstance + ".xml";
									
			OutputStream output = new FileOutputStream(mergeFilename);
			
			// This is simply the action string, so default ruleFieldname = paragraphText by default and the default provider is the encounter provider
			
			ruleFieldname = "provider";
			
			
			String providerName = enc.getProvider().getUserId().toString(); //Util.toProperCase(enc.getProvider().getGivenName()) + " " + Util.toProperCase(enc.getProvider().getFamilyName());
			parameters.put(ruleFieldname, providerName);
			
			ruleFieldname = "paragraphText";
			parameters.put(ruleFieldname, result.get(0).toString());
			
			if (atdService.produce(patient, formInstance, output, 
					enc.getEncounterId(),
					parameters,
					rulePackagePrefix,
					false ,1 ,
					session.getSessionId()))
			{

				response.setEncounter(enc);
				response.setProvider(enc.getProvider().toString());
				response.setMrn(patient.getPatientIdentifier().getIdentifier());
				response.setFormInstanceId(formInstance.getFormInstanceId());
				Integer status = getAlertStatusIdByName("waiting_for_export");
				if (status == null){
					status = 1;
				}
				response.setStatus(status);
				createNBSModuleResponse(response);
			}
			
			TeleformFileMonitor.addToPendingStatesWithFilename( formInstance , mergeDirectory + formInstanceId +" .19");
			
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.nbsmodule.NBSService#getAlerts(org.openmrs.Patient, java.util.Properties)
	 */
	public List<NBSModuleResponse>  getNBSAlerts(Patient resultPatient, Integer formId, Integer providerId, Encounter encounter, Date date, Integer status){
	
		List<NBSModuleResponse> nbsAlerts = getNBSModuleDAO().getNBSAlerts(resultPatient,formId, providerId, encounter, date, status);
		return nbsAlerts;
	}
		
	public List<NBSModuleResponse>  captureNBSAlerts(Patient resultPatient, Integer formId, Integer providerId, Encounter encounter, Date date){
		NBSService nbsService = Context.getService(NBSService.class);
		Integer status = getAlertStatusIdByName("waiting_for_export");
		List<NBSModuleResponse> nbsAlerts = getNBSModuleDAO().getNBSAlerts(resultPatient,formId, providerId, null, null, status );
		for (NBSModuleResponse alert : nbsAlerts){
			Integer statusId = getAlertStatusIdByName("processing_alert");
			if (status == null){
				status = 1;
			}
			alert.setStatus(statusId);
			nbsService.updateNBSModuleResponse(alert);
		}
		return nbsAlerts;
	}
	
	public Integer getAlertStatusIdByName(String name){
		return getNBSModuleDAO().getAlertStatusIdByName(name);
	}

	public void consume(InputStream input, Patient patient, Integer encounterId, FormInstance formInstance,
	                    Integer sessionId, List<FormField> fieldsToConsume, Integer locationTagId) {
		try {
			DssService dssService = Context.getService(DssService.class);
			ATDService atdService = Context.getService(ATDService.class);
			ParameterHandler parameterHandler = new NBSParameterHandler();
			
			// make sure storeObs gets loaded before running consume
			// rules
			dssService.loadRule("storeObs", false);
			
			atdService.consume(input, formInstance, patient, encounterId, null, null, parameterHandler, fieldsToConsume,
			    locationTagId, sessionId);
			
		}
		catch (Exception e) {
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
	}

}
