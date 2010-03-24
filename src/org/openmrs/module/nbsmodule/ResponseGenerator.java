package org.openmrs.module.nbsmodule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.Base64;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.nbsmodule.util.Util;
import org.openmrs.module.sockethl7listener.HL7MessageConstructor;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;


/**
 * Determines which encounters require a response hl7 to RMRS, 
 * and creates the out-bound hl7 messages to send on the specified port.
 * @author msheley
 *
 */
public class ResponseGenerator extends AbstractTask {

	private Log log = LogFactory.getLog(this.getClass());
	private TaskDefinition taskConfig;
	private String host;
	private Integer port;
	private Integer socketReadTimeout;
	private HL7SocketHandler socketHandler;
	
	
	@Override
	public void initialize(TaskDefinition config)
	{
		Context.openSession();
		
		try {
			if (Context.isAuthenticated() == false)
				authenticate();
			
			this.taskConfig = config;
			//port to export
			String portName = this.taskConfig.getProperty("port");
			host  = this.taskConfig.getProperty("host");
			String socketReadTimeoutString  = this.taskConfig.getProperty("socketReadTimeout");
			
			if (host == null){
				host = "localhost";
			}
			
			if (portName != null){
				port = Integer.parseInt(portName);
			} else
			{
				port = 0;
			}
			if (socketReadTimeoutString != null){
				socketReadTimeout = Integer.parseInt(socketReadTimeoutString);
			} else {
				socketReadTimeout = 5; //seconds
			}
			
		}finally{
			
			Context.closeSession();
		}

	}

	/* 
	 * Executes loading the queue table for sessions requiring hl7 responses
	 * Constructs the hl7 messages, sends the message, and saves text to a file
	 */
	@Override
	public void execute()
	{
		
		Context.openSession();
		socketHandler = new HL7SocketHandler();
		
		try
		{
			if (Context.isAuthenticated() == false)
				authenticate();

			
			NBSService nbsService = Context.getService(NBSService.class);
			List<NBSModuleResponse> nbsAlerts = nbsService.captureNBSAlerts(null, null, null, null, null);
			if (nbsAlerts.size()> 0){
				socketHandler.openSocket(host, port);
				processAlerts(nbsAlerts);
			}
			
	       
		} catch (Exception e){
			log.error(e.getMessage());	
			
		} finally
		{
			socketHandler.closeSocket();
			Context.closeSession();
		}

	}
	
	

