package org.openmrs.module.nbs.hibernateBeans;

import java.util.Date;

/**
 * @author msheley
 * 
 */
public class NbsHL7ExportStatus implements java.io.Serializable {

	private Integer hl7ExportStatusId = null;
	private String name = null;
	private String description = null;
	private Date dateCreated = null;
	
	
	public Integer getHl7ExportStatusId() {
		return hl7ExportStatusId;
	}
	public void setHl7ExportStatusId(Integer hl7ExportStatusId) {
		this.hl7ExportStatusId = hl7ExportStatusId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	

	
}
