package org.openmrs.module.nbs.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class getAllWithSameDate implements Rule
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
		Object mostRecentResultObject = parameters.get("param1");
		Result mostRecentResult = null;
		List<Result>  sameDateResults = new ArrayList<Result>();
		
		if(mostRecentResultObject != null && mostRecentResultObject instanceof Result){
			mostRecentResult = (Result) mostRecentResultObject;
		}
		else {
			return Result.emptyResult();
		}
			
		
		
    	Result results = null;
    	int i = 3;
		Object paramObj = parameters.get("param2");
		while(paramObj != null){
			paramObj = parameters.get("param"+i);
			i++;
			if(paramObj instanceof Result){
				results = (Result) parameters.get("param"+i);
			}else{

				continue;
			}
			if(results != null){
				for(Result result:results){
					if(result !=  null  ){
						Date resultDate = result.getResultDate();
						if ( resultDate != null && resultDate.equals(mostRecentResult.getResultDate())){
							result.setValueText(String.valueOf(result.toNumber()));
							sameDateResults.add(result);
						}
						
						
					}
				}	
			}
		}
	
		
		if(sameDateResults.isEmpty()){
			return new Result();
		}
		return new Result(sameDateResults);
	}
}
	