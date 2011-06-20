package org.openmrs.module.nbs.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class conceptNameResult implements Rule
{
	private LogicService logicService = Context.getLogicService();

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}
	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
			{
		System.out.println("parameters: "+parameters);

		Result result = Result.emptyResult();
		Result conceptNameResult = null;
		String name = null;
		int i = 1;
		
		Object paramObj = parameters.get("param1");
		if(paramObj != null && paramObj instanceof Result ){
			paramObj = parameters.get("param1");
			result = (Result) parameters.get("param1");
		}
		if(result != null){
			Obs resultObs = (Obs)result.toObject();
			if (resultObs != null){
				Concept concept = resultObs.getConcept();
				if (concept != null && concept.getName() != null){
					name =  concept.getName().getName();
				}
	
			}
		}	
			
		if (name == null){
			conceptNameResult = new Result();
		}
		else {
			conceptNameResult = new Result(name);
		}
		
		return conceptNameResult;
	}
	
}