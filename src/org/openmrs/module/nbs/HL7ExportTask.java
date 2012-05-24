/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.nbs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.Base64;
import org.openmrs.module.chirdlutil.util.FileDateComparator;
import org.openmrs.module.chirdlutil.util.FileListFilter;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7Export;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMap;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMapType;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.XCN;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PV1;

/**
 * Implementation of a task that process all form entry queues. NOTE: This class does not need to be
 * StatefulTask as we create the context in the constructor.
 * 
 * @version 1.1 1.1 - made processor static to ensure only one HL7 processor runs
 */
public class HL7ExportTask extends AbstractTask {
	
	// Logger
	private static Log log = LogFactory.getLog(HL7ExportTask.class);
	
	// Instance of hl7 processor
	private TaskDefinition taskConfig;
	private String host;
	private Integer port;
	private String resendOption;
	private String resendNoAck;
	private Integer socketReadTimeout;
	private HL7SocketHandler socketHandler;
	private static Boolean isRunning = false;
	private boolean shutdown = false;
	private Integer interval;
	private String sendPing;
	private boolean stop = false;
	private Integer sleepAfterConnectError;
	private boolean testMode = false;
	private boolean sendISDHMessage = false;
	private String providerFirstName = "";
	private String providerLastName = "";
	private String providerIdent = "";
	
	/**
	 * Default Constructor (Uses SchedulerConstants.username and SchedulerConstants.password
	 */
	public HL7ExportTask() {
		
	}
	
	
	@Override
	public void initialize(TaskDefinition config)
	{
		
		isInitialized = false;
		Context.openSession();
		try {
			
			if (Context.isAuthenticated() == false)
				authenticate();
			this.taskConfig = config;
			//port to export
			String portName = this.taskConfig.getProperty("port");
			String intervalString = this.taskConfig.getProperty("interval");
			host  = this.taskConfig.getProperty("host");
			resendOption  = this.taskConfig.getProperty("resendOption");
			resendNoAck  = this.taskConfig.getProperty("resendNoAck");
			sendPing  = this.taskConfig.getProperty("sendPing");
			
			
			String  sleepAfterConnectErrorString  = this.taskConfig.getProperty("sleepAfterConnectError");
			String socketReadTimeoutString  = this.taskConfig.getProperty("socketReadTimeout");
			String testModeSring  = this.taskConfig.getProperty("testMode");
			
			if (testModeSring != null  || testModeSring.trim().equalsIgnoreCase("true")
					|| testModeSring.trim().equalsIgnoreCase("1") 
					|| testModeSring.trim().equalsIgnoreCase("y")
					|| testModeSring.trim().equalsIgnoreCase("yes")){
				testMode = true;
			}
			
			String sendISDHString  = this.taskConfig.getProperty("sendISDH");
			
			if (sendISDHString != null  || sendISDHString.trim().equalsIgnoreCase("true")
					|| sendISDHString.trim().equalsIgnoreCase("1") 
					|| sendISDHString.trim().equalsIgnoreCase("y")
					|| sendISDHString.trim().equalsIgnoreCase("yes")){
				sendISDHMessage = true;
			}
			
			if (host == null){
				host = "localhost";
			}
			
			if (portName != null){
				port = Integer.parseInt(portName);
			} else
			{
				port = 0;
			}
			
			if (intervalString != null ){
				try {
					interval = Integer.valueOf(intervalString);
				} catch (NumberFormatException e) {
					log.error("Interval property cannot be converted to integer value." );
					interval = 30000;
				}
			} else
			{
				interval = 0;
			}
			if (socketReadTimeoutString != null && ! socketReadTimeoutString.trim().equals("")){
				socketReadTimeout = Integer.parseInt(socketReadTimeoutString);
			} else {
				socketReadTimeout = 5; //seconds
			}
			
			if (sleepAfterConnectErrorString != null && ! socketReadTimeoutString.trim().equals("")){
				sleepAfterConnectError = Integer.valueOf(sleepAfterConnectErrorString);
			} else {
				sleepAfterConnectError = 600000; //seconds
			}
			
			stop = false;
			
		}finally{
			
			isInitialized = true;
			Context.closeSession();
		}

	}
	/**
	 * Process the next form entry in the database and then remove the form entry from the database.
	 */
	public void execute() {
		
		
		try {
			
			
			
			socketHandler = new HL7SocketHandler();
			processHL7InQueue();
		}
		catch (Exception e) {
			log.error("Error running hl7 export task", e);
			throw new APIException("Error running hl7 export task", e);
		}
		finally {
			
		}
	}
	
