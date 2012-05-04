/********************************************************************
 Translated from - Diabetes_Hba1c.mlm on Wed Jan 19 14:00:23 EST 2011

 Title:  Diabetes Hba1c
 Filename:  Diabetes_Hba1c
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Tammy Dugan
 Specialist:  Pediatrics
 Date: 2010-08-03T11:00:00-0400
 Validation :
 Purpose:  Lookup up Hba1c results for diabetic patients.
 Explanation: 
 Keywords: 
 Citations: 
 Links: 
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
public class Diabetes_Hba1c implements Rule, DssRule{

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
		return "Tammy Dugan";
	}

	/*** @see org.openmrs.module.dss.DssRule#getCitations()*/
	public String getCitations(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getDate()*/
	public String getDate(){
		return "2010-08-03T11:00:00-0400";
	}

	/*** @see org.openmrs.module.dss.DssRule#getExplanation()*/
	public String getExplanation(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getInstitution()*/
	public String getInstitution(){
		return "Indiana University School of Medicine";
	}

	/*** @see org.openmrs.module.dss.DssRule#getKeywords()*/
	public String getKeywords(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getLinks()*/
	public String getLinks(){
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getPurpose()*/
	public String getPurpose(){
		return "Lookup up Hba1c results for diabetic patients.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Pediatrics";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "Diabetes Hba1c";
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
		return null;
	}

	/*** @see org.openmrs.module.dss.DssRule#getData()*/
	public String getData(){
		return "read If read read read read read read read read read read read endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLogic()*/
	public String getLogic(){
		return "If call call call call call call call call call conclude endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "write write write write";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMin()*/
	public Integer getAgeMin(){
		return 0;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMinUnits()*/
	public String getAgeMinUnits(){
		return "years";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMax()*/
	public Integer getAgeMax(){
		return 100;
	}

	/*** @see org.openmrs.module.dss.DssRule#getAgeMaxUnits()*/
	public String getAgeMaxUnits(){
		return "years";
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
			resultLookup.put("mode",mode);		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){

			Result hba1c_1=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Glycated Hb-Total (Hplc)").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_1",hba1c_1);
			Result hba1c_2=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hgb A1C Bld Qn HPLC").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_2",hba1c_2);
			Result hba1c_3=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Glycated Hgb %").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_3",hba1c_3);
			Result hba1c_4=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Glycos Hgb A-1%").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_4",hba1c_4);
			Result hba1c_5=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hgb A1C Bld Qn (Meth)").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_5",hba1c_5);
			Result hba1c_6=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hgb A1c Bld Qn (POC)").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_6",hba1c_6);
			Result hba1c_7=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hgb A1c Bld Qn IA").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_7",hba1c_7);
			Result hba1c_8=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hgb Glycosylated (VA)").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_8",hba1c_8);
			Result hba1c_9=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("HgbA1C % Ser EIA").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_9",hba1c_9);
			Result hba1c_10=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Glycated Hgb").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_10",hba1c_10);
			Result hba1c_11=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Glycos Hgb A-1").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_11",hba1c_11);
			Result hba1c_12=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hgb A1C Bld Qn HPLC").within(Duration.years(-1)).last());
			resultLookup.put("hba1c_12",hba1c_12);}

			if(evaluate_logic(parameters)){
				Result ruleResult = new Result();
		Result hba1c_9 = (Result) resultLookup.get("hba1c_9");
		Result hba1c_8 = (Result) resultLookup.get("hba1c_8");
		Result hba1c_7 = (Result) resultLookup.get("hba1c_7");
		Result hba1c_6 = (Result) resultLookup.get("hba1c_6");
		Result hba1c_10 = (Result) resultLookup.get("hba1c_10");
		Result hba1c_1 = (Result) resultLookup.get("hba1c_1");
		Result hba1c_12 = (Result) resultLookup.get("hba1c_12");
		Result hba1c_11 = (Result) resultLookup.get("hba1c_11");
		Result hba1c_4 = (Result) resultLookup.get("hba1c_4");
		Result hba1c_5 = (Result) resultLookup.get("hba1c_5");
		Result hba1c_2 = (Result) resultLookup.get("hba1c_2");
		Result hba1c_3 = (Result) resultLookup.get("hba1c_3");

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
		Result hba1c_9 = (Result) resultLookup.get("hba1c_9");
		Result hba1c_8 = (Result) resultLookup.get("hba1c_8");
		Result hba1c_7 = (Result) resultLookup.get("hba1c_7");
		Result hba1c_6 = (Result) resultLookup.get("hba1c_6");
		Result mode = (Result) resultLookup.get("mode");
		Result hba1c_10 = (Result) resultLookup.get("hba1c_10");
		Result hba1c_1 = (Result) resultLookup.get("hba1c_1");
		Result hba1c_12 = (Result) resultLookup.get("hba1c_12");
		Result hba1c_11 = (Result) resultLookup.get("hba1c_11");
		Result hba1c_4 = (Result) resultLookup.get("hba1c_4");
		Result hba1c_5 = (Result) resultLookup.get("hba1c_5");
		Result hba1c_2 = (Result) resultLookup.get("hba1c_2");
		Result hba1c_3 = (Result) resultLookup.get("hba1c_3");

				Object value = null;
				String variable = null;
				int varLen = 0;
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
				varLen = "HG A1C LEVEL < 7.0%".length();
				value=userVarMap.get("HG A1C LEVEL < 7.0%");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("HG A1C LEVEL < 7.0%".endsWith("_value"))
				{
					variable = "HG A1C LEVEL < 7.0%".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HG A1C LEVEL < 7.0%".endsWith("_date"))
				{
					variable = "HG A1C LEVEL < 7.0%".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HG A1C LEVEL < 7.0%".endsWith("_object"))
				{
					variable = "HG A1C LEVEL < 7.0%".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HG A1C LEVEL < 7.0%") != null){
						value = resultLookup.get("HG A1C LEVEL < 7.0%").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","HG A1C LEVEL < 7.0%");
				}
				varLen = "hba1c_13_object".length();
				value=userVarMap.get("hba1c_13_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("hba1c_13_object".endsWith("_value"))
				{
					variable = "hba1c_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_13_object".endsWith("_date"))
				{
					variable = "hba1c_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_13_object".endsWith("_object"))
				{
					variable = "hba1c_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_13_object") != null){
						value = resultLookup.get("hba1c_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","hba1c_13_object");
				}
				Result result1 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result1",result1);
				varLen = "HG A1C LEVEL 7.0-9.0%".length();
				value=userVarMap.get("HG A1C LEVEL 7.0-9.0%");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("HG A1C LEVEL 7.0-9.0%".endsWith("_value"))
				{
					variable = "HG A1C LEVEL 7.0-9.0%".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HG A1C LEVEL 7.0-9.0%".endsWith("_date"))
				{
					variable = "HG A1C LEVEL 7.0-9.0%".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HG A1C LEVEL 7.0-9.0%".endsWith("_object"))
				{
					variable = "HG A1C LEVEL 7.0-9.0%".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HG A1C LEVEL 7.0-9.0%") != null){
						value = resultLookup.get("HG A1C LEVEL 7.0-9.0%").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","HG A1C LEVEL 7.0-9.0%");
				}
				varLen = "hba1c_13_object".length();
				value=userVarMap.get("hba1c_13_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("hba1c_13_object".endsWith("_value"))
				{
					variable = "hba1c_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_13_object".endsWith("_date"))
				{
					variable = "hba1c_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_13_object".endsWith("_object"))
				{
					variable = "hba1c_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_13_object") != null){
						value = resultLookup.get("hba1c_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","hba1c_13_object");
				}
				Result result2 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result2",result2);
				varLen = "HEMOGLOBIN A1C LEVEL > 9.0%".length();
				value=userVarMap.get("HEMOGLOBIN A1C LEVEL > 9.0%");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("HEMOGLOBIN A1C LEVEL > 9.0%".endsWith("_value"))
				{
					variable = "HEMOGLOBIN A1C LEVEL > 9.0%".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HEMOGLOBIN A1C LEVEL > 9.0%".endsWith("_date"))
				{
					variable = "HEMOGLOBIN A1C LEVEL > 9.0%".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HEMOGLOBIN A1C LEVEL > 9.0%".endsWith("_object"))
				{
					variable = "HEMOGLOBIN A1C LEVEL > 9.0%".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HEMOGLOBIN A1C LEVEL > 9.0%") != null){
						value = resultLookup.get("HEMOGLOBIN A1C LEVEL > 9.0%").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","HEMOGLOBIN A1C LEVEL > 9.0%");
				}
				varLen = "hba1c_13_object".length();
				value=userVarMap.get("hba1c_13_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("hba1c_13_object".endsWith("_value"))
				{
					variable = "hba1c_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_13_object".endsWith("_date"))
				{
					variable = "hba1c_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_13_object".endsWith("_object"))
				{
					variable = "hba1c_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_13_object") != null){
						value = resultLookup.get("hba1c_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","hba1c_13_object");
				}
				Result result3 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result3",result3);
				varLen = "HEMOGLOBIN A1C LEVEL <= 9.0%".length();
				value=userVarMap.get("HEMOGLOBIN A1C LEVEL <= 9.0%");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("HEMOGLOBIN A1C LEVEL <= 9.0%".endsWith("_value"))
				{
					variable = "HEMOGLOBIN A1C LEVEL <= 9.0%".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("HEMOGLOBIN A1C LEVEL <= 9.0%".endsWith("_date"))
				{
					variable = "HEMOGLOBIN A1C LEVEL <= 9.0%".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("HEMOGLOBIN A1C LEVEL <= 9.0%".endsWith("_object"))
				{
					variable = "HEMOGLOBIN A1C LEVEL <= 9.0%".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("HEMOGLOBIN A1C LEVEL <= 9.0%") != null){
						value = resultLookup.get("HEMOGLOBIN A1C LEVEL <= 9.0%").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","HEMOGLOBIN A1C LEVEL <= 9.0%");
				}
				varLen = "hba1c_13_object".length();
				value=userVarMap.get("hba1c_13_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("hba1c_13_object".endsWith("_value"))
				{
					variable = "hba1c_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_13_object".endsWith("_date"))
				{
					variable = "hba1c_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_13_object".endsWith("_object"))
				{
					variable = "hba1c_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_13_object") != null){
						value = resultLookup.get("hba1c_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","hba1c_13_object");
				}
				Result result4 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result4",result4);
				varLen = "GLYCATED HEMOGLOBIN TEST".length();
				value=userVarMap.get("GLYCATED HEMOGLOBIN TEST");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("GLYCATED HEMOGLOBIN TEST".endsWith("_value"))
				{
					variable = "GLYCATED HEMOGLOBIN TEST".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("GLYCATED HEMOGLOBIN TEST".endsWith("_date"))
				{
					variable = "GLYCATED HEMOGLOBIN TEST".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("GLYCATED HEMOGLOBIN TEST".endsWith("_object"))
				{
					variable = "GLYCATED HEMOGLOBIN TEST".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("GLYCATED HEMOGLOBIN TEST") != null){
						value = resultLookup.get("GLYCATED HEMOGLOBIN TEST").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","GLYCATED HEMOGLOBIN TEST");
				}
				varLen = "hba1c_13_object".length();
				value=userVarMap.get("hba1c_13_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("hba1c_13_object".endsWith("_value"))
				{
					variable = "hba1c_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_13_object".endsWith("_date"))
				{
					variable = "hba1c_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_13_object".endsWith("_object"))
				{
					variable = "hba1c_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_13_object") != null){
						value = resultLookup.get("hba1c_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","hba1c_13_object");
				}
				Result result5 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result5",result5);
				varLen = "GLYCOSYLATED HB, HOME DEV".length();
				value=userVarMap.get("GLYCOSYLATED HB, HOME DEV");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("GLYCOSYLATED HB, HOME DEV".endsWith("_value"))
				{
					variable = "GLYCOSYLATED HB, HOME DEV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("GLYCOSYLATED HB, HOME DEV".endsWith("_date"))
				{
					variable = "GLYCOSYLATED HB, HOME DEV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("GLYCOSYLATED HB, HOME DEV".endsWith("_object"))
				{
					variable = "GLYCOSYLATED HB, HOME DEV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("GLYCOSYLATED HB, HOME DEV") != null){
						value = resultLookup.get("GLYCOSYLATED HB, HOME DEV").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","GLYCOSYLATED HB, HOME DEV");
				}
				varLen = "hba1c_13_object".length();
				value=userVarMap.get("hba1c_13_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("hba1c_13_object".endsWith("_value"))
				{
					variable = "hba1c_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_13_object".endsWith("_date"))
				{
					variable = "hba1c_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_13_object".endsWith("_object"))
				{
					variable = "hba1c_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_13_object") != null){
						value = resultLookup.get("hba1c_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","hba1c_13_object");
				}
				Result result6 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result6",result6);
				varLen = "hba1c_1_object".length();
				value=userVarMap.get("hba1c_1_object");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("hba1c_1_object".endsWith("_value"))
				{
					variable = "hba1c_1_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_1_object".endsWith("_date"))
				{
					variable = "hba1c_1_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_1_object".endsWith("_object"))
				{
					variable = "hba1c_1_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_1_object") != null){
						value = resultLookup.get("hba1c_1_object").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","hba1c_1_object");
				}
				varLen = "hba1c_2_object".length();
				value=userVarMap.get("hba1c_2_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("hba1c_2_object".endsWith("_value"))
				{
					variable = "hba1c_2_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_2_object".endsWith("_date"))
				{
					variable = "hba1c_2_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_2_object".endsWith("_object"))
				{
					variable = "hba1c_2_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_2_object") != null){
						value = resultLookup.get("hba1c_2_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","hba1c_2_object");
				}
				varLen = "hba1c_3_object".length();
				value=userVarMap.get("hba1c_3_object");
				if(value != null){
					parameters.put("param3",value);
				}
				// It must be a result value or date
				else if("hba1c_3_object".endsWith("_value"))
				{
					variable = "hba1c_3_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_3_object".endsWith("_date"))
				{
					variable = "hba1c_3_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_3_object".endsWith("_object"))
				{
					variable = "hba1c_3_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_3_object") != null){
						value = resultLookup.get("hba1c_3_object").toString();
					}
				}
				if(value != null){
					parameters.put("param3",value);
				}
				else
				{
					parameters.put("param3","hba1c_3_object");
				}
				varLen = "hba1c_4_object".length();
				value=userVarMap.get("hba1c_4_object");
				if(value != null){
					parameters.put("param4",value);
				}
				// It must be a result value or date
				else if("hba1c_4_object".endsWith("_value"))
				{
					variable = "hba1c_4_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_4_object".endsWith("_date"))
				{
					variable = "hba1c_4_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_4_object".endsWith("_object"))
				{
					variable = "hba1c_4_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_4_object") != null){
						value = resultLookup.get("hba1c_4_object").toString();
					}
				}
				if(value != null){
					parameters.put("param4",value);
				}
				else
				{
					parameters.put("param4","hba1c_4_object");
				}
				varLen = "hba1c_5_object".length();
				value=userVarMap.get("hba1c_5_object");
				if(value != null){
					parameters.put("param5",value);
				}
				// It must be a result value or date
				else if("hba1c_5_object".endsWith("_value"))
				{
					variable = "hba1c_5_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_5_object".endsWith("_date"))
				{
					variable = "hba1c_5_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_5_object".endsWith("_object"))
				{
					variable = "hba1c_5_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_5_object") != null){
						value = resultLookup.get("hba1c_5_object").toString();
					}
				}
				if(value != null){
					parameters.put("param5",value);
				}
				else
				{
					parameters.put("param5","hba1c_5_object");
				}
				varLen = "hba1c_6_object".length();
				value=userVarMap.get("hba1c_6_object");
				if(value != null){
					parameters.put("param6",value);
				}
				// It must be a result value or date
				else if("hba1c_6_object".endsWith("_value"))
				{
					variable = "hba1c_6_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_6_object".endsWith("_date"))
				{
					variable = "hba1c_6_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_6_object".endsWith("_object"))
				{
					variable = "hba1c_6_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_6_object") != null){
						value = resultLookup.get("hba1c_6_object").toString();
					}
				}
				if(value != null){
					parameters.put("param6",value);
				}
				else
				{
					parameters.put("param6","hba1c_6_object");
				}
				varLen = "hba1c_7_object".length();
				value=userVarMap.get("hba1c_7_object");
				if(value != null){
					parameters.put("param7",value);
				}
				// It must be a result value or date
				else if("hba1c_7_object".endsWith("_value"))
				{
					variable = "hba1c_7_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_7_object".endsWith("_date"))
				{
					variable = "hba1c_7_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_7_object".endsWith("_object"))
				{
					variable = "hba1c_7_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_7_object") != null){
						value = resultLookup.get("hba1c_7_object").toString();
					}
				}
				if(value != null){
					parameters.put("param7",value);
				}
				else
				{
					parameters.put("param7","hba1c_7_object");
				}
				varLen = "hba1c_8_object".length();
				value=userVarMap.get("hba1c_8_object");
				if(value != null){
					parameters.put("param8",value);
				}
				// It must be a result value or date
				else if("hba1c_8_object".endsWith("_value"))
				{
					variable = "hba1c_8_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_8_object".endsWith("_date"))
				{
					variable = "hba1c_8_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_8_object".endsWith("_object"))
				{
					variable = "hba1c_8_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_8_object") != null){
						value = resultLookup.get("hba1c_8_object").toString();
					}
				}
				if(value != null){
					parameters.put("param8",value);
				}
				else
				{
					parameters.put("param8","hba1c_8_object");
				}
				varLen = "hba1c_9_object".length();
				value=userVarMap.get("hba1c_9_object");
				if(value != null){
					parameters.put("param9",value);
				}
				// It must be a result value or date
				else if("hba1c_9_object".endsWith("_value"))
				{
					variable = "hba1c_9_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_9_object".endsWith("_date"))
				{
					variable = "hba1c_9_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_9_object".endsWith("_object"))
				{
					variable = "hba1c_9_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_9_object") != null){
						value = resultLookup.get("hba1c_9_object").toString();
					}
				}
				if(value != null){
					parameters.put("param9",value);
				}
				else
				{
					parameters.put("param9","hba1c_9_object");
				}
				varLen = "hba1c_10_object".length();
				value=userVarMap.get("hba1c_10_object");
				if(value != null){
					parameters.put("param10",value);
				}
				// It must be a result value or date
				else if("hba1c_10_object".endsWith("_value"))
				{
					variable = "hba1c_10_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_10_object".endsWith("_date"))
				{
					variable = "hba1c_10_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_10_object".endsWith("_object"))
				{
					variable = "hba1c_10_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_10_object") != null){
						value = resultLookup.get("hba1c_10_object").toString();
					}
				}
				if(value != null){
					parameters.put("param10",value);
				}
				else
				{
					parameters.put("param10","hba1c_10_object");
				}
				varLen = "hba1c_11_object".length();
				value=userVarMap.get("hba1c_11_object");
				if(value != null){
					parameters.put("param11",value);
				}
				// It must be a result value or date
				else if("hba1c_11_object".endsWith("_value"))
				{
					variable = "hba1c_11_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_11_object".endsWith("_date"))
				{
					variable = "hba1c_11_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_11_object".endsWith("_object"))
				{
					variable = "hba1c_11_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_11_object") != null){
						value = resultLookup.get("hba1c_11_object").toString();
					}
				}
				if(value != null){
					parameters.put("param11",value);
				}
				else
				{
					parameters.put("param11","hba1c_11_object");
				}
				varLen = "hba1c_12_object".length();
				value=userVarMap.get("hba1c_12_object");
				if(value != null){
					parameters.put("param12",value);
				}
				// It must be a result value or date
				else if("hba1c_12_object".endsWith("_value"))
				{
					variable = "hba1c_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("hba1c_12_object".endsWith("_date"))
				{
					variable = "hba1c_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("hba1c_12_object".endsWith("_object"))
				{
					variable = "hba1c_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("hba1c_12_object") != null){
						value = resultLookup.get("hba1c_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param12",value);
				}
				else
				{
					parameters.put("param12","hba1c_12_object");
				}
				varLen = "result1_object".length();
				value=userVarMap.get("result1_object");
				if(value != null){
					parameters.put("param13",value);
				}
				// It must be a result value or date
				else if("result1_object".endsWith("_value"))
				{
					variable = "result1_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result1_object".endsWith("_date"))
				{
					variable = "result1_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result1_object".endsWith("_object"))
				{
					variable = "result1_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result1_object") != null){
						value = resultLookup.get("result1_object").toString();
					}
				}
				if(value != null){
					parameters.put("param13",value);
				}
				else
				{
					parameters.put("param13","result1_object");
				}
				varLen = "result2_object".length();
				value=userVarMap.get("result2_object");
				if(value != null){
					parameters.put("param14",value);
				}
				// It must be a result value or date
				else if("result2_object".endsWith("_value"))
				{
					variable = "result2_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result2_object".endsWith("_date"))
				{
					variable = "result2_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result2_object".endsWith("_object"))
				{
					variable = "result2_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result2_object") != null){
						value = resultLookup.get("result2_object").toString();
					}
				}
				if(value != null){
					parameters.put("param14",value);
				}
				else
				{
					parameters.put("param14","result2_object");
				}
				varLen = "result3_object".length();
				value=userVarMap.get("result3_object");
				if(value != null){
					parameters.put("param15",value);
				}
				// It must be a result value or date
				else if("result3_object".endsWith("_value"))
				{
					variable = "result3_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result3_object".endsWith("_date"))
				{
					variable = "result3_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result3_object".endsWith("_object"))
				{
					variable = "result3_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result3_object") != null){
						value = resultLookup.get("result3_object").toString();
					}
				}
				if(value != null){
					parameters.put("param15",value);
				}
				else
				{
					parameters.put("param15","result3_object");
				}
				varLen = "result4_object".length();
				value=userVarMap.get("result4_object");
				if(value != null){
					parameters.put("param16",value);
				}
				// It must be a result value or date
				else if("result4_object".endsWith("_value"))
				{
					variable = "result4_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result4_object".endsWith("_date"))
				{
					variable = "result4_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result4_object".endsWith("_object"))
				{
					variable = "result4_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result4_object") != null){
						value = resultLookup.get("result4_object").toString();
					}
				}
				if(value != null){
					parameters.put("param16",value);
				}
				else
				{
					parameters.put("param16","result4_object");
				}
				varLen = "result5_object".length();
				value=userVarMap.get("result5_object");
				if(value != null){
					parameters.put("param17",value);
				}
				// It must be a result value or date
				else if("result5_object".endsWith("_value"))
				{
					variable = "result5_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result5_object".endsWith("_date"))
				{
					variable = "result5_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result5_object".endsWith("_object"))
				{
					variable = "result5_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result5_object") != null){
						value = resultLookup.get("result5_object").toString();
					}
				}
				if(value != null){
					parameters.put("param17",value);
				}
				else
				{
					parameters.put("param17","result5_object");
				}
				varLen = "result6_object".length();
				value=userVarMap.get("result6_object");
				if(value != null){
					parameters.put("param18",value);
				}
				// It must be a result value or date
				else if("result6_object".endsWith("_value"))
				{
					variable = "result6_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result6_object".endsWith("_date"))
				{
					variable = "result6_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result6_object".endsWith("_object"))
				{
					variable = "result6_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result6_object") != null){
						value = resultLookup.get("result6_object").toString();
					}
				}
				if(value != null){
					parameters.put("param18",value);
				}
				else
				{
					parameters.put("param18","result6_object");
				}
				Result finalResult = logicService.eval(patient, "mostRecentResult",parameters);
				resultLookup.put("finalResult",finalResult);
				varLen = "finalResult_object".length();
				value=userVarMap.get("finalResult_object");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("finalResult_object".endsWith("_value"))
				{
					variable = "finalResult_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("finalResult_object".endsWith("_date"))
				{
					variable = "finalResult_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("finalResult_object".endsWith("_object"))
				{
					variable = "finalResult_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("finalResult_object") != null){
						value = resultLookup.get("finalResult_object").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","finalResult_object");
				}
				Result finalResultConceptName = logicService.eval(patient, "conceptNameResult",parameters);
				resultLookup.put("finalResultConceptName",finalResultConceptName);
				varLen = "finalResultConceptName".length();
				value=userVarMap.get("finalResultConceptName");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("finalResultConceptName".endsWith("_value"))
				{
					variable = "finalResultConceptName".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("finalResultConceptName".endsWith("_date"))
				{
					variable = "finalResultConceptName".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("finalResultConceptName".endsWith("_object"))
				{
					variable = "finalResultConceptName".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("finalResultConceptName") != null){
						value = resultLookup.get("finalResultConceptName").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","finalResultConceptName");
				}
				Result finalResultUnits = logicService.eval(patient, "getConceptUnits",parameters);
				resultLookup.put("finalResultUnits",finalResultUnits);
			return true;
		}
	return false;	}

	public void initAction() {
		this.actions = new ArrayList<String>();
		actions.add("|| finalResult_date ||@hba1cResultDate");
		actions.add("|| finalResult_value ||@hba1cResultValue");
		actions.add("|| finalResultConceptName_value ||@hba1cConceptName");
		actions.add("|| finalResultUnits_value ||@hba1cResultUnit");
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