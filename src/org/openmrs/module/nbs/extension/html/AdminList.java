package org.openmrs.module.nbs.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * @author Tammy Dugan
 *
 */
public class AdminList extends AdministrationSectionExt {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTitle() {
		return "nbs.title";
	}
	
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("module/nbs/testCheckin.form", "Test checkin through AOP");
		map.put("module/nbs/parseDictionary.form", "Parse dictionary file");
		map.put("module/nbs/loadObs.form", "Load old obs");
		map.put("module/nbs/fillOutPSF.form?formName=PSF", "Scan PSF");
		map.put("module/nbs/fillOutPWS.form?formName=PWS", "Scan PWS");
		map.put("module/nbs/greaseBoard.form", "Grease Board");
		map.put("module/nbs/viewPatient.form", "View Encounters");
		map.put("module/nbs/ruleTester.form", "Rule Tester");
		map.put("module/nbs/weeklyReports.form", "Weekly Reports");
		return map;
	}
	
}
