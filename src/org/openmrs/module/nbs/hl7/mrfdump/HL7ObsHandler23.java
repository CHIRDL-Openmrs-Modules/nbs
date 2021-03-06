/**
 * 
 */
package org.openmrs.module.nbs.hl7.mrfdump;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbs.hl7.mckesson.PatientHandler;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v23.datatype.CE;
import ca.uhn.hl7v2.model.v23.datatype.NM;
import ca.uhn.hl7v2.model.v23.datatype.ST;
import ca.uhn.hl7v2.model.v23.datatype.TS;
import ca.uhn.hl7v2.model.v23.datatype.TX;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author tmdugan
 * 
 */
public class HL7ObsHandler23 implements HL7ObsHandler
{

	protected static final Log log = LogFactory.getLog(HL7ObsHandler23.class);

	public static MSH getMSH(ORU_R01 oru)
	{
		return oru.getMSH();
	}
	public static MSH getMSH(ADT_A01 adt)
	{
		return adt.getMSH();
	}

	public static PID getPID(ORU_R01 oru)
	{
		return oru.getRESPONSE().getPATIENT().getPID();
	}
	
	public static PID getPID(ADT_A01 adt)
	{
		return  adt.getPID();
	}

	private OBX getOBX(Message message, int orderRe, int obRep)
	{
		if (message instanceof ORU_R01)
		{
			return getOBX((ORU_R01) message, orderRe, obRep);
		}
		
		if (message instanceof ADT_A01)
		{
			return getOBX((ADT_A01) message);
		}
		return null;
	}

	private OBX getOBX(ORU_R01 oru, int orderRep, int obRep)
	{

		OBX obx = null;
		try
		{
			obx = oru.getRESPONSE().getORDER_OBSERVATION(orderRep)
					.getOBSERVATION(obRep).getOBX();
		} catch (HL7Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}

		return obx;
	}
	
	private OBX getOBX(ADT_A01 adt)
	{

		OBX obx = null;
		try
		{
			obx = adt.getOBX();
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}

		return obx;
	}

	public static OBR getOBR(ORU_R01 oru, int orderRep)
	{

		OBR obr = null;
		try
		{
			obr = oru.getRESPONSE().getORDER_OBSERVATION(orderRep).getOBR();
		} catch (HL7Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}

		return obr;
	}

