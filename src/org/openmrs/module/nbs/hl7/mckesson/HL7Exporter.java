package org.openmrs.module.nbs.hl7.mckesson;

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
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.ConceptSource;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
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
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7Export;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMap;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.nbs.util.Util;
import org.openmrs.module.sockethl7listener.HL7MessageConstructor;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ca.uhn.hl7v2.model.v25.segment.OBX;



/**
 * Determines which encounters require an hl7 to be exported to RMRS, 
 * and creates the outbound hl7 messages to send on the specified port.
 * @author msheley
 *
 */
public class HL7Exporter extends AbstractTask {

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
		
		NbsService nbsService = Context.getService(NbsService.class);
		ATDService atdService = Context.getService(ATDService.class);
		EncounterService encounterService = Context.getService(EncounterService.class);
		AdministrationService adminService = Context.getAdministrationService();
		socketHandler = new HL7SocketHandler();
		
		String conceptCategory = "";
		NbsHL7Export export = new NbsHL7Export();
		Context.openSession();
		
		try
		{
			
			if (Context.isAuthenticated() == false)
				authenticate();

			
			
			//get list of pending exports
			List <NbsHL7Export> exportList = nbsService.getPendingHL7Exports();
			Iterator <NbsHL7Export> it = exportList.iterator();
		
			while (it.hasNext()){
				
				
				socketHandler.openSocket(host, port);
				//add socketReadTimeout to scheduler
				
				export = it.next();
			
				//Get location of hl7 configuration file from location_tag_attribute_value
				Integer encId = export.getEncounterId();
				Integer hl7ExportQueueId = export.getQueueId();
				
				Integer formId = getFormIdByExportQueueId(hl7ExportQueueId);
			
				
				//Get the hl7 config file
				String configFileName;
				if (formId == null){
					
					configFileName = IOUtil.formatDirectoryName(adminService
							.getGlobalProperty("nbs.defaultHl7ConfigFileLocation"));
					
				}else {
					configFileName = getConfigFileLocation( formId);
				}
				if (configFileName == null || configFileName.equalsIgnoreCase("") ) {
					export.setStatus(nbsService.getNbsExportStatusByName("hl7_config_file_not_found"));
					nbsService.saveNbsHL7Export(export);
					continue;
				}
				
				
				//Construct the message
				
				Properties hl7Properties = Util.getProps(configFileName);
				if (hl7Properties ==null){
					export.setStatus(nbsService.getNbsExportStatusByName("no_hl7_config_properties"));
					nbsService.saveNbsHL7Export(export);
					continue;
				}
				
				
				
				int numberOfOBXSegments = 0;
				boolean sendObs = false;
			
				Encounter openmrsEncounter = (Encounter) encounterService.getEncounter(encId);
				
				org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor constructor = 
					new org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor(configFileName, null );
				
			
				List<Encounter> queryEncounterList = new ArrayList<Encounter>();
				queryEncounterList.add(openmrsEncounter);
			
				constructor.AddSegmentMSH(openmrsEncounter);
				Patient patient = openmrsEncounter.getPatient();
				if (patient == null){
					return;
				}
				PatientIdentifier identifier = patient.getPatientIdentifier();
				constructor.AddSegmentPID(openmrsEncounter.getPatient());
				constructor.AddSegmentPV1(openmrsEncounter);
				constructor.setAssignAuthority(identifier);
				
			
				if (!addOBXForTiff(constructor, openmrsEncounter , formId, null, 
							hl7Properties)){
						export.setStatus(nbsService.getNbsExportStatusByName("Image_not_found"));
						nbsService.saveNbsHL7Export(export);
						continue;
				}
					
				//Send the message
				String message = constructor.getMessage();
				export.setStatus(nbsService.getNbsExportStatusByName("hl7_sent"));
				export.setDateProcessed(new Date());
					
				if (message != null && !message.equals("")){
					Date ackDate = sendMessage(message, openmrsEncounter, socketHandler);
					if (ackDate != null) { 
						export.setStatus(nbsService.getNbsExportStatusByName("ACK_received"));
						export.setAckDate(ackDate);
						
					}	else {
						export.setStatus(nbsService.getNbsExportStatusByName("ACK_not_received"));
					}
					saveMessageFile(message,encId, ackDate);
				}
				 
				nbsService.saveNbsHL7Export(export);
				
			}
			
	    
		} catch (IOException e ){
			log.error("Error opening socket:" + e.getMessage());
			
		}catch (Exception e)
		
