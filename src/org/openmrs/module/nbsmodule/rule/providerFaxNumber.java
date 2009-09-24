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
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class providerFaxNumber implements Rule
{

	private LogicService logicService = Context.getLogicService();

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		PersonService personService = (PersonService) Context
				.getService(PersonService.class);
		SocketHL7ListenerService hl7listService = (SocketHL7ListenerService) 
											Context.getService(SocketHL7ListenerService.class); 

		 String provider = (String) parameters.get("provider");
		 try{
			 Integer providerId = Integer.valueOf(provider);
			 
				if (providerId != null)
				{
					Person providerPerson = personService.getPerson(providerId);
					if(provider != null)
					{
						String firstName = providerPerson.getGivenName();
						String lastName = providerPerson.getFamilyName();
					
						if( ( firstName != null) && ( lastName != null))
						{
							String faxnumber = hl7listService.getFaxNumber(firstName, lastName);
							return new Result(faxnumber);
						}
											
						
					}
				}
			 } catch(NumberFormatException e) {
				 
				return Result.emptyResult();
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
