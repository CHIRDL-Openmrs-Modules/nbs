

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestProviderDatasource extends BaseModuleContextSensitiveTest
{
	@Before
	public void runBeforeEachTest() throws Exception {
		authenticate();
	}
	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpBeforeTransaction()
	 */
	
	@Test
	public void testProviderDatasource() throws Exception
	{
		DssService dssService = (DssService) Context
				.getService(DssService.class);
		Patient patient = Context.getPatientService().getPatient(30522);

		ArrayList<Rule> ruleList = new ArrayList<Rule>();
		Rule rule = new Rule();
		rule.setTokenName("TestProviderDatasource");

		ruleList.add(rule);
		String defaultPackagePrefix = "org.openmrs.module.nbs.rule.";
		dssService.runRulesAsString(patient, ruleList,
				defaultPackagePrefix,null);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase()
	{
		return false;
	}
}