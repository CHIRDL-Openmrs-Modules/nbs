package org.openmrs.module.nbs.hl7.mckesson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.sockethl7listener.util.Util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.model.v231.datatype.NM;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.ED;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Constructs the hl7 message resulting from an abnormal newborn screen
 * @author msheley
 *
 */
/**
 * @author msheley
 * 
 */
public class HL7MessageConstructor extends org.openmrs.module.sockethl7listener.HL7MessageConstructor {

	String formInstance = null;
	private Log log = LogFactory.getLog(this.getClass());
	private Properties props;

	
	public HL7MessageConstructor() {

		super();

	}

	/**
	 * Set the properties from xml in hl7configFileLoaction
	 * 
	 * @param hl7configFileLocation
	 */
	public HL7MessageConstructor(String hl7configFileLocation, String formInst) {

		super(hl7configFileLocation);
		super.setProperties(hl7configFileLocation);
		
		props = Util.getProps(hl7configFileLocation);
		
	}
	
	
	
	
	
	public OBR AddSegmentOBR(Encounter enc, String univServiceId,
			String univServIdName, int orderRep)  {

		OBR obr = super.AddSegmentOBR(enc, univServiceId, univServIdName, orderRep);

		// Accession number from form instance
		String accessionNumber = formInstance;

		try {

			obr.getFillerOrderNumber().getEntityIdentifier().setValue(
					accessionNumber);
			obr.getOrderingProvider(0).getFamilyName().getSurname().setValue(
					"");
			obr.getOrderingProvider(0).getGivenName().setValue(
			"");
			obr.getOrderingProvider(0).getIDNumber().setValue("");
			obr.getResultCopiesTo(0).getFamilyName().getSurname().setValue(
			"");
			obr.getResultCopiesTo(0).getGivenName().setValue(
			"");
			obr.getResultCopiesTo(0).getIDNumber().setValue("");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return obr;

	}

	
	public PV1 AddSegmentPV1(Encounter enc, Integer providerId)  {

		PV1 pv1 = super.AddSegmentPV1(enc);
		
		
		PersonService personService = Context.getPersonService();
		
		try {
			
			
		
			Person providerPerson = personService.getPerson(providerId);
			String providerFirstName = null;
			String providerLastName = null;
			String npi = null;
			if (providerPerson != null) {
				PersonAttribute pa = providerPerson.getAttribute("Provider ID");
				if (pa != null && pa.getValue()!= null ){
					npi = pa.getValue();
				}
				
				providerFirstName = providerPerson.getGivenName();
				providerLastName = providerPerson.getFamilyName();
			}
			
			//hl7 will have destination practice or pcp in PV1-7, so we can
			//get the location for D4D from the provider.
			pv1.getAttendingDoctor(0).getFamilyName().getSurname().setValue(providerLastName);
			pv1.getAttendingDoctor(0).getGivenName().setValue(providerFirstName);
			pv1.getAttendingDoctor(0).getIDNumber().setValue(npi);

			
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return pv1;

	}

	public String getFormInstance() {
		return formInstance;
	}

	public void setFormInstance(String formInstance) {
		this.formInstance = formInstance;
	}
	
	public void setAssignAuthority(PatientIdentifier pi) {
		
		super.setAssignAuthority(pi);
	}
	
	@Override
	public PID AddSegmentPID(Patient pat) {
		
		
		try {
			PID pid = super.AddSegmentPID(pat);
			PersonService personService = Context.getPersonService();
			PatientIdentifier pi = pat.getPatientIdentifier();
			String assignAuthFromIdentifierType = getAssigningAuthorityFromIdentifierType(pi);
			pid.getPatientIdentifierList(0).getAssigningAuthority()
				.getNamespaceID().setValue(assignAuthFromIdentifierType);
			
		
			return pid;

		} catch (DataTypeException e) {
			log.error(e);
			return null;
		} catch (HL7Exception e) {
			log.error(e);
			return null;
		}

	}

	
}
