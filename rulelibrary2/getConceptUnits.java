package org.openmrs.module.nbs.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class getConceptUnits implements Rule
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
    	String conceptName = (String) parameters.get("param1");
    	Result result = new Result();
    	
    	ConceptService conceptService = Context.getConceptService();
    	Concept concept = conceptService.getConceptByName(conceptName);
    	ConceptNumeric conceptNumberic = null;
    	String units = null;
    	
    	if (concept != null){
    		conceptNumberic  = conceptService.getConceptNumeric(concept.getConceptId());
    	}
    	if (conceptNumberic!= null){
    		units = conceptNumberic.getUnits();
    	}
    	if (units != null){
    		return new Result(units);
    	} 
    	
		return result;
	}
	
}