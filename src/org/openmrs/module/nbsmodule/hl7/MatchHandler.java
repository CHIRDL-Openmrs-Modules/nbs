package org.openmrs.module.nbsmodule.hl7;

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
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbsmodule.util.MatchLogger;
import org.openmrs.module.nbsmodule.util.Util;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.PatientHandler;
import org.openmrs.module.sockethl7listener.Provider;
import org.openmrs.module.sockethl7listener.HL7SocketHandler.Parser;

/**
 * Evaluates differences between hl7 patient and matched patient to define the
 * most accurate results.
 * 
 * @author msheley
 * 
 */
public class MatchHandler extends org.openmrs.module.sockethl7listener.MatchHandler{
	
	private static final String PROVIDER_ID = "Provider ID";
	private static final String PROVIDER_NAME = "Provider Name";
	private static final String MATCH_INFO = "Other Matching Information";
	public static final String ATTRIBUTE_NEXT_OF_KIN = "Mother's Name";
	public static final String ATTRIBUTE_TELEPHONE = "Telephone Number";
	public static final String ATTRIBUTE_RACE = "Race";
	public static final String ATTRIBUTE_BIRTHPLACE = "Birthplace";
	private static final String PATIENT_IDENT_TYPE = "MRN";
	private static final Logger logger = Logger.getLogger("SocketHandlerLogger");
	private static final Logger matchLogger = Logger.getLogger("MatchResultsLogger");
	private static final Logger matchStatsLogger = Logger.getLogger("MatchStatsLogger");
	private static final Logger duplicateLogger = Logger.getLogger("DuplicateLogger");
	
	
	//protected PatientIdentifier bestIdentifier;
	//protected PersonName bestName;
	//protected String correctGender;
	//protected Date DOB;
	//protected Provider bestProv;
	//protected PersonAddress bestAddress;
	//protected PersonAttribute bestNKAttribute;
	//protected PersonAttribute bestTelephoneAttr;
	
	
	public MatchHandler()
	{
		super();
	}
	
	

	@Override
	protected Patient initializePatient(Patient hl7Patient, Patient matchedPatient)
	{
		Patient resolvedPatient = super.initializePatient(hl7Patient, matchedPatient);
		MatchLogger.logComparison(hl7Patient, matchedPatient);
		return resolvedPatient;

	}
	
	public Provider getBestProvider(Patient hl7Patient, Patient matchedPatient,
			Date encounterDate)
	{

		Provider bestProvider = new Provider();
		PersonAttribute matchedAttr = matchedPatient.getAttribute(MATCH_INFO);
		PersonAttribute hl7Attr = hl7Patient.getAttribute(MATCH_INFO);

		String hl7Provln = "";
		String hl7Provid = "";
		String hl7Provfn = "";
		String resProvln = "";
		String resProvfn = "";
		String resProvid = "";
		String hl7String = "";
		String matchString = "";

		if (matchedAttr != null)
		{
			matchString = matchedAttr.getValue();
			if (matchString != null)
			{
				resProvid = Util.getAttributeComponent(matchString, "drid");
				resProvfn = Util.getAttributeComponent(matchString, "drfn");
				resProvln = Util.getAttributeComponent(matchString, "drln");
			}
		}

		if (hl7Attr != null)
		{
			hl7String = hl7Attr.getValue();
			if (hl7String != null)
			{
				hl7Provid = Util.getAttributeComponent(hl7String, "drid");
				hl7Provfn = Util.getAttributeComponent(hl7String, "drfn");
				hl7Provln = Util.getAttributeComponent(hl7String, "drln");
			}
		}

		if (matchedAttr != null)
		{
			
			if (!matchedAttr.getDateCreated().after(encounterDate))
			{
				// hl7 message date is more recent
				if (hl7Provfn.equals(""))
				{
					hl7Provfn = resProvfn;
				}
				if (hl7Provln.equals(""))
				{
					hl7Provln = resProvln;
				}
				if (hl7Provid.equals(""))
				{
					hl7Provid = resProvid;
				}
				bestProvider.setLastName(hl7Provln);
				bestProvider.setFirstName(hl7Provfn);
				bestProvider.setId(hl7Provid);

			} else if (matchedAttr.getDateCreated().compareTo(encounterDate) > 0)
			{
				// existing non voided name is the best name
				// make sure values are not void
				// do not add hl7 attribute
				bestProvider.setFirstName(resProvfn);
				bestProvider.setLastName(resProvln);
				bestProvider.setId(resProvid);
			}
		}

		return bestProvider;

	}

