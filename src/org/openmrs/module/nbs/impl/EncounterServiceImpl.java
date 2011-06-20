package org.openmrs.module.nbs.impl;

import org.openmrs.module.nbs.service.EncounterService;

/**
 * Encounter-related services
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class EncounterServiceImpl extends org.openmrs.api.impl.EncounterServiceImpl implements EncounterService
{
	org.openmrs.module.nbs.db.EncounterDAO dao = null;
	
	/**
	 * 
	 */
	public EncounterServiceImpl()
	{
	}

	/**
	 * @param dao
	 */
	public void setNbsEncounterDAO(org.openmrs.module.nbs.db.EncounterDAO dao)
	{
		this.dao = dao;
		super.setEncounterDAO(this.dao);
	}
	
}
