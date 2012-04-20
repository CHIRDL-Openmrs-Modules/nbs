/**
 * 
 */
package org.openmrs.module.nbs.hl7.sms;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.nbs.QueryKite;
import org.openmrs.module.nbs.QueryKiteException;
import org.openmrs.module.nbs.datasource.ObsNbsDatasource;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.hl7.mckesson.MatchHandler;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.PatientHandler;
import org.openmrs.module.sockethl7listener.Provider;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ADT_A04;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * @author tmdugan
 * 
 */
public class HL7SocketHandler extends org.openmrs.module.sockethl7listener.HL7SocketHandler
{
	protected final Log log = LogFactory.getLog(getClass());

	private static final String ATTRIBUTE_RELIGION = "Religion";
	private static final String ATTRIBUTE_MARITAL = "Civil Status";
	private static final String ATTRIBUTE_MAIDEN = "Mother's maiden name";

	/**
	 * @param parser
	 * @param patientHandler
	 */
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser,
			PatientHandler patientHandler, HL7ObsHandler hl7ObsHandler,
			HL7EncounterHandler hl7EncounterHandler,
			HL7PatientHandler hl7PatientHandler,ArrayList<HL7Filter> filters)
	{
		super(parser, patientHandler, hl7ObsHandler, hl7EncounterHandler,
				hl7PatientHandler, filters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#processMessage(ca.uhn.hl7v2.model.Message)
	 */
	@Override
	public  Message processMessage(Message message,HashMap<String,Object> parameters) throws ApplicationException
	{
		ATDService atdService = Context.getService(ATDService.class);

		parameters.put("processCheckinHL7Start",new java.util.Date());
		String incomingMessageString = null;
		AdministrationService adminService = Context.getAdministrationService();

		// change the message version so we can use default hl7 handlers
		if (message instanceof ca.uhn.hl7v2.model.v23.message.ADT_A01)
		{
			try
			{
				ca.uhn.hl7v2.model.v23.message.ADT_A01 adt = (ca.uhn.hl7v2.model.v23.message.ADT_A01) message;
				adt.getMSH().getVersionID().setValue("2.5");
				adt.getMSH().getMessageType().getTriggerEvent().setValue("A01");
				incomingMessageString = this.parser.encode(message);
				message = this.parser.parse(incomingMessageString);
			} catch (Exception e)
			{
				//Write the hl7 to a file in the error directory if it cannot be parsed
				ATDError error = new ATDError("Fatal", "Hl7 Parsing",
						"Error parsing the sms checkin hl7 " + e.getMessage(),
						org.openmrs.module.chirdlutil.util.Util.getStackTrace(e),
						new Date(), null);
				atdService.saveError(error);
				String smsParseErrorDirectory = IOUtil
						.formatDirectoryName(adminService
								.getGlobalProperty("nbs.smsParseErrorDirectory"));
				if (smsParseErrorDirectory != null)
				{
					String filename = "r" + org.openmrs.module.chirdlutil.util.Util.archiveStamp() + ".hl7";

					FileOutputStream outputFile = null;

					try
					{
						outputFile = new FileOutputStream(smsParseErrorDirectory
								+ "/" + filename);
					} catch (FileNotFoundException e1)
					{
						this.log.error("Could not find file: "
								+ smsParseErrorDirectory + "/" + filename);
					}
					if (outputFile != null)
					{
						try
						{

							ByteArrayInputStream input = new ByteArrayInputStream(
									incomingMessageString.getBytes());
							IOUtil.bufferedReadWrite(input, outputFile);
							outputFile.flush();
							outputFile.close();
						} catch (Exception e1)
						{
							try
							{
								outputFile.flush();
								outputFile.close();
							} catch (Exception e2)
							{
							}
							this.log
									.error("There was an error writing the dump file");
							this.log.error(e1.getMessage());
							this.log.error(org.openmrs.module.chirdlutil.util.Util
									.getStackTrace(e));
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

		try
		{
			incomingMessageString = this.parser.encode(message);
			message.addNonstandardSegment("ZVX");
		} catch (HL7Exception e)
		{
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}

		
		return super.processMessage(message,parameters);
	}

	//search for patient based on medical record number then
	//run alias query to see if any patient records to merge
	//@Override
	/*
	 * @should update new telephone number
	 * 
	 */
	/*public Patient findPatient(Patient hl7Patient, Date encounterDate,HashMap<String,Object> parameters)
	{
		Patient resultPatient = null;
		String mrn = null;

		try
		{
			Patient matchedPatient = null;
			Set<PatientIdentifier> identifiers = hl7Patient.getIdentifiers();
			for (PatientIdentifier identifier : identifiers){
				mrn = identifier.getIdentifier();
				 matchedPatient = findPatient(mrn);
				if (matchedPatient != null){
					break;
				}
			}
			if (matchedPatient == null)
			{
				resultPatient = createPatient(hl7Patient);
			} else
			{
				resultPatient = updatePatient(matchedPatient, hl7Patient,
						encounterDate);
			}
			
			


		    parameters.put("processCheckinHL7End",new java.util.Date());
			
		    parameters.put("queryKiteAliasStart",new java.util.Date());
			
			// merge alias medical record number patients with the matched
			// patient
			processAliasString(mrn, resultPatient);
			
			parameters.put("queryKiteAliasEnd",new java.util.Date());

		} catch (RuntimeException e)
		{
			logger.error("Exception during patient lookup. " + e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			
		}
		return resultPatient;

	}*/

	/*
	 * @should get location id from encounter
	 */
	private Integer getLocationTagId(Encounter encounter)
	{
		if (encounter != null)
		{
			//lookup location tag id that matches printer location
			if(encounter.getPrinterLocation()!= null){
				Location location = encounter.getLocation();
				Set<LocationTag> tags = location.getTags();
				
				if(tags != null){
					for(LocationTag tag:tags){
						if(tag.getTag().equalsIgnoreCase(encounter.getPrinterLocation())){
							return tag.getLocationTagId();
						}
					}
				}
			}
		}
		return null;
	}
	
	
	private Integer getLocationId(Encounter encounter)
	{
		if (encounter != null)
		{
			return encounter.getLocation().getLocationId();
		}
		return null;
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
	
	/*private Patient findPatient(String mrn)
	{
		PatientService patientService = Context.getPatientService();
		List<Patient> lookupPatients = patientService.getPatients(null,
				mrn, null, true);

		if (lookupPatients != null && lookupPatients.size() > 0)
		{
			return lookupPatients.iterator().next();
		}

		return null;
	}
	*/

	private void processAliasString(String mrn, Patient preferredPatient)
	{
		ATDService atdService = Context.getService(ATDService.class);
		PatientService patientService = Context.getPatientService();
		String aliasString = null;
		try {
			aliasString = QueryKite.aliasQuery(mrn);
		} catch (QueryKiteException e) {
			ATDError ce = e.getATDError();
			atdService.saveError(ce);
			
		}

		// alias query failed
		if (aliasString == null)
		{
			return;
		}
		String[] fields = PipeParser.split(aliasString, "|");
		if (fields != null)
		{
			int length = fields.length;

			if(length>=2){
				if(fields[1].equals("FAILED")){
					ATDError error = new ATDError("Error", "Query Kite Connection"
							, "Alias query returned FAILED for mrn: "+mrn
							, null, new Date(), null);
					atdService.saveError(error);
					return;
				}
				if(fields[1].equals("unknown_patient")){
					ATDError error = new ATDError("Warning", "Query Kite Connection"
							, "Alias query returned unknown_patient for mrn: "+mrn
							, null, new Date(), null);
					atdService.saveError(error);
					return;
				}
			}
			
			for (int i = 1; i < length; i++)
			{
				if (fields[i].contains("DONE"))
				{
					break;
				}
				
				//don't look up the preferred patient's mrn
				//so we don't merge a patient to themselves
				if(Util.removeLeadingZeros(fields[i]).equals(Util.removeLeadingZeros(mrn))||fields[i].equals("NONE")){
					continue;
				}

				List<Patient> lookupPatients = patientService.getPatients(
						null, Util.removeLeadingZeros(fields[i]), null);

				if (lookupPatients != null && lookupPatients.size() > 0)
				{
					Iterator<Patient> iter = lookupPatients.iterator();

					while (iter.hasNext())
					{
						Patient currPatient = iter.next();
						// only merge different patients
						if (!preferredPatient.getPatientId().equals(currPatient
								.getPatientId()))
						{
							patientService.mergePatients(preferredPatient,
									currPatient);
						}else{
							ATDError error = new ATDError("Error","General Error","Tried to merge patient: "+
									currPatient.getPatientId()+" with itself.",null,new Date(),null);
							atdService.saveError(error);
						}
					}
				}
			}
		}
	}
	
	@Override
	protected Patient updatePatient(Patient mp,
			Patient hl7Patient,Date encounterDate){
	
		Patient resultPatient = super.updatePatient(mp, hl7Patient, encounterDate);
		
		//ssn
		PatientService patientService = Context.getPatientService();
		PatientIdentifier newSSN = hl7Patient.getPatientIdentifier("SSN");
		
		if (newSSN != null && newSSN.getIdentifier() != null){
			
			PatientIdentifier currentSSN = resultPatient.getPatientIdentifier("SSN");
			
			if (currentSSN == null){
				resultPatient.addIdentifier(newSSN);
			}
			else {
				//Check if hl7 SSN and existing SSN identical
				if (!currentSSN.getIdentifier().equalsIgnoreCase(newSSN.getIdentifier())){
					resultPatient.getPatientIdentifier("SSN").setVoided(true);
					resultPatient.addIdentifier(newSSN);
				}
				
			}
		}

		//religion
		PersonAttribute newReligionAttr = hl7Patient.getAttribute(ATTRIBUTE_RELIGION);
		PersonAttribute currentReligionAttr = resultPatient.getAttribute(ATTRIBUTE_RELIGION);
		if (newReligionAttr != null){
			String newReligion = newReligionAttr.getValue();
			if (newReligion != null && currentReligionAttr != null
					&& ! currentReligionAttr.getValue()
					.equalsIgnoreCase(newReligion)){
				resultPatient.addAttribute(newReligionAttr);
			}
		}
		
		//marital
		PersonAttribute newMaritalStatAttr = hl7Patient.getAttribute(ATTRIBUTE_MARITAL);
		PersonAttribute currentMaritalStatAttr = resultPatient.getAttribute(ATTRIBUTE_MARITAL);
		if (newMaritalStatAttr != null){
			String newMaritalStat = newMaritalStatAttr.getValue();
			if (newMaritalStat != null && currentMaritalStatAttr != null
					&& ! currentMaritalStatAttr.getValue()
					.equalsIgnoreCase(newMaritalStat)){
				resultPatient.addAttribute(newMaritalStatAttr);
			}
		}
		
		
		//maiden name
		
		PersonAttribute newMaidenNameAttr = hl7Patient.getAttribute(ATTRIBUTE_MAIDEN);
		PersonAttribute currentMaidenNameAttr = resultPatient.getAttribute(ATTRIBUTE_MAIDEN);
		if (newMaidenNameAttr != null){
			String newMaidenName = newMaidenNameAttr.getValue();
			if (newMaidenName != null && currentMaidenNameAttr != null
					&& currentMaritalStatAttr!=null&&
					! currentMaritalStatAttr.getValue()
					.equalsIgnoreCase(newMaidenName)){
				resultPatient.addAttribute(newMaidenNameAttr);
			}
		}
		
		Patient updatedPatient = patientService.savePatient(resultPatient);
		
		return updatedPatient;
	}
	
	/*@Override
	public Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			Encounter newEncounter,HashMap<String,Object> parameters)
	{
		
		MatchHandler.setPatientMatchingAttribute(provider, patient, encounterDate);
        
		return super.checkin(provider, patient, encounterDate,  message,
				incomingMessageString, newEncounter, parameters);
	}
	@Override
	public org.openmrs.Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			org.openmrs.Encounter newEncounter, HashMap<String,Object> parameters){
		
		SocketHL7ListenerService hl7ListService = Context.getService(SocketHL7ListenerService.class);
		PatientService patientService = Context.getPatientService();
		User user = createUserForProvider(provider);
		if (user == null){
			logger.error("Could not create a user or find an existing user for provider: firstname=" 
					+ provider.getFirstName() + " lastname=" + provider.getLastName() + "id=" 
					+ provider.getId()  );
			return null;
		}
		provider.setUserId(user.getUserId());
		
		Patient resultPatient = findPatient(patient,encounterDate,parameters);	
		if (resultPatient == null || resultPatient.getPatientId() == null){
			hl7ListService.setHl7Message(0, 0, incomingMessageString, false, false, this.getPort());
			return null;
		}
		
		Patient pat = patientService.getPatient(resultPatient.getPatientId());
		
		Encounter encounter = (org.openmrs.module.nbs.hibernateBeans.Encounter) processEncounter(incomingMessageString,pat,
					encounterDate, newEncounter, provider,parameters);
		
		if (encounter == null) return null;
		
		if (message instanceof ORU_R01 || message instanceof ADT_A01)
		{
			CreateObservation(encounter, true,message,0,0,encounter.getLocation(),resultPatient);
			
		}
			
		// trigger rules for NBS module and ATD module
		ProcessedMessagesManager.messageProcessed(encounter);
		
		return encounter;
	}
*/
	@Override
	public org.openmrs.Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			org.openmrs.Encounter newEncounter, HashMap<String,Object> parameters)
	{
		Date processCheckinHL7Start = (Date) parameters.get("processCheckinHL7Start");
		if(processCheckinHL7Start == null){
			parameters.put("processCheckinHL7Start", new java.util.Date());
		}
		MatchHandler.setPatientMatchingAttribute(provider, patient, encounterDate);
		
		return super.checkin(provider, patient, encounterDate,  message,
				incomingMessageString, newEncounter,parameters);
	}
	
	

	/*@Override
	public Obs CreateObservation(org.openmrs.Encounter enc,
			boolean saveToDatabase, Message message, int orderRep, int obxRep,
			Location existingLoc, Patient resultPatient)
	{
		ConceptService cs = Context.getConceptService();
		LogicService logicService = Context.getLogicService();
		
		//new born screening does not use Obs datasource
		ObsNbsDatasource xmlDatasource = (ObsNbsDatasource) logicService
		.getLogicDataSource("RMRS");
		

		try {
			if (enc != null){
			
				if (resultPatient.getPatientIdentifier() != null){
					String  mrn = resultPatient.getPatientIdentifier().getIdentifier();
					String incomingMessageString = this.parser.encode(message);
					xmlDatasource.parseHL7ToObs(incomingMessageString,
						enc.getPatientId(),mrn);
						Obs obs = new Obs();
					
					
				}
			}
		} catch (HL7Exception e) {
			
			e.printStackTrace();
		}
		
		HashMap<String, Set<Obs>> regenObsMap = 
			xmlDatasource.getRegenObs(resultPatient.getPatientId());

		if (message instanceof ca.uhn.hl7v2.model.v25.message.ORU_R01){
			//Our source for messages fo diabetes patients are always in the IHIE cohort
			//Diabetes_cohort will always be true.
			String conceptName = "DIABETES_COHORT";
			Obs newObs = CreateObservation(enc, resultPatient, conceptName, 
					"TRUE");
			Set<Obs> obs = new HashSet<Obs>();
			obs.add(newObs);
			regenObsMap.put(conceptName,obs );
			
		
			//add to obs table too
			Util.saveObs(enc.getPatient(), cs.getConceptByName("DIABETES_COHORT"), 
				enc.getEncounterId(), "TRUE");
		}
		
		//Asthma patients are not always in the cohort.
		ConceptService conceptService = Context.getConceptService();
		Set<Obs> asthmaCohorts = regenObsMap.get("ASTHMA_COHORT");
		String asthma = "";
		if (asthmaCohorts != null && !asthmaCohorts.isEmpty()){
		
			for ( Obs asthmaCohort : asthmaCohorts){
				asthma = asthmaCohort.getValueText();
			}
			Concept asthmaConcept = conceptService.getConceptByName("ASTHMA_COHORT");
			if (asthmaConcept != null){
				Util.saveObs(enc.getPatient(), asthmaConcept, enc.getEncounterId(), 
				asthma);
				
			} else {
				log.error("Asthma cohort concept does not exist.");
			}
		}
		
		return null;
	}*/

	@Override
	public org.openmrs.Encounter processEncounter(String incomingMessageString,
			Patient p, Date encDate, org.openmrs.Encounter newEncounter,
			Provider provider,HashMap<String,Object> parameters)
	{
		ATDService atdService = Context.getService(ATDService.class);
		org.openmrs.Encounter encounter = super.processEncounter(
				incomingMessageString, p, encDate, newEncounter, provider,parameters);
		//store the encounter id with the session
		Integer encounterId = newEncounter.getEncounterId();
		getSession(parameters).setEncounterId(encounterId);
		atdService.updateSession(getSession(parameters));
		if(incomingMessageString == null){
			return encounter;
		}
		LocationService locationService = Context.getLocationService();

		String locationString = null;
		Date appointmentTime = null;
		String insuranceCode = null;
		String printerLocation = null;
		Message message;
		try
		{
			message = this.parser.parse(incomingMessageString);
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			encounter = encounterService.getEncounter(encounter
					.getEncounterId());
			Encounter nbsEncounter = (org.openmrs.module.nbs.hibernateBeans.Encounter) encounter;

			// load additional nbs encounter attributes
			if (this.hl7EncounterHandler instanceof org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25)
			{
				Location location = ((org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getLocation(message,incomingMessageString);

				appointmentTime = ((org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getAppointmentTime(message);

				insuranceCode = ((org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getInsuranceCode(message);

				if (insuranceCode != null)
				{
					nbsEncounter.setInsuranceSmsCode(insuranceCode);
				}
				if (appointmentTime != null)
				{
					nbsEncounter.setScheduledTime(appointmentTime);
				}
				if (printerLocation != null)
				{
					nbsEncounter.setPrinterLocation(printerLocation);
				}


				if (message instanceof ADT_A01 || message instanceof ADT_A04){
					List<EncounterType> encTypes = encounterService.getAllEncounterTypes(); 
					Iterator<EncounterType> it = encTypes.iterator();
					String name = null;
					EncounterType encounterType = null;
					for (EncounterType encType : encTypes){
						if (encType != null){
							name = encType.getName();
							if (name != null && name.equalsIgnoreCase("hl7Message_INPC")){
								encounterType = encType;
								break;
							}
						}
					  	
					}
					
					if (encounterType == null){
						encounterType = new EncounterType("hl7Message_INPC", "Hl7 trigger message from INPC");
					}
					encounterService.saveEncounterType(encounterType);
					nbsEncounter.setEncounterType(encounterType);
					
				}

					nbsEncounter.setLocation(location);
				
					encounterService.saveEncounter(nbsEncounter);
					
					
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

		return encounter;
	}
	
	public Obs CreateObservation(org.openmrs.Encounter enc,
			Patient resultPatient, String conceptName, String value)
	{
		Obs obs = new Obs();
		org.openmrs.Concept concept = new org.openmrs.Concept();
		concept.addName(new ConceptName(conceptName, null));
		obs.setConcept(concept);
		obs.setValueText(value);
		obs.setDateCreated(new Date());
		obs.setPerson(resultPatient);
		
		return obs;
	}
	
	private User createUserForProvider(Provider provider){

		User providerUser = null;
		List<Role> roles = new ArrayList <Role> ();
		User savedProviderUser = null;
		String password = null;
		boolean changed = false;
		PersonService personService = Context.getPersonService();
		UserService us = Context.getUserService();

		try {

			//set the username
			String username = "";
			String firstname = provider.getFirstName();
			String lastname = provider.getLastName();
			String userid = provider.getId();
			String fn = "";
			String ln = "";

			if(firstname == null){
				firstname = "";
			}
			if(lastname == null){
				lastname = "";
			}
			if(userid == null){
				userid = "";
			}
			if(firstname.contains(" ")){
				fn = firstname.replace(" ", "_");
			}
			else {
				fn = firstname;
			}
			if(lastname.contains(" ")){
				ln = lastname.replace(" ", "_");
			}
			else{
				ln = lastname;
			}


			username = fn + "." + ln + "." + userid;

			List<User> providers  = us.getAllUsers();
			for (User prov : providers){
				String provUserName = prov.getUsername();
				if (provUserName != null && provUserName.equalsIgnoreCase(username)){
					providerUser = prov;
					continue;
				}

			}

			if (providerUser == null){

				providerUser = new User();
				providerUser.setUsername(username);



				//get existing provider or create password if no provider exists.

				UUID uuid = UUID.randomUUID();
				password = uuid.toString();
				changed = true;


				Role r = us.getRole("Provider");
				providerUser.addRole(r); 



				if (provider != null){

					PersonName providerName = new PersonName(firstname, "", lastname);
					providerName.isPreferred();
					providerUser.addName(providerName);
					providerUser.setGender("U");
					providerUser.setVoided(false);

					//Store the provider's id in the provider's person attribute.
					PersonAttribute pattr = new PersonAttribute();
					if (personService.getPersonAttributeTypeByName("Provider ID") != null&&
							provider.getId()!=null && provider.getId().length()>0){
						PersonAttribute attr = providerUser.getAttribute(
								personService.getPersonAttributeTypeByName("Provider ID"));
						//only update if this is truly a new attribute value
						if (attr == null || !attr.getValue().equals(provider.getId())) {
							pattr.setAttributeType(personService.getPersonAttributeTypeByName("Provider ID"));
							pattr.setValue(provider.getId());
							pattr.setCreator(Context.getAuthenticatedUser());
							pattr.setDateCreated(new Date());
							providerUser.addAttribute(pattr);
						}
					}
				}

			}


			savedProviderUser = us.saveUser(providerUser, password);


		} catch (Exception e){
			log.error("Error while creating or updating a user for provider.", e);
		}

		return savedProviderUser;

	}
	
	
	

}