	@Override
	public void shutdown()
	{
		super.shutdown();
		try
		{
			//this.server.stop();
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
	
	
	/**
	 * Loops through alerts and constructs hl7 message for each
	 * @param alerts Pending list of alerts to be sent to providers
	 */
	public void  processAlerts(List<NBSModuleResponse> alerts)
	{
		NBSService nbsService = Context.getService(NBSService.class);
		String rmrsName = "";
		String rmrsId = "";
		String subType = "";
		String units = "";
		String message = "";
		String hl7ConfigFile = null;
		boolean doReadAck = true;
		String interval = "";

		ChirdlUtilService cuService = Context.getService(ChirdlUtilService.class);
		LocationAttributeValue hl7ConfigAttrValue = cuService.getLocationAttributeValue(1,"hl7ConfigFile" );
		if (hl7ConfigAttrValue != null) {
			hl7ConfigFile = hl7ConfigAttrValue.getValue();
		}
		
		
		if (hl7ConfigFile != null){
			Properties hl7Properties = Util.getProps(hl7ConfigFile);
			if (hl7Properties != null){
				rmrsName = hl7Properties.getProperty("univ_serv_id_name");
				rmrsId = hl7Properties.getProperty("univ_serv_id");
				subType = hl7Properties.getProperty("OBX_sub_data_type");
				String readAck = hl7Properties.getProperty("doReadAck");
				interval = hl7Properties.getProperty("interval");
				if (readAck == null || readAck.equals("") || readAck.equalsIgnoreCase("true")){
					doReadAck = true;
				}
				else doReadAck = false;
			}
		} 
		
		
		try
		{
			for(NBSModuleResponse currAlert:alerts)
			{
				
				Encounter encounter = currAlert.getEncounter();
				String mrn = currAlert.getMrn();
				if (mrn == null){
					log.error("Null value for mrn in nbs_alert table.");
					break;
				}
			    Integer encounter_id = encounter.getEncounterId();
				Integer formId = currAlert.getFormId();
				Patient patient = currAlert.getPatient();
				
				FormInstance formInstance = new FormInstance();
				formInstance.setFormId(formId);
				formInstance.setFormInstanceId(currAlert.getFormInstanceId());
				formInstance.setLocationId(encounter.getLocation().getId());
				
				String encodedForm;
				
				encodedForm = encodeForm(formInstance);
				if (encodedForm == null){
					Integer status = nbsService.getAlertStatusIdByName("image_not_found");
					currAlert.setStatus(status);
					log.error("The form does not exist in image directory, so no message created");
					continue;
				}
				
				
				HL7MessageConstructor hl7mc = new HL7MessageConstructor(hl7ConfigFile);
				hl7mc.AddSegmentMSH(encounter);
				hl7mc.AddSegmentPID(patient);
				hl7mc.AddSegmentPV1(encounter);
				hl7mc.AddSegmentOBR(encounter, null, null, 0);
				hl7mc.AddSegmentOBX(rmrsName, rmrsId, subType, rmrsId, 
						encodedForm, units, currAlert.getDateCreated(), "ED", 0, 0);
				
				
				
				HL7Outbound hl7b = new HL7Outbound();
				hl7b.setHl7Message(hl7mc.getMessage());
				hl7b.setEncounter(encounter);
				hl7b.setAckReceived(null);
				hl7b.setPort(port);
				hl7b.setHost(host);
				
				Integer status = nbsService.getAlertStatusIdByName("waiting_for_ack");
				currAlert.setStatus(status);
				nbsService.updateNBSModuleResponse(currAlert);
				
				HL7Outbound out = socketHandler.sendMessage(hl7b, socketReadTimeout);
				Date ackDate = null;

				if (out != null && out.getAckReceived() != null){
					ackDate = hl7b.getAckReceived();
					 log.info("Ack received host:" + host + "; port:" + port 
							 + "- first try. Encounter_id = " + encounter.getEncounterId());
					 status = nbsService.getAlertStatusIdByName("ack_received");
						currAlert.setStatus(status);
				}
				saveMessage(hl7mc.getMessage(), encounter_id, ackDate);
			    nbsService.updateNBSModuleResponse(currAlert);
			    if (interval != null && !interval.equals("") && !interval.equals("0")){
			    	Thread.sleep(Integer.valueOf(interval)*1000);
			    }

					
			}
				
			

	        	
		} catch (HibernateException e)
		{
			log.error(Util.getStackTrace(e));
		} catch (IOException ioe ){
			log.error("Exception sending alert message . " +  Util.getStackTrace(ioe));
		} catch (Exception e){
			log.error("Process alert exception" + Util.getStackTrace(e));
		}
		
		return;
	}
	
	/**
	 * Finds the form for the specific alert and encodes with Base64
	 * @param alert_id Identifies which provider alert
	 * @param form_id  Identifies which form
	 * @return  encoded message string
	 */
	private String encodeForm(FormInstance formInstance)  {
	
		AdministrationService adminService = Context.getAdministrationService();
		String strenc = null;
		String fileType = adminService
		   .getGlobalProperty("sockethl7listener.exportFileType");
		
		if (fileType == null || fileType.equals("")){
			log.info("File type not specified in global property. Using tif for default.");
			fileType = "tif";
		}
		
		ATDService atdService = Context.getService(ATDService.class);
		FormAttributeValue directoryAttr = atdService.getFormAttributeValue(formInstance.getFormId(), "defaultImageDirectory",1,1);
		String dir = directoryAttr.getValue();
		String formName = "";
		if (formInstance != null) {
			formName = formInstance.toString().replace("_", "~");
		}
		
		String file = dir + "//" +  formName + "." + fileType;
		
		try {
			FileInputStream fis = new FileInputStream(file);			
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			
			int c;
			while((c = fis.read()) != -1){
			  bas.write(c);
			}
		
			strenc = Base64.byteArrayToBase64(bas.toByteArray(),false);
			if (strenc == null ) return null;
		
			
			fis.close();
			bas.flush();
			bas.close();	
		
			
		} catch (FileNotFoundException e) {
			log.error("Image file " + "'" + file + "' " + " not found. ");
			
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch(RuntimeException e){
			log.error("Exception while encoding tif. ",e);
		}
		
		return strenc;
	}
	
	
	/**
	 * Saves message string to archive directory
	 * @param message The hl7 message string.
	 * @param encid  Identifies the encounter that triggers the alert.
	 */
	public void saveMessage( String message, Integer encid, Date ackDate){
		AdministrationService adminService = Context.getAdministrationService();
		EncounterService es = Context.getService(EncounterService.class);
		String archiveDir = "";
		String filename = "";
		String ack = "";
		try {
			Encounter enc = es.getEncounter(encid);
			Patient patient = new Patient();
			patient = enc.getPatient();
			PatientIdentifier pi = patient.getPatientIdentifier();
			String mrn = "";
			if (ackDate != null){
				ack = "-ACK";
			}
		
			if (pi != null) mrn = pi.getIdentifier();
			 filename =  Util.archiveStamp() + "_"+ mrn + ack + ".hl7";
		
			archiveDir = IOUtil.formatDirectoryName(adminService
				.getGlobalProperty("nbsmodule.outboundHl7ArchiveDirectory"));
		

			FileOutputStream archiveFile = null;
		
			archiveFile = new FileOutputStream(
					archiveDir + "/" + filename);
		
			if (archiveDir != null && archiveFile != null)
			{
			
				ByteArrayInputStream messageStream = new ByteArrayInputStream(
						message.getBytes());
				IOUtil.bufferedReadWrite(messageStream, archiveFile);
				archiveFile.flush();
				archiveFile.close();
				
			}
			else log.error("Couldn't find file: "+archiveDir + "/" + filename);
			
		} catch (Exception e){
			log.error(Util.getStackTrace(e));
		}
		return;
		
	}
	
	
}