	public static Date TranslateDate(TS ts)
	{
		int day = 0;
		int month = 0;
		int year = 0;
		int hour = 12;
		int minute = 0;
		int second = 0;

		String dateString = ts.getTimeOfAnEvent().getValue();

		if (dateString == null)
		{
			return null;
		}

		char[] dateChars = dateString.toCharArray();
		int length = dateChars.length;

		String yearString = "";
		String monthString = "";
		String dayString = "";
		String hourString = "";
		String minuteString = "";
		String secondString = "";

		for (int i = 0; i < length; i++)
		{
			if (i <= 3)
			{
				yearString += dateChars[i];
			}
			if (i >= 4 && i <= 5)
			{
				monthString += dateChars[i];
			}
			if (i >= 6 && i <= 7)
			{
				dayString += dateChars[i];
			}
			if (i >= 8 && i <= 9)
			{
				hourString += dateChars[i];
			}
			if (i >= 10 && i <= 11)
			{
				minuteString += dateChars[i];
			}
			if (i >= 12 && i <= 13)
			{
				secondString += dateChars[i];
			}
		}

		if (yearString.length() > 0)
		{
			try
			{
				year = Integer.parseInt(yearString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (monthString.length() > 0)
		{
			try
			{
				month = Integer.parseInt(monthString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (dayString.length() > 0)
		{
			try
			{
				day = Integer.parseInt(dayString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (hourString.length() > 0)
		{
			try
			{
				hour = Integer.parseInt(hourString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (minuteString.length() > 0)
		{
			try
			{
				minute = Integer.parseInt(minuteString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (secondString.length() > 0)
		{
			try
			{
				second = Integer.parseInt(secondString);
			} catch (NumberFormatException e)
			{
			}
		}

		Calendar cal = Calendar.getInstance();
		if(year == 0){
			return null;
		}
		cal.set(year, month - 1, day, hour, minute, second);

		return cal.getTime();

	}

	public String getSendingFacility(Message message)
	{
		if (message instanceof ORU_R01)
		{
			MSH msh = getMSH((ORU_R01) message);
			return msh.getSendingFacility().getNamespaceID().getValue();
		}
		if (message instanceof ADT_A01)
		{
			MSH msh = getMSH( (ADT_A01) message);
			return msh.getSendingFacility().getNamespaceID().getValue();
		}

		return null;
	}

	public Date getDateStarted(Message message)
	{
		if (!(message instanceof ORU_R01))
		{
			return null;
		}
		
		// OBR segment --Observation start time
		TS tsObsvStartDateTime = getOBR((ORU_R01) message, 0)
				.getObservationDateTime();
		if (tsObsvStartDateTime.getTimeOfAnEvent().getValue() == null)
		{
			tsObsvStartDateTime = getMSH((ORU_R01) message)
					.getDateTimeOfMessage();
		}
		Date sdt = TranslateDate(tsObsvStartDateTime);
		return sdt;
	}

	public Date getDateStopped(Message message)
	{
		if (!(message instanceof ORU_R01))
		{
			return null;
		}

		// OBR Segment Observation stop time - usually not present
		Date edt = null;
		TS tsObsvEndDateTime = getOBR((ORU_R01) message, 0)
				.getObservationEndDateTime();
		if (tsObsvEndDateTime.getTimeOfAnEvent().getValue() != null)
		{
			edt = TranslateDate(tsObsvEndDateTime);
		}
		return edt;
	}

	public String getObsValueType(Message message, int orderRep, int obxRep)
	{
		return getOBX(message, orderRep, obxRep).getValueType().toString();
	}

	public Date getObsDateTime(Message message, int orderRep, int obxRep)
	{
		TS tsObsDateTime = getOBX(message, orderRep, obxRep)
				.getDateTimeOfTheObservation();
		Date obsDateTime = TranslateDate(tsObsDateTime);
		return obsDateTime;
	}

	public String getConceptId(Message message, int orderRep, int obxRep)
	{
		CE ceObsIdentifier = getOBX(message, orderRep, obxRep)
				.getObservationIdentifier();
		return ceObsIdentifier.getIdentifier().toString();
	}

	public String getConceptName(Message message, int orderRep, int obxRep)
	{
		CE ceObsIdentifier = getOBX(message, orderRep, obxRep)
				.getObservationIdentifier();
		return ceObsIdentifier.getText().toString();
	}

	public String getTextResult(Message message, int orderRep, int obxRep)
	{
		Varies[] values = getOBX(message, orderRep, obxRep)
				.getObservationValue();
		Varies value = null;
		String dataString = null;

		if (values.length > 0)
		{
			value = values[0];

			if (value.getData() instanceof TX)
			{
				TX data = (TX) value.getData();
				dataString = data.getValue();
			}

			if (value.getData() instanceof ST)
			{
				ST data = (ST) value.getData();
				dataString = data.getValue();
			}

			return dataString;
		}
		return null;
	}

	public Date getDateResult(Message message, int orderRep, int obxRep)
	{
		Varies[] values = getOBX(message, orderRep, obxRep)
				.getObservationValue();
		Varies value = null;
		if (values.length > 0)
		{

			value = values[0];
			TS ts = (TS) value.getData();
			Date date = TranslateDate(ts);
			return date;
		}
		return null;
	}

	public Double getNumericResult(Message message, int orderRep, int obxRep)
	{
		double dVal = 0;
		Varies[] values = getOBX(message, orderRep, obxRep)
				.getObservationValue();
		Varies value = null;
		if (values.length > 0)
		{
			value = values[0];
			String nmvalue = ((NM) value.getData()).getValue();

			if (nmvalue != null)
			{
				try
				{
					dVal = Double.parseDouble(nmvalue);
				} catch (NumberFormatException ex)
				{
				}
			}
		}
		return dVal;
	}

	private Concept processCEType(Varies value, Logger logger,
			String pIdentifierString, String conceptQuestionId)
	{
		String conceptName = ((CE) value.getData()).getText().toString();
		String stConceptId = ((CE) value.getData()).getIdentifier().toString();
		Integer intObxValueID = null;

		try
		{
			intObxValueID = Integer.parseInt(stConceptId);
		} catch (NumberFormatException ne)
		{
			logger
					.warn("PARSE WARNING: Unable to parse integer from CE observation value id string - Invalid value in OBX  = "
							+ stConceptId
							+ " for patient MRN "
							+ pIdentifierString
							+ " \n concept question id : "
							+ conceptQuestionId
							+ " concept name: "
							+ conceptName + " Check coded value.");
		}

		// Success conversion to int
		if (intObxValueID != null)
		{
			try
			{
				Concept answer = new Concept();
				answer.setConceptId(intObxValueID);
				ConceptName name = new ConceptName();
				name.setName( conceptName);
				name.setLocale(new Locale("en_US"));
				answer.addName(name);

				return answer;
			} catch (RuntimeException e)
			{
				logger.error("createObs() failed. MRN: " + pIdentifierString
						+ ";Invalid OBX value: " + stConceptId
						+ ";concept question id: " + conceptQuestionId
						+ "; concept name: " + conceptName);
				logger.error(e.getMessage());
				logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}

		}
		return null;
	}

	public Concept getCodedResult(Message message, int orderRep, int obxRep,
			Logger logger, String pIdentifierString, String obsvID,
			String obsValueType, Logger conceptNotFoundLogger)
	{
		Varies[] values = getOBX(message, orderRep, obxRep)
				.getObservationValue();
		Varies value = null;
		Concept conceptResult = null;

		if (values.length > 0)
		{
			value = values[0];

			if (obsValueType.equals("CE"))
			{
				conceptResult = processCEType(value, logger, pIdentifierString,
						obsvID);
			}
		}

		return conceptResult;
	}
	
	public ArrayList<Obs> getObs(Message message) throws HL7Exception
	{
		PipeParser parser = new PipeParser();
		parser.setValidationContext(new NoValidation());
		PatientHandler patientHandler = new PatientHandler();
		HL7PatientHandler hl7PatientHandler = new HL7PatientHandler23();
		HL7EncounterHandler hl7EncounterHandler = new HL7EncounterHandler23();
		HL7SocketHandler hl7SocketHandler = new HL7SocketHandler(parser,
				patientHandler, this, hl7EncounterHandler,
				hl7PatientHandler,null);
		ArrayList<Obs> allObs = new ArrayList<Obs>();
		LocationService locationService = Context.getLocationService();
		String sendingFacility = getSendingFacility(message);
		Location existingLoc = locationService.getLocation(sendingFacility);
		
		// Set the patient
		String[] pIdentifierList = hl7PatientHandler.getPatientIdentifierList(message);
		Patient resultPatient = null;
		PatientService patientService = Context.getPatientService();
		
		if (pIdentifierList != null)
		{
			for (String currIdent : pIdentifierList)
			{
				for (Patient pa : patientService.getPatients(null, currIdent,
						null))
				{
					resultPatient = pa;
				}
			}

			if (message instanceof ADT_A01){
				ADT_A01 adt = (ADT_A01) message;
				OBX obx = adt.getOBX();
				Obs obs = hl7SocketHandler.CreateObservation(null,
						false, message, 0, 0, existingLoc,
						resultPatient);
				if (obs != null) allObs.add(obs);
			}
			
			if (message instanceof ORU_R01)
			{
				ORU_R01 oru = (ORU_R01) message;

				int numOrders = oru.getRESPONSE().getORDER_OBSERVATIONReps();

				for (int i = 0; i < numOrders; i++)
				{
					ORU_R01_ORDER_OBSERVATION order = oru.getRESPONSE()
							.getORDER_OBSERVATION(i);

					int numObs = order.getOBSERVATIONReps();
					for (int j = 0; j < numObs; j++)
					{
						Obs obs = hl7SocketHandler.CreateObservation(null,
								false, message, i, j, existingLoc,
								resultPatient);
						allObs.add(obs);
					}
				}
				
				hl7SocketHandler.CreateObservation(null, false, message, 0, 0, existingLoc, resultPatient);
			}
		}
		return allObs;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.sockethl7listener.HL7ObsHandler#getReps(ca.uhn.hl7v2.model.Message)
	 */
	public int getReps(Message message)
	{
		int reps = 0;
		if (message instanceof ORU_R01)
		{
			reps = ((ORU_R01) message).getRESPONSE()
					.getORDER_OBSERVATION().getOBSERVATIONReps();
		} else if ((message instanceof ADT_A01))
		{
			reps = ((ADT_A01) message).getOBXReps();
		}
		return reps;
	}
}
