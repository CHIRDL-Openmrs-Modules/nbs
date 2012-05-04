package org.openmrs.module.nbs.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
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
import org.openmrs.module.chirdlutil.util.Util;

/**
 * 
 * Calculates a person's age in years based from their date of birth to the
 * index date
 * 
 */
public class encounterLocation implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{		
		// observation
		Integer encounterId = (Integer) parameters.get("encounterId");
		
		
		LogicCriteria encounterCriteria = null;
		Result result = null;
		Encounter encounter = null;
		String locationName = null;
		
		if(encounterId != null)
		{
			encounterCriteria = 
				new LogicCriteria("encounterId").equalTo(encounterId);
			
		}
		result = context.read(patient,context.getLogicDataSource("encounter"), 
				encounterCriteria.last());
		
		if (result != null){
			encounter = (Encounter) result.toObject(); 
			
		}
		if (encounter != null){
			locationName = encounter.getLocation().getName();
			return new Result(locationName);
		} else {
			return Result.emptyResult();
		}
		
		
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
		return 0;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.NUMERIC;
	}
}
