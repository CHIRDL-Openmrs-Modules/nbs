/**
 * 
 */
package org.openmrs.module.nbs.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.PatientHandler;
import org.openmrs.module.sockethl7listener.Provider;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;

/**
 * @author tmdugan
 * 
 */
public class HL7SocketHandler extends
		org.openmrs.module.nbs.hl7.sms.HL7SocketHandler {
	
	/**
	 * @param parser
	 * @param patientHandler
	 */
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser,
			PatientHandler patientHandler, HL7ObsHandler hl7ObsHandler,
			HL7EncounterHandler hl7EncounterHandler,
			HL7PatientHandler hl7PatientHandler,
			ArrayList<HL7Filter> filters) {
		
		super(parser, patientHandler, hl7ObsHandler, hl7EncounterHandler,
				hl7PatientHandler, filters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmrs.module.sockethl7listener.HL7SocketHandler#processMessage(
	 * ca.uhn.hl7v2.model.Message)
	 */
	@Override
	public  Message processMessage(Message message,
			HashMap<String,Object> parameters) throws ApplicationException {
		AdministrationService adminService = Context.getAdministrationService();
		String incomingMessageString = null;

		// switch message version and type to values for default hl7 handlers
		if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A04) {
			try {
				ca.uhn.hl7v2.model.v22.message.ADT_A04 adt = (ca.uhn.hl7v2.model.v22.message.ADT_A04) message;
				adt.getMSH().getVersionID().setValue("2.5");
				adt.getMSH().getMessageType().getTriggerEvent().setValue("A01");
				incomingMessageString = this.parser.encode(message);
				message = this.parser.parse(incomingMessageString);
			} catch (Exception e) {
				ATDError error = new ATDError("Fatal", "Hl7 Parsing",
						"Error parsing the McKesson checkin hl7 "
								+ e.getMessage(),
						org.openmrs.module.chirdlutil.util.Util.getStackTrace(e),
						new Date(), null);
				ATDService atdService = Context.getService(ATDService.class);

				atdService.saveError(error);
				String mckessonParseErrorDirectory = IOUtil
						.formatDirectoryName(adminService
								.getGlobalProperty("nbs.mckessonParseErrorDirectory"));
				if (mckessonParseErrorDirectory != null) {
					String filename = "r" + Util.archiveStamp() + ".hl7";

					FileOutputStream outputFile = null;

					try {
						outputFile = new FileOutputStream(
								mckessonParseErrorDirectory + "/" + filename);
					} catch (FileNotFoundException e1) {
						this.log.error("Could not find file: "
								+ mckessonParseErrorDirectory + "/" + filename);
					}
					if (outputFile != null) {
						try {

							ByteArrayInputStream input = new ByteArrayInputStream(
									incomingMessageString.getBytes());
							IOUtil.bufferedReadWrite(input, outputFile);
							outputFile.flush();
							outputFile.close();
						} catch (Exception e1) {
							try {
								outputFile.flush();
								outputFile.close();
							} catch (Exception e2) {
							}
							this.log
									.error("There was an error writing the dump file");
							this.log.error(e1.getMessage());
							this.log.error(Util.getStackTrace(e));
						}
					}
				}
				return null;
			}
		}
		
		if (message instanceof ca.uhn.hl7v2.model.v22.message.ORU_R01) {
			try {
				ca.uhn.hl7v2.model.v22.message.ORU_R01 oru = (ca.uhn.hl7v2.model.v22.message.ORU_R01) message;
				oru.getMSH().getVersionID().setValue("2.5");
				incomingMessageString = this.parser.encode(message);
				message = this.parser.parse(incomingMessageString);
			} catch (Exception e) {
				ATDError error = new ATDError("Fatal", "Hl7 Parsing",
						"Error parsing the McKesson checkin hl7 "
								+ e.getMessage(),
						org.openmrs.module.chirdlutil.util.Util.getStackTrace(e),
						new Date(), null);
				ATDService atdService = Context.getService(ATDService.class);

				atdService.saveError(error);
				String mckessonParseErrorDirectory = IOUtil
						.formatDirectoryName(adminService
								.getGlobalProperty("nbs.mckessonParseErrorDirectory"));
				if (mckessonParseErrorDirectory != null) {
					String filename = "r" + Util.archiveStamp() + ".hl7";

					FileOutputStream outputFile = null;

					try {
						outputFile = new FileOutputStream(
								mckessonParseErrorDirectory + "/" + filename);
					} catch (FileNotFoundException e1) {
						this.log.error("Could not find file: "
								+ mckessonParseErrorDirectory + "/" + filename);
					}
					if (outputFile != null) {
						try {

							ByteArrayInputStream input = new ByteArrayInputStream(
									incomingMessageString.getBytes());
							IOUtil.bufferedReadWrite(input, outputFile);
							outputFile.flush();
							outputFile.close();
						} catch (Exception e1) {
							try {
								outputFile.flush();
								outputFile.close();
							} catch (Exception e2) {
							}
							this.log
									.error("There was an error writing the dump file");
							this.log.error(e1.getMessage());
							this.log.error(Util.getStackTrace(e));
						}
					}
				}
				return null;
			}
		}

		try {
			incomingMessageString = this.parser.encode(message);
			message.addNonstandardSegment("ZVX");
		} catch (HL7Exception e) {
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}

		if (this.hl7EncounterHandler instanceof org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25)
		{
			//String printerLocation = ((org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
			//		.getPrinterLocation(message,incomingMessageString);
			
			//if (printerLocation != null && printerLocation.equals("0"))
			//{
				// ignore this message because it is just kids getting shots
			//	return message;
			//}
		}
		return super.processMessage(message,parameters);
	}
	

	@Override
	public org.openmrs.Encounter processEncounter(String incomingMessageString,
			Patient p, Date encDate, org.openmrs.Encounter newEncounter,
			Provider provider, HashMap<String, Object> parameters)
	{
		ATDService atdService = Context.getService(ATDService.class);
		org.openmrs.Encounter encounter = super.processEncounter(
				incomingMessageString, p, encDate, newEncounter, provider,
				parameters);
		//store the encounter id with the session
		Integer encounterId = encounter.getEncounterId();
		getSession(parameters).setEncounterId(encounterId);
		atdService.updateSession(getSession(parameters));
		if (incomingMessageString == null)
		{
			return encounter;
		}

		LocationService locationService = Context.getLocationService();
		Location location = null;

		
		Date appointmentTime = null;
		
		String printerLocation = null;
		Message message;
		try
		{
			message = this.parser.parse(incomingMessageString);
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			encounter = encounterService.getEncounter(encounter
					.getEncounterId());
			if (this.hl7EncounterHandler instanceof org.openmrs.module.nbs.hl7.mckesson.HL7EncounterHandler25)
			{
				location = ((org.openmrs.module.nbs.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getLocation(message, incomingMessageString);

				appointmentTime = ((org.openmrs.module.nbs.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getAppointmentTime(message);

			}

		} catch (EncodingNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		encounter = encounterService.getEncounter(encounterId);
		Encounter nbsEncounter = (org.openmrs.module.nbs.hibernateBeans.Encounter) encounter;

		
		nbsEncounter.setScheduledTime(appointmentTime);
		nbsEncounter.setPrinterLocation(printerLocation);
		
		nbsEncounter.setLocation(location);
		nbsEncounter.setInsuranceSmsCode(null);

		encounterService.saveEncounter(nbsEncounter);
		
		return encounter;
	}
	
	private Session getSession(HashMap<String,Object> parameters)
	{
		Session session = (Session) parameters.get("session");
		if (session == null)
		{
			ATDService atdService = Context.getService(ATDService.class);
			session = atdService.addSession();
			parameters.put("session", session);
		}
		return session;
	}
	
	@Override
	public org.openmrs.Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			org.openmrs.Encounter newEncounter, HashMap<String,Object> parameters)
	{
		NbsService nbsService = Context.getService(NbsService.class);
			
		MatchHandler.setPatientMatchingAttribute(provider, patient, encounterDate);
		
		
		if (provider == null){
			log.error("Provider is required for an encounter." +
					"Provider name and id are null for " +   patient.getGivenName() + " " 
					+ patient.getFamilyName());
			return null;
		}
		String id = "";
		id = provider.getId();
		if (id == null || id.trim().equalsIgnoreCase("") ){
			id = nbsService.getNPI(provider.getFirstName(), provider.getLastName());
		}
		
		provider.setId(id);
		 
		Encounter enc =  (Encounter) super.checkin(provider, patient, encounterDate,  message,
				incomingMessageString, newEncounter,parameters);
		

		
		return enc;
	}
	
	
}
