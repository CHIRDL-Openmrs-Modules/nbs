package org.openmrs.module.nbsmodule.impl;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.nbsmodule.NBSModuleResponse;
import org.openmrs.module.nbsmodule.NBSService;
import org.openmrs.module.nbsmodule.db.NBSModuleDAO;
import org.openmrs.module.nbsmodule.util.Util;
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
		String patientID;
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
		
		if(result != null && !result.exists())
		{	
			return;
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
				response.setPatient(patient);
				
				if(ruleFieldname.equalsIgnoreCase("provider"))
				{
					response.setProvider(result.get(i).toString());
				}
				else {
					response.setProvider(enc.getProvider().toString());
					response.setProviderId(enc.getProvider().getUserId());
				}
				
				
				response.setDatestamp(new Date());
				response.setFormId(formId);
				
				
				if (!getNBSModuleDAO().duplicateAlert(response))
				{
					
				int formInstanceId = atdService.addFormInstance(formId).getFormInstanceId();
				
				FormAttributeValue val = atdService.getFormAttributeValue(formId, "defaultMergeDirectory");
				String mergeDirectory = val.getValue(); 
				String mergeFilename = mergeDirectory + "//" + formInstanceId + ".xml";
										
				OutputStream output = new FileOutputStream(mergeFilename);
				parameters.put(ruleFieldname, result.get(i).toString());
				
				if(atdService.produce(patient, formInstanceId,output,formId,
						enc.getEncounterId(),parameters, null, false)) {
					response.setEncounter(enc);
					response.setMrn(patient.getPatientIdentifier().getIdentifier());
					response.setAlertId(formInstanceId);
					createNBSModuleResponse(response);
				}
				
				TeleformFileMonitor.addToPendingStatesWithFilename(formId, formInstanceId, mergeDirectory + formInstanceId +" .19");
				}
			}
		}
		else
		{
			NBSModuleResponse response = new NBSModuleResponse();
			response.setPatient(patient);
			response.setProviderId(enc.getProvider().getUserId());
			response.setDatestamp(new Date());
			response.setFormId(formId);
			
			if (!getNBSModuleDAO().duplicateAlert(response))
			{
				
			int formInstanceId = atdService.addFormInstance(formId).getFormInstanceId();
			
			org.openmrs.module.atd.hibernateBeans.FormAttributeValue val = atdService.getFormAttributeValue(formId, "defaultMergeDirectory");
			String mergeDirectory = val.getValue(); 
			String mergeFilename = mergeDirectory + "//" + formInstanceId + ".xml";
									
			OutputStream output = new FileOutputStream(mergeFilename);
			
			// This is simply the action string, so default ruleFieldname = paragraphText by default and the default provider is the encounter provider
			
			ruleFieldname = "provider";
			
			
			String providerName = enc.getProvider().getUserId().toString(); //Util.toProperCase(enc.getProvider().getGivenName()) + " " + Util.toProperCase(enc.getProvider().getFamilyName());
			parameters.put(ruleFieldname, providerName);
			
			ruleFieldname = "paragraphText";
			parameters.put(ruleFieldname, result.get(0).toString());
			
			if(atdService.produce(patient, formInstanceId,output,formId,
					enc.getEncounterId(),parameters, null, false)) {

				response.setEncounter(enc);
				response.setProvider(enc.getProvider().toString());

				response.setMrn(patient.getPatientIdentifier().getIdentifier());
				
				response.setAlertId(formInstanceId);
				createNBSModuleResponse(response);
			}
			
			TeleformFileMonitor.addToPendingStatesWithFilename(formId, formInstanceId, mergeDirectory + formInstanceId +" .19");
			}
		}
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.nbsmodule.NBSService#getAlerts(org.openmrs.Patient, java.util.Properties)
	 */
	public List<String>  getAlerts(Patient resultPatient, Properties prop)
	{
		Integer pid = null;
		String message = "";
		if (resultPatient!= null){
			pid = resultPatient.getPatientId();
		}
		//if pid is null, get all alerts not yet processed
		List<NBSModuleResponse> nbsAlerts = getNBSModuleDAO().getNBSAlertsByPatient(pid);
		List<String> messages = new ArrayList<String>();
		
		try
		{
			for(NBSModuleResponse currAlert:nbsAlerts)
			{
				int encounter_id = 0;
				int nbs_alert_id = 0;
				int alert_id = 0;
				String mrn = "";
				int form_id = 0;
				if (currAlert.getEncounter().getEncounterId() != null)
				{
					encounter_id = currAlert.getEncounter().getEncounterId();
				} else
				{
					log.error("ERROR: Null value for encounter_id in nbs_alert table ");
					break;
				}
				if (currAlert.getMrn() != null)
				{
					mrn = currAlert.getMrn();
				} else
				{
					// continue with MRN?
					log.error("ERROR: Null value for mrn in nbs_alert table.");
				}

				nbs_alert_id = currAlert.getNbsalertId();

				if (currAlert.getAlertId() != null)
				{
					alert_id = currAlert.getAlertId();
				}
				if (currAlert.getFormId() != null)
				{
					form_id = currAlert.getFormId();
				}
				ATDService atdService = Context.getService(ATDService.class);
				FormAttributeValue value = atdService.getFormAttributeValue(form_id, "defaultImageDirectory");
				value.getValue();

				HL7MessageConstructor hl7mc = new HL7MessageConstructor(prop);
				message = hl7mc.createHl7Message(encounter_id, mrn, value.getValue(),
				alert_id);
				if (message != null)
				{
					currAlert.setStatus(1);
					getNBSModuleDAO().updateNBSModuleResponse(currAlert);
					messages.add(message);
					saveMessage(message, encounter_id);
				}
				
				
			

			}

	        	
		} catch (HibernateException e)
		{
			e.printStackTrace();
		} catch (Exception e){
			Util.getStackTrace(e);
		}
		return messages;
	}
	
	/**
	 * Saves message string to archive directory
	 * @param message
	 * @param encid
	 */
	public void saveMessage( String message, Integer encid){
		AdministrationService adminService = Context.getAdministrationService();
		EncounterService es = Context.getService(EncounterService.class);
		String archiveDir = "";
		String filename = "";
		try {
			Encounter enc = es.getEncounter(encid);
			Patient patient = new Patient();
			patient = enc.getPatient();
			PatientIdentifier pi = patient.getPatientIdentifier();
			String mrn = "";
		
		
			if (pi != null) mrn = pi.getIdentifier();
			 filename =  Util.archiveStamp() + "_"+ mrn + ".hl7";
		
			archiveDir = IOUtil.formatDirectoryName(adminService
				.getGlobalProperty("nbsmodule.outboundHl7ArchiveDirectory"));
		

			FileOutputStream archiveFile = null;
		
			archiveFile = new FileOutputStream(
					archiveDir + "/" + filename);
		
			if (archiveDir != null && archiveFile != null)
			{
			
				ByteArrayInputStream messageStream = new ByteArrayInputStream(
						message.getBytes());
				IOUtil.bufferedReadWrite(messageStream, archiveFile);
				archiveFile.flush();
				archiveFile.close();
				
			}
			log.error("Couldn't find file: "+archiveDir + "/" + filename);
			
		} catch (Exception e){
			log.error(Util.getStackTrace(e));
		}
		return;
		
	}
}
