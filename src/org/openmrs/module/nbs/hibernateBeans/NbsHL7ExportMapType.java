package org.openmrs.module.nbs.hibernateBeans;

import java.util.Date;

import org.openmrs.User;

/**
 * @author msheley
 * 
 */
public class NbsHL7ExportMapType implements java.io.Serializable {

public static final long serialVersionUID = 2112313431211L;
	
	private Integer nbsHl7ExportMapTypeId;
	
	private String format;
	
	private String description;
	
	private Integer foreignKey;
	
	private Boolean searchable = false;
	
	private String name= null;
	
	private User creator;
	
	private Date dateCreated;
	
	private User  changedBy;
	
	private Date dateChanged;
	
	private Date dateRetired;
	
	private User retiredBy;
	
	private Boolean retired;
	
	private String retireReason;
		

	

	public Boolean getRetired() {
		return retired;
	}

	public void setRetired( Boolean  retired) {
		this.retired = retired;
	}

	public Integer getNbsHl7ExportMapTypeId() {
		return nbsHl7ExportMapTypeId;
	}

	public void setNbsHl7ExportMapTypeId(Integer nbsHl7ExportMapTypeId) {
		this.nbsHl7ExportMapTypeId = nbsHl7ExportMapTypeId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(Integer foreignKey) {
		this.foreignKey = foreignKey;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}

	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}

	public Date getDateRetired() {
		return dateRetired;
	}

	public void setDateRetired(Date dateRetired) {
		this.dateRetired = dateRetired;
	}

	public User getRetiredBy() {
		return retiredBy;
	}

	public void setRetiredBy(User retiredBy) {
		this.retiredBy = retiredBy;
	}

	public String getRetireReason() {
		return retireReason;
	}

	public void setRetireReason(String retireReason) {
		this.retireReason = retireReason;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

}
