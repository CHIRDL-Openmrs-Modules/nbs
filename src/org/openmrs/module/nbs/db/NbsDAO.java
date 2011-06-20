package org.openmrs.module.nbs.db;


import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7Export;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMap;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportStatus;
import org.openmrs.module.nbs.hibernateBeans.OldRule;
import org.openmrs.module.nbs.hibernateBeans.Study;
import org.openmrs.module.nbs.hibernateBeans.StudyAttributeValue;


/**
 * Nbs-related database functions
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
//@Transactional
public interface NbsDAO {

	public void addStatistics(Statistics statistics);
	
	public void updateStatistics(Statistics statistics);
	
	public List<Study> getActiveStudies();
	
	public List<Statistics> getStatByFormInstance(int formInstanceId,String formName, Integer locationId);

	public List<Statistics> getStatByIdAndRule(int formInstanceId,int ruleId,String formName, Integer locationId);
		
	public StudyAttributeValue getStudyAttributeValue(Study study,String studyAttributeName);

	public List<OldRule> getAllOldRules();

	public List<String> getInsCategories();
	
	public String getObsvNameByObsvId(String obsvId);
	
	public String getInsCategoryByCarrier(String carrierCode);
	
	public String getInsCategoryBySMS(String smsCode);
	
	public String getInsCategoryByInsCode(String insCode);
	
	public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName);

	public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName);
	
	public NbsHL7Export insertEncounterToHL7ExportQueue(NbsHL7Export export);

	public List<NbsHL7Export> getPendingHL7Exports();
	
	public void saveNbsHL7Export(NbsHL7Export export);
	
	public List<NbsHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId);
	
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
			Integer locationTagId,Integer locationId);
		
	/** Insert queued hl7 export to map table
	 * @param map
	 * @return
	 */
	public void  saveHL7ExportMap (NbsHL7ExportMap map);
	
	public NbsHL7ExportMap getNbsExportMapByQueueId(Integer queue_id);
	
	public NbsHL7ExportStatus getNbsExportStatusByName (String name);
	
	public NbsHL7ExportStatus getNbsExportStatusById (Integer id);

}
