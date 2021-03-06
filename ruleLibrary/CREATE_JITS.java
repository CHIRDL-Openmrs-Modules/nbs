package org.openmrs.module.nbs.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.nbs.NbsStateActionHandler;
import org.openmrs.module.nbs.service.NbsService;
import org.openmrs.module.nbs.util.Util;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;

public class CREATE_JITS implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
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
		ATDService atdService = Context.getService(ATDService.class);
		FormService formService = Context.getFormService();
		PersonService personService = Context.getPersonService();
		
		String formName = (String) parameters.get("param1");
		Form form = formService.getForms(formName,null,null,false,null,null,null).get(0);
		Integer formId = form.getFormId();
		Integer sessionId = (Integer) parameters.get("sessionId");
		Integer locationTagId = (Integer) parameters.get("locationTagId"); 
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		//we don't know the formInstanceId yet because the JIT hasn't been created
		formInstance = new FormInstance(formInstance.getLocationId(),formId,null);
		Result results = Result.emptyResult();

		Integer locationId = formInstance.getLocationId(); 
	
    	
		Object paramObj = "";
		String providerId = null;
    	int i = 2;
    	
    	
    	while(paramObj != null){
			paramObj = parameters.get("param"+i);
			if(paramObj instanceof Result){
				results = (Result) parameters.get("param"+i);
			}else{

				continue;
			}
			if(results != null){
				for(Result result:results){
					if (result != null ) {
						providerId = result.toString();
    					//check if sent in last few days
    					 parameters.put("providerId", providerId);
						 String firstName = "";
						 String lastName = "";
						 
						 if (providerId != null){
							 Person provider = personService.getPerson(personId);
							 if (provider != null){
								 firstName = provider.getGivenName();
								 lastName = provider.getFamilyName();
							 }
						 }
						 parameters.put("providerFirstName", firstName);
						 parameters.put("providerLastName", lastName);
						 
    					 logicService.eval(patient, "CREATE_JIT",parameters);
    				}
				}
			}
			i++;
		}
    	
		return Result.emptyResult();
	}
}