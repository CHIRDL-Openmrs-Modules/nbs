/**
 * 
 */
package org.openmrs.module.nbs.action;

import java.util.Date;
import java.util.HashMap;

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
import org.openmrs.module.nbs.hibernateBeans.NbsHL7Export;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMap;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMapType;
import org.openmrs.module.nbs.service.NbsService;


/**
 * @author tmdugan
 *
 */
public class LoadHL7ExportQueue implements ProcessStateAction
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
		NbsService nbsService = Context
		.getService(NbsService.class);
		ATDService atdService = Context
		.getService(ATDService.class);
		
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		

		//Get existing form_id.
		//If not a form, form id will be null in export map table.
		//Default config location will have the format for non-form;
		
		//Integer formId = (Integer) parameters.get("formId");
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		String providerId = (String) parameters.get("providerId");
		Integer formId = formInstance.getFormId();
		String formIdString = String.valueOf(formId);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();

		try {
			
			NbsHL7Export export = new NbsHL7Export();
		
			export.setDateInserted(new Date());
			export.setEncounterId(encounterId);
			export.setSessionId(sessionId);
			export.setVoided(false);
			export.setStatus(1);
			NbsHL7ExportMap exportMap = new NbsHL7ExportMap();
			
			NbsHL7ExportMapType formMapType = nbsService.getHL7ExportMapTypeByName("form_instance");
			if ( formMapType == null) {
				log.error("Null form map type.  Check name of export map type.");
				return;
			}
			NbsHL7ExportMapType providerMapType = nbsService.getHL7ExportMapTypeByName("provider_id");
			if ( formMapType == null) {
				log.error("Null provider map type.  Check name of export map type.");
				return;
			}
			
			NbsHL7ExportMap  exportMapProvider = new NbsHL7ExportMap();
			NbsHL7Export insertedExport = nbsService.insertEncounterToHL7ExportQueue(export);
			exportMap.setValue(formInstance.toString());
			exportMap.setHl7ExportQueueId(insertedExport.getQueueId());
			exportMap.setDateInserted(new Date());
			exportMap.setVoided(false);
			exportMap.setNbsHl7ExportMapTypeId(formMapType.getNbsHl7ExportMapTypeId());
			nbsService.saveHL7ExportMap(exportMap);
			
			exportMapProvider.setValue(providerId);
			exportMapProvider.setHl7ExportQueueId(insertedExport.getQueueId());
			exportMapProvider.setDateInserted(new Date());
			exportMapProvider.setVoided(false);
			exportMapProvider.setNbsHl7ExportMapTypeId(providerMapType.getNbsHl7ExportMapTypeId());
			nbsService.saveHL7ExportMap(exportMapProvider);
			
		} catch (Exception e){
			log.error("Exception loading hl7 export table"  , e);
		} finally{

			StateManager.endState(patientState);
			NbsStateActionHandler.changeState(patient, sessionId, currState,stateAction,
					parameters,locationTagId,locationId);
		}
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
