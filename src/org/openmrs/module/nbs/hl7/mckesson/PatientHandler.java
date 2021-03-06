package org.openmrs.module.nbs.hl7.mckesson;

import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbs.util.Util;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;


import ca.uhn.hl7v2.model.Message;

public class PatientHandler extends org.openmrs.module.sockethl7listener.PatientHandler
{

	private static final String ATTRIBUTE_RELIGION = "Religion";
	private static final String ATTRIBUTE_MARITAL = "Civil Status";
	private static final String ATTRIBUTE_MAIDEN = "Mother's maiden name";

	public PatientHandler()
	{
		super();
	}

	public PatientHandler(Properties prop)
	{
		this();
	}

	//save literal hl7 race value instead of mapping it
	@Override
	protected void setRace(Message message, Patient hl7Patient,
			Date encounterDate, HL7PatientHandler hl7PatientHandler)
	{
		String race = hl7PatientHandler.getRace(message);
		addAttribute(hl7Patient, ATTRIBUTE_RACE, race, encounterDate);
	}

	//set additional patient attributes
	@Override
	public Patient setPatientFromHL7(Message message, Date encounterDate,
			Location sendingFacility, HL7PatientHandler hl7PatientHandler)
	{
		Patient hl7Patient = super.setPatientFromHL7(message, encounterDate,
				sendingFacility, hl7PatientHandler);

		if (hl7PatientHandler instanceof PatientHandler)
		{
			// patient ssn 
			String ssn = ((org.openmrs.module.nbs.hl7.mckesson.HL7PatientHandler25) hl7PatientHandler)
					.getSSN(message);
			if (Util.isValidSSN(ssn) )
			{
				PatientIdentifierType type = this.patientService.getPatientIdentifierTypeByName("SSN");
				PatientIdentifier pi = new PatientIdentifier(ssn,type,sendingFacility);
				pi.setDateCreated(encounterDate);
				pi.setCreator(Context.getAuthenticatedUser());
				pi.setPatient(hl7Patient);
				hl7Patient.addIdentifier(pi);
			} else {
				//Only create the person's attribute ssn if it was an invalid string
				//if it was invalid because it was null, don't create the attr.
				if (ssn!=null){
					PersonAttributeType ssnType = this.personService.getPersonAttributeTypeByName("SSN");
					PersonAttribute pa = new PersonAttribute(ssnType,ssn);
					pa.setCreator(Context.getAuthenticatedUser());
					pa.setDateCreated(encounterDate);
					hl7Patient.addAttribute(pa);
				}
			}
			
			// patient religion
			String religion = ((org.openmrs.module.nbs.hl7.mckesson.HL7PatientHandler25) hl7PatientHandler)
					.getReligion(message);
			if (religion != null)
			{
				addAttribute(hl7Patient, ATTRIBUTE_RELIGION, religion,
						encounterDate);
			}
			String marital = ((org.openmrs.module.nbs.hl7.mckesson.HL7PatientHandler25) hl7PatientHandler)
					.getMaritalStatus(message);
			if (marital != null)
			{
				addAttribute(hl7Patient, ATTRIBUTE_MARITAL, marital,
						encounterDate);
			}
			String maiden = ((org.openmrs.module.nbs.hl7.mckesson.HL7PatientHandler25) hl7PatientHandler)
					.getMothersMaidenName(message);
			if (maiden != null)
			{
				addAttribute(hl7Patient, ATTRIBUTE_MAIDEN, maiden,
						encounterDate);
			}
		}
		return hl7Patient;
	}

	//replace name of Inf with Baby
	@Override
	protected void setPatientName(Message message, Patient hl7Patient,
			Date encounterDate, HL7PatientHandler hl7PatientHandler)
	{
		super.setPatientName(message, hl7Patient, encounterDate,
				hl7PatientHandler);

		// check all patient names and replace 'Inf' with 'Baby'
		Set<PersonName> names = hl7Patient.getNames();

		for (PersonName name : names)
		{
			if (name.getGivenName().equals("Inf"))
			{
				name.setGivenName("Baby");
			}
		}
	}
}
