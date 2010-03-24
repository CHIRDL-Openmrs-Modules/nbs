package org.openmrs.module.nbsmodule.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.nbsmodule.NBSModuleResponse;

/**
 * HelloWorldResponse-related database functions
 * 
 * @author Ben Wolfe
 * @version 1.0
 */
public interface NBSModuleDAO {

	/**
	 * Creates a new helloWorldResponse record
	 * 
	 * @param helloWorldResponse to be created
	 * @throws DAOException
	 */
	public void createNBSModuleResponse(NBSModuleResponse alertResponse) throws DAOException;

	/**
	 * Get helloWorldResponse by internal identifier
	 * 
	 * @param helloWorldResponseId internal helloWorldResponse identifier
	 * @return helloWorldResponse with given internal identifier
	 * @throws DAOException
	 */
	public NBSModuleResponse getNBSModuleResponse(Integer alertId) throws DAOException;

	/**
	 * Update helloWorldResponse 
	 * 
	 * @param helloWorldResponse to be updated
	 * @throws DAOException
	 */
	public void updateNBSModuleResponse(NBSModuleResponse helloWorldResponse) throws DAOException;
	
	public List<NBSModuleResponse> getResponses() throws DAOException;
	
	//public List<NBSModuleResponse> getNBSAlertsByPatient(Patient patient);
	public List<NBSModuleResponse> getNBSAlerts(Patient patient, Integer formId, Integer providerId,
			Encounter encounter, Date date, Integer status);
	
	/**
	 * Get the nbs alert status id by name string.
	 * @param name
	 * @return status
	 */
	public Integer getAlertStatusIdByName(String name);

}
