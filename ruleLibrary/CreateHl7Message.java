/**
 * 
 */
package org.openmrs.module.nbs.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.nbs.NbsStateActionHandler;
import org.openmrs.module.nbs.hibernateBeans.Encounter;
import org.openmrs.module.nbs.service.EncounterService;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.nbs.util.Util;
import org.openmrs.module.sockethl7listener.HL7MessageConstructor;

/**
 * @author msheley
 *
 */
public class CreateHl7Message implements Rule {

	
	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * 
	 */
	public CreateHl7Message() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient, java.util.Map)
	 */
	public Result eval(LogicContext context, Patient patient,
		Map<String, Object> parameters) throws LogicException {
		
		NbsService nbsService = Context.getService(NbsService.class);
		EncounterService encounterService = Context.getService(EncounterService.class);
		ConceptService conceptService = Context.getService(ConceptService.class);
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		
		
		String answer = (String) parameters.get("param1");
		if (answer == null){
			String message = "Answer parameter not found. ";
			logError(atdService, null, message, null);
			return null;
		}
		
		Integer sessionId = (Integer) parameters.get("sessionId");
		Integer locationTagId = (Integer) parameters.get("locationTagId"); 
		
		if (patient == null){
			return null;
		}
		
		//Get the hl7 config file
		String configFileName = IOUtil.formatDirectoryName(adminService
					.getGlobalProperty("nbs.InpcHl7ConfigFileLocation"));
			
		
		if (configFileName == null || configFileName.equalsIgnoreCase("") ) {
			
			String message = "Global property for feedback hl7 is not set : " + configFileName;
			logError(atdService, sessionId, message, null);
			return null;
		}
		
		
		//Construct the message
		
		Properties hl7Properties = Util.getProps(configFileName);
		
		if (hl7Properties ==null){
			String message = "No properties found for file " + configFileName;
			logError(atdService, sessionId, message, null);
			return null;
		}
		
		
		
		int numberOfOBXSegments = 0;
		boolean sendObs = false;
	
		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		
		org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor constructor = 
			new org.openmrs.module.nbs.hl7.mckesson.HL7MessageConstructor(configFileName, null );
		
	
		constructor.AddSegmentMSH(encounter);
		PatientIdentifier identifier = patient.getPatientIdentifier();
		constructor.AddSegmentPID(patient);
		constructor.AddSegmentPV1(encounter);
		constructor.setAssignAuthority(identifier);
		//this.addOBXBlock(constructor, encounter, obsList, mappings, batteryName, orderRep)
		constructor.AddSegmentOBR(encounter, null, null, 0);
		//constructor.AddSegmentOBX(answer, 0, 0, concept.getc, concept.get, units, datetime, hl7Abbreviation, orderRep, obsRep)
		return Result.emptyResult();
		
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
		
	}



	/* (non-Javadoc)
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	private void logError(ATDService atdService, Integer sessionId, String message, Throwable e) {
		log.error("Error auto-faxing form");
		log.error(message);
		ATDError atdError = new ATDError("Error", "General Error", message, null, new Date(), sessionId);
		atdService.saveError(atdError);
	}

}
