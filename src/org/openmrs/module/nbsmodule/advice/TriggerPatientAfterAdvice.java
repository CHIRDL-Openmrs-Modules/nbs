package org.openmrs.module.nbsmodule.advice;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.sockethl7listener.ProcessedMessagesManager;
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
				
				FormInstance formInstance = tfState.getFormInstance();
				Integer formInstanceId = null;
				if (formInstance != null){
					formInstanceId = formInstance.getFormInstanceId();
					int lastIndex = processedFilename.lastIndexOf("\\");
					String directory = processedFilename.substring(0,lastIndex);
			
				
						if(criteria == "EXISTS")
				    	{
				    		TeleformFileMonitor.addToTifFileProcessing( formInstance, directory, ".tif");
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
				}
				
				TeleformFileMonitor.statesProcessed();
		
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		
	}

}