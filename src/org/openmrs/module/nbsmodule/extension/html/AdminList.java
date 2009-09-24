package org.openmrs.module.nbsmodule.extension.html;

import java.util.HashMap;
import java.util.Map;
import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.*;

public class AdminList extends AdministrationSectionExt {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTitle() {
		return "nbsmodule.title";
	}
	
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("module/nbsmodule/viewNBSAlerts.htm", "nbsmodule.view");
		map.put("module/nbsmodule/addResponse.form", "nbsmodule.addResponse");
		
		return map;
	}
	
}
