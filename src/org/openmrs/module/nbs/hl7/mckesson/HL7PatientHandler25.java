/**
 * 
 */
package org.openmrs.module.nbs.hl7.mckesson;

import java.util.Set;
import java.util.TreeSet;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.Util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.datatype.FN;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.XPN;
import ca.uhn.hl7v2.model.v25.segment.PID;

/**
 * @author tmdugan
 * 
 */
public class HL7PatientHandler25 extends
		org.openmrs.module.sockethl7listener.HL7PatientHandler25
{

	//ignore mothers name for nbs
	@Override
	public String getMothersName(Message message)
	{
		// ignore NK segment for nbs messages
		return null;
	}

	//------get additional person attributes for nbs patients
	public String getSSN(Message message)
	{
		PID pid = this.getPID(message);
		ST ssnST = pid.getSSNNumberPatient();
		String ssn = null;
		if (ssnST != null)
		{
			try
			{
				ssn = ssnST.getValue();

				// Manual checkin is writing this incorrectly.. VA 11-17-05
				if (ssn != null && ssn.equals("--"))
				{
					ssn = "";
				}
			} catch (RuntimeException e1)
			{
				logger
						.debug("Warning: SSN information not available in PID segment.");
			}
		}
		return ssn;
	}

	public String getReligion(Message message)
	{
		PID pid = this.getPID(message);
		CE religionCE = pid.getReligion();
		String religion = null;
		if (religionCE != null)
		{
			try
			{
				ST religionST = religionCE.getIdentifier();

				if (religionST != null)
				{
					religion = religionST.getValue();
				}

			} catch (RuntimeException e1)
			{
				logger
						.debug("Warning: religion information not available in PID segment.");
			}
		}
		return religion;
	}

	public String getMaritalStatus(Message message)
	{
		PID pid = getPID(message);
		CE maritalCE = pid.getMaritalStatus();
		String marital = null;

		if (maritalCE != null)
		{
			try
			{
				ST maritalST = maritalCE.getIdentifier();

				if (maritalST != null)
				{
					marital = maritalST.getValue();
				}

			} catch (RuntimeException e1)
			{
				logger
						.debug("Warning: marital information not available in PID segment.");
			}

		}
		return marital;
	}

	public String getMothersMaidenName(Message message)
	{
		PID pid = getPID(message);
		String maiden = null;
		try
		{
			XPN maidenXPN = pid.getMotherSMaidenName(0);

			if (maidenXPN != null)
			{
				try
				{
					FN maidenFN = maidenXPN.getFamilyName();

					if (maidenFN != null)
					{
						ST maidenST = maidenFN.getSurname();
						if (maidenST != null)
						{
							maiden = maidenST.getValue();
						}
					}
				} catch (RuntimeException e1)
				{
					logger
							.debug("Warning: maiden information not available in PID segment.");
				}

			}
		} catch (HL7Exception e)
		{
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		return maiden;
	}

	//----MRN for nbs messages has an explicit check digit
	@Override
	protected String getMRN(CX ident)
	{
		String stIdent = null;
		
		ST id;
		if (ident != null && (id = ident.getIDNumber()) != null)
		{
			stIdent = id.getValue();
			ST checkDigitST = ident.getCheckDigit();

			if ( checkDigitST == null || checkDigitST.getValue() != null)
			{
				String checkDigit = checkDigitST.getValue();
				stIdent += "-" + checkDigit;
			}
			stIdent = Util.removeLeadingZeros(stIdent);
		}
		return stIdent;
	}
	
	@Override
	public Set<PatientIdentifier> getIdentifiers(Message message)
	{
		PID pid = getPID(message);
		CX[] identList = null;
		PatientService patientService = Context.getPatientService();
		Set<PatientIdentifier> identifiers = new TreeSet<org.openmrs.PatientIdentifier>();

		try
		{

			identList = pid.getPatientIdentifierList();
		} catch (RuntimeException e2)
		{
			// Unable to extract identifier from PID segment
			logger
					.error("Error extracting identifier from PID segment (MRN). ");
			// Still need to continue. Execute find match without the identifer
		}
		if (identList == null)
		{
			logger.warn(" No patient identifier available for this message.");
			// Still need to continue. Execute find match without the identifer
			return identifiers;
		}

		if (identList.length != 0)
		{
			// personAttrList ="mrn:";

			for (CX ident : identList)
			{
				// First set up the identifier type; We currently use MRN
				// Get the id number for the authorizing facility

				PatientIdentifier pi = new PatientIdentifier();
				String stIdent = getMRN(ident);
				String assignAuth = "";
				PatientIdentifierType pit = null;

				if (stIdent != null)
				{
					assignAuth = ident.getAssigningAuthority().getNamespaceID()
							.getValue();
					
					if (assignAuth == null || assignAuth.trim().equals("")){
						assignAuth = "OTHER";
					} else {
						
					}
					
					
					pit = patientService
						.getPatientIdentifierTypeByName("MRN_" + assignAuth);
					
					if (pit == null)
					{
						PatientIdentifierType idType = new PatientIdentifierType();
						idType.setDescription(assignAuth);
						idType.setName("MRN_" + assignAuth);
						pit = patientService.savePatientIdentifierType(idType);
							
					}
					pi.setIdentifierType(pit);
					pi.setIdentifier(stIdent);
					pi.setPreferred(true);

					identifiers.add(pi);

				} else
				{
					logger.error("No MRN in PID segement. ");
				}

			}
		}
		return identifiers;
	}
	
	
}
