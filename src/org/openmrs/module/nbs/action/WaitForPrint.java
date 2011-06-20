/**
 * 
 */
package org.openmrs.module.nbs.action;

import java.util.HashMap;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.nbs.NbsStateActionHandler;
import org.openmrs.module.nbs.service.NbsService;

/**
 * @author tmdugan
 *
 */
public class WaitForPrint implements ProcessStateAction
{

	/* (non-Javadoc)
	 * @see org.openmrs.module.nbs.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		LocationService locationService = Context.getLocationService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		
		Integer locationTagId = patientState.getLocationTagId();
	
		NbsService nbsService = Context
				.getService(NbsService.class);
		
		Integer sessionId = patientState.getSessionId();
		ATDService atdService = Context.getService(ATDService.class);
		PatientState stateWithFormId = atdService.getPrevPatientStateByAction(sessionId, patientState.getPatientStateId()
		,"PRODUCE FORM INSTANCE");

		FormInstance formInstance = patientState.getFormInstance();

		if(formInstance == null&&stateWithFormId != null)
		{
			formInstance = stateWithFormId.getFormInstance();
		}
		
		if (formInstance == null){
			formInstance =  (FormInstance) parameters.get("formInstance");
		}
		
		
		atdService = Context.getService(ATDService.class);
		patientState.setFormInstance(formInstance);
		atdService.updatePatientState(patientState);
		
		String mergeDirectory = IOUtil
				.formatDirectoryName(org.openmrs.module.atd.util.Util
						.getFormAttributeValue(formInstance.getFormId(),
								"defaultMergeDirectory", locationTagId,
								formInstance.getLocationId()));
		TeleformFileState teleformFileState = TeleformFileMonitor
				.addToPendingStatesWithFilename(formInstance, mergeDirectory
						+ formInstance.toString() + ".20");
		
		Location defaultLocation = locationService.getLocation("Default Location");
		Integer defaultLocationId = 1;
		if (defaultLocation != null){
			defaultLocationId = defaultLocation.getLocationId();
		} 
		patientState.setLocationId(defaultLocationId);
		Integer formId = (Integer) parameters.get("formId");
		patientState.setFormId(formId);
		teleformFileState.addParameter("patientState", patientState);
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		StateManager.endState(patientState);
		NbsStateActionHandler.changeState(patientState.getPatient(), patientState
				.getSessionId(), patientState.getState(),
				patientState.getState().getAction(),parameters,
				patientState.getLocationTagId(),
				patientState.getLocationId());
		
	}

}
