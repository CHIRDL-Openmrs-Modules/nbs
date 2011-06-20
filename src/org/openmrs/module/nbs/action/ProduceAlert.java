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
public class ProduceAlert implements ProcessStateAction
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

		if(parameters == null){
			parameters = new HashMap<String,Object>();
		}
		
		parameters.put("sessionId", sessionId);
		parameters.put("locationTagId", locationTagId);
		parameters.put("locationId",locationId);
		FormInstance formInstance = new FormInstance(locationId, null, null);
		parameters.put("formInstance", formInstance);
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
		
		atdService.updatePatientState(patientState);
		
		
		Rule rule = new Rule();
		rule.setTokenName("RTA");
		rule.setParameters(parameters);
		
		String defaultPackagePrefix = Util.formatPackagePrefix(
				adminService.getGlobalProperty("atd.defaultPackagePrefix"));
		
		String rulePackagePrefix = adminService.getGlobalProperty("dss.rulePackagePrefix");
		
		atdService.evaluateRule("RTA", patient, parameters,rulePackagePrefix);
		
		//LogicService logicService = Context.getLogicService();

		//ObsNbsDatasource xmlDatasource = (ObsNbsDatasource) logicService
				//.getLogicDataSource("RMRS");

		// clear the in-memory obs from the MRF dump at the end of each
		// produce state
		//xmlDatasource.deleteRegenObsByPatientId(patientId);
		
		StateManager.endState(patientState);
		NbsStateActionHandler.changeState(patient, sessionId, currState, stateAction, parameters,
				locationTagId, locationId);
		
		
		
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
