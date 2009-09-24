package org.openmrs.module.nbsmodule.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.PatientDashboardTabExt;

public class NBSPatientDashboardTabExt extends PatientDashboardTabExt {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getPortletUrl() {
		return "patientDSS";
	}

	@Override
	public String getRequiredPrivilege() {
		return "Patient Dashboard - View Forms Section";
	}

	@Override
	public String getTabId() {
		return "patientNBS";
	}

	@Override
	public String getTabName() {
		return "nbsmodule.patientDashboard.NBS";
	}
	
}
