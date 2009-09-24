package org.openmrs.module.nbsmodule;

import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Patient;



/**
 * Alert response for NBS
 * 
 * @author Vibha Anand
 * @version 1.0
 */
public class NBSModuleResponse implements java.io.Serializable {

	public static final long serialVersionUID = 113222232L;

	// Fields
	private Integer nbsalertId;
	private Integer alertId;
	private Boolean retired = false;
	private Date datestamp;
	private Patient patient;
	private Encounter encounter;
	private String recordSta = "23";
	private String mrn = "";
	private String provider = "";
	private Integer formId;
	private Integer status = 0;
	private Integer providerId = null;

	// Constructors

	/** default constructor */
	public NBSModuleResponse() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NBSModuleResponse) {
			NBSModuleResponse t = (NBSModuleResponse)obj;
			if (this.getAlertId() != null && t.getAlertId() != null)
				return (this.getAlertId().equals(t.getAlertId()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (this.getAlertId() == null) return super.hashCode();
		return this.getAlertId().hashCode();
	}

	// Property accessors
	/**
	 * @return the nbsalertId
	 */
	public Integer getNbsalertId()
	{
		return this.nbsalertId;
	}

	/**
	 * @param nbsalertId the nbsalertId to set
	 */
	public void setNbsalertId(Integer nbsalertId)
	{
		this.nbsalertId = nbsalertId;
	}

	/**
	 * @return the alertId
	 */
	public Integer getAlertId()
	{
		return this.alertId;
	}

	/**
	 * @param alertId the alertId to set
	 */
	public void setAlertId(Integer alertId)
	{
		this.alertId = alertId;
	}

	/**
	 * @return the retired
	 */
	public Boolean getRetired()
	{
		return this.retired;
	}

	/**
	 * @param retired the retired to set
	 */
	public void setRetired(Boolean retired)
	{
		this.retired = retired;
	}

	/**
	 * @return the datestamp
	 */
	public Date getDatestamp()
	{
		return this.datestamp;
	}

	/**
	 * @param datestamp the datestamp to set
	 */
	public void setDatestamp(Date datestamp)
	{
		this.datestamp = datestamp;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient()
	{
		return this.patient;
	}

	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient)
	{
		this.patient = patient;
	}

	/**
	 * @return the encounter
	 */
	public Encounter getEncounter()
	{
		return this.encounter;
	}

	/**
	 * @param encounter the encounter to set
	 */
	public void setEncounter(Encounter encounter)
	{
		this.encounter = encounter;
	}

	/**
	 * @return the recordSta
	 */
	public String getRecordSta()
	{
		return this.recordSta;
	}

	/**
	 * @param recordSta the recordSta to set
	 */
	public void setRecordSta(String recordSta)
	{
		this.recordSta = recordSta;
	}

	/**
	 * @return the mrn
	 */
	public String getMrn()
	{
		return this.mrn;
	}

	/**
	 * @param mrn the mrn to set
	 */
	public void setMrn(String mrn)
	{
		this.mrn = mrn;
	}

	/**
	 * @return the provider
	 */
	public String getProvider()
	{
		return this.provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	/**
	 * @return the formId
	 */
	public Integer getFormId()
	{
		return this.formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(Integer formId)
	{
		this.formId = formId;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus()
	{
		return this.status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public Integer getProviderId()
	{
		return this.providerId;
	}

	public void setProviderId(Integer providerId)
	{
		this.providerId = providerId;
	}
}