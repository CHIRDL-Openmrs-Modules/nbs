package org.openmrs.module.nbsmodule.advice;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.nbsmodule.NBSService;
import org.openmrs.module.sockethl7listener.ProcessedMessagesManager;
import org.openmrs.module.sockethl7listener.util.Util;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.aop.AfterReturningAdvice;

public class TriggerPatientAfterAdvice implements AfterReturningAdvice {

	private Log log = LogFactory.getLog(this.getClass());

	private Integer pid = 0;
	private Integer eid = 0;

	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
	
		try {
			if (method.getName().equals("messageProcessed"))
			{
							
				if(method.getParameterTypes()[0].getName().compareTo("org.openmrs.Encounter") == 0) {
				 
			    Encounter enc = (Encounter)args[0];
		        eid = enc.getEncounterId();
		        
				Patient p = enc.getPatient();
				
				pid = p.getPatientId();
				
				log.debug("Method: " + method.getName() + ". After advice called for Patient" + (pid.toString()) + " time(s) now.");
				org.openmrs.module.nbsmodule.NBSService nbs = Context.getService(org.openmrs.module.nbsmodule.NBSService.class);
			 	
				nbs.writeNBSModuleResponse(p, enc);
				ProcessedMessagesManager.encountersProcessed();
		        
				}
			}
			else if (method.getName().equals("fileProcessed"))
			{
				TeleformFileState tfState = (TeleformFileState) args[0];
				String criteria = tfState.getCriteria();
			
				String processedFilename =  tfState.getFilename();
				File processedFile = new File(processedFilename);
				
				int formInstanceId = tfState.getFormInstanceId();
				int formId = tfState.getFormId();
				
				int lastIndex = processedFilename.lastIndexOf("\\");
			
				String directory = processedFilename.substring(0,lastIndex);
		
			//	if(processedFile.exists())
			//	{
					if(criteria == "EXISTS")
			    	{
			    		TeleformFileMonitor.addToTifFileProcessing(formId, formInstanceId, directory, ".tif");
				   	}
			    	else if (criteria == "GT_SENTINEL_TIME")
			    	{
			    		String tifFilenameDesired =   directory + "//" + Integer.valueOf(formInstanceId) + ".tif";
				    	File tifFileDesired = new File(tifFilenameDesired);
				    	processedFile.renameTo(tifFileDesired);
			    	}
			    	else
			    	{
			    		
			    		System.out.println("*****ERROR: criteria is not exist:" );
			    	}
					
					TeleformFileMonitor.statesProcessed();
			//	}
			//	else
			//	{
			//		System.out.println("*****ERROR: Processed file does not exist:" + processedFilename);
			//	}
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		
	}

}