	private String updateAttribute(Patient resolvedPatient,
			PatientIdentifier bestIdentifier, PersonName bestName,
			String correctGender, Date resolvedDOB, Provider prov,
			PersonAddress bestAddress, PersonAttribute resolvedPatientNK,
			PersonAttribute telephone)
	{

		String resultMatchingAttribute = "mrn:;fn:;ln:;race:;nkfn:;nkln:;db:;mb:;yb:;sex:;city:;st:;zip:;tel:;drid:;drfn:;drln:";
		if (resolvedPatient == null){
			return resultMatchingAttribute;
		}
		if (resolvedPatient.getAttribute(MATCH_INFO) != null)
		{
			resultMatchingAttribute = resolvedPatient.getAttribute(MATCH_INFO)
					.getValue();
		}

		String resolvedPatientMRN = "";
		if (bestIdentifier != null)
		{
			resolvedPatientMRN = bestIdentifier.getIdentifier();
			if (resolvedPatientMRN == null)
				resolvedPatientMRN = "";
		}

		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				"mrn:[^;]{0,};", "mrn:" + resolvedPatientMRN + ";");

		// fn
		String firstName = "";
		String bestFamilyName = "";
		if (bestName != null)
		{
			firstName = bestName.getGivenName();
			if (firstName == null)
				firstName = "";
			bestFamilyName = bestName.getFamilyName();
			if (bestFamilyName == null)
				bestFamilyName = "";
		}
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";fn:[^;]{0,};", ";fn:" + firstName + ";");
		// ln
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";ln:[^;]{0,};", ";ln:" + bestFamilyName + ";");

		// gender
		// If hl7 gender is null, use the matchedPatient results.
		if (correctGender == null)
			correctGender = "";
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";sex:[^;]{0,};", ";sex:" + correctGender + ";");

		// dob
		int db = 0, mb = 0, yb = 0;

		Calendar calMatch = Calendar.getInstance();
		if (resolvedDOB != null) {
			calMatch.setTime(resolvedDOB);
			db = calMatch.get(Calendar.DAY_OF_MONTH);
			mb = calMatch.get(Calendar.MONTH) + 1; // month is 0-based.
			yb = calMatch.get(Calendar.YEAR);
		} else {
			logger.warn("Invalid date time format for DOB discovered when "
					+ "constructing patient matching attribute string. Check DOB in PID segment of HL7.");
		}

		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";db:[^;]{0,};", ";db:" + db + ";");
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";mb:[^;]{0,};", ";mb:" + mb + ";");
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";yb:[^;]{0,};", ";yb:" + yb + ";");

		// Provider ID
		// PersonAttribute providerAttr
		String prid = prov.getId();
		if (prid == null)
			prid = "";
		String prfn = prov.getFirstName();
		if (prfn == null)
			prfn = "";
		String prln = prov.getLastName();
		if (prln == null)
			prln = "";

		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";drid:[^;]{0,};", ";drid:" + prov.getId() + ";");
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";drfn:[^;]{0,};", ";drfn:" + prov.getFirstName() + ";");
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";drln:[^;]{0,}", ";drln:" + prov.getLastName());

		// Address
		String city = "", st = "", zip = "";
		if (bestAddress != null)
		{
			city = bestAddress.getCityVillage();
			if (city == null)
				city = "";
			st = bestAddress.getStateProvince();
			if (st == null)
				st = "";
			zip = bestAddress.getPostalCode();
			if (zip == null)
				zip = "";
		}

		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				"city:[^;]{0,};", "city:" + city + ";");
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";st:[^;]{0,};", ";st:" + st + ";");
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";zip:[^;]{0,};", ";zip:" + zip + ";");

		String nkfn = "", nkln = "", nkname = "";
		if (resolvedPatientNK != null)
		{
			nkname = resolvedPatientNK.getValue();
		}
		if (nkname != null)
		{
			int index = nkname.indexOf("|");
			if (index > 0)
			{
				nkfn = nkname.substring(0, index);
				nkln = nkname.substring(index + 1);
			} else
			{
				nkln = nkname;
			}
		}
		if (nkfn == null)
			nkfn = "";
		if (nkln == null)
			nkln = "";

		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";nkfn:[^;]{0,};", ";nkfn:" + nkfn + ";");
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";nkln:[^;]{0,};", ";nkln:" + nkln + ";");

		String telString = "";
		if (telephone != null){
			telString = telephone.getValue();
			
		}
		resultMatchingAttribute = resultMatchingAttribute.replaceAll(
				";tel:[^;]{0,};", ";tel:" + telString + ";");

		return resultMatchingAttribute;

	}

	
	public boolean compare(String str1, String str2)
	{
		boolean match = false;
		match = (str1 == null && str2 == null)
				|| ((str1 != null && str2 != null) && str1
						.equalsIgnoreCase(str2.trim()));

		return match;
	}

	public static String setPatientMatchingAttribute(Provider provider,
			Patient p, Date encounterDate)
	{
		String mrn = "", attribute = "", drid = "", drfn = "", drln = "", nkfn = "", nkln = "", db = "", yb = "", mb = "", fn = "", ln = "", city = "", zip = "", st = "", race = "", tel = "", sex = "", nkname = "";

		if (provider != null)
		{
			drid = provider.getId();
			if (drid == null)
			{
				drid = "";
			}
			drfn = provider.getFirstName();
			if (drfn == null)
			{
				drfn = "";
			}
			drln = provider.getLastName();
			if (drln == null)
			{
				drln = "";
			}
		}

		fn = p.getGivenName();
		if (fn == null)
			fn = "";
		ln = p.getFamilyName();
		if (ln == null)
			ln = "";
		sex = p.getGender();
		if (sex == null)
			sex = "";

		if (p.getAttribute(ATTRIBUTE_RACE) != null)
		{
			race = p.getAttribute(ATTRIBUTE_RACE).getValue();
		}
		if (p.getAttribute(ATTRIBUTE_TELEPHONE) != null)
		{
			tel = p.getAttribute(ATTRIBUTE_TELEPHONE).getValue();
		}
		if (p.getPatientIdentifier() != null)
		{
			mrn = p.getPatientIdentifier().getIdentifier();
		}
		if (p.getAttribute(ATTRIBUTE_NEXT_OF_KIN) != null)
		{
			nkname = p.getAttribute(ATTRIBUTE_NEXT_OF_KIN).getValue();
		}
		if (nkname != null)
		{
			int index = nkname.indexOf("|");
			if (index > 0)
			{
				nkfn = nkname.substring(0, index);
				nkln = nkname.substring(index + 1);
			} else
			{
				nkln = nkname;
			}
		}

		if (p.getPersonAddress() != null)
		{
			city = p.getPersonAddress().getCityVillage();
			if (city == null)
				city = "";
			zip = p.getPersonAddress().getPostalCode();
			if (zip == null)
				zip = "";
			st = p.getPersonAddress().getStateProvince();
			if (st == null)
				st = "";
		}

		Calendar cal = Calendar.getInstance();
		Date bDate = p.getBirthdate();
		if (bDate == null){
			yb = "";
			mb = "";
			db = "";
		}
		else {
			cal.setTime(bDate);
			yb = Integer.toString(cal.get(Calendar.YEAR));
			mb = Integer.toString(cal.get(Calendar.MONTH) + 1);
			db = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		}
		
		
		attribute = "mrn:" + mrn + ";fn:" + fn + ";ln:" + ln + ";race:" + race
				+ ";nkfn:" + nkfn + ";nkln:" + nkln + ";db:" + db + ";mb:" + mb
				+ ";yb:" + yb + ";sex:" + sex + ";city:" + city + ";st:" + st
				+ ";zip:" + zip + ";tel:" + tel + ";drid:" + drid + ";drfn:"
				+ drfn + ";drln:" + drln;

		PersonService personService = Context.getPersonService();
		PersonAttributeType otherMatchingAttrType = personService
				.getPersonAttributeTypeByName(MATCH_INFO);

		if (otherMatchingAttrType == null)
		{
			createAttributeType(MATCH_INFO);
		}

		PersonAttribute matchingInfoAttribute = new PersonAttribute();
		matchingInfoAttribute.setAttributeType(otherMatchingAttrType);
		matchingInfoAttribute.setDateCreated(encounterDate);
		matchingInfoAttribute.setCreator(Context.getAuthenticatedUser());
		matchingInfoAttribute.setValue(attribute);
		p.addAttribute(matchingInfoAttribute);
		return attribute;
	}
	
	private static PersonAttributeType createAttributeType(String patString)
	{
		
		PersonAttributeType personAttr = new PersonAttributeType();
		try
		{
			personAttr.setName(patString);
			personAttr.setFormat("java.lang.String");
			personAttr.setDescription(patString);
			personAttr.setSearchable(true);
			PersonService personService = Context.getPersonService();
			personService.savePersonAttributeType(personAttr);
			Context.clearSession();
			Context.closeSession();
		} catch (RuntimeException e)
		{
			logger.error("Unable to create new attribute type:" + patString);
			logger.error(e.getStackTrace());
		}
		return personAttr;

	}
	
	public Patient setPatient(Patient hl7Patient, Patient matchedPatient,
			Date encounterDate)
	{
		
		PersonService personService = Context.getPersonService();
		
		Patient resolvedPatient = super.setPatient(hl7Patient, matchedPatient, encounterDate );
		
		PersonAttributeType matchInfoAttributeType = personService.getPersonAttributeTypeByName(MATCH_INFO);

		bestProv = getBestProvider(hl7Patient, resolvedPatient,
				encounterDate);
		
		String newAttributeValue = updateAttribute(resolvedPatient, bestIdentifier,
				bestName, correctGender, DOB, bestProv, bestAddress,
				bestNKAttribute, bestTelephoneAttr);
		
		PersonAttribute newPersonAttribute = new PersonAttribute(matchInfoAttributeType, newAttributeValue);
		
		//addAttribute method: If attribute with this type and value exist and are identical, nothing is done.
		//If attribute with this type has a different value, it voids that one and creates a new one.
		//If attribute of that type does not exist, it creates the attribute;
		resolvedPatient.addAttribute(newPersonAttribute);
//
	//	if(resolvedPatient.getAttribute(MATCH_INFO) != null){
	//		resolvedPatient.getAttribute(MATCH_INFO).setValue(attribute);
	//	}

		return resolvedPatient;
	}
	
	
}
