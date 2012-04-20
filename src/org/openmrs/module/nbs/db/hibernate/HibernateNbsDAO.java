package org.openmrs.module.nbs.db.hibernate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.nbs.db.NbsDAO;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7Export;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMap;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportMapType;
import org.openmrs.module.nbs.hibernateBeans.NbsHL7ExportStatus;
import org.openmrs.module.nbs.hibernateBeans.OldRule;
import org.openmrs.module.nbs.hibernateBeans.Study;
import org.openmrs.module.nbs.hibernateBeans.StudyAttribute;
import org.openmrs.module.nbs.hibernateBeans.StudyAttributeValue;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of Nbs database methods.
 * 
 * @author Tammy Dugan
 * 
 */
@Transactional
public class HibernateNbsDAO implements NbsDAO
{

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * 
	 */
	public HibernateNbsDAO()
	{
	}

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public void addStatistics(Statistics statistics)
	{
		try
		{
			this.sessionFactory.getCurrentSession().save(statistics);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public void updateStatistics(Statistics statistics)
	{
		try
		{
			this.sessionFactory.getCurrentSession().update(statistics);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public List<Statistics> getStatByFormInstance(int formInstanceId,
			String formName, Integer locationId)
	{
		try
		{
			String sql = "select * from atd_statistics where form_instance_id=? and form_name=? "+
			"and location_id=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, formInstanceId);
			qry.setString(1, formName);
			qry.setInteger(2,locationId);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<OldRule> getAllOldRules()
	{
		try
		{
			String sql = "select * from Nbs_old_rule";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addEntity(OldRule.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Statistics> getStatByIdAndRule(int formInstanceId, int ruleId,
			String formName, Integer locationId)
	{
		try
		{
			String sql = "select * from Nbs_statistics where form_instance_id=? "+
			"and rule_id=? and form_name=? and location_id=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, formInstanceId);
			qry.setInteger(1, ruleId);
			qry.setString(2, formName);
			qry.setInteger(3,locationId);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Study> getActiveStudies()
	{
		try
		{
			String sql = "select * from Nbs_study where status=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, 1);
			qry.addEntity(Study.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	private StudyAttribute getStudyAttributeByName(String studyAttributeName)
	{
		try
		{
			String sql = "select * from Nbs_study_attribute "
					+ "where name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, studyAttributeName);
			qry.addEntity(StudyAttribute.class);

			List<StudyAttribute> list = qry.list();

			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}
			return null;
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName)
	{
		try
		{
			StudyAttribute studyAttribute = this
					.getStudyAttributeByName(studyAttributeName);

			if (study != null && studyAttribute != null)
			{
				Integer studyId = study.getStudyId();
				Integer studyAttributeId = studyAttribute.getStudyAttributeId();

				String sql = "select * from Nbs_study_attribute_value where study_id=? and study_attribute_id=?";
				SQLQuery qry = this.sessionFactory.getCurrentSession()
						.createSQLQuery(sql);

				qry.setInteger(0, studyId);
				qry.setInteger(1, studyAttributeId);
				qry.addEntity(StudyAttributeValue.class);

				List<StudyAttributeValue> list = qry.list();

				if (list != null && list.size() > 0)
				{
					return list.get(0);
				}

			}
			return null;
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryByCarrier(String carrierCode)
	{
		try
		{
			String sql = "select distinct category from Nbs_insurance_category where star_carrier_code=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, carrierCode);
			qry.addScalar("category");
			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryByInsCode(String insCode)
	{
		try
		{
			String sql = "select distinct category from Nbs_insurance_category where ins_code=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");
			qry.setString(0, insCode);

			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryBySMS(String smsCode)
	{
		try
		{
			String sql = "select distinct category from Nbs_insurance_category where sms_code=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");
			qry.setString(0, smsCode);

			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<String> getInsCategories()
	{
		try
		{
			String sql = "select distinct category from Nbs_insurance_category " +
			"where category is not null and category <> '' order by category";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");

			List<String> list = qry.list();
			ArrayList<String> categories = new ArrayList<String>();
			for (String currResult : list)
			{
				categories.add(currResult);
			}

			return categories;
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getObsvNameByObsvId(String obsvId)
	{
		try
		{
			Connection con = this.sessionFactory.getCurrentSession()
					.connection();
			String sql = "select obsv_name from Nbs1_obsv_dictionary where obsv_id = ?";

			try
			{
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1, obsvId);

				ResultSet rs = stmt.executeQuery();
				if (rs.next())
				{
					return rs.getString(1);
				}
				stmt.close();
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(Util.getStackTrace(e));
			}
			return null;
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName)
	{
		try
		{
			String sql = "select * from atd_statistics where obsv_id is not null and encounter_id=? and form_name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, encounterId);
			qry.setString(1, formName);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName)
	{
		try
		{
			String sql = "select * from atd_statistics where rule_id is null and obsv_id is not null and encounter_id=? and form_name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, encounterId);
			qry.setString(1, formName);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public NbsHL7Export insertEncounterToHL7ExportQueue(NbsHL7Export export){
		sessionFactory.getCurrentSession().saveOrUpdate(export);
		return export;
	}
	
	public List <NbsHL7Export> getPendingHL7Exports(){
		
		
		SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from Nbs_hl7_export " +
		" where date_processed is null and voided = 0 and (status = 1 or status = 300)");

		qry.addEntity(NbsHL7Export.class);
		List <NbsHL7Export> exports = qry.list();
		return exports;
	}

	
	public void saveNbsHL7Export(NbsHL7Export export) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(export);
		
		return;
	}
	
	public void saveHL7ExportMapType(NbsHL7ExportMapType mapType){
		this.sessionFactory.getCurrentSession().saveOrUpdate(mapType);
		
	}
	
	public NbsHL7ExportMapType getHL7ExportMapTypeByName (String name){
	
		try {
			String sql = "select * from nbs_hl7_export_map_type where name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, name);
			qry.addEntity(NbsHL7ExportMapType.class);
			
			List<NbsHL7ExportMapType> list = qry.list();

			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}

	} catch (Exception e)
	{
		log.error(Util.getStackTrace(e));
	}
	return null;
		
	}
public NbsHL7Export getNextPendingHL7Export(String resendImagesNotFound, String resendNoAck){
		
		try {
			String resend = "";
			String resendNoAckExtension = "";
			if (resendImagesNotFound != null && (resendImagesNotFound.equalsIgnoreCase("yes")||
					resendImagesNotFound.equalsIgnoreCase("true")))
			{
				NbsHL7ExportStatus status = getNbsExportStatusByName("image_not_found");
				if (status != null ){
					resend = " or status = " + status.getHl7ExportStatusId() ;
				}
			} 
			
			if (resendNoAck != null && (resendNoAck.equalsIgnoreCase("yes")||
					resendNoAck.equalsIgnoreCase("true")))
			{
				NbsHL7ExportStatus status = getNbsExportStatusByName("ACK_not_received");
				NbsHL7ExportStatus statusSocket = getNbsExportStatusByName("open_socket_failed");
				if (status != null ){
					resendNoAckExtension = " or status = " + status.getHl7ExportStatusId()
					 +  " or status = " + statusSocket.getHl7ExportStatusId();
				}
			} 
			
			SQLQuery qry = this.sessionFactory.getCurrentSession()
				.createSQLQuery("select * from Nbs_hl7_export " +
			" where voided = 0 and ((status = 1 and date_processed is null) " 
						+ resend + resendNoAckExtension + " ) order by date_inserted ");

			
			qry.addEntity(NbsHL7Export.class);
			List <NbsHL7Export> exports = qry.list();
			if (exports != null && exports.size() > 0) {
				return exports.get(0);
			}
		} catch (HibernateException e) {
			log.error(e);
		}
		
		return null;
	}
	
	
	public List<NbsHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId){
		SQLQuery qry = this.sessionFactory.getCurrentSession()
		.createSQLQuery("select * from Nbs_hl7_export where encounter_id = ? " + 
				" and date_processed is null and voided = 0 order by date_inserted desc");
		qry.setInteger(0, encounterId);
		qry.addEntity(NbsHL7Export.class);
		List <NbsHL7Export> exports = qry.list();
		return exports;
	}
	
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
			Integer locationTagId,Integer locationId){
		
		try
		{
			
			String dateRestriction = "";
			if (optionalDateRestriction != null)
			{
				dateRestriction = " and start_time >= ?";
			} 
			
			String sql = "select * from atd_patient_state a "+
						"inner join atd_session b on a.session_id=b.session_id where state in ("+
						"select state_id from atd_state where state_action_id in ("+
						"select state_action_id from atd_state_action where action_name in ('RESCAN','REPRINT')) "+
						") "+
						"and encounter_id=? and retired=? and location_tag_id=? and location_id=? "+dateRestriction;
			
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			
			qry.setInteger(0, encounterId);
			qry.setBoolean(1, false);
			qry.setInteger(2, locationTagId);
			qry.setInteger(3, locationId);
			
			if (optionalDateRestriction != null)
			{
				qry.setDate(4, optionalDateRestriction);
			}
			
			qry.addEntity(PatientState.class);
			return qry.list();
		} catch (Exception e)
		{
			log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public void  saveHL7ExportMap (NbsHL7ExportMap map){
		try
		{
			this.sessionFactory.getCurrentSession().save(map);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}
	
	public NbsHL7ExportMap getNbsExportMapByQueueId(Integer queueId, NbsHL7ExportMapType mapType){
		try {
			
			String mapTypeString = "";
			Integer mapTypeId = null;
			if (mapType != null){
				mapTypeString  = " and nbs_hl7_export_map_type_id = ?";
				mapTypeId = mapType.getNbsHl7ExportMapTypeId();
			}
			
			
			SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from Nbs_hl7_export_map " +
			" where hl7_export_queue_id = ? " + mapTypeString );
			qry.setInteger(0, queueId);
			if (mapTypeId != null) {
				qry.setInteger(1, mapTypeId);
			}
			qry.addEntity(NbsHL7ExportMap.class);
			List<NbsHL7ExportMap> list = qry.list();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public NbsHL7ExportStatus getNbsExportStatusByName (String name){
		/*try {
			SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from Nbs_hl7_export_status " +
			" where name = ?");
			qry.setString(0, name);
			qry.addEntity(NbsHL7ExportStatus.class);
			List<NbsHL7ExportStatus> list = qry.list();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
		
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(NbsHL7ExportStatus.class).add(
			    Expression.eq("name", name));
			try {
				if (crit.list().size() < 1) {
					log.warn("No export status found with name: " + name);
					return null;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			return (NbsHL7ExportStatus) crit.list().get(0);
	}
	
	public NbsHL7ExportStatus getNbsExportStatusById (Integer id){
		
		try {
			SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from Nbs_hl7_export_status " +
			" where hl7_export_status_id = ?");
			qry.setInteger(0, id);
			qry.addEntity(NbsHL7ExportStatus.class);
			List<NbsHL7ExportStatus> list = qry.list();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	
	
}
