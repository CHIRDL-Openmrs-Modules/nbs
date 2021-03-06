/**
 * 
 */
package org.openmrs.module.nbs.hl7;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.nbs.service.NbsService;

import ca.uhn.hl7v2.model.Message;

/**
 * @author tmdugan
 * 
 */
public class PrinterLocationHL7Filter implements HL7Filter
{
	protected final Log log = LogFactory.getLog(getClass());
	
	public boolean ignoreMessage(HL7EncounterHandler hl7EncounterHandler,
			Message message,String incomingMessageString)
	{
		NbsService nbsService = Context
				.getService(NbsService.class);
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);

		String printerLocation = null;
		String locationString = null;
		String locationTagAttributeName = "ActivePrinterLocation";

		if (hl7EncounterHandler instanceof org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25)
		{

			printerLocation = ((org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25) hl7EncounterHandler)
					.getPrinterLocation(message,incomingMessageString);
			locationString = ((org.openmrs.module.nbs.hl7.sms.HL7EncounterHandler25) hl7EncounterHandler)
					.getLocationString(message);
		}

		// get the location tag that matches the printer location
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationString);
		
		if(location == null){
			log.error("Location "+locationString+" does not exist. Cannot process this message.");
			return true;
		}
		
		Set<LocationTag> locationTags = location.getTags();
		
		//there are no location tags mapped for this location so
		//don't filter
		if(locationTags == null||locationTags.size()==0){
			return false;
		}
		
		LocationTag targetLocationTag = null;
		for (LocationTag locationTag : locationTags)
		{
			if (locationTag.getTag().equalsIgnoreCase(printerLocation))
			{
				targetLocationTag = locationTag;
				break;
			}
		}
		
		if (targetLocationTag != null)
		{
			LocationTagAttributeValue locationTagAttributeValue = chirdlUtilService.getLocationTagAttributeValue(targetLocationTag
					.getLocationTagId(), locationTagAttributeName,location.getLocationId());
			if (locationTagAttributeValue != null)
			{
				String activePrinterLocationString = locationTagAttributeValue
						.getValue();
				if (activePrinterLocationString.equalsIgnoreCase("true"))
				{
					return false; // don't ignore this location because it is
									// active
				}
			}
		}

		return true;
	}
}
