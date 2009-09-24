package org.openmrs.module.nbsmodule.db.hibernate;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.nbsmodule.NBSModuleResponse;
import org.openmrs.module.nbsmodule.db.NBSModuleDAO;

public class HibernateNBSModuleDAO implements NBSModuleDAO {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	public HibernateNBSModuleDAO() { }
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) { 
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.api.db.NBSService#getNBSModuleResponse(java.lang.Long)
	 */
	public NBSModuleResponse getNBSModuleResponse(Integer alertId) {
		return (NBSModuleResponse) sessionFactory.getCurrentSession().get(NBSModuleResponse.class, alertId);
	}
	
	public List<NBSModuleResponse> getNBSAlertsByPatient(Integer pid)
	{
		//if pid is null, get all alerts not processed yet (all patients)
		String patientRestriction = "";
		if (pid != null){
			patientRestriction = " and patient_id = :pid ";
		} 
		// Select all in nbs_alert table that have not been processed for this
		// patient.
		// Not processed means that the status = 0. After processing, the status
		// = 1
		String sqlSelect = "SELECT * "
				+ " from nbs_alert  WHERE status = :notProcessed " + patientRestriction;
		SQLQuery query = this.sessionFactory.getCurrentSession()
				.createSQLQuery(sqlSelect);
		query.setInteger("notProcessed", 0);
		if (pid != null){
			query.setInteger("pid", pid);
			
		}
		query.addEntity(NBSModuleResponse.class);
		return query.list();
	}
	
	public void createNBSModuleResponse(NBSModuleResponse alertResponse) throws DAOException {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(alertResponse);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean duplicateAlert(NBSModuleResponse alert)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		String sqlSelect = "SELECT * "
				+ " from nbs_alert where patient_id=? and form_id=? and provider_id=? and datestamp >= ?";
		SQLQuery query = this.sessionFactory.getCurrentSession()
				.createSQLQuery(sqlSelect);
		query.setInteger(0,alert.getPatient().getPatientId());
		query.setInteger(1, alert.getFormId());
		query.setInteger(2, alert.getProviderId());
		query.setDate(3, calendar.getTime());
		query.addEntity(NBSModuleResponse.class);
		List<NBSModuleResponse> alerts =  query.list();
		
		if(alerts.size()>0){
			return true;
		}
		return false;
	}


	public void updateNBSModuleResponse(NBSModuleResponse alertResponse) throws DAOException {
		if (alertResponse.getAlertId() == null)
			createNBSModuleResponse(alertResponse);
		else {
			alertResponse = (NBSModuleResponse)sessionFactory.getCurrentSession().merge(alertResponse);
			sessionFactory.getCurrentSession().saveOrUpdate(alertResponse);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<NBSModuleResponse> getResponses() throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(NBSModuleResponse.class).list();
	}

}
