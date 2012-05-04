/********************************************************************
 Translated from - Asthma_JIT.mlm on Wed Jul 21 15:51:57 EDT 2010

 Title:  Diabetes_JIT
 Filename:  Diabetes_JIT
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Steve Downs
 Specialist:  Pediatrics
 Date: 2010-07-05T03:38:06-0400
 Validation :
 Purpose:  Alerts MD to update lab tests for diabetic patients.
 Explanation:  Alerts MD to update lab tests for diabetic patients with scheduled appointments at clinic.
 Keywords:  diabetes
 Citations: 

********************************************************************/
package org.openmrs.module.nbs.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.dss.DssRule;
import org.openmrs.logic.Duration;
import java.util.StringTokenizer;

import org.openmrs.api.ConceptService;
import java.text.SimpleDateFormat;
public class Diabetes_JIT implements Rule, DssRule{

	private Patient patient;
	private String firstname;
	private ArrayList<String> actions;
	private HashMap<String, String> userVarMap;

	private HashMap <String, Result> resultLookup;

	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	/*** @see org.openmrs.logic.rule.Rule#getDuration()*/
	public int getDuration() {
		return 60*30;   // 30 minutes
	}

	/*** @see org.openmrs.logic.rule.Rule#getDatatype(String)*/
	public Datatype getDatatype(String token) {
		return Datatype.TEXT;
	}

	/*** @see org.openmrs.logic.rule.Rule#getParameterList()*/
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	/*** @see org.openmrs.logic.rule.Rule#getDependencies()*/
	public String[] getDependencies() {
		return new String[] { };
	}

	/*** @see org.openmrs.logic.rule.Rule#getTTL()*/
	public int getTTL() {
		return 0; //60 * 30; // 30 minutes
	}

	/*** @see org.openmrs.logic.rule.Rule#getDatatype(String)*/
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAuthor()*/
	public String getAuthor(){
		return "Steve Downs";
	}