	/**
	 * Export message to ISDH to send provider information as object. When ever an nbs message is sent
	 * to providers,  we send an hl7 message to ISDH with provider name/id. 
	 * 
	 */
	public boolean exportISDH(NbsHL7Export nbsHl7Export) {
		
		
		ATDService atdService = Context.getService(ATDService.class);
		EncounterService encounterService = Context.getService(EncounterService.class);
		LocationService locService = Context.getLocationService();
		AdministrationService  adminService = Context.getAdministrationService();
		boolean entryProcessed = true;
		Integer sessionId = nbsHl7Export.getSessionId();
		
		try
		{
		
			
			if (!socketHandler.openSocket(host, port)){
				log.error("Unable to open socket to send ISDH message for host: " + host + " port: " + port );
				ATDError error = new ATDError("Warning", "Hl7 Export", 
						"Cannot connect to server for export of ISDH message."
						,"",
						 new Date(), null);
				atdService.saveError(error);
				socketHandler.closeSocket();	
				return false;
			}
			
			
			Integer encId = nbsHl7Export.getEncounterId();
			Encounter openmrsEncounter = (Encounter) encounterService.getEncounter(encId);
			
			//Get patient and session id
			Patient patient = openmrsEncounter.getPatient();
			if (patient == null){
				sessionId = nbsHl7Export.getSessionId();
				ATDError ce = new ATDError("Error", "Hl7 Export", 
						"Cannot export ISDH because patient is null. " + openmrsEncounter.getEncounterId(), 
						null ,
						 new Date(), sessionId);
				atdService.saveError(ce);
				return entryProcessed;
			}	
			
			//Get hl7 configuration file 
			String isdhConfigFileName = IOUtil.formatDirectoryName(adminService
					.getGlobalProperty("nbs.defaultISDHConfigFileLocation"));
			
			if (isdhConfigFileName == null || isdhConfigFileName.trim().equals("")){
				ATDError ce = new ATDError("Error", "Hl7 Export", 
						"ISDH Configuration file name not found. " + openmrsEncounter.getEncounterId(), 
						null ,
						 new Date(), sessionId);
				atdService.saveError(ce);
				return entryProcessed;
			}
			
			Integer hl7ExportQueueId = nbsHl7Export.getQueueId();
			Integer trigger_NBS_ProviderId = this.getProviderIdByExportQueueId(hl7ExportQueueId);
		
			
			//Construct message
			org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor constructor = 
				new org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor(isdhConfigFileName, null );
			
			
			List<Encounter> queryEncounterList = new ArrayList<Encounter>();
			queryEncounterList.add(openmrsEncounter);
		
			//MSH
			constructor.AddSegmentMSH(openmrsEncounter);
			
			//PID
			PatientIdentifier identifier = patient.getPatientIdentifier();
			String npi = null;
			constructor.AddSegmentPID(openmrsEncounter.getPatient());
			
			//PV1
			//*** Need to get provider id for IDSH provider.  Create a user/provider")
			//*** For now, get from properties
			Properties properties = constructor.getProps();
			
			providerFirstName = properties.getProperty("isdhProviderFirstName");
			providerLastName = properties.getProperty("isdhProviderLastName");
			providerIdent = properties.getProperty("isdhProviderID");
			
			PV1 providerSegment = constructor.AddSegmentPV1(openmrsEncounter,
					providerFirstName, providerLastName, providerIdent, trigger_NBS_ProviderId);
			if (providerSegment != null  ){
				XCN attDoctor = providerSegment.getAttendingDoctor(0);
				if (attDoctor != null){
					ST pv1ID = attDoctor.getIDNumber();
					if (pv1ID != null){
						npi =providerSegment.getAttendingDoctor(0).getIDNumber().getValue();
					}
				}
			}

			constructor.setAssignAuthority(identifier);
			
			//Add this if ISDH wants an OBX leave off until we know for sure.
			/*if (!addOBX(constructor, openmrsEncounter , formInstance.getFormId(), null, 
					properties, trigger_NBS_ProviderId)){
					sessionId = nbsHl7Export.getSessionId();
					ATDError ce = new ATDError("Error", "Hl7 Export", 
							"Error creating OBX for ISDH " + formInstance, 
							null ,
							 new Date(), sessionId);
					atdService.saveError(ce);
					return entryProcessed;
			}*/
				
			
		
			String message = constructor.getMessage();
			Date ackDate = null;
			if (testMode) {
				saveMessageFile(message,encId, ackDate, sendISDHMessage);
				return true;
			}
			
			
			if (message != null && !message.equals("")){
				ackDate = sendMessage(message, openmrsEncounter, socketHandler);
				entryProcessed = false;
			}
			saveMessageFile(message,encId, ackDate,sendISDHMessage);
	    
		} catch (IOException e ){
			log.error("Error exporting message:" + e);
			
		}catch (Exception e)
		
		{
			String message = e.getMessage();
			ATDError ce = new ATDError("Error", "Hl7 Export", 
					"Error creating ISDH hl7 export:" + message,Util.getStackTrace(e) , 
					 new Date(), sessionId);
			atdService.saveError(ce);
			
		} finally
		{
			socketHandler.closeSocket();
		
		}
		
		return entryProcessed;

	}	
	
	/**
	 * Export hl7 from a single queue entry in the export table. Tests connection to host and
	 * port.  Opens the socket, executes a ping test for reception of ACK, constructs an hl7 
	 * message based on a configuration xml and sends the message to host and port.
	 * 
	 * @param hl7InQueue queue entry to be processed
	 */
	public boolean exportHL7(NbsHL7Export nbsHl7Export) {
		
		
		ATDService atdService = Context.getService(ATDService.class);
		NbsService NbsService = Context.getService(NbsService.class);
		EncounterService encounterService = Context.getService(EncounterService.class);
		LocationService locService = Context.getLocationService();
		ConceptService conceptService = Context.getConceptService();
		boolean entryProcessed = true;
		
		try
		{
		
			
			if (!socketHandler.openSocket(host, port)){
				log.error("Unable to open socket for host: " + host + " port: " + port );
				ATDError error = new ATDError("Warning", "Hl7 Export", 
						"Cannot connect to server for export."
						,"",
						 new Date(), null);
				atdService.saveError(error);
				
					nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("open_socket_failed"));
					nbsHl7Export.setDateProcessed(new Date());
					NbsService.saveNbsHL7Export(nbsHl7Export);
					socketHandler.closeSocket();
					Thread.sleep(sleepAfterConnectError);
				
				return false;
			}
			
			
			Integer encId = nbsHl7Export.getEncounterId();
			Encounter openmrsEncounter = (Encounter) encounterService.getEncounter(encId);
			
			//try pinging for ACK
			if ( sendPing != null && (sendPing.equalsIgnoreCase("yes") || sendPing.equalsIgnoreCase("y")
					|| sendPing.equalsIgnoreCase("1")) && ! sendPing(openmrsEncounter)){
				log.error("Ping did not receive ACK : " + host + " port: " + port );
				nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("ACK_not_received"));
				nbsHl7Export.setDateProcessed(new Date());
				NbsService.saveNbsHL7Export(nbsHl7Export);
				return false;
				
			}
			
			//Get patient
			Patient patient = openmrsEncounter.getPatient();
			if (patient == null){
				Integer sessionId = nbsHl7Export.getSessionId();
				ATDError ce = new ATDError("Error", "Hl7 Export", 
						"Cannot export because patient is null. " + openmrsEncounter.getEncounterId(), 
						null ,
						 new Date(), sessionId);
				atdService.saveError(ce);
				return entryProcessed;
			}	
			
			
			//Get hl7 configuration file 
			String configFileName = getConfigFileName(nbsHl7Export);
			if (configFileName == null || configFileName.trim().equals("")){
				return entryProcessed;
			}
			
			//Get hl7 properties
			Properties hl7Properties = getHl7Properties(nbsHl7Export);
			if (hl7Properties == null ){
				return entryProcessed;
			}
			
			Integer hl7ExportQueueId = nbsHl7Export.getQueueId();
			FormInstance formInstance = this.getFormInstanceByExportQueueId(hl7ExportQueueId);
			Integer providerId = this.getProviderIdByExportQueueId(hl7ExportQueueId);
		
			
			//Construct message
			org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor constructor = 
				new org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor(configFileName, null );
			
			
			List<Encounter> queryEncounterList = new ArrayList<Encounter>();
			queryEncounterList.add(openmrsEncounter);
		
			//MSH
			constructor.AddSegmentMSH(openmrsEncounter);
			Integer sessionId = nbsHl7Export.getSessionId();
			LocationTag  locTag = locService.getLocationTagByName("Default Location Tag");
			Integer locTagId = locTag.getLocationTagId();
			
			//PID
			PatientIdentifier identifier = patient.getPatientIdentifier();
			String npi = null;
			constructor.AddSegmentPID(openmrsEncounter.getPatient());
			
			//PV1
			PV1 providerSegment = constructor.AddSegmentPV1(openmrsEncounter,providerId);
			if (providerSegment != null  ){
				XCN attDoctor = providerSegment.getAttendingDoctor(0);
				if (attDoctor != null){
					ST pv1ID = attDoctor.getIDNumber();
					if (pv1ID != null){
						npi =providerSegment.getAttendingDoctor(0).getIDNumber().getValue();
					}
				}
			}

			if (npi == null || npi.equalsIgnoreCase("")){
				
				nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("no_provider_npi"));
				NbsService.saveNbsHL7Export(nbsHl7Export);
				ATDError ce = new ATDError("Error", "Hl7 Export", 
						"Cannot export form instance " + formInstance + " because provider NPI is null. " + openmrsEncounter.getEncounterId(), 
						null ,
						 new Date(), sessionId);
				atdService.saveError(ce);
				
