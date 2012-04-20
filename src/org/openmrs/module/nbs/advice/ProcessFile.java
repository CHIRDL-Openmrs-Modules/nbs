/**
 * 
 */
package org.openmrs.module.nbs.advice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.nbs.NbsStateActionHandler;

/**
 * @author tmdugan
 * 
 */
public class ProcessFile implements Runnable
{
	private Log log = LogFactory.getLog(this.getClass());

	private PatientState patientState = null;
	private String filename = null;
	private FormInstance formInstance = null;
	private Integer patientId = null;
	private String stateName = null;
	private Integer patientStateId = null;
	Map<String, Object> parameters = null;
	

	public ProcessFile(TeleformFileState tfState)
	{
		//set these values in the constructor instead of the 
		//run method of the thread to prevent crossthreading
		//from corrupting the patientState
		//set these values in the constructor instead of the 
		//run method of the thread to prevent crossthreading
		//from corrupting the patientState
		this.filename = tfState.getFullFilePath();
		this.formInstance = tfState.getFormInstance();
		parameters = tfState.getParameters();
		log.error("processing file method here::::");
		if (parameters == null){
			log.error("paremters are null for tfSTate:");
		} else{
			log.error(" parameters for tfState.:  size = " + parameters.size());
			Set<Entry<String,Object>> set =  parameters.entrySet();
			ArrayList<String> list = new ArrayList<String>();
			Iterator it = set.iterator();
			while (it.hasNext()){
				log.error( it.next().toString());
			}
		}
		
		if (parameters != null)
		{
			PatientState patientState = (PatientState) parameters.get("patientState");
			this.patientStateId = patientState.getPatientStateId();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */

	public void run()
	{
		try
		{
		
			Context.openSession();
			
			AdministrationService adminService = Context
			.getAdministrationService();
			Context.authenticate(adminService
			.getGlobalProperty("scheduler.username"), adminService
			.getGlobalProperty("scheduler.password"));
			ATDService atdService = Context.getService(ATDService.class);

			//this prevents lazy initialization exceptions from the
			//object being passed across the aop call
			PatientState patientState = atdService.getPatientState(this.patientStateId);
			HashMap<String, Object> stateParameters = patientState
					.getParameters();
			if (stateParameters == null)
			{
				stateParameters = new HashMap<String, Object>();
			}
			if (parameters != null ){
				stateParameters.put("providerId", parameters.get("providerId"));
				stateParameters.put("providerLastName", parameters.get("providerLastName"));
				stateParameters.put("providerFirstName", parameters.get("providerFirstName"));
			}
			stateParameters.put("filename", this.filename);
			stateParameters.put("formInstance", this.formInstance);
		
			NbsStateActionHandler.getInstance().changeState(patientState,
					stateParameters);
		} 
		catch (Exception e)
		{
			log.error("Error processing file", e);
		} 
		finally
		{
			Context.closeSession();
			log.info("Finished execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
				new Timestamp(new Date().getTime()) + ")");
		}
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
    public String getName() {
	    return "Process File (State: " + stateName + " Patient: " + patientId + " Patient State: " + 
	    	patientState.getPatientStateId() + ")";
    }

	
}
