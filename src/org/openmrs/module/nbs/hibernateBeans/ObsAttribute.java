/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.nbs.hibernateBeans;

/**
 * Holds information to store in the chirdlutilbackports_obs_attribute table
 * 
 * @author Steve McKee
 */
public class ObsAttribute implements java.io.Serializable {
	
	// Fields
	private Integer obsAttributeId = null;
	private String name = null;
	private String description = null;
	
	// Constructors
	
	/** default constructor */
	public ObsAttribute() {
	}
	
	/**
	 * @return the obsAttributeId
	 */
	public Integer getObsAttributeId() {
		return this.obsAttributeId;
	}
	
	/**
	 * @param obsAttributeId the obsAttributeId to set
	 */
	public void setObsAttributeId(Integer obsAttributeId) {
		this.obsAttributeId = obsAttributeId;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
