package org.openmrs.module.nbs.test.service;

import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.nbs.test.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import static org.junit.Assert.assertTrue;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestEncounterService extends BaseModuleContextSensitiveTest
{
	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@Before
	public void runBeforeEachTest() throws Exception 
	{
		// create the basic user and give it full rights
		initializeInMemoryDatabase();
		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEncounterService() throws Exception
	{
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		int patientId = 30520;
		Calendar calendar = Calendar.getInstance();
		calendar.set(1, Calendar.OCTOBER, 2007);
		Iterator<Encounter> iter = null;

		// test all methods that return encounters from nbs Encounter Service

		Encounter encounter = encounterService.getEncounter(1);
		assertTrue(encounter instanceof org.openmrs.module.nbs.hibernateBeans.Encounter);

		List<Encounter> encounterList = encounterService
				.getEncountersByPatientId(patientId);

		if (encounterList == null)
		{
			fail();
		} else
		{
			iter = encounterList.iterator();
			while (iter.hasNext())
			{
				assertTrue(iter.next() instanceof org.openmrs.module.nbs.hibernateBeans.Encounter);
			}
		}

	}
}