				Concept concept = conceptService.getConcept("atd_sent_to_provider");
				org.openmrs.module.nbs.util.Util.saveObs(patient, concept, encId, "missing_npi" , formInstance, null, locTagId);
				log.error("Provider NPI is null for form instance: " + formInstance );
				return entryProcessed;
			}
			constructor.setAssignAuthority(identifier);
			
			//TIFF
			if (!addOBXForTiff(constructor, openmrsEncounter , formInstance.getFormId(), null, 
						hl7Properties)){
				nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("Image_not_found"));
					NbsService.saveNbsHL7Export(nbsHl7Export);
					sessionId = nbsHl7Export.getSessionId();
					ATDError ce = new ATDError("Error", "Hl7 Export", 
							"Image not found for hl7 export " + formInstance, 
							null ,
							 new Date(), sessionId);
					Concept concept = conceptService.getConcept("atd_sent_to_provider");
					//org.openmrs.module.nbs.util.Util.saveObs(patient, concept, encId, "Image_not_found" , formInstance, null, locTagId);
					
					//atdService.saveError(ce);
					return entryProcessed;
			}
				
			
			//Send the message
			String message = constructor.getMessage();
			
			
			if (testMode) {
				saveMessageFile(message,encId, null);
				nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("test_mode"));
				NbsService.saveNbsHL7Export(nbsHl7Export);
				return true;
			}
			
			nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("hl7_sent"));
			nbsHl7Export.setDateProcessed(new Date());
			
			Date ackDate = null;
			if (message != null && !message.equals("")){
				ackDate = sendMessage(message, openmrsEncounter, socketHandler);
				if (ackDate != null) { 
					nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("ACK_received"));
					nbsHl7Export.setAckDate(ackDate);
					Concept concept = conceptService.getConcept("atd_sent_to_provider");
					org.openmrs.module.nbs.util.Util.saveObs(patient, concept, encId, formInstance.toString(), formInstance, null, locTagId);
					
				}	else {
					nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("ACK_not_received"));
					stop = true;
					entryProcessed = false;
				}
				saveMessageFile(message,encId, ackDate);
			}
			
	
			NbsService.saveNbsHL7Export(nbsHl7Export);
	    
		} catch (IOException e ){
			log.error("Error exporting message:" + e);
			
		}catch (Exception e)
		
		{
			Integer sessionId = nbsHl7Export.getSessionId();
			String message = e.getMessage();
			ATDError ce = new ATDError("Error", "Hl7 Export", 
					"Error creating hl7 export:" + message,Util.getStackTrace(e) , 
					 new Date(), sessionId);
			atdService.saveError(ce);
			
		} finally
		{
			socketHandler.closeSocket();
			
		}
		
		return entryProcessed;

	}	
	
	
	/**
	 * Transform the next pending HL7 inbound queue entry. If there are no pending items in the
	 * queue, this method simply returns quietly.
	 * 
	 * Send to ISDH in a second hl7 if the actual nbs hl7 was exported
	 * 
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public boolean processNextHL7InQueue(String resendOption, String resendNoAck) {
		Context.openSession();
		boolean entryProcessed;
		boolean ISDHProcessed;
		NbsHL7Export nbsHl7Export = null;
		try {
			if (Context.isAuthenticated() == false);
				authenticate();
			NbsService nbsService = Context.getService(NbsService.class);
			entryProcessed = false;
			nbsHl7Export = nbsService.getNextPendingHL7Export(resendOption, resendNoAck);
			if (nbsHl7Export != null) {
				entryProcessed = exportHL7(nbsHl7Export);;
			}
			if (nbsHl7Export != null && sendISDHMessage == true
					&&  entryProcessed == true){
				ISDHProcessed = exportISDH(nbsHl7Export);;
				
			}
		} finally {
			Context.closeSession();
		}
		return entryProcessed;
	}
	
	
	/**
	 * Starts up a thread to process all existing HL7InQueue entries
	 */
	public void processHL7InQueue( ) throws HL7Exception {
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("HL7 processor aborting (another processor already running)");
				return;
			}
			isRunning = true;
		}
		try {
			log.debug("Start processing hl7 in queue");
			while ( processNextHL7InQueue(this.resendOption, this.resendNoAck ) 
					&& !stop && shutdown == false) {
				// loop until queue is empty
				try {
					Thread.sleep(interval);//check every 5 seconds
				}
				catch (InterruptedException e) {
					log.error("Error generated", e);
				}
			}
			log.debug("Done processing hl7 in queue");
		}
		finally {
			isRunning = false;
		}
	}
	
	private String getFormInstanceIdByExportQueueId(Integer queueId){
		NbsService NbsService = Context.getService(NbsService.class);
		NbsHL7ExportMapType formInstanceMapType = 
			NbsService.getHL7ExportMapTypeByName("form_instance");
		NbsHL7ExportMap exportmap =  NbsService.getNbsExportMapByQueueId(queueId, formInstanceMapType);
		if (exportmap == null){
			return null;
		}
		return exportmap.getValue();
	
	}
	
	private Integer getProviderIdByExportQueueId(Integer queueId){
		NbsService NbsService = Context.getService(NbsService.class);
		NbsHL7ExportMapType providerMapType = NbsService.getHL7ExportMapTypeByName("provider_id");
		NbsHL7ExportMap exportmap =  NbsService.getNbsExportMapByQueueId(queueId, providerMapType);
		if (exportmap == null){
			return null;
		}
		Integer providerId = Integer.valueOf(exportmap.getValue());
		return providerId;
	
	}
	
	
	private FormInstance getFormInstanceByExportQueueId(Integer queueId){
		String formInstanceIdString = getFormInstanceIdByExportQueueId(queueId);
		if (formInstanceIdString == null){
			return null;
		}
		String[] segments = formInstanceIdString.split("_");
		if (segments == null){
			return null;
		}
		FormInstance formInstance = new FormInstance(Integer.valueOf(segments[0]), 
				Integer.valueOf(segments[1]), Integer.valueOf(segments[2]));
		
		return formInstance;
	}
	
	
	
	