	/*** @see org.openmrs.module.dss.DssRule#getCitations()*/
	public String getCitations(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getDate()*/
	public String getDate(){
		return "2010-07-05T03:38:06-0400";
	}

	/*** @see org.openmrs.module.dss.DssRule#getExplanation()*/
	public String getExplanation(){
		return "Alerts MD to update lab tests for diabetic patients with scheduled appointments at clinic.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getInstitution()*/
	public String getInstitution(){
		return "Indiana University School of Medicine";
	}

	/*** @see org.openmrs.module.dss.DssRule#getKeywords()*/
	public String getKeywords(){
		return "diabetes";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLinks()*/
	public String getLinks(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPurpose()*/
	public String getPurpose(){
		return "Alerts MD to update lab tests for diabetic patients.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Pediatrics";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "Diabetes_JIT";
	}

	/*** @see org.openmrs.module.dss.DssRule#getVersion()*/
	public Double getVersion(){
		return 1.0;
	}

	/*** @see org.openmrs.module.dss.DssRule#getType()*/
	public String getType(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPriority()*/
	public Integer getPriority(){
		return 1;
	}

	/*** @see org.openmrs.module.dss.DssRule#getData()*/
	public String getData(){
		return "read";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLogic()*/
	public String getLogic(){
		return "If conclude endif If endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "call";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMin()*/
	public Integer getAgeMin(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMinUnits()*/
	public String getAgeMinUnits(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMax()*/
	public Integer getAgeMax(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMaxUnits()*/
	public String getAgeMaxUnits(){
		return null;
	}

private static boolean containsIgnoreCase(Result key,List<Result> lst){
for(Result element:lst){
if(key != null&&key.toString().equalsIgnoreCase(element.toString())){
	return true;
}
}	
return false;
}
	private static String toProperCase(String str){

		if(str == null || str.length()<1){
			return str;
		}

		StringBuffer resultString = new StringBuffer();
		String delimiter = " ";
		StringTokenizer tokenizer = new StringTokenizer(str,delimiter,true);
		String currToken = null;

		while(tokenizer.hasMoreTokens()){
			currToken = tokenizer.nextToken();
			if(!currToken.equals(delimiter)){
				if(currToken.length()>0){
					currToken = currToken.substring(0, 1).toUpperCase()
					+ currToken.substring(1).toLowerCase();
				}
			}
			resultString.append(currToken);
		}
		return resultString.toString();
	}

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException {

		String actionStr = "";
		resultLookup = new HashMap <String, Result>();
		Boolean ageOK = null;

		try {
			this.patient=patient;
			userVarMap = new HashMap <String, String>();
			firstname = patient.getPersonName().getGivenName();
			userVarMap.put("firstname", toProperCase(firstname));
			String lastName = patient.getFamilyName();
			userVarMap.put("lastName", lastName);
			String gender = patient.getGender();
			userVarMap.put("Gender", gender);
			if(gender.equalsIgnoreCase("M")){
				userVarMap.put("gender","his");
				userVarMap.put("hisher","his");
			}else{
				userVarMap.put("gender","her");
				userVarMap.put("hisher","her");
			}
			initAction();

			Result mode=new Result((String) parameters.get("mode"));
			resultLookup.put("mode",mode);

			if(evaluate_logic(parameters)){
				Result ruleResult = new Result();
				String value = null;
				String variable = null;
				int varLen = 0;
				varLen = "Diabetes_Care_Worksheet".length();
				value=userVarMap.get("Diabetes_Care_Worksheet");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("Diabetes_Care_Worksheet".endsWith("_value"))
				{
					variable = "Diabetes_Care_Worksheet".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("Diabetes_Care_Worksheet".endsWith("_date"))
				{
					variable = "Diabetes_Care_Worksheet".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else
				{
					if (resultLookup.get("Diabetes_Care_Worksheet") != null){
						value = resultLookup.get("Diabetes_Care_Worksheet").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","Diabetes_Care_Worksheet");
				}
				logicService.eval(patient, "CREATE_JIT",parameters);
								for(String currAction:actions){
					currAction = doAction(currAction);
					ruleResult.add(new Result(currAction));
				}
				return ruleResult;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return Result.emptyResult();
		}
		return Result.emptyResult();
	}

	private boolean evaluate_logic(Map<String, Object> parameters) throws LogicException {

		Result Gender = new Result(userVarMap.get("Gender"));
		Result mode = (Result) resultLookup.get("mode");

		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
			return true;
		}
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("CONSUME"))){
}	return false;	}

	public void initAction() {
		this.actions = new ArrayList<String>();
	}

private String substituteString(String variable,String outStr){
//see if the variable is in the user map
String value = userVarMap.get(variable);
if (value != null)
{
}
// It must be a result value or date
else if (variable.contains("_value"))
{
	variable = variable.replace("_value","").trim();
if(resultLookup.get(variable) != null){value = resultLookup.get(variable).toString();
}}
// It must be a result date
else if (variable.contains("_date"))
{
String pattern = "MM/dd/yy";
SimpleDateFormat dateForm = new SimpleDateFormat(pattern);
variable = variable.replace("_date","").trim();
if(resultLookup.get(variable) != null){value = dateForm.format(resultLookup.get(variable).getResultDate());
}}
else
{
if(resultLookup.get(variable) != null){value = resultLookup.get(variable).toString();
}}
if (value != null)
{
	outStr += value;
}
return outStr;
}
public String doAction(String inStr)
{
int startindex = -1;
int endindex = -1;
int index = -1;
String outStr = "";
while((index = inStr.indexOf("||"))>-1)
{
if(startindex == -1){
startindex = 0;
outStr+=inStr.substring(0,index);
}else if(endindex == -1){
endindex = index-1;
String variable = inStr.substring(startindex, endindex).trim();
outStr = substituteString(variable,outStr);
startindex = -1;
endindex = -1;
}
inStr = inStr.substring(index+2);
}
outStr+=inStr;
return outStr;
}
}