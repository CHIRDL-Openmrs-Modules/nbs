/**
 * 
 */
package org.openmrs.module.nbs.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.nbs.NbsStateActionHandler;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.nbs.service.EncounterService;

/**
 * @author tmdugan
 *
 */
public class Rescan implements ProcessStateAction
{

	/* (non-Javadoc)
	 * @see org.openmrs.module.nbs.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
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
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();

		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		
		patientState.setFormInstance(formInstance);
		atdService.updatePatientState(patientState);

		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formInstance.getFormId());
		String formName = form.getName();

		// void obs from previous scan
		ObsService obsService = Context.getObsService();
		/*for (Statistics currStat : stats)
		{
			Integer obsId = currStat.getObsvId();
			Obs obs = obsService.getObs(obsId);
			obsService.voidObs(obs, "voided due to rescan");
		}*/

		NbsStateActionHandler.consume(sessionId, formInstance, patient,
				 parameters,
				null,locationTagId);
		StateManager.endState(patientState);
		
		//start a new session if this was a PSF_RESCAN
		if (patientState.getState().getName()
				.equalsIgnoreCase("PSF_rescan"))
		{
			Session newSession = atdService.addSession();
			sessionId = newSession.getSessionId();
			newSession.setEncounterId(encounterId);
			atdService.updateSession(newSession);
			
			NbsStateActionHandler.changeState(patient, sessionId, currState, 
					stateAction,parameters,locationTagId,locationId);
		}

	}
	private void voidObsForConcept(String conceptName,Integer encounterId){
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		ObsService obsService = Context.getObsService();
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<Concept>();
		
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept(conceptName);
		questions.add(concept);
		List<Obs> obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		for(Obs currObs:obs){
			obsService.voidObs(currObs, "voided due to rescan");
		}
	}
	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters){
		StateAction stateAction = patientState.getState().getAction();
		processAction(stateAction,patientState.getPatient(),patientState,parameters);

	}
}
