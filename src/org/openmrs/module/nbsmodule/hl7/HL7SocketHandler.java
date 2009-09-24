package org.openmrs.module.nbsmodule.hl7;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.nbsmodule.util.MatchLogger;
import org.openmrs.module.nbsmodule.util.Util;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.PatientHandler;
import org.openmrs.module.sockethl7listener.Provider;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;

public class HL7SocketHandler extends
		org.openmrs.module.sockethl7listener.HL7SocketHandler {
	
	protected final Log log = LogFactory.getLog(getClass());
	private Integer sessionId = null;
	private Date timeCheckinHL7Received = null;
	
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser,
			PatientHandler patientHandler, HL7ObsHandler hl7ObsHandler,
			HL7EncounterHandler hl7EncounterHandler,
			HL7PatientHandler hl7PatientHandler)
	{
		super(parser, patientHandler, hl7ObsHandler, hl7EncounterHandler,
				hl7PatientHandler,null);
	}
	
	@Override
	public Message processMessage(Message message) throws ApplicationException
	{
		this.timeCheckinHL7Received = null;
		this.sessionId = null;
		String incomingMessageString = null;
		
		try
		{
			incomingMessageString = this.parser.encode(message);
			//archiveHL7Message(incomingMessageString);
			
			
		} catch (HL7Exception e)
		{
			logger.error(e.getMessage());
			logger.error(Util.getStackTrace(e));
		}

	
		return super.processMessage(message);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#createEncounter(ca.uhn.hl7v2.model.v25.segment.MSH,
	 *      ca.uhn.hl7v2.model.v25.segment.PID, org.openmrs.Location,
	 *      org.openmrs.Patient, java.util.Date,
	 *      org.openmrs.module.sockethl7listener.Provider)
	 */
	@Override
	protected org.openmrs.Encounter createEncounter(Patient resultPatient,
			org.openmrs.Encounter newEncounter, Provider provider)
	{
		
		org.openmrs.Encounter encounter = super.createEncounter(resultPatient,newEncounter,provider);
		
		//add state/session code here
		ATDService atdService = Context.getService(ATDService.class);
		Session session = atdService.getSession(getSessionId());
		session.setEncounterId(encounter.getEncounterId());
		atdService.updateSession(session);
		
		//State state = atdService.getStateByName("Clinic Registration");
		//PatientState patientState = atdService.addPatientState(resultPatient, state, getSessionId(), null);
		//patientState.setStartTime(encounter.getEncounterDatetime());
		//patientState.setEndTime(encounter.getEncounterDatetime());
		//atdService.updatePatientState(patientState);
		
		//if any modification of encounter information needed, put it here
		//Integer encounterId = encounter.getEncounterId();
		//EncounterService encounterService = Context
		//		.getService(EncounterService.class);
		//encounter = encounterService.getEncounter(encounterId);
		
		

		//encounterService.saveEncounter(encounter);

		return encounter;
	}
	
	@Override
	public Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			Encounter newEncounter)
	{
		
		MatchHandler.setPatientMatchingAttribute(provider, patient, encounterDate);
        
		return super.checkin(provider, patient, encounterDate,  message,
				incomingMessageString, newEncounter);
	}
	
	@Override
	public Patient findPatient(Patient hl7Patient, Date encounterDate)
	{
		PatientService patientService = Context.getPatientService();
		Patient resultPatient = new Patient();
		MatchLogger.logFindRequest(hl7Patient,encounterDate);
		
		try {
			Patient matchedPatient = patientService.findPatient(hl7Patient);
			if (matchedPatient == null) {
				resultPatient = createPatient(hl7Patient);
			}
			else {
				resultPatient = updatePatient(matchedPatient,
						hl7Patient,encounterDate);
				
			}
			
			if (resultPatient != null ){
				if (matchedPatient == null){
					MatchLogger.info("Patient " + resultPatient.getPatientId() +" created successfully");
				}
				else {
					MatchLogger.info("Patient " + resultPatient.getPatientId() + " updated successfully");
				}
			}else {
				MatchLogger.warn("Patient was not created or updated successfully");
			}
			
			//add state handling here
			ATDService atdService = Context.getService(ATDService.class);
			//State state = atdService.getStateByName("Process Checkin HL7");
			//PatientState patientState = atdService.addPatientState(resultPatient, state, getSessionId(), null);
			//patientState.setStartTime(getTimeCheckinHL7Received());
			//patientState.setEndTime(new java.util.Date());
			//atdService.updatePatientState(patientState);
			
			
		} catch (RuntimeException e) {
			logger.error("Exception creating or updating patient. " + e.getMessage() 
					+ " pid = " +  resultPatient.getPatientId());
		}
		return resultPatient;

	}

	
	private void archiveHL7Message(String message){
		
		Context.openSession();
		Integer test = 0;
		AdministrationService adminService = Context.getAdministrationService();
		
		
		
		String hl7ArchiveDirectory = adminService
		   .getGlobalProperty("nbsmodule.archiveHL7MessageDirectory");
		try {
			if (hl7ArchiveDirectory != null && !hl7ArchiveDirectory.equals("")){
				if(!(hl7ArchiveDirectory.endsWith("/")||hl7ArchiveDirectory.endsWith("\\")))
				{
					hl7ArchiveDirectory+="/";
				}
				
		        Calendar now = Calendar.getInstance();
		        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH_mm_ss_SSS");
		        String filename =  formatter.format(now.getTime()) + ".hl7";

				PrintWriter out
				   = new PrintWriter(new BufferedWriter(new FileWriter(hl7ArchiveDirectory + filename)));
				out.write(message);
				out.close();

			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			Context.closeSession();
		}
	}
	
	private Date getTimeCheckinHL7Received()
	{
		if(this.timeCheckinHL7Received == null){
			this.timeCheckinHL7Received = new java.util.Date();
		}
		return this.timeCheckinHL7Received;
	}

	private Integer getSessionId()
	{
		if (this.sessionId == null)
		{
			ATDService atdService = Context.getService(ATDService.class);
			Session session = atdService.addSession();
			this.sessionId = session.getSessionId();
		}
		return this.sessionId;
	}
	
	
	@Override
	protected Patient createPatient(Patient p){
		
		int pid = 0;
		Patient resultPatient = super.createPatient(p);
			
		if (resultPatient!= null){
				pid = resultPatient.getPatientId();
				MatchLogger.logCreatedPatient(p, pid);	
		}
		return resultPatient;
	}
	
	
	
	@Override
	protected Location setLocation(String locCode){
		 LocationService locationService = Context.getLocationService();
		 Location location = new Location ();
		 Location loc  = locationService.getLocation(locCode);
		 Date date = new Date();
		 
		 if (loc == null) {
			Location inpcloc = locationService.getLocation("INPC");
		    if (inpcloc == null) {
		    	//this is the default. Create if not present
		    	location.setName("INPC");
		    	location.setDateCreated(date);
		    	location.setCreator(Context.getAuthenticatedUser());
		    	locationService.saveLocation(location);
		    	loc = location;
		    } else {
		    	loc = inpcloc;
		    }
		 }
		 
		return loc;
	}
	
	@Override
	protected Patient updatePatient(Patient mp,
			Patient hl7Patient,Date encounterDate){
	
	//	Patient resultPatient = super.updatePatient(mp, hl7Patient, encounterDate);
	//	MatchLogger.logResults(resultPatient);
		

		PatientService patientService = Context.getPatientService();
		MatchHandler matchHandler = new MatchHandler();
		
		Patient resolvedPatient = matchHandler.setPatient(hl7Patient, mp, encounterDate);
		Patient testPatient = patientService.getPatient(resolvedPatient.getPatientId());
		Patient resultPatient = new Patient();
		if (resolvedPatient == null){
			return null;
		}
		resultPatient = patientService.savePatient(testPatient);
		MatchLogger.logResults(resolvedPatient);
		
		//try {
		//	resultPatient = patientService.savePatient(resolvedPatient);
		//} catch (APIException e) {
		//	logger.info("Save Patient api exception",e);
		//}
		
		return resultPatient;
	}
	
	

}
