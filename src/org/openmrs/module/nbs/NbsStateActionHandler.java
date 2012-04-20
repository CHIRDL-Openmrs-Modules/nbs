/**
 * 
 */
package org.openmrs.module.nbs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.StateActionHandler;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Program;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.nbs.advice.ThreadManager;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.nbs.util.Util;

/**
 * @author tmdugan
 * 
 */
public class NbsStateActionHandler implements StateActionHandler
{
	private static Log log = LogFactory.getLog(NbsStateActionHandler.class);
	private static NbsStateActionHandler stateActionHandler = null;
	
	public void fillUnfinishedStates()
	{
		AdministrationService adminService = Context.getAdministrationService();
		Context.authenticate(adminService
				.getGlobalProperty("scheduler.username"), adminService
				.getGlobalProperty("scheduler.password"));

		ATDService atdService = Context.getService(ATDService.class);
		Calendar todaysDate = Calendar.getInstance();
		todaysDate.set(Calendar.HOUR_OF_DAY, 0);
		todaysDate.set(Calendar.MINUTE, 0);
		todaysDate.set(Calendar.SECOND, 0);
		LocationService locationService = Context.getLocationService();

		List<Location> locations = locationService.getAllLocations();
		
		for(Location location:locations){
		
		Set<LocationTag> tags = location.getTags();
		
		if(tags != null){
		
			for(LocationTag tag:tags){
				Integer locationId = location.getLocationId();
				Integer locationTagId = tag.getLocationTagId();
		List<PatientState> unfinishedStatesToday = atdService.
			getUnfinishedPatientStatesAllPatients(todaysDate.getTime(),locationTagId,locationId);
				
		int numUnfinishedStates = unfinishedStatesToday.size();
		double processedStates = 0;
		
		log.info("fillUnfinishedStates(): Starting Today's state initialization....");
		for(PatientState currPatientState:unfinishedStatesToday)
		{	
			State state = currPatientState.getState();
			if (state != null)
			{
				StateAction stateAction = state.getAction();

				try
				{
					if (stateAction!=null&&stateAction.getActionName().equalsIgnoreCase(
							"CONSUME FORM INSTANCE"))
					{
						TeleformFileState teleformFileState = TeleformFileMonitor
							.addToPendingStatesWithoutFilename(
								currPatientState.getFormInstance());
						teleformFileState.addParameter("patientState",
								currPatientState);
					}
					HashMap<String,Object> parameters = new HashMap<String,Object>();
					parameters.put("formInstance", currPatientState.getFormInstance());
					processAction(stateAction, currPatientState.getPatient(),
							currPatientState,parameters);
				} catch (Exception e)
				{
					log.error(e.getMessage());
					log
							.error(org.openmrs.module.chirdlutil.util.Util
									.getStackTrace(e));
				}
			}
			if(processedStates%100==0){
				log.info("State initialization is: "+(int)((processedStates/numUnfinishedStates)*100)+"% complete. "+
						processedStates+" out of "+numUnfinishedStates+" processed.");
			}
			processedStates++;
		}
		
		log.info("Today's state initialization is: "+(int)((processedStates/numUnfinishedStates)*100)+"% complete.");
		}}}
		Thread thread = new Thread(new InitializeOldStates());
		ThreadManager.startThread(thread);
	}
	
	public static NbsStateActionHandler getInstance()
	{
		if(stateActionHandler == null){
			stateActionHandler = new NbsStateActionHandler();
		}
		return stateActionHandler;
	}
	
	public NbsStateActionHandler(){
		
	}
	
