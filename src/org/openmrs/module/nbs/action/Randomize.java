/**
 * 
 */
package org.openmrs.module.nbs.action;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
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
import org.openmrs.module.nbs.hibernateBeans.Study;
import org.openmrs.module.nbs.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.nbs.randomizer.BasicRandomizer;
import org.openmrs.module.nbs.randomizer.Randomizer;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.nbs.service.EncounterService;

/**
 * @author tmdugan
 *
 */
public class Randomize implements ProcessStateAction
{
	private static Log log = LogFactory.getLog(NbsStateActionHandler.class);

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

		NbsService nbsService = Context.getService(NbsService.class);
		ATDService atdService = Context
		.getService(ATDService.class);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();

		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		List<Study> activeStudies = nbsService.getActiveStudies();

		if (activeStudies !=  null){
			for (Study currActiveStudy : activeStudies)
			{
				StudyAttributeValue studyAttributeValue = nbsService
				.getStudyAttributeValue(currActiveStudy,
						"Custom Randomizer");

				Randomizer randomizer = null;

				if (studyAttributeValue != null)
				{
					String randomizerClassName = "org.openmrs.module.nbs.randomizer."+
					studyAttributeValue.getValue();

					try
					{
						Class theClass = Class.forName(randomizerClassName);
						randomizer = (Randomizer) theClass.newInstance();
					} catch (Exception e)
					{
						log.error("Error creating custom randomizer: "
								+ randomizerClassName);
						log.error(e.getMessage());
						log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
					}
				} else
				{
					randomizer = new BasicRandomizer();
				}

				if (randomizer != null)
				{
					EncounterService encounterService = Context.getService(EncounterService.class);
					randomizer.randomize(currActiveStudy, patient, encounterService.getEncounter(encounterId));
				}
			}
		}
		StateManager.endState(patientState);
		NbsStateActionHandler.changeState(patient, sessionId, currState,
				stateAction,parameters,locationTagId,locationId);
		
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
