package org.openmrs.module.nbs.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.nbs.Percentile;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7Export;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMap;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportStatus;
import org.openmrs.module.nbs.hibernateBeans.OldRule;
import org.openmrs.module.nbs.hibernateBeans.Study;
import org.openmrs.module.nbs.hibernateBeans.StudyAttributeValue;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface NbsService
{
	public void consume(InputStream input, Patient patient,
			Integer encounterId,FormInstance formInstance,Integer sessionId,
			List<FormField> fieldsToConsume,Integer locationTagId);

	public void produce(OutputStream output, PatientState state,
			Patient patient,Integer encounterId,String dssType,
			int maxDssElements,Integer sessionId);

	public List<Study> getActiveStudies();

	public List<Statistics> getStatByFormInstance(int formInstanceId,String formName,
			Integer locationId);

	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName);

	public List<Statistics> getStatByIdAndRule(int formInstanceId,int ruleId,String formName,
			Integer locationId);
	
	public List<String> getInsCategories();
	
	public String getObsvNameByObsvId(String obsvId);
	
	public String getInsCategoryByCarrier(String carrierCode);

	public String getInsCategoryBySMS(String smsCode);
	
	public String getInsCategoryByInsCode(String insCode);
	
	public PatientState getPrevProducePatientState(Integer sessionId, Integer patientStateId );
	
	public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName);

	public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName);
	
	public NbsHL7Export insertEncounterToHL7ExportQueue(NbsHL7Export export);

	public List<NbsHL7Export> getPendingHL7Exports();
	
	public void saveNbsHL7Export(NbsHL7Export export);
	
	public List<NbsHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId);
	
	/**
	 * @param patientId
	 * @param optionalDateRestrictio
	 * 
	 * Search patient states to determine if a reprint has ever been performed during that
	 * session.
	 * 
	 * @return
	 */
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, Integer locationTagId, Integer locationId);
	
	/**
	 * Gets a list of the printer stations for PSF
	 * @return List of form attributes
	 */
	public List<String> getPrinterStations(Location location);
	
	public void  saveHL7ExportMap (NbsHL7ExportMap map);
	
	public NbsHL7ExportMap getNbsExportMapByQueueId(Integer queue_id);
	
	public NbsHL7ExportStatus getNbsExportStatusByName (String name);
	
	public NbsHL7ExportStatus getNbsExportStatusById (Integer id);
	
}