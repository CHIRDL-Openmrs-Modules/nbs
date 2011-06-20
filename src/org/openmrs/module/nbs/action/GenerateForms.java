/**
 * 
 */
package org.openmrs.module.nbs.action;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.nbs.NbsStateActionHandler;
import org.openmrs.module.nbs.datasource.ObsNbsDatasource;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.dss.hibernateBeans.Rule;

/**
 * @author tmdugan
 * 
 */
public class GenerateForms implements ProcessStateAction
{
	private static Log log = LogFactory.getLog(NbsStateActionHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.nbs.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction,
	 *      org.openmrs.Patient,
	 *      org.openmrs.module.atd.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		NbsService nbsService = Context
				.getService(NbsService.class);
		ATDService atdService = Context
				.getService(ATDService.class);
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);
		AdministrationService adminService = Context.getAdministrationService();

		
		String mode = "PRODUCE";
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		
		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		String formName = null;
		if(parameters != null){
			formName = (String) parameters.get("formName");
		}
		if(formName == null){
			formName = currState.getFormName();
		}
		LocationTagAttributeValue locTagAttrValue = 
			chirdlUtilService.getLocationTagAttributeValue(locationTagId, formName, locationId);
		
		Integer formId = null;
		
		if(locTagAttrValue != null){
			String value = locTagAttrValue.getValue();
			if(value != null){
				try
				{
					formId = Integer.parseInt(value);
				} catch (Exception e)
				{
				}
			}
		}
		
		if(formId == null){
			//open an error state
			currState = atdService.getStateByName("ErrorState");
			atdService.addPatientState(patient,
					currState, sessionId,locationTagId,locationId);
			log.error(formName+
					" locationTagAttribute does not exist for locationTagId: "+
					locationTagId+" locationId: "+locationId);
			return;
		}
		
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		
		// write the form
		FormInstance formInstance = atdService.addFormInstance(formId,
				locationId);
		
		if(parameters == null){
			parameters = new HashMap<String,Object>();
		}
		
		parameters.put("sessionId", sessionId);
		parameters.put("formInstance", formInstance);
		parameters.put("locationTagId", locationTagId);
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		parameters.put("locationId",locationId);
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		String locationName = null;
		if(location != null){
			locationName = location.getName();
		}
		parameters.put("location", locationName);
		if(encounterId != null)
		{
			parameters.put("encounterId", encounterId);
		}
		
		parameters.put("mode", mode);
		patientState.setFormInstance(formInstance);
		atdService.updatePatientState(patientState);
		Integer formInstanceId = formInstance.getFormInstanceId();
		
		Rule rule = new Rule();
		
		rule.setTokenName("RTA");
		rule.setParameters(parameters);
		
		String defaultPackagePrefix = Util.formatPackagePrefix(
				adminService.getGlobalProperty("atd.defaultPackagePrefix"));
		
		String rulePackagePrefix = adminService.getGlobalProperty("dss.rulePackagePrefix");
		
		atdService.evaluateRule("RTA", patient, parameters,rulePackagePrefix);
		
		LogicService logicService = Context.getLogicService();

		ObsNbsDatasource xmlDatasource = (ObsNbsDatasource) logicService
				.getLogicDataSource("RMRS");

		// clear the in-memory obs from the MRF dump at the end of each
		// produce state
		xmlDatasource.deleteRegenObsByPatientId(patientId);

		StateManager.endState(patientState);
		NbsStateActionHandler.changeState(patient, sessionId, currState, stateAction, parameters,
				locationTagId, locationId);
		
		
		
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