		{
			Integer sessionId = export.getSessionId();
			String message = e.getMessage();
			ATDError ce = new ATDError("Error", "Hl7 Export", 
					message, "Error creating hl7 export: " + e.getMessage(),
					 new Date(), sessionId);
			atdService.saveError(ce);
			
		} finally
		{
			socketHandler.closeSocket();
			Context.closeSession();
		}

	}
	/**
	 * Saves message string to archive directory
	 * @param message
	 * @param encid
	 */
	public void saveMessageFile( String message, Integer encid, Date ackDate){
		AdministrationService adminService = Context.getAdministrationService();
		EncounterService es = Context.getService(EncounterService.class);

		
		org.openmrs.Encounter enc = es.getEncounter(encid);
		Patient patient = new Patient();
		patient = enc.getPatient();
		PatientIdentifier pi = patient.getPatientIdentifier();
		String mrn = "";
		String ack = "";
		if (ackDate != null){
			ack = "-ACK";
		}
		
		if (pi != null) mrn = pi.getIdentifier();
		String filename =  org.openmrs.module.chirdlutil.util.Util.archiveStamp() + "_"+ mrn + ack + ".hl7";
		
		String archiveDir = IOUtil.formatDirectoryName(adminService
				.getGlobalProperty("nbs.outboundHl7ArchiveDirectory"));
		

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
	
	
	private Concept getRMRSConceptByName(String rmrsname){
		
		ConceptService cs = Context.getConceptService();
		Concept rmrsConcept = null;
		
		List<ConceptClass> classList = new ArrayList<ConceptClass>();
		try {
			ConceptClass rmrsClass = cs.getConceptClassByName("RMRS");
			classList.add(rmrsClass);
			
			List<Concept> conceptsWithSameName = cs.getConcepts(rmrsname, 
				Context.getLocale(), false, classList, null);
			
			if (conceptsWithSameName != null && conceptsWithSameName.size() >0){
				if (conceptsWithSameName.size()>1){
					log.error("More than one RMRS concept exist with exact name of " 
							+ rmrsname);
				}
				rmrsConcept = conceptsWithSameName.get(0);
			} else {
				log.error("No RMRS class concepts found with exact name of " + rmrsname);
			}
		} catch (APIException e) {
			log.error("ConceptClass api exception." + e.getMessage());
			
		} catch (Exception e){
			log.error( e.getMessage());
		}
		
		return rmrsConcept;
	}
	
	private String getRMRSCodeFromConcept(Concept rmrsConcept){
		String rmrsCode = "";
		//get the mappings
		//get the source
		
		Iterator<ConceptMap> mapsIter = rmrsConcept.getConceptMappings().iterator(); 
		 if  (mapsIter.hasNext()){
			 rmrsCode = mapsIter.next().getSourceCode(); 
		 }
		 return rmrsCode;
	}
	
	private String getUnits(Concept concept){
		String units = "";
		ConceptService cs = Context.getConceptService();
		ConceptDatatype datatype = concept.getDatatype();
		Integer conceptId = concept.getConceptId();
		if (datatype != null && datatype.isNumeric()){
			ConceptNumeric rmrsNumericConcept =(ConceptNumeric) cs.getConcept(conceptId);
			units = rmrsNumericConcept.getUnits();
		}
		return units;
	}
	
	
	private boolean checkConceptSet(Concept conceptToTest, String batteryName){
		boolean match = false;
		ConceptService cs = Context.getConceptService();
		
		if (conceptToTest == null || batteryName == null || batteryName.equals("")){
			return false;
		}
		
		List<Concept> conceptsWithBatteryName = new ArrayList<Concept>();
		conceptsWithBatteryName = cs.getConcepts(batteryName + " nbs", 
				 Context.getLocale(), false, null, null);
		
		Concept batteryConcept = new Concept();
		for (Concept concept : conceptsWithBatteryName){
			if (concept.isSet()){
				batteryConcept = concept;
				continue;
			}
		}
		Collection<ConceptSet> allConceptsInBattery = cs.getConceptSetsByConcept(batteryConcept);
		for (ConceptSet concept : allConceptsInBattery){
			
			if ( conceptToTest.getConceptId().equals(concept.getConcept().getConceptId())){
				match = true;
				continue;
			}
		
		}
			
		return match;
	
	}
	
	private Hashtable<String,String> loadHashTable( Document doc){
		
		String nbsConceptName = null;
		String rmrsConceptName = null;
		NodeList nbsNodes = null;
		if (doc != null){
			nbsNodes = doc.getElementsByTagName("nbs");
		}
		Hashtable<String,String> conceptMapping = new Hashtable<String,String>();
		
		 for (int i=0; i<nbsNodes.getLength(); i++) {
			 Node nbsTextNode = nbsNodes.item(i).getFirstChild();
			 if (nbsTextNode != null){
				 nbsConceptName = nbsTextNode.getNodeValue();
			 }
			 Node rmrsNode = nbsNodes.item(i).getNextSibling();
			 
		    if (rmrsNode!= null) {
		    	Node rmrsTextNode = rmrsNode.getFirstChild();
		    	if (rmrsTextNode != null) {
		    		rmrsConceptName = rmrsTextNode.getNodeValue();
		    	}
		    }
		    conceptMapping.put(nbsConceptName, rmrsConceptName);
		    
		 }
		 return conceptMapping;
	}
	private List<Obs> getObsListByBattery(Encounter encounter, Hashtable<String,String> table, 
			String batteryName ){
		
		ConceptService conceptService = Context.getConceptService();
		ObsService obsService = Context.getObsService();
		List<Concept> concepts = new ArrayList<Concept>();
		List<Obs> obsList = null;
		
		try {
			
			Enumeration<String> en = table.keys();
			while (en.hasMoreElements()){
				String nbsConceptName = (String )en.nextElement();
			    Concept nbsConcept = conceptService.getConceptByName(nbsConceptName);
			    if (nbsConcept != null &&
			    		checkConceptSet(nbsConcept,batteryName)) {
			    	concepts.add(nbsConcept);
			    }
			    
			 }
			
			if (concepts.size()>0){
				 List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();	
				 encounters.add(encounter);
				   
				 obsList = obsService.getObservations(null, encounters,
							concepts, null, null, null, null, null, null, null, null, false);		 
			}
		}catch (Exception e){
			log.error("Error collecting obs list from concept map.");
		}
			
		return obsList;

	}
	private int addOBXBlock(HL7MessageConstructor constructor,
			Encounter encounter, List<Obs> obsList, Hashtable<String,String> mappings
			, String batteryName, int orderRep){
		//Get all obs for one encounter, where the concept is in the mapping properties xml
		//If an obs for that concept does not exist for an encounter, we do not create an OBX
			
		Locale locale = new Locale("en_US");
		String units = "";
		String rmrsCode = "";
		String hl7Abbreviation = "";
		ConceptDatatype conceptDatatype = null;
		int obsRep = 0;
		addOBRSegment(constructor, encounter,  batteryName, orderRep );

		for (Obs obs : obsList){
			Concept nbsConcept = obs.getConcept();
			ConceptName cname = nbsConcept.getName(locale);
			if (cname != null){
				String rmrsName = mappings.get(cname.getName());
				if (rmrsName == null) continue;
				Concept rmrsConcept = getRMRSConceptByName(rmrsName);
				if (rmrsConcept != null){
					rmrsCode = getRMRSCodeFromConcept(rmrsConcept);
				}
				String value = obs.getValueAsString(locale);
				

				if (nbsConcept.isNumeric()  ){
					Double obsRounded =
						org.openmrs.module.chirdlutil.util.Util
						.round(Double.valueOf(obs.getValueNumeric()), 1);
					if (obsRounded != null){
						value = String.valueOf(obsRounded);
					}
				}
				conceptDatatype = rmrsConcept.getDatatype();
				String sourceCode = "";
				
				if (conceptDatatype != null && conceptDatatype.isCoded()){
					Concept answer = obs.getValueCoded();
					
					Collection<ConceptMap> maps = answer.getConceptMappings();
					ConceptMap map = null;
					if (maps != null){
						Iterator<ConceptMap> it = maps.iterator();
						if (it.hasNext()){
							//get first
							map = it.next();
						}
						if (map != null) {
							sourceCode = map.getSourceCode();
						}
					}
					
				}
				hl7Abbreviation = conceptDatatype.getHl7Abbreviation();
				units = getUnits(rmrsConcept);
				Date datetime = obs.getObsDatetime();
				constructor.AddSegmentOBX(rmrsName, rmrsCode, 
						null, sourceCode, value, units, datetime, hl7Abbreviation, orderRep, obsRep);
				obsRep++;
				
				
			}
		
		}
			
		return obsRep;
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
					+ "- first try. Encounter_id = " + enc.getEncounterId());
			try {
				if (message != null) {
					
					hl7b = socketHandler.sendMessage(hl7b, socketReadTimeout);
					 if (hl7b != null && hl7b.getAckReceived() != null){
						 ackDate = hl7b.getAckReceived();
						 log.info("Ack received host:" + host + "; port:" + port 
								 + "- second try. Encounter_id = " + enc.getEncounterId());
					 }
				}
			} catch (Exception e1) {
				log.error("Error exporting message host:" + host + "; port:" + port 
					+ "- second try. Encounter_id = " + enc.getEncounterId());
			}
		}
		return ackDate;
	}
	
	private Integer getFormIdByExportQueueId(Integer queueId){
		Integer formId = null;
		NbsService nbsService = Context.getService(NbsService.class);
		NbsHL7ExportMap exportmap =  nbsService.getNbsExportMapByQueueId(queueId, null);
		if (exportmap == null){
			return null;
		}
		
		String formIdString = exportmap.getValue();
		if (formIdString == null){
			return null;
		}
		formId = Integer.valueOf(formIdString);
		
		return formId;
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
	
	private String getBatteryIdByConcept(Concept batteryConcept){
		
	
				String sourceCode = "";
				Collection<ConceptMap> maps = batteryConcept.getConceptMappings();
				Iterator<ConceptMap>  it = maps.iterator();
				if (it.hasNext()){
					sourceCode = it.next().getSourceCode();
				}
				return sourceCode;
		
	}
	
	
/*	private String getnbsExportConceptMapByQueueId(Integer hl7ExportQueueId){
		String filename = null;
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);
		nbsService nbsService = Context.getService(NbsService.class);
		NbsHL7ExportMap exportmap =  nbsService.getnbsExportMapByQueueId(hl7ExportQueueId);
		ATDService atdService = Context.getService(ATDService.class);
		atdService.getFormAttributeValue(formId, formAttributeName, locationTagId, locationId)
		
		if (exportmap == null){
			return null;
		}
		
		String locationTagAttributeIdStr = exportmap.getValue();
		if ( locationTagAttributeIdStr != null){
			Integer locationTagAttributeId = Integer.valueOf(locationTagAttributeIdStr);
			LocationTagAttributeValue value =  
				chirdlUtilService.getLocationTagAttributeValueById(locationTagAttributeId);
			if (value != null) {
				filename = value.getValue();
			}
		}
		
		
		
		return filename;
	}
	*/
	
	
	
	private Document getDocument(String conceptMapFile){
		File file = new File(conceptMapFile);
		Document doc = null;
		if(file.exists()){
		     try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.parse(file);
					
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
			} catch (ParserConfigurationException e) {
				
				log.error("Unable to parse XML map file");
			} catch (SAXException e) {
				
				log.error("SAX handler exeception when parsing XML map file");
			}
		}
		return doc;
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
	
	private void addOBRSegment(HL7MessageConstructor constructor, 
			Encounter openmrsEncounter,  String batteryName, int orderRep){
		
		ConceptService cs = Context.getConceptService();
	
		String batteryId = null;
		
		if (batteryName != null){
			Concept batteryConcept = cs.getConceptByName(batteryName);
			batteryId = getBatteryIdByConcept(batteryConcept);
		}
		
		constructor.AddSegmentOBR(openmrsEncounter, batteryId, batteryName, orderRep);
		
	}
	
	private Concept createConcept(String attachmentBatteryName, String attachmentBatteryCode){
		Concept concept = new Concept();
		ConceptService conceptService = Context.getConceptService();
		ConceptClass conceptClass = conceptService.getConceptClassByName("RMRS");
		ConceptName conceptName = new ConceptName(attachmentBatteryName, new Locale("ENGLISH"));
		ConceptDatatype conceptDatatype = 
			conceptService.getConceptDatatypeByName("Text");
		concept.setConceptClass(conceptClass);
		concept.addName(conceptName);
		concept.setDatatype(conceptDatatype);
		concept.setSet(false);
		
		Concept newConcept = conceptService.saveConcept(concept);
		List<ConceptSource>  sources = conceptService.getAllConceptSources();
		
		
		return newConcept;
	}
	
	
		
}
