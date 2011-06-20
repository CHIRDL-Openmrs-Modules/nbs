package org.openmrs.module.nbs.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbs.hl7.mckesson.MatchHandler;
import org.openmrs.module.sockethl7listener.HL7SocketHandler.Parser;

public class MatchLogger {
	
	private static final String PROVIDER_ID = "Provider ID";
	private static final String PROVIDER_NAME = "Provider Name";
	private static final String MATCH_INFO = "Other Matching Information";
	public static final String ATTRIBUTE_NEXT_OF_KIN = "Mother's Name";
	public static final String ATTRIBUTE_TELEPHONE = "Telephone Number";
	public static final String ATTRIBUTE_RACE = "Race";
	public static final String ATTRIBUTE_BIRTHPLACE = "Birthplace";
	private static final String PATIENT_IDENT_TYPE = "MRN";
	private static final Logger matchLogger = Logger.getLogger("MatchResultsLogger");
	private static final Logger matchStatsLogger = Logger.getLogger("MatchStatsLogger");
	private static final Logger duplicateLogger = Logger.getLogger("DuplicateLogger");
	private static final Logger logger = Logger.getLogger("SocketHandlerLogger");

	public static void logComparison(Patient hl7Patient, Patient matchedPatient)
	{
		 
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
		String mrn, fn, ln, gender, race, city, state, zip, db, mb, yb, tel, hl7Tel;
		String telUpdateDate;
		Calendar hl7cal = Calendar.getInstance();
		Date bDate = hl7Patient.getBirthdate();
		int month = 0;
		int year = 0;
		int day = 0;
		int matchMonth = 0;
		int matchYear = 0;
		int matchDay = 0;
		
		if (bDate != null){
			hl7cal.setTime(bDate);
			month = hl7cal.get(Calendar.MONTH) + 1;
			year = hl7cal.get(Calendar.YEAR);
			day = hl7cal.get(Calendar.DAY_OF_MONTH);
	
			Calendar matchCal = Calendar.getInstance();
			if (matchedPatient.getBirthdate()!= null){
				hl7cal.setTime(matchedPatient.getBirthdate());
				matchMonth = hl7cal.get(Calendar.MONTH) + 1;
				matchYear = hl7cal.get(Calendar.YEAR); 
				matchDay = hl7cal.get(Calendar.DAY_OF_MONTH);
			}
		} else {
			logger.warn("Invalid date time format for DOB discovered when "
					+ " logging comparison for patient matching. Check DOB in PID segment of HL7.");
		}
		
	
		if(matchedPatient.getAttribute(MATCH_INFO) == null ||
				hl7Patient.getAttribute(MATCH_INFO) == null){
			return;
		}
				
		
		// From the matched patient attribute that had been updated based on the
		// match.
		String matching_output = matchedPatient.getAttribute(MATCH_INFO)
				.getValue();
		String matchNKFN = Util.getAttributeComponent(matching_output, "nkfn");
		String matchNKLN = Util.getAttributeComponent(matching_output, "nkln");
		String matchDRID = Util.getAttributeComponent(matching_output, "drid");
		String matchDRFN = Util.getAttributeComponent(matching_output, "drfn");
		String matchDRLN = Util.getAttributeComponent(matching_output, "drln");
		String matchTel = Util.getAttributeComponent(matching_output, "tel");
		String matchRace = Util.getAttributeComponent(matching_output, "race");
	
		PersonAddress matchPA = matchedPatient.getPersonAddress();
		String matchCity, matchState, matchZip;
	
		if (matchPA != null)
		{
	
			if ((matchCity = matchPA.getCityVillage()) == null)
			{
				matchCity = "";
			}
			if ((matchState = matchPA.getStateProvince()) == null)
			{
				matchState = "";
			}
			if ((matchZip = matchPA.getPostalCode()) == null)
			{
				matchZip = "";
			}
	
		} else
		{
			matchCity = "";
			matchState = "";
			matchZip = "";
		}
	
		String nameCreateDate = sdf.format(matchedPatient.getPersonName()
				.getDateCreated());
		String addrCreateDate = sdf.format(matchedPatient.getPersonAddress()
				.getDateCreated());
		String identifierCreateDate = sdf.format(matchedPatient
				.getPatientIdentifier().getDateCreated());
	
		String matching_input = hl7Patient.getAttribute(MATCH_INFO).getValue();
		int index1 = matching_input.indexOf("fn:");
		int index2 = matching_input.indexOf(":", index1);
		int index3 = matching_input.indexOf(";", index2);
		String inputfnRaw = matching_input.substring(index2 + 1, index3);
		String inputfn = inputfnRaw;
	
		String inputIdent = Util.getAttributeComponent(matching_input, "mrn");
		String inputln =Util. getAttributeComponent(matching_input, "ln");
		String inputGender = Util.getAttributeComponent(matching_input, "sex");
		String inputCity = Util.getAttributeComponent(matching_input, "city");
		String inputState = Util.getAttributeComponent(matching_input, "st");
		String inputZip = Util.getAttributeComponent(matching_input, "zip");
		String inputNKFN = Util.getAttributeComponent(matching_input, "nkfn");
		String inputNKLN = Util.getAttributeComponent(matching_input, "nkln");
		String inputDRID = Util.getAttributeComponent(matching_input, "drid");
		String inputDRFN = Util.getAttributeComponent(matching_input, "drfn");
		String inputDRLN = Util.getAttributeComponent(matching_input, "drln");
		String inputDB = Util.getAttributeComponent(matching_input, "db");
		String inputMB = Util.getAttributeComponent(matching_input, "mb");
		String inputYB = Util.getAttributeComponent(matching_input, "yb");
		String inputRace = Util.getAttributeComponent(matching_input, "race");
		String inputTel = Util.getAttributeComponent(matching_input, "tel");
		PatientIdentifier matchIdent = matchedPatient.getPatientIdentifier();
		String matchfn = matchedPatient.getGivenName();
		String matchln = matchedPatient.getFamilyName();
		String matchGender = matchedPatient.getGender();
	
		try
		{

			String matchString = "MATCHED mrn:" + matchIdent + "|fn:" + matchfn
				+ "|ln:" + matchln + "|sex:" + matchGender + "|race:"
				+ matchRace + "|city:" + matchCity + "|st:" + matchState
				+ "|zip:" + matchZip + "|db:" + matchDay + "|mb:"
				+ matchMonth + "|yb:" + matchYear + "|tel:" + matchTel
				+ "|nkfn:" + matchNKFN + "|nkln:" + matchNKLN + "|drid:"
				+ matchDRID + "|drfn:" + matchDRFN + "|drln:" + matchDRLN
				+ "|openmrs_id:" + matchedPatient.getPatientId();
			info(matchString);
			
			mrn = String.format(
					"\r\n%1$-10s %2$-20s %3$-20s %4$-20s As of: %5$s \r\n",
					"MRN:", inputIdent, "MATCHED MRN:", matchedPatient
							.getPatientIdentifier(), identifierCreateDate);
			fn = String.format(
					"%1$-10s %2$-20s %3$-20s %4$-20s As of : %5$s \r\n", "FN:",
					inputfn, "MATCHED FN:", matchfn, nameCreateDate);
			ln = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n", "LN:",
					inputln, "MATCHED LN:", matchln);
			gender = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"GENDER:", inputGender, "MATCHED GENDER:", matchGender);
			race = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"RACE:", inputRace, "MATCHED RACE:", matchRace);
			city = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"CITY:", inputCity, "MATCHED CITY:", matchCity);
			state = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"STATE:", inputState, "MATCHED STATE:", matchState);
			zip = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n", "ZIP:",
					inputZip, "MATCHED ZIP:", matchZip);
			db = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n", "DB:",
					inputDB, "MATCHED DB:", matchDay);
			mb = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n", "MB:",
					inputMB, "MATCHED MB:", matchMonth);
			yb = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n", "YB:",
					inputYB, "MATCHED YB:", matchYear);
			tel = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n", "TEL:",
					inputTel, "MATCHED TEL:", matchTel);
			String nkfn = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"NKFN:", inputNKFN, "MATCHED NKFN:", matchNKFN);
			String nkln = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"NKLN:", inputNKLN, "MATCHED NKLN:", matchNKLN);
			String drid = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"DRID:", inputDRID, "MATCHED DRID:", matchDRID);
			String drfn = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"DRFN:", inputDRFN, "MATCHED DRFN:", matchDRFN);
			String drln = String.format("%1$-10s %2$-20s %3$-20s %4$-20s \r\n",
					"DRLN:", inputDRLN, "MATCHED DRLN:", matchDRLN);
			String openmrs_id = String.format("%1$-30s %2$-20s %3$-20s \r\n",
					" ", "OPENMRS_ID:", matchedPatient.getPatientId());
	
			matchLogger.info(mrn + fn + ln + gender + race + city + state + zip
					+ db + mb + yb + tel + nkfn + nkln + drid + drfn + drln
					+ openmrs_id);
			
	
		} catch (IllegalFormatException e)
		{
			logger.error("Exception when formating match handler logging.", e);
		}
	
	}

	public static void logCreatedPatient(Patient hl7Patient, int openmrs_id  ){
		
		
		String id, mrn ="" ,fn = "", ln = "", gender = "", race = "",
		city = "", state = "", zip = "", db = "", mb = "", 
		yb = "", tel= "" , hl7Tel = "", matchTel = "", addr1 = "";
		
		Calendar hl7cal = Calendar.getInstance();
		hl7cal.setTime(hl7Patient.getBirthdate());
		int month = hl7cal.get(Calendar.MONTH) + 1;
		int year = hl7cal.get(Calendar.YEAR);
		int day = hl7cal.get(Calendar.DAY_OF_MONTH);
		
		 if (hl7Patient == null) return;
		
		PersonAttribute  hl7TelAttr = hl7Patient.getAttribute(ATTRIBUTE_TELEPHONE);
		hl7Tel  = hl7TelAttr == null? "" : hl7TelAttr.getValue();
		
		PersonAttribute  raceAttr = hl7Patient.getAttribute("Race");
		race  = raceAttr == null? "" : raceAttr.getValue();
		
		
		PersonAttribute hl7NKNameAttr = hl7Patient.getAttribute(ATTRIBUTE_NEXT_OF_KIN);
		PersonName hl7NKName = Parser.parseNKName(hl7NKNameAttr);
	
		PersonAttribute matchAttr = hl7Patient.getAttribute(MATCH_INFO);
		String provln = "", provfn = "", provid = "";
		
		
		
		if (matchAttr != null){
			String matchAttrList = matchAttr.getValue();
			provfn = Parser.parseProvider(matchAttrList, "drfn");
			provln = Parser.parseProvider(matchAttrList, "drln");
			provid = Parser.parseProvider(matchAttrList, "drid");
		}
		
		PersonAddress hl7PA = hl7Patient.getPersonAddress();
		
		String hl7City = "", hl7State = "", hl7Zip = "", hl7Addr1 = "";
		if (hl7PA != null) {
			hl7Addr1 = hl7PA.getAddress1();
			if (hl7Addr1 == null || hl7Addr1.equals("")){
				hl7Addr1 = "";
			}
			hl7City = hl7PA.getCityVillage();
			if (hl7City == null || hl7City.equals("")){
				hl7City = "";
			}
		
			hl7State = hl7PA.getStateProvince();
			if (hl7State == null || hl7State.equals("")){
				hl7State = "";
			}
			hl7Zip = hl7PA.getPostalCode();
			
			if (hl7Zip == null || hl7Zip.equals("")){
				hl7Zip = "";
			}
		}
		
		
		try {
			id =   String.format("\r\n%1$-10s %2$-20s \r\n", "OPENMRS_ID:",openmrs_id);
			mrn =    String.format("%1$-10s %2$-20s \r\n", "MRN:", hl7Patient.getPatientIdentifier());
			fn =     String.format("%1$-10s %2$-20s \r\n", "FN:", hl7Patient.getGivenName());
			ln =     String.format("%1$-10s %2$-20s \r\n", "LN:", hl7Patient.getFamilyName());
			gender = String.format("%1$-10s %2$-20s \r\n", "GENDER:", hl7Patient.getGender());
			race = String.format("%1$-10s %2$-20s \r\n", "RACE:", race);
			//addr1 =    String.format("%1$-10s %2$-20s \r\n", "STREET", hl7Addr1);
			city =   String.format("%1$-10s %2$-20s \r\n", "CITY:", hl7City);
			state =  String.format("%1$-10s %2$-20s \r\n", "STATE:", hl7State);
			zip =    String.format("%1$-10s %2$-20s \r\n", "ZIP:", hl7Zip);
			db =    String.format("%1$-10s %2$-20s \r\n", "DB:", day);
			mb =    String.format("%1$-10s %2$-20s \r\n", "MB:", month);
			yb =    String.format("%1$-10s %2$-20s  \r\n", "YB:", year);
			tel =    String.format("%1$-10s %2$-20s \r\n", "TEL:", hl7Tel);
		
			String hl7NKGivenName = "";
			String hl7NKFamilyName = "";
			
			if(hl7NKName != null)
			{
				hl7NKGivenName = hl7NKName.getGivenName();
				hl7NKFamilyName = hl7NKName.getFamilyName();
			}
			String nkfn = String.format("%1$-10s %2$-20s \r\n", "NKFN:",hl7NKGivenName );
			String nkln = String.format("%1$-10s %2$-20s \r\n", "NKLN:",hl7NKFamilyName );
			String drfn = String.format("%1$-10s %2$-20s \r\n", "DRFN:", provfn);
			String drln = String.format("%1$-10s %2$-20s \r\n", "DRLN:", provln);
			
			
			matchLogger.info("PATIENT CREATED: \r\n" + id +  mrn + fn + ln + gender + city + state + zip + db + mb + yb + tel + nkfn + nkln + drfn + drln);
		} catch (IllegalFormatException e) {
			logger.error("Caught Exception when formating match handler logging.", e);
		}
		
		
	}

	public static void logFindRequest(Patient hl7Patient,Date encounterDate){
	
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try {
			
			
			Calendar hl7cal = Calendar.getInstance();
			hl7cal.setTime(hl7Patient.getBirthdate());
			int month = hl7cal.get(Calendar.MONTH) + 1;
			int year = hl7cal.get(Calendar.YEAR);
			int day = hl7cal.get(Calendar.DAY_OF_MONTH);
			
		} catch (Exception e){
				logger.error("Exception calculating birthdate in MatchHandler logging." +
						" Possibly due to invalid time format in hl7.");
		}
			
			if(hl7Patient.getAttribute(MATCH_INFO) == null){
				return;
			}
			String matching_input = hl7Patient.getAttribute(MATCH_INFO).getValue();
			String inputfn = Util.getAttributeComponent(matching_input, "fn");
			String inputIdent = Util.getAttributeComponent(matching_input, "mrn");
			String inputln = Util.getAttributeComponent(matching_input, "ln");
			String inputGender = Util.getAttributeComponent(matching_input, "sex");
			String inputCity = Util.getAttributeComponent(matching_input, "city");
			String inputState = Util.getAttributeComponent(matching_input, "st");
			String inputZip = Util.getAttributeComponent(matching_input, "zip");
			String inputNKFN = Util.getAttributeComponent(matching_input, "nkfn");
			String inputNKLN = Util.getAttributeComponent(matching_input, "nkln");
			String inputDRID = Util.getAttributeComponent(matching_input, "drid");
			String inputDRFN = Util.getAttributeComponent(matching_input, "drfn");
			String inputDRLN = Util.getAttributeComponent(matching_input, "drln");
			String inputDB = Util.getAttributeComponent(matching_input, "db");
			String inputMB = Util.getAttributeComponent(matching_input, "mb");
			String inputYB = Util.getAttributeComponent(matching_input, "yb");
			String inputRace = Util.getAttributeComponent(matching_input, "race");
			String inputTel = Util.getAttributeComponent(matching_input, "tel");
			
			
			
		try{
			
			matchLogger.info("HL7 message arrived.\r\nPATIENT NAME: " + hl7Patient.getPersonName().getFamilyName() 
					+ "  " + hl7Patient.getGivenName() + "  Encounter date/time: " +  sdf.format(encounterDate) );
			matchStatsLogger.info("\r\nPATIENT NAME: " + hl7Patient.getPersonName().getFamilyName() 
					+ "  " + hl7Patient.getGivenName() + "  Encounter date/time: " +  sdf.format(encounterDate) );
			
			
			String logString = "FIND    mrn:" + inputIdent + "|fn:" + inputfn + "|ln:" + inputln 
			+ "|sex:" + inputGender + "|race:" + inputRace + "|city:" + inputCity + "|st:" + inputState
			+ "|zip:" + inputZip + "|db:"+ inputDB + "|mb:" + inputMB + "|yb:" + inputYB
			+ "|tel:" + inputTel + "|nkfn:" + inputNKFN + "|nkln:" + inputNKLN + "|drid:" + inputDRID
			+ "|drfn:" + inputDRFN + "|drln:" + inputDRLN  ;
			//For stats logging
			matchLogger.info(logString);
			matchStatsLogger.info(logString);
		
		} catch (IllegalFormatException e) {
			logger.error("Exception when formating match handler logging.", e);
		}
		
		
	}

	private static void logObsFromEncounter(Encounter encounter, Date encDate,
			String incomingMessageString){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
		ObsService os = Context.getObsService();
		encounter.getPatient().getFamilyName();
		encounter.getPatient().getGivenName();
		Set<Obs> obsSet = new TreeSet<Obs>();
		obsSet = os.getObservations(encounter);
		String message = "\r\n\r\nDuplcate Encounter: Patient id = " + encounter.getPatient().getPatientId() 
	    + " " + encounter.getPatient().getGivenName() +  " " + encounter.getPatient().getFamilyName() 
		+ "; Encounter date =" + formatter.format(encDate) 
		+ "\r\nNew HL7:\r\n" + incomingMessageString + "\r\nOriginal encounter: Date: " 
		+ formatter.format(encounter.getEncounterDatetime()) + "\r\n";
	    duplicateLogger.info(message);
	    
	
	
	    
	    
		//for (Obs obs : os.getObservations(encounter)){
	    for (Obs obs : obsSet){
			String dateTime = "";
			String obsValueCoded = "--";
			String obsvValueCodedText = "";
			String obsvValueBool = "";
			String obsvNumeric = "";
			Double getValueNum = null;
			Boolean bValue = null;
			Concept concept = null;
			String obsvValueString = obs.getValueAsString(Locale.ENGLISH);
			String obsName = obs.getConcept().getName().toString();
			String obsValueText = obs.getValueText();
			if ((concept = obs.getValueCoded()) != null){
				obsValueCoded = concept.getConceptId().toString();
				obsvValueCodedText = concept.getName().getName();
			}
			Date obsvValueDateTime = obs.getValueDatetime();
			if (obsvValueDateTime != null){
				dateTime = formatter.format(obsvValueDateTime);
				obsvValueString = dateTime;
			}
			if (( getValueNum = obs.getValueNumeric())!= null ){
				obsvNumeric= obs.getValueNumeric().toString();
			}
			if ((bValue = obs.getValueAsBoolean())!= null){
				obsvValueBool = bValue.toString();
			}
			
			
			String str =   String.format("%1$-50s %2$-10s %3$-20s   ",
					 obsName, obsValueCoded, obsvValueString); 
			
			duplicateLogger.info(  str);
			
		}
	    
	    
		
	}

	public static void logResults(Patient resolvedPatient)
	{
		String mrn, fn, ln, gender, race, city, state, zip, db, mb, yb, tel, addr1;
		Calendar cal = Calendar.getInstance();
		if (resolvedPatient == null){
			logger.warn("Exception during patient update. Patient was not updated and an "+
					"encounter was not created for that patient.");
			return;
					
			
		}
		Date dob = resolvedPatient.getBirthdate(); 
		int month = 0;
		int year = 0;
		int day = 0;
		if (dob != null){
			cal.setTime(resolvedPatient.getBirthdate());
			month = cal.get(Calendar.MONTH) + 1;
			year = cal.get(Calendar.YEAR);
			day = cal.get(Calendar.DAY_OF_MONTH);
		} else {
			logger.warn("DOB has invalid format "
					+ "when logging matching results. Check hl7 dob format in PID segment");
		}
	
		PersonAddress PA = resolvedPatient.getPersonAddress();
		for (PersonAddress pa : resolvedPatient.getAddresses())
		{
			if (pa.getPreferred())
			{
				PA = pa;
				break;
			}
		}
	
		String resCity = "", resState = "", resZip = "", resAddr1 = "";
		if (PA != null)
		{
			resAddr1 = PA.getAddress1();
			if (resAddr1 == null)
			{
				resAddr1 = "";
			}
			resCity = PA.getCityVillage();
			if (resCity == null)
			{
				resCity = "";
			}
			resState = PA.getStateProvince();
			if (resState == null)
			{
				resState = "";
			}
			resZip = PA.getPostalCode();
			if (resZip == null)
			{
				resZip = "";
			}
	
		}
	
		PersonAttribute telAttr = resolvedPatient
				.getAttribute(ATTRIBUTE_TELEPHONE);
		String resTel = telAttr == null ? "" : telAttr.getValue();
	
		PersonAttribute raceAttr = resolvedPatient.getAttribute(ATTRIBUTE_RACE);
		String resRace = raceAttr == null ? "" : raceAttr.getValue();
	
		String fName = resolvedPatient.getGivenName();
		String lName = resolvedPatient.getFamilyName();
		/*
		 * for (PersonName pn : resolvedPatient.getNames()){ if
		 * (pn.getPreferred()) { fName = pn.getGivenName(); lName =
		 * pn.getFamilyName();
		 *  } }
		 */
	
		PersonAttribute resolvedNKNameAttr = resolvedPatient
				.getAttribute(ATTRIBUTE_NEXT_OF_KIN);
		PersonName resolvedNKName = parseNKName(resolvedNKNameAttr);
	
		String resdrid = "", resdrfn = "", resdrln = "";
		if (resolvedPatient.getAttribute(MATCH_INFO) != null)
		{
			String resultString = resolvedPatient.getAttribute(MATCH_INFO)
					.getValue();
			if (resultString != null)
			{
				resdrid = Util.getAttributeComponent(resultString, "drid");
				resdrfn = Util.getAttributeComponent(resultString, "drfn");
				resdrln = Util.getAttributeComponent(resultString, "drln");
			}
		}
		try
		{
			mrn = String.format(
					"Resolved to: \r\n          %1$-10s %2$-20s \r\n", "MRN:",
					resolvedPatient.getPatientIdentifier());
			fn = String.format("          %1$-10s %2$-20s  \r\n", "FN:", fName);
			ln = String.format("          %1$-10s %2$-20s  \r\n", "LN:", lName);
			gender = String.format("          %1$-10s %2$-20s \r\n", "GENDER:",
					resolvedPatient.getGender());
			race = String.format("          %1$-10s %2$-20s \r\n", "RACE:",
					resRace);
			city = String.format("          %1$-10s %2$-20s \r\n", "CITY:",
					resCity);
			state = String.format("          %1$-10s %2$-20s  \r\n", "STATE:",
					resState);
			zip = String.format("          %1$-10s %2$-20s \r\n", "ZIP:",
					resZip);
			db = String.format("          %1$-10s %2$-20s \r\n", "DB:", day);
			mb = String.format("          %1$-10s %2$-20s \r\n", "MB:", month);
			yb = String.format("          %1$-10s %2$-20s\r\n", "YB:", year);
			tel = String.format("          %1$-10s %2$-20s \r\n", "TEL:",
					resTel);
			addr1 = String.format("          %1$-10s %2$-20s \r\n", "STREET",
					resAddr1);
			String nkfn = String.format("          %1$-10s %2$-20s \r\n",
					"NKFN:", resolvedNKName.getGivenName());
			String nkln = String.format("          %1$-10s %2$-20s \r\n",
					"NKLN:", resolvedNKName.getFamilyName());
			String drid = String.format("          %1$-10s %2$-20s \r\n",
					"DRID:", resdrid);
			String drfn = String.format("          %1$-10s %2$-20s \r\n",
					"DRFN:", resdrfn);
			String drln = String.format("          %1$-10s %2$-20s  \r\n",
					"DRLN:", resdrln);
			String openmrs_id = String.format("          %1$-10s %2$-20s \r\n",
					"OPENMRS_ID:", resolvedPatient.getPatientId());
	
			matchLogger.info("Result attribute string: \r\n"
					+ resolvedPatient.getAttribute(MATCH_INFO));
			matchLogger.info(mrn + fn + ln + gender + city + state + zip + db
					+ mb + yb + tel + nkfn + nkln + drid + drfn + drln
					+ openmrs_id);
		} catch (IllegalFormatException e)
		{
			logger.error("Exception when formating match handler logging.", e);
		}
	
	}
	
	public static PersonName parseNKName(PersonAttribute NKNameAttr)
	{
		String firstname = "";
		String lastname = "";
		if (NKNameAttr != null)
		{
			String NKnameValue = NKNameAttr.getValue();
			int index1 = NKnameValue.indexOf("|");
			if (index1 != -1)
			{
				firstname = NKnameValue.substring(0, index1);
				lastname = NKnameValue.substring(index1 + 1);
			} else
			{
				firstname = NKnameValue;
			}

		}

		PersonName NKName = new PersonName();
		NKName.setFamilyName(lastname);
		NKName.setGivenName(firstname);

		return NKName;
	}
	
	public static void info (String message){
		matchLogger.info(message);
		logger.info(message);
		matchStatsLogger.info(message);
	}
	
	public static void warn (String message){
		matchLogger.warn(message);
		logger.warn(message);
		matchStatsLogger.warn(message);
	}
	
	public static void error (String message){
		logger.warn(message);
	}

}