private String getConfigFileLocation( Integer formId){
		
		
		String filename = null;
		Integer locId = null;
		Integer locTagId = null;
		
		LocationService locService = Context.getLocationService();
		
		
		ATDService atdService = Context.getService(ATDService.class);
		Location loc= locService.getLocation("Default Location");
		LocationTag  locTag = locService.getLocationTagByName("Default Location Tag");
		locId = loc.getLocationId();
		locTagId = locTag.getLocationTagId();
		
		FormAttributeValue formAttrValue = atdService.getFormAttributeValue(formId, "HL7ConfigFile", locTagId, locId);
		if (formAttrValue != null){
			filename = formAttrValue.getValue();
		}
		return filename;
	}
	

	private boolean addOBXForTiff(org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor constructor,
			Encounter encounter, Integer formId ,Hashtable<String,
			String> mappings, 
			Properties hl7Properties){
		
		
		boolean obxcreated = false;
		Integer locationTagId = null;
		String formName = null;
		int obsRep = 0;
		
		String obrBatteryName = hl7Properties.getProperty("obr_name");
		String obrBatteryCode = hl7Properties.getProperty("obr__code");
		String obxAlertTitle = hl7Properties.getProperty("obx_title");
		String obxAlertTitleCode = hl7Properties.getProperty("obx_title_code");
		String obxAlertDescripton = hl7Properties.getProperty("obx_alert_description");
		String obxAlert = hl7Properties.getProperty("obx_alert");
		String obxAlertCode = hl7Properties.getProperty("obx_alert_code");
		
		
		
		ATDService atdService = Context.getService(ATDService.class);
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		if (form != null){
			formName = form.getName();
		}
		
		String formDir = "";
		String hl7Abbreviation = "ED";
		Integer encounterId = encounter.getEncounterId();
		locationTagId = getLocationTagIdByEncounter(encounterId);
		
		
		List<PatientState> patientStates = 
			atdService.getPatientStatesWithFormInstances(formName, encounterId);
		
		FormInstance formInstance = null;
		Integer formInstanceId = null;
		Integer formLocationId = null;
		
		if (patientStates == null){
			return false; 
		}
		
		Iterator<PatientState> psIterator = patientStates.iterator();
		if (psIterator.hasNext()){
			formInstance = psIterator.next().getFormInstance();
			if (formInstance == null){
				return false;
			}
		}
		
		String filename = "";
		String encodedForm = "";
		try {
			formId = formInstance.getFormId();
			formInstanceId = formInstance.getFormInstanceId();
			formLocationId = formInstance.getLocationId();
			
			if (formId == null || locationTagId == null || formLocationId == null
					|| formInstanceId == null){
				return false;
			}
			
			formDir  = IOUtil
						.formatDirectoryName(org.openmrs.module.atd.util.Util
								.getFormAttributeValue(formId,
										"imageDirectory", locationTagId,
										formLocationId));
			

			if (formDir == null || formDir.equals("")){
				return false;
			}
			filename = formLocationId + "-" + formId + "-" + formInstanceId;
			
			//This FilenameFilter will get ALL tifs starting with the filename
			//including of rescan versions nnn_1.tif, nnn_2.tif, etc
			FilenameFilter filtered = new FileListFilter(filename, "pdf");
			File dir = new File(formDir);
			File[] files = dir.listFiles(filtered); 
			if (files == null || files.length == 0){
				return false;
			}
			
			//This FileDateComparator will list in order
			//with newest file first.
			Arrays.sort(files, new FileDateComparator());
		
			encodedForm = encodeForm(files[0]);
				
			int orderRep = 0;
			
			constructor.setFormInstance(formInstance.toString());
			constructor.AddSegmentOBR(encounter, obrBatteryCode, obrBatteryName, orderRep);
			constructor.AddSegmentOBX(obxAlertTitle, obxAlertTitleCode,
						null, null, obxAlertDescripton, null, new Date() , "ST", orderRep, obsRep);
			OBX resultOBX = constructor.AddSegmentOBX(obxAlert, obxAlertCode, 
						null, "", encodedForm, "", new Date() , hl7Abbreviation, orderRep, obsRep + 1);
			if (resultOBX != null){
					obxcreated = true;
			} 
		
		} catch (Exception e) {
			log.error("Exception adding OBX for tiff image. " + e.getMessage());
		}
			
		return obxcreated;
		
	}
	
	private boolean addOBX(org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor constructor,
			Encounter encounter, Integer formId ,Hashtable<String,
			String> mappings, 
			Properties hl7Properties,
			Integer attendingProviderId){
		
		
		ATDService atdService = Context.getService(ATDService.class);
		boolean obxcreated = false;
		Integer locationTagId = null;
		String formName = null;
		int obsRep = 0;
		
		String obrBatteryName = hl7Properties.getProperty("obr_name");
		String obrBatteryCode = hl7Properties.getProperty("obr__code");
		String obxAlertTitle = hl7Properties.getProperty("obx_title");
		String obxAlertTitleCode = hl7Properties.getProperty("obx_title_code");
		String obxAlertDescripton = hl7Properties.getProperty("obx_alert_description");
		String obxAlert = hl7Properties.getProperty("obx_alert");
		String obxAlertCode = hl7Properties.getProperty("obx_alert_code");
		
		
		String formDir = "";
		String hl7Abbreviation = "ST";
		Integer encounterId = encounter.getEncounterId();
		locationTagId = getLocationTagIdByEncounter(encounterId);
		
		
		List<PatientState> patientStates = 
			atdService.getPatientStatesWithFormInstances(formName, encounterId);
		
		FormInstance formInstance = null;
		Integer formInstanceId = null;
		Integer formLocationId = null;
		
		if (patientStates == null){
			return false; 
		}
		
		Iterator<PatientState> psIterator = patientStates.iterator();
		if (psIterator.hasNext()){
			formInstance = psIterator.next().getFormInstance();
			if (formInstance == null){
				return false;
			}
		}
		
		String filename = "";
		String encodedForm = "";
		try {
			formId = formInstance.getFormId();
			formInstanceId = formInstance.getFormInstanceId();
			formLocationId = formInstance.getLocationId();
			
			if (formId == null || locationTagId == null || formLocationId == null
					|| formInstanceId == null){
				return false;
			}
			
			formDir  = IOUtil
						.formatDirectoryName(org.openmrs.module.atd.util.Util
								.getFormAttributeValue(formId,
										"imageDirectory", locationTagId,
										formLocationId));
			

			if (formDir == null || formDir.equals("")){
				return false;
			}
			filename = formLocationId + "-" + formId + "-" + formInstanceId;
			
			//This FilenameFilter will get ALL tifs starting with the filename
			//including of rescan versions nnn_1.tif, nnn_2.tif, etc
			FilenameFilter filtered = new FileListFilter(filename, "pdf");
			File dir = new File(formDir);
			File[] files = dir.listFiles(filtered); 
			if (files == null || files.length == 0){
				return false;
			}
			
			//This FileDateComparator will list in order
			//with newest file first.
			Arrays.sort(files, new FileDateComparator());
		
			encodedForm = encodeForm(files[0]);
				
			int orderRep = 0;
			
			constructor.setFormInstance(formInstance.toString());
			constructor.AddSegmentOBR(encounter, obrBatteryCode, obrBatteryName, orderRep);
			constructor.AddSegmentOBX(obxAlertTitle, obxAlertTitleCode,
						null, null, obxAlertDescripton, null, new Date() , "ST", orderRep, obsRep);
			OBX resultOBX = constructor.AddSegmentOBX(obxAlert, obxAlertCode, 
						null, "", encodedForm, "", new Date() , hl7Abbreviation, orderRep, obsRep + 1);
			if (resultOBX != null){
					obxcreated = true;
			} 
		
		} catch (Exception e) {
			log.error("Exception adding OBX for tiff image. " + e.getMessage());
		}
			
		return obxcreated;
		
	}
	
	private Integer getLocationTagIdByEncounter(Integer encId){
		Integer locationTagId = null;
		String printerLocation = null;
		
		EncounterService encounterService = Context
		.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService
			.getEncounter(encId);
		
		try {
			if (encId != null && encounter != null)
			{
				// see if the encounter has a printer location
				// this will give us the location tag id
				printerLocation = encounter.getPrinterLocation();

				// if the printer location is null, pick
				// any location tag id for the given location
				if (printerLocation == null)
				{
					Location location = encounter.getLocation();
					if (location != null)
					{
						Set<LocationTag> tags = location.getTags();

						if (tags != null && tags.size() > 0)
						{
							printerLocation = ((LocationTag) tags.toArray()[0])
									.getTag();
						}
					}
				}
				if (printerLocation != null)
				{
					LocationService locationService = Context
							.getLocationService();
					LocationTag tag = locationService
							.getLocationTagByName(printerLocation);
					if (tag != null)
					{
						locationTagId = tag.getLocationTagId();
					}
				}
				
			}
		} catch (APIException e) {
			log.error("LocationTag api exception: " +  e.getMessage());
		} catch (Exception e){
			log.error(e.getMessage());
		}
		return locationTagId;
	}
	
	private String encodeForm(File file){
			String encodedForm = null;
			try {
				
				FileInputStream fis = new FileInputStream(file);			
				ByteArrayOutputStream bas = new ByteArrayOutputStream();
				
				int c;
				while((c = fis.read()) != -1){
				  bas.write(c);
				}
				encodedForm = Base64.byteArrayToBase64(bas.toByteArray(), false);

				fis.close();
				bas.flush();
				bas.close();	
				
				
			} catch (FileNotFoundException e) {
				log.error("Tiff file not found");
			} catch (IOException e) {
				log.error("Unable to read tiff file.");
			}
			
			return encodedForm;
		
		
	}
	
	public void saveMessageFile( String message, Integer encid, Date ackDate){
		saveMessageFile(message, encid, ackDate, false);
	}
	
	/**
	 * Saves message string to archive directory
	 * @param message
	 * @param encid
	 */
	public void saveMessageFile( String message, Integer encid, Date ackDate, boolean ISDH){
		AdministrationService adminService = Context.getAdministrationService();
		EncounterService es = Context.getService(EncounterService.class);

		
		org.openmrs.Encounter enc = es.getEncounter(encid);
		Patient patient = new Patient();
		patient = enc.getPatient();
		PatientIdentifier pi = patient.getPatientIdentifier();
		String mrn = "";
		String ack = "";
		String archiveDir = "";
		
		if (ISDH){
			ack = "-ISDH";
		}
		if (testMode){
			ack += "-TEST";
		}
			
		if (ackDate != null){
			ack += "-ACK";
		}
		
		if (pi != null) mrn = pi.getIdentifier();
		String filename =  org.openmrs.module.chirdlutil.util.Util.archiveStamp() + "_"+ mrn + ack + ".hl7";
		
		if (ISDH){
			archiveDir = IOUtil.formatDirectoryName(
					adminService.getGlobalProperty("Nbs.ISDHoutboundHl7ArchiveDirectory"));
		} else {
			archiveDir = IOUtil.formatDirectoryName(
					adminService.getGlobalProperty("Nbs.outboundHl7ArchiveDirectory"));
		}
	
		FileOutputStream archiveFile = null;
		try
		{
			archiveFile = new FileOutputStream(
					archiveDir + "/" + filename);
		} catch (FileNotFoundException e1)
		{
			log.error("Couldn't find file: "+archiveDir + "/" + filename);
		}
		if (archiveDir != null && archiveFile != null)
		{
			try
			{

				ByteArrayInputStream messageStream = new ByteArrayInputStream(
						message.getBytes());
				IOUtil.bufferedReadWrite(messageStream, archiveFile);
				archiveFile.flush();
				archiveFile.close();
			} catch (Exception e)
			{
				try
				{
					archiveFile.flush();
					archiveFile.close();
				} catch (Exception e1)
				{
				}
				log.error("There was an error writing the hl7 file");
				log.error(e.getMessage());
				log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}
		}
		return;
		
	}
	private Date sendMessage(String message, Encounter enc, 
			HL7SocketHandler socketHandler ){
		Date ackDate = null;
		
		HL7Outbound hl7b = new HL7Outbound();
		hl7b.setHl7Message(message);
		hl7b.setEncounter(enc);
		hl7b.setAckReceived(null);
		hl7b.setPort(port);
		hl7b.setHost(host);
		
		try {
			if (message != null) {
				hl7b = socketHandler.sendMessage(hl7b, socketReadTimeout);
				
				if (hl7b != null && hl7b.getAckReceived() != null){
					ackDate = hl7b.getAckReceived();
					 log.info("Ack received host:" + host + "; port:" + port 
							 + "- first try. Encounter_id = " + enc.getEncounterId());
				}
					
			}
			
		} catch (Exception e){
			log.error("Error exporting message host:" + host + "; port:" + port 
					+ " Encounter_id = " + enc.getEncounterId());
		}

	
		return ackDate;
	}
	
	private boolean sendPing(Encounter openmrsEncounter){
		
		
		AdministrationService adminService = Context.getAdministrationService();
		String configFileName = IOUtil.formatDirectoryName(adminService
				.getGlobalProperty("nbs.ping_config_file_location"));
		
		Properties hl7Properties = org.openmrs.module.nbs.util.Util.getProps(configFileName);
		
		if (hl7Properties == null ){
			return false;
		}
		
		String message = hl7Properties.getProperty("messageText");
		Date ackDate = null;
		if (message == null || message.trim().equals("")){
			return false;
		}
		
		ackDate = sendMessage(message, openmrsEncounter, socketHandler);
		if (ackDate == null){
			log.error("HL7 export ping failed.");
			return false;
		}
		log.error("HL7 export ping confirmed. " + message.substring(0, 50));
		saveMessageFile(message,openmrsEncounter.getEncounterId(), ackDate);
		
		return true;
	}
	
	
	private Properties getHl7Properties(NbsHL7Export nbsHl7Export){
		AdministrationService adminService = Context.getAdministrationService(); 
		ATDService atdService = Context.getService(ATDService.class);
		NbsService NbsService = Context.getService(NbsService.class);
		
		Integer hl7ExportQueueId = nbsHl7Export.getQueueId();
		FormInstance formInstance = this.getFormInstanceByExportQueueId(hl7ExportQueueId);
		Integer providerId = this.getProviderIdByExportQueueId(hl7ExportQueueId);
		
		
		//Get the hl7 config file
		String configFileName;
		if (formInstance == null ){
			
			configFileName = IOUtil.formatDirectoryName(adminService
					.getGlobalProperty("nbs.defaultHl7ConfigFileLocation"));
			
		}else {
			configFileName = getConfigFileLocation( formInstance.getFormId());
		}
		
		if (configFileName == null || configFileName.equalsIgnoreCase("") ) {
			nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("hl7_config_file_not_found"));
			NbsService.saveNbsHL7Export(nbsHl7Export);
			Integer sessionId = nbsHl7Export.getSessionId();
			ATDError ce = new ATDError("Error", "Hl7 Export", 
					"Hl7 export config file not found: " + configFileName, 
					null ,
					 new Date(), sessionId);
			atdService.saveError(ce);
			return null;
		}
			
		Properties hl7Properties = org.openmrs.module.nbs.util.Util.getProps(configFileName);
		if (hl7Properties ==null){
			nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("no_hl7_config_properties"));
			NbsService.saveNbsHL7Export(nbsHl7Export);
			Integer sessionId = nbsHl7Export.getSessionId();
			ATDError ce = new ATDError("Error", "Hl7 Export", 
					"HL7 config file found but no properties are present: " + configFileName, 
					null ,
					 new Date(), sessionId);
			atdService.saveError(ce);
		}
		
		
		
		return hl7Properties;
		
	}
	
	
	private String  getConfigFileName(NbsHL7Export nbsHl7Export){
		
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		NbsService NbsService = Context.getService(NbsService.class);
		Integer hl7ExportQueueId = nbsHl7Export.getQueueId();
		FormInstance formInstance = this.getFormInstanceByExportQueueId(hl7ExportQueueId);
		
		String configFileName;
		if (formInstance == null ){
			
			configFileName = IOUtil.formatDirectoryName(adminService
					.getGlobalProperty("nbs.defaultHl7ConfigFileLocation"));
			
		}else {
			configFileName = getConfigFileLocation( formInstance.getFormId());
		}
		
		if (configFileName == null || configFileName.equalsIgnoreCase("") ) {
			nbsHl7Export.setStatus(NbsService.getNbsExportStatusByName("hl7_config_file_not_found"));
			NbsService.saveNbsHL7Export(nbsHl7Export);
			Integer sessionId = nbsHl7Export.getSessionId();
			ATDError ce = new ATDError("Error", "Hl7 Export", 
					"Hl7 export config file not found: " + configFileName, 
					null ,
					 new Date(), sessionId);
			atdService.saveError(ce);
			return null;
		}
		return configFileName;
		
	}
	/**
	 * @see org.openmrs.scheduler.Task#shutdown()
	 */
	@Override
	public void shutdown() {
		taskDefinition = null;
		shutdown = true;
		
	}
	
	
}
