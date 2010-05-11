package org.openmrs.module.nbsmodule;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openmrs.Encounter;
import org.openmrs.FormField;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
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
	 * Get NBS alerts that have not yet been sent to providers.
	 * @param patient
	 * @param formId
	 * @param providerId
	 * @param encounter
	 * @param date
	 * @return
	 */
	public List<NBSModuleResponse>  getNBSAlerts(Patient resultPatient, Integer formId, 
			Integer providerId, Encounter encounter, Date date, Integer status);
	/**
	 * Gets the unprocessed alerts and modifies status to indicate currently being processed.
	 * @param resultPatient
	 * @param formId
	 * @param providerId
	 * @param encounter
	 * @param date
	 * @return
	 */
	public List<NBSModuleResponse>  captureNBSAlerts(Patient resultPatient, Integer formId, 
			Integer providerId, Encounter encounter, Date date);
		
	
	/**
	 * @param name
	 * @return
	 */
	public Integer getAlertStatusIdByName(String name);
	
	public void consume(InputStream input, Patient patient, Integer encounterId, FormInstance formInstance,
	                    Integer sessionId, List<FormField> fieldsToConsume, Integer locationTagId);

}