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
package org.openmrs.module.nbsmodule.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;

/**
 * Provides access to provider data.
 * 
 */
public class ProviderDataSource implements LogicDataSource
{

	private static final Collection<String> keys = new ArrayList<String>();

	private LogicProviderDAO logicProviderDAO;

	/**
	 * @return the logicProviderDAO
	 */
	public LogicProviderDAO getLogicProviderDAO()
	{
		return this.logicProviderDAO;
	}

	/**
	 * @param logicProviderDAO the logicProviderDAO to set
	 */
	public void setLogicProviderDAO(LogicProviderDAO logicProviderDAO)
	{
		this.logicProviderDAO = logicProviderDAO;
	}

	static
	{
		String[] keyList = new String[]
		{ "username", "system_Id", "provider_id",
				"given_name","middle_name","family_name","fax_number","user_id" };
		for (String k : keyList)
			keys.add(k);
	}

	public Map<Integer, Result> read(LogicContext context, Cohort who,
			LogicCriteria criteria)
	{
		SocketHL7ListenerService socketHL7ListenerService = 
			(SocketHL7ListenerService) Context.getService(SocketHL7ListenerService.class);
		Map<Integer, Result> resultMap = new HashMap<Integer, Result>();
		String rootToken = criteria.getRootToken(); //component to return
		
		ArrayList<Integer> encounterList = 
			(ArrayList<Integer>) context.getGlobalParameter("encounterList");
		
		for (Integer personId : who.getMemberIds())
		{
			// get all providers from given encounters for the patient
			List<Integer> allProviders = getLogicProviderDAO().getAllProviders(
					personId,encounterList);

			if(allProviders == null)
			{
				return resultMap;
			}
			
			// restrict the provider list based on the criteria
			List<User> providerList = getLogicProviderDAO().getProviders(
					allProviders, criteria);

			if(providerList == null)
			{
				return resultMap;
			}
			
			// put in the result map
			for (User user : providerList)
			{
				Result currResult = resultMap.get(personId);

				if (currResult == null)
				{
					currResult = new Result();
				}
				
				if(rootToken.equalsIgnoreCase("username"))
				{
					currResult.add(new Result(user.getUsername()));
				}else if(rootToken.equalsIgnoreCase("system_Id"))
				{
					currResult.add(new Result(user.getSystemId()));
				}else if(rootToken.equalsIgnoreCase("provider_id"))
				{
					PersonService personService = Context.getPersonService();
					Person p = personService.getPerson(user.getPersonId());
					PersonAttribute providerAttribute = p.getAttribute("Provider ID");
					if(providerAttribute != null)
					{
						currResult.add(new Result(providerAttribute.getValue()));	
					}
				}else if(rootToken.equalsIgnoreCase("user_id"))
				{
					currResult.add(new Result(String.valueOf(user.getUserId())));
				}else if(rootToken.equalsIgnoreCase("given_name"))
				{
					currResult.add(new Result(user.getGivenName()));
				}else if(rootToken.equalsIgnoreCase("middle_name"))
				{
					currResult.add(new Result(user.getMiddleName()));
				}else if(rootToken.equalsIgnoreCase("family_name"))
				{
					currResult.add(new Result(user.getFamilyName()));
				}else if(rootToken.equalsIgnoreCase("fax_number"))
				{
					String firstName = user.getGivenName();
					String lastName = user.getFamilyName();
					String faxNumber = socketHL7ListenerService.getFaxNumber(firstName, lastName);
					currResult.add(new Result(faxNumber));
				}

				resultMap.put(personId, currResult);
			}
		}

		return resultMap;
	}

	public int getDefaultTTL()
	{
		return 60 * 60 * 4; // 4 hours
	}

	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#getKeys()
	 */
	public Collection<String> getKeys()
	{
		return keys;
	}

	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#hasKey(java.lang.String)
	 */
	public boolean hasKey(String key)
	{
		return keys.contains(key);
	}

}
