/**
 * 
 */
package org.openmrs.module.nbs.action;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.nbs.action.ConsumeFormInstance;
import org.openmrs.module.nbs.NbsStateActionHandler;

/**
 * @author tmdugan
 *
 */
public class ConsumeFormInstance implements ProcessStateAction
{
	private static Log log = LogFactory.getLog(ConsumeFormInstance.class);
	/* (non-Javadoc)
	 * @see org.openmrs.module.nbs.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();

		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formInstance.getFormId());
		patientState.setFormInstance(formInstance);
		ATDService atdService = Context.getService(ATDService.class);
		atdService.updatePatientState(patientState);
		startTime = System.currentTimeMillis();
		NbsStateActionHandler.consume(sessionId,formInstance,patient,
				parameters,null,locationTagId);
		startTime = System.currentTimeMillis();
		StateManager.endState(patientState);
		log.error("Consume: Total time to consume "+form.getName()+": "+(System.currentTimeMillis()-totalTime));
		NbsStateActionHandler.changeState(patient, sessionId, currState,
				stateAction,parameters,locationTagId,locationId);

	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
