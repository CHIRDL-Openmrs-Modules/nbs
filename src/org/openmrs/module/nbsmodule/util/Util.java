/**
 * 
 */
package org.openmrs.module.nbsmodule.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * This class contains several utility methods used by the dss module
 * and other modules that depend on it.
 * 
 * @author Tammy Dugan
 */
public class Util
{
	public static final String MEASUREMENT_LB = "lb";
	public static final String MEASUREMENT_IN = "in";
	
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";

	
	
	
	public static String getStackTrace(Exception x) {
		OutputStream buf = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(buf);
		x.printStackTrace(p);
		return buf.toString();
	}
	
	public static String archiveStamp()
	{
		Date currDate = new java.util.Date();
		String dateFormat = "yyyyMMdd-kkmmss-SSS";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		String formattedDate = formatter.format(currDate);
		return formattedDate;
	}
	
	public static Properties getProps(String filename)
	{
		try
		{

			Properties prop = new Properties();
			InputStream propInputStream = new FileInputStream(filename);
			prop.loadFromXML(propInputStream);
			return prop;

		} catch (FileNotFoundException e)
		{

		} catch (InvalidPropertiesFormatException e)
		{

		} catch (IOException e)
		{
			// TODO Auto-generated catch block

		}
		return null;
	}
	
	public static String getAttributeComponent(String matching_input,
			String attributeName)
	{
		String result = "";
		int index1 = matching_input.indexOf(attributeName);
		int index2 = matching_input.indexOf(":", index1);
		int index3 = matching_input.indexOf(";", index2);
		if (index3 == -1)
			result = matching_input.substring(index2 + 1);
		else
			result = matching_input.substring(index2 + 1, index3);

		return result;
	}
	
	
	public static Date removeTime(Date date) {
	    if(date == null) {
	      throw new IllegalArgumentException("The argument 'date' cannot be null.");
	    }

	    // Get an instance of the Calendar.
	    Calendar calendar = Calendar.getInstance();

	    // Make sure the calendar will not perform automatic correction.
	    calendar.setLenient(false);

	    // Set the time of the calendar to the given date.
	    calendar.setTime(date);

	    // Remove the hours, minutes, seconds and milliseconds.
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    // Return the date again.
	    return calendar.getTime();
	  }

	
}
