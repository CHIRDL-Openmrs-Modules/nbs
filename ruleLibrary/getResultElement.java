package org.openmrs.module.nbs.rule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class getResultElement implements Rule
{

	private LogicService logicService = Context.getLogicService();

	/**
	 * @see org.openmrs.logic.rule.Rule#eval(org.openmrs.Patient,
	 *      org.openmrs.logic.LogicCriteria)
	 */
	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		
		Integer index = null;
		List<Result> results = null;
		Result distinctResult = null;
	
		if(parameters != null)
		{
			Object param1Obj = parameters.get("param1");
			if (param1Obj != null){
				index = Integer.parseInt((String)param1Obj);
				System.out.println("the index is " +  index);
			}
			
			results = (List<Result>) parameters.get("param2");
		}
		
		if (index != null && results != null && index < results.toArray().length)
		{
			distinctResult = (Result) results.toArray()[index];
		}
		if(distinctResult == null){
			distinctResult = new Result();
		}
		if (distinctResult.toString()== null){
			distinctResult.setValueText(String.valueOf(distinctResult.toNumber()));
		}
		return  distinctResult;
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; 
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.NUMERIC;
	}

}
