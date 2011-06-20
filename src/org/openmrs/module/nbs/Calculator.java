/**
 * 
 */
package org.openmrs.module.nbs;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * @author Tammy Dugan
 * 
 */
public class Calculator
{
	private static final String MALE_STRING = "M";
	private static final String FEMALE_STRING = "F";

	private static final int MALE_INT = 1;
	private static final int FEMALE_INT = 2;

	private static final String PERCENTILE_HC = "hc";
	private static final String PERCENTILE_LEN = "length";
	private static final String PERCENTILE_BMI = "bmi";
	private static final String PERCENTILE_WT = "weight";

	/**
	 * 
	 */
	public Calculator()
	{
	}


	/**
	 * Translates string gender into integer sex
	 * 
	 * @param gender string gender ("M" or "F")
	 * @return
	 */
	public int translateGender(String gender)
	{
		int sex = 0;

		if (gender == null)
		{
			return sex;
		}

		if (gender.equalsIgnoreCase(MALE_STRING))
		{
			sex = MALE_INT;
		}

		if (gender.equalsIgnoreCase(FEMALE_STRING))
		{
			sex = FEMALE_INT;
		}

		return sex;
	}
}
