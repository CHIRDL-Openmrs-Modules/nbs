
package org.openmrs.module.nbsmodule;
import java.util.Date;


public class NbsAlertStatus implements java.io.Serializable {

	public static final long serialVersionUID = 113222232L;
	// Fields
	private Integer nbsAlertStatusId;
	public Integer getNbsAlertStatusId() {
		return nbsAlertStatusId;
	}
	public void setNbsAlertStatusId(Integer nbsAlertStatusId) {
		this.nbsAlertStatusId = nbsAlertStatusId;
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

	private String name;
	private String description;
	private Date dateCreated;
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
}


	// Constructors