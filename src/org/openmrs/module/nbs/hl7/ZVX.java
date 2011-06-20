package org.openmrs.module.nbs.hl7;

import ca.uhn.hl7v2.parser.PipeParser;

public class ZVX
{

	private String printerLocation = null;

	public ZVX()
	{
	}

	/**
	 * Parses the zpv segment string into fields
	 * 
	 * @param zpvString
	 */
	public void parseFields(String zpvString)
	{

		String[] fields = PipeParser.split(zpvString, "|");
		if (fields != null)
		{
			int length = fields.length;
			this.printerLocation = (length >= 2) ? fields[1] : "";

		}
	}

	/**
	 * Gets the ZPV segment string from the incoming hl7 message string
	 * 
	 * @param mstring
	 * @return
	 */
	private String getZVXSegmentString(String mstring)
	{
		String ret = "";

		String[] segments = PipeParser.split(mstring, "\r");
		for (String s : segments)
		{
			if (s != null && s.startsWith("ZPV"))
			{
				ret = s;
			}
		}

		return ret;
	}

	/**
	 * Parses the ZPV segment string from the incoming message string, and
	 * inserts the information as ZPV segment into message object.
	 * 
	 * @param mstring
	 */
	public void loadZVXSegment(String mstring)
	{
		parseFields(getZVXSegmentString(mstring));
	}

	/**
	 * @return the printerLocation
	 */
	public String getPrinterLocation()
	{
		return this.printerLocation;
	}

	/**
	 * @param printerLocation the printerLocation to set
	 */
	public void setPrinterLocation(String printerLocation)
	{
		this.printerLocation = printerLocation;
	}
	
}
