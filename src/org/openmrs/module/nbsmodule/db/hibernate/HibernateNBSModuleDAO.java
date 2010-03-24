package org.openmrs.module.nbsmodule.db.hibernate;

import java.util.Calendar;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.nbsmodule.NBSModuleResponse;
import org.openmrs.module.nbsmodule.db.NBSModuleDAO;
import org.openmrs.module.nbsmodule.util.Util;
import org.openmrs.module.nbsmodule.NbsAlertStatus;

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
	
	@SuppressWarnings("unchecked")
	public List<NBSModuleResponse> getNBSAlerts(Patient patient, Integer formId, Integer providerId,
			Encounter encounter, Date date, Integer status)
	{
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NBSModuleResponse.class);
		if (patient != null)
			criteria.add(Expression.eq("patient", patient));
		if (formId != null)
			criteria.add(Expression.eq("formId", formId));
		if (providerId != null)
			criteria.add(Expression.eq("providerId",providerId));
		if (encounter != null){
			criteria.add(Expression.eq("encounter",encounter));
		}
		if (date != null){
			Date dateAM = Util.removeTime(date);
			Calendar cal = Calendar.getInstance();
		 	cal.setTime(dateAM);
		 	cal.add(Calendar.DATE, 1);
			criteria.add(Expression.between("dateCreated", dateAM, cal.getTime()));
		}
		if(status != null)
			   criteria.add(Expression.eq("status", status));
		
		return  criteria.list();
	}
	
	public void createNBSModuleResponse(NBSModuleResponse alertResponse) throws DAOException {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(alertResponse);
		} catch (Exception e){
			e.printStackTrace();
		}
	}




	public void updateNBSModuleResponse(NBSModuleResponse alertResponse) throws DAOException {
		if (alertResponse.getFormInstanceId() == null)
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
	
	@SuppressWarnings("unchecked")
	public Integer getAlertStatusIdByName(String name){
		Integer status = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NbsAlertStatus.class);
		if (name != null)
			criteria.add(Expression.eq("name", name));
		List<NbsAlertStatus> list = criteria.list();
		if (list != null && list.size() > 0){
			status = list.get(0).getNbsAlertStatusId();
		}
		return  status;

	}

}
