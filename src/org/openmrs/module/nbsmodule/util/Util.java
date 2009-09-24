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
	
	/**
	 * Converts specific measurements in English units to metric
	 * 
	 * @param measurement measurement to be converted
	 * @param measurementUnits units of the measurement
	 * @return double metric value of the measurement
	 */
	public static double convertUnitsToMetric(double measurement,
			String measurementUnits)
	{
		if (measurementUnits == null)
		{
			return measurement;
		}

		if (measurementUnits.equalsIgnoreCase(MEASUREMENT_IN))
		{
			measurement = measurement * 2.54; // convert inches to centimeters
		}

		if (measurementUnits.equalsIgnoreCase(MEASUREMENT_LB))
		{
			measurement = measurement * 0.45359237; // convert pounds to kilograms
		}
		return measurement; // truncate to one decimal
												  // place
	}
	
	/**
	 * Returns the numeric part of a string input as a string
	 * @param input alphanumeric input
	 * @return String all numeric
	 */
	public static String extractIntFromString(String input)
	{
		if(input == null)
		{
			return null;
		}
		String[] tokens = Pattern.compile("\\D").split(input);
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < tokens.length; i++)
			result.append(tokens[i]);
		return result.toString();
	}
	
	/**
	 * Adds period if necessary to a package prefix
	 * @param packagePrefix a java package prefix
	 * @return String formatted package prefix
	 */
	public static String formatPackagePrefix(String packagePrefix)
	{
		if (packagePrefix!=null&&!packagePrefix.endsWith("."))
		{
			packagePrefix += ".";
		}
		return packagePrefix;
	}
	
	public static String toProperCase(String str)
	{
		if(str == null || str.length()<1)
		{
			return str;
		}
		
		StringBuffer resultString = new StringBuffer();
		String delimiter = " ";
		
		StringTokenizer tokenizer = new StringTokenizer(str,delimiter,true);
		
		String currToken = null;
		
		while(tokenizer.hasMoreTokens())
		{
			currToken = tokenizer.nextToken();
			
			if(!currToken.equals(delimiter))
			{
				if(currToken.length()>0)
				{
					currToken = currToken.substring(0, 1).toUpperCase()
						+ currToken.substring(1).toLowerCase();
				}
			}
			
			resultString.append(currToken);
		}
		
		return resultString.toString();
	}
	
	
	public static Double round(Double value,int decimalPlaces)
	{
		if(decimalPlaces<0||value == null)
		{
			return value;
		}
		
		double intermVal = value*Math.pow(10, decimalPlaces);
		intermVal = Math.round(intermVal);
		return intermVal/(Math.pow(10, decimalPlaces));
	}
	
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
	

	
}
