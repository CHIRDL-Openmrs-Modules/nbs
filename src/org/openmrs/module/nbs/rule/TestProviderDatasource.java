/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.nbs.rule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class TestProviderDatasource implements Rule
{

	private LogicService logicService = Context.getLogicService();

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		LogicDataSource datasource = this.logicService
				.getLogicDataSource("provider");

		Result result = context.read(patient, datasource, new LogicCriteria(
				"USERNAME"));
		System.out.println("Result is: " + result.toString());

		result = context.read(patient, datasource, new LogicCriteria(
				"SYSTEM_ID"));
		System.out.println("Result is: " + result);

		result = context.read(patient, datasource, new LogicCriteria(
				"PROVIDER_ID"));
		System.out.println("Result is: " + result);

		result = context.read(patient, datasource, new LogicCriteria(
				"given_name"));
		System.out.println("Result is: " + result);

		result = context.read(patient, datasource, new LogicCriteria(
				"middle_name"));
		System.out.println("Result is: " + result);

		result = context.read(patient, datasource, new LogicCriteria(
				"family_name"));
		System.out.println("Result is: " + result);

		result = context.read(patient, datasource, new LogicCriteria(
				"fax_number"));
		System.out.println("Result is: " + result);
		
		result = context.read(patient, datasource, new LogicCriteria(
				"user_id"));
		System.out.println("Result is: " + result);
		
		// Test with restrictions
		LogicCriteria criteria = new LogicCriteria("username").equalTo("JOHN.SMITH.055555");
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		criteria = new LogicCriteria("SYSTEM_ID").equalTo("3-4");
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		criteria = new LogicCriteria("PROVIDER_ID").equalTo("055555");
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		criteria = new LogicCriteria("given_name").equalTo("JOHN");
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		criteria = new LogicCriteria("middle_name").equalTo("W");
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		criteria = new LogicCriteria("family_name").equalTo("SMITH");
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		// test AND
		LogicCriteria leftCriteria  = new LogicCriteria("family_name").equalTo("SMITH");
		LogicCriteria rightCriteria = new LogicCriteria("given_name").equalTo("JOHN");
		criteria = leftCriteria.and(rightCriteria);
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		// test AND with no result
		leftCriteria = new LogicCriteria("family_name").equalTo("SMITH");
		rightCriteria = new LogicCriteria("given_name").equalTo("Bob");
		criteria = leftCriteria.and(rightCriteria);
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		// test OR
		leftCriteria = new LogicCriteria("family_name").equalTo("SMITH");
		rightCriteria = new LogicCriteria("given_name").equalTo("Bob");
		criteria = leftCriteria.or(rightCriteria);
		result = context.read(patient, datasource, criteria);
		System.out.println("Result is: " + result);

		// test with latest encounter
		criteria = new LogicCriteria("username").equalTo("JOHN.SMITH.055555");
		Cohort patientSet = new Cohort();
		patientSet.addMember(patient.getPatientId());

		EncounterService encounterService = Context.getEncounterService();
		List<Encounter> encounters = encounterService.getEncountersByPatientId(
				patient.getPatientId());

		ArrayList<Integer> encounterList = new ArrayList<Integer>();

		if (encounters.size() > 0)
		{
			encounterList.add(encounters.get(0).getEncounterId());
		}
		context.setGlobalParameter("encounterList", encounterList);

		result = datasource.read(context, patientSet, criteria).get(
				patient.getPatientId());
		System.out.println("Result is: " + result);

		// test with encounters in certain date range
		criteria = new LogicCriteria("username").equalTo("JOHN.SMITH.055555");
		patientSet = new Cohort();
		patientSet.addMember(patient.getPatientId());

		encounterService = Context.getEncounterService();
		Date endDate = new java.util.Date();
		Date startDate = new java.util.Date(2008, Calendar.MARCH, 18);
		List<Encounter> encountersByDate = encounterService.getEncounters(
				patient, null,startDate, endDate,null,null,true);

		encounterList = new ArrayList<Integer>();

		if (encountersByDate.size() > 0)
		{
			for (Encounter currEncounter : encountersByDate)
			{
				encounterList.add(currEncounter.getEncounterId());
			}
		}
		context.setGlobalParameter("encounterList", encounterList);

		result = datasource.read(context, patientSet, criteria).get(
				patient.getPatientId());
		System.out.println("Result is: " + result);

		return result;
	}

	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	public String[] getDependencies()
	{
		return null;
	}

	public int getTTL()
	{
		return 60 * 60 * 24; // 1 day
	}

	public Datatype getDefaultDatatype()
	{
		return Datatype.TEXT;
	}
}