	private ProcessStateAction loadProcessStateAction(StateAction stateAction){
		
		ProcessStateAction processStateAction = null;
		
		try
		{
			String stateActionClass = stateAction.getActionClass();
			Class classInstatiation = Class.forName(stateActionClass);
			if(stateActionClass == null)
			{
				return null;
			}
			//class the initialization method is in
			processStateAction = (ProcessStateAction) classInstatiation.newInstance();
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		return processStateAction;
	}
	
	public void changeState(PatientState patientState,
			HashMap<String,Object> parameters){
		StateAction stateAction = patientState.getState().getAction();
		if (stateAction == null)
		{
			return;
		}

		ProcessStateAction processStateAction = loadProcessStateAction(stateAction);

		if (processStateAction != null)
		{
			processStateAction.changeState(patientState, parameters);
		}	
	}
	
	public  void processAction(StateAction stateAction, Patient patient,
			PatientState patientState,HashMap<String,Object> parameters)
	{
		if (stateAction == null)
		{
			return;
		}

		// lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);

		ProcessStateAction processStateAction = loadProcessStateAction(stateAction);

		if (processStateAction != null)
		{
			processStateAction.processAction(stateAction, patient,
					patientState, parameters);
		}	
	}

	public static void consume(Integer sessionId,FormInstance formInstance,Patient patient,
			HashMap<String,Object> parameters,List<FormField> fieldsToConsume,
			Integer locationTagId)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		NbsService nbsService = Context.getService(NbsService.class);
		Integer encounterId = atdService.getSession(sessionId).getEncounterId();
		String exportFilename = null;
		
		if(parameters != null){
			exportFilename = (String) parameters.get("filename");
		}
		try
		{
			InputStream input = new FileInputStream(exportFilename);
			
			startTime = System.currentTimeMillis();
			nbsService.consume(input,patient,encounterId,
					formInstance,sessionId,fieldsToConsume,locationTagId);
			startTime = System.currentTimeMillis();
			input.close();
		} catch (Exception e)
		{
			log.error("Error consuming nbs file: " + exportFilename);
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		
		// save specific observations
		//saveObs(encounterId, patient,locationTagId);
		log.info("NbsStateActionHandler.consume: time of saveObs: "+
			(System.currentTimeMillis()-startTime));
		startTime = System.currentTimeMillis();
		// remove the parsed xml from the xml datasource
		try
		{
			Integer purgeXMLDatasourceProperty = null;
			try
			{
				purgeXMLDatasourceProperty = Integer.parseInt(adminService
						.getGlobalProperty("atd.purgeXMLDatasource"));
			} catch (Exception e)
			{
			}
			LogicService logicService = Context.getLogicService();

			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService
					.getLogicDataSource("xml");
			if (purgeXMLDatasourceProperty != null
					&& purgeXMLDatasourceProperty == 1)
			{
				xmlDatasource.deleteParsedFile(formInstance);
			}
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
	
	private static  void saveObs(Integer encounterId,Patient patient,
			Integer locationTagId){
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		ObsService obsService = Context.getObsService();
		ATDService atdService = Context.getService(ATDService.class);
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<Concept>();
		HashMap<String,Object> parameters = new HashMap<String,Object>();
		Calculator calculator = new Calculator();
		parameters.put("encounterId", encounterId);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept("BMICentile");
		questions.add(concept);
		List<Obs> obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		
	}
	
	public static  void changeState(Patient patient, Integer sessionId,
			State currState,StateAction action,
			HashMap<String,Object> parameters,
			Integer locationTagId,Integer locationId)
	{
		ATDService atdService = Context.getService(ATDService.class);
		LocationService locationService = Context.getLocationService();
		
		List<ATDError> errors = null;
		// change to error state if fatal error exists for session
		//only look up errors for consume state, for now
		if (action!=null&&action.getActionName().equalsIgnoreCase("CONSUME FORM INSTANCE"))
		{
			errors = atdService.getATDErrorsByLevel(
					"Fatal", sessionId);
		}
		if (errors != null && errors.size() > 0)
		{
			//open an error state
			currState = atdService.getStateByName("ErrorState");
			atdService.addPatientState(patient,
					currState, sessionId,locationTagId,locationId);
		} else
		{
			Location defaultLocation = locationService.getLocation("Default Location");
			Integer defaultLocationId = 1;
			if (defaultLocation != null){
				defaultLocationId = defaultLocation.getLocationId();
			} 
			
			Program program = atdService.getProgram(locationTagId, defaultLocationId);
			StateManager.changeState(patient, sessionId, currState,program,
					parameters,locationTagId,defaultLocationId,NbsStateActionHandler.getInstance());
			}

	}
}
