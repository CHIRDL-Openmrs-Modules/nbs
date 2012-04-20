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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * 
 */
@SkipBaseSetup
public class NBSServiceTest extends BaseContextSensitiveTest {
	
	@Before
	public void runBeforeEachTest() throws Exception {
		authenticate();
	}
	 /**
	 * @see org.openmrs.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
		
	}

	@Test
	public void testClass() throws Exception 
	{
		NbsService nbsService = 
			(NbsService) Context.getService(NbsService.class);
		Patient patient = Context.getPatientService().getPatient(631);
		Encounter encounter = Context.getEncounterService().getEncounter(871);
	//	nbsService.writeNBSModuleResponse(patient, encounter);
	}
	
}

