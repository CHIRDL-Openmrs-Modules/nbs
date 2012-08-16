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
 * Holds information to store in the chirdlutilbackports_obs_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class ObsAttributeValue implements java.io.Serializable {
	
	// Fields
	private Integer obsAttributeValueId = null;
	private Integer obsId = null;
	private Integer obsAttributeId = null;
	private String value = null;
	
	// Constructors
	
	/** default constructor */
	public ObsAttributeValue() {
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
	 * @return the obsId
	 */
	public Integer getObsId() {
		return this.obsId;
	}
	
	/**
	 * @param obsId the obsId to set
	 */
	public void setObsId(Integer obsId) {
		this.obsId = obsId;
	}
	
	/**
	 * @return the obsAttributeValueId
	 */
	public Integer getObsAttributeValueId() {
		return this.obsAttributeValueId;
	}
	
	/**
	 * @param obsAttributeValueId the obsAttributeValueId to set
	 */
	public void setObsAttributeValueId(Integer obsAttributeValueId) {
		this.obsAttributeValueId = obsAttributeValueId;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
