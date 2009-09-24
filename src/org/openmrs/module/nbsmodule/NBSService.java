package org.openmrs.module.nbsmodule;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.nbsmodule.db.NBSModuleDAO;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface NBSService {

	public void setNBSModuleDAO(NBSModuleDAO dao);

	/**
	 * Saves (creates) a new hello world response
	 * 
	 * @param saying to be created
	 * @throws APIException
	 */
	@Authorized({"Add Hello World Response"})
	public void createNBSModuleResponse(NBSModuleResponse saying) throws APIException;

	/**
	 * Get response by internal identifier
	 * 
	 * @param responseId internal saying identifier
	 * @return response with given internal identifier
	 * @throws APIException
	 */
	@Authorized({"View Hello World Response"})
	@Transactional(readOnly=true)
	public NBSModuleResponse getNBSModuleResponse(String pid) throws APIException;
	public void writeNBSModuleResponse(Patient p, Encounter enc) throws APIException;
	public void produce(String tokenName, String formName, Patient patient,Encounter enc)throws Exception;
	
	/**
	 * Save response
	 * 
	 * @param response to be updated
	 * @throws APIException
	 */
	@Authorized({"Edit Hello World Response"})
	public void updateNBSModuleResponse(NBSModuleResponse response) throws APIException;

	@Authorized({"View Hello World Response"})
	public List<NBSModuleResponse> getResponses() throws APIException;
	
	/**
	 * @param resultPatient
	 * @param prop
	 * @return
	 */
	public List<String> getAlerts(Patient resultPatient, Properties prop);

}