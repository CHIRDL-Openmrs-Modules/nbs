package org.openmrs.module.nbsmodule.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class providerName implements Rule
{

	private LogicService logicService = Context.getLogicService();

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		PersonService personService = (PersonService) Context
				.getService(PersonService.class);

		 String provider = (String) parameters.get("provider");
		 try{
		 Integer providerId = Integer.valueOf(provider);
		 
		if (providerId != null)
		{
			Person providerPerson =  personService.getPerson(providerId);
			if(provider != null)
			{
				String providerName = "Dr. ";
				
				if(providerPerson.getGivenName() != null)
				{
					providerName+=Util.toProperCase(providerPerson.getGivenName())+" ";
					
				}
				if(providerPerson.getFamilyName() != null)
				{
					providerName+=Util.toProperCase(providerPerson.getFamilyName());
				}
				return new Result(providerName);
			}
		}
		 } catch(NumberFormatException e) {
			 
			return new Result(provider); 
		 }
		 
		return Result.emptyResult();
	}

	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 60 * 60 * 24; // 1 day
	}

	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.NUMERIC;
	}
}
