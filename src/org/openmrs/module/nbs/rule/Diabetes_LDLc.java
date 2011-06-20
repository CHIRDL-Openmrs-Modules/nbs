/********************************************************************
 Translated from - Diabetes_LDLc.mlm on Thu Aug 05 11:24:57 EDT 2010

 Title:  Diabetes_LDLc
 Filename:  Diabetes_LDLc
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Tammy Dugan
 Specialist:  Pediatrics
 Date: 2010-08-03T11:00:00-0400
 Validation :
 Purpose:  Lookup up LDLc results for diabetic patients.
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
public class Diabetes_LDLc implements Rule, DssRule{

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
		return "Lookup up LDLc results for diabetic patients.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Pediatrics";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "Diabetes_LDLc";
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
		return "If call call call call call call call call call call conclude endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "write write";
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

			Result ldlc_1=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL SerPl Elp unit/vol").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_1",ldlc_1);
			Result ldlc_2=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL Calc Bld Qn (POC)").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_2",ldlc_2);
			Result ldlc_3=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL SerPl Calc Qn").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_3",ldlc_3);
			Result ldlc_4=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL SerPl UC Qn").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_4",ldlc_4);
			Result ldlc_5=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL Direct SerPl Qn").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_5",ldlc_5);
			Result ldlc_6=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL Total Direct SerPl Qn UC").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_6",ldlc_6);
			Result ldlc_7=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL Total Sum Direct SerPl Qn UC").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_7",ldlc_7);
			Result ldlc_8=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL SerPl Qn mmol/L").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_8",ldlc_8);
			Result ldlc_9=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LIPID PROFILE").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_9",ldlc_9);
			Result ldlc_10=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL SerPl Qn Calc").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_10",ldlc_10);
			Result ldlc_11=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("LDL SerPl Qn Elp").within(Duration.years(-1)).last());
			resultLookup.put("ldlc_11",ldlc_11);
			Result ldlc_12=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hosp Procedures").within(Duration.years(-1)));
			resultLookup.put("ldlc_12",ldlc_12);}

			if(evaluate_logic(parameters)){
				Result ruleResult = new Result();
		Result ldlc_10 = (Result) resultLookup.get("ldlc_10");
		Result ldlc_8 = (Result) resultLookup.get("ldlc_8");
		Result ldlc_9 = (Result) resultLookup.get("ldlc_9");
		Result ldlc_11 = (Result) resultLookup.get("ldlc_11");
		Result ldlc_1 = (Result) resultLookup.get("ldlc_1");
		Result ldlc_12 = (Result) resultLookup.get("ldlc_12");
		Result ldlc_2 = (Result) resultLookup.get("ldlc_2");
		Result ldlc_3 = (Result) resultLookup.get("ldlc_3");
		Result ldlc_4 = (Result) resultLookup.get("ldlc_4");
		Result ldlc_5 = (Result) resultLookup.get("ldlc_5");
		Result ldlc_6 = (Result) resultLookup.get("ldlc_6");
		Result ldlc_7 = (Result) resultLookup.get("ldlc_7");

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
		Result ldlc_10 = (Result) resultLookup.get("ldlc_10");
		Result mode = (Result) resultLookup.get("mode");
		Result ldlc_8 = (Result) resultLookup.get("ldlc_8");
		Result ldlc_9 = (Result) resultLookup.get("ldlc_9");
		Result ldlc_11 = (Result) resultLookup.get("ldlc_11");
		Result ldlc_1 = (Result) resultLookup.get("ldlc_1");
		Result ldlc_12 = (Result) resultLookup.get("ldlc_12");
		Result ldlc_2 = (Result) resultLookup.get("ldlc_2");
		Result ldlc_3 = (Result) resultLookup.get("ldlc_3");
		Result ldlc_4 = (Result) resultLookup.get("ldlc_4");
		Result ldlc_5 = (Result) resultLookup.get("ldlc_5");
		Result ldlc_6 = (Result) resultLookup.get("ldlc_6");
		Result ldlc_7 = (Result) resultLookup.get("ldlc_7");

				Object value = null;
				String variable = null;
				int varLen = 0;
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
				varLen = "LDL-C <100 MG/DL".length();
				value=userVarMap.get("LDL-C <100 MG/DL");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("LDL-C <100 MG/DL".endsWith("_value"))
				{
					variable = "LDL-C <100 MG/DL".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("LDL-C <100 MG/DL".endsWith("_date"))
				{
					variable = "LDL-C <100 MG/DL".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("LDL-C <100 MG/DL".endsWith("_object"))
				{
					variable = "LDL-C <100 MG/DL".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("LDL-C <100 MG/DL") != null){
						value = resultLookup.get("LDL-C <100 MG/DL").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","LDL-C <100 MG/DL");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result1 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result1",result1);
				varLen = "LDL-C 100-129 MG/DL".length();
				value=userVarMap.get("LDL-C 100-129 MG/DL");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("LDL-C 100-129 MG/DL".endsWith("_value"))
				{
					variable = "LDL-C 100-129 MG/DL".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("LDL-C 100-129 MG/DL".endsWith("_date"))
				{
					variable = "LDL-C 100-129 MG/DL".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("LDL-C 100-129 MG/DL".endsWith("_object"))
				{
					variable = "LDL-C 100-129 MG/DL".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("LDL-C 100-129 MG/DL") != null){
						value = resultLookup.get("LDL-C 100-129 MG/DL").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","LDL-C 100-129 MG/DL");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result2 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result2",result2);
				varLen = "LDL-C>= 130 MG/DL".length();
				value=userVarMap.get("LDL-C>= 130 MG/DL");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("LDL-C>= 130 MG/DL".endsWith("_value"))
				{
					variable = "LDL-C>= 130 MG/DL".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("LDL-C>= 130 MG/DL".endsWith("_date"))
				{
					variable = "LDL-C>= 130 MG/DL".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("LDL-C>= 130 MG/DL".endsWith("_object"))
				{
					variable = "LDL-C>= 130 MG/DL".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("LDL-C>= 130 MG/DL") != null){
						value = resultLookup.get("LDL-C>= 130 MG/DL").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","LDL-C>= 130 MG/DL");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result3 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result3",result3);
				varLen = "LIPID PANEL".length();
				value=userVarMap.get("LIPID PANEL");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("LIPID PANEL".endsWith("_value"))
				{
					variable = "LIPID PANEL".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("LIPID PANEL".endsWith("_date"))
				{
					variable = "LIPID PANEL".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("LIPID PANEL".endsWith("_object"))
				{
					variable = "LIPID PANEL".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("LIPID PANEL") != null){
						value = resultLookup.get("LIPID PANEL").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","LIPID PANEL");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result4 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result4",result4);
				varLen = "LIPIDS, BLOOD; TOTAL".length();
				value=userVarMap.get("LIPIDS, BLOOD; TOTAL");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("LIPIDS, BLOOD; TOTAL".endsWith("_value"))
				{
					variable = "LIPIDS, BLOOD; TOTAL".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("LIPIDS, BLOOD; TOTAL".endsWith("_date"))
				{
					variable = "LIPIDS, BLOOD; TOTAL".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("LIPIDS, BLOOD; TOTAL".endsWith("_object"))
				{
					variable = "LIPIDS, BLOOD; TOTAL".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("LIPIDS, BLOOD; TOTAL") != null){
						value = resultLookup.get("LIPIDS, BLOOD; TOTAL").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","LIPIDS, BLOOD; TOTAL");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result5 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result5",result5);
				varLen = "LIPOPROTEIN BLD, HR FRACT".length();
				value=userVarMap.get("LIPOPROTEIN BLD, HR FRACT");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("LIPOPROTEIN BLD, HR FRACT".endsWith("_value"))
				{
					variable = "LIPOPROTEIN BLD, HR FRACT".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("LIPOPROTEIN BLD, HR FRACT".endsWith("_date"))
				{
					variable = "LIPOPROTEIN BLD, HR FRACT".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("LIPOPROTEIN BLD, HR FRACT".endsWith("_object"))
				{
					variable = "LIPOPROTEIN BLD, HR FRACT".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("LIPOPROTEIN BLD, HR FRACT") != null){
						value = resultLookup.get("LIPOPROTEIN BLD, HR FRACT").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","LIPOPROTEIN BLD, HR FRACT");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result6 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result6",result6);
				varLen = "LIPOPROTEIN, BLD, BY NMR".length();
				value=userVarMap.get("LIPOPROTEIN, BLD, BY NMR");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("LIPOPROTEIN, BLD, BY NMR".endsWith("_value"))
				{
					variable = "LIPOPROTEIN, BLD, BY NMR".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("LIPOPROTEIN, BLD, BY NMR".endsWith("_date"))
				{
					variable = "LIPOPROTEIN, BLD, BY NMR".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("LIPOPROTEIN, BLD, BY NMR".endsWith("_object"))
				{
					variable = "LIPOPROTEIN, BLD, BY NMR".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("LIPOPROTEIN, BLD, BY NMR") != null){
						value = resultLookup.get("LIPOPROTEIN, BLD, BY NMR").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","LIPOPROTEIN, BLD, BY NMR");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result7 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result7",result7);
				varLen = "ASSAY OF BLOOD LIPOPROTEIN".length();
				value=userVarMap.get("ASSAY OF BLOOD LIPOPROTEIN");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("ASSAY OF BLOOD LIPOPROTEIN".endsWith("_value"))
				{
					variable = "ASSAY OF BLOOD LIPOPROTEIN".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ASSAY OF BLOOD LIPOPROTEIN".endsWith("_date"))
				{
					variable = "ASSAY OF BLOOD LIPOPROTEIN".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ASSAY OF BLOOD LIPOPROTEIN".endsWith("_object"))
				{
					variable = "ASSAY OF BLOOD LIPOPROTEIN".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ASSAY OF BLOOD LIPOPROTEIN") != null){
						value = resultLookup.get("ASSAY OF BLOOD LIPOPROTEIN").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","ASSAY OF BLOOD LIPOPROTEIN");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result8 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result8",result8);
				varLen = "ASSAY OF BLOOD LIPOPROTEINS".length();
				value=userVarMap.get("ASSAY OF BLOOD LIPOPROTEINS");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("ASSAY OF BLOOD LIPOPROTEINS".endsWith("_value"))
				{
					variable = "ASSAY OF BLOOD LIPOPROTEINS".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ASSAY OF BLOOD LIPOPROTEINS".endsWith("_date"))
				{
					variable = "ASSAY OF BLOOD LIPOPROTEINS".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ASSAY OF BLOOD LIPOPROTEINS".endsWith("_object"))
				{
					variable = "ASSAY OF BLOOD LIPOPROTEINS".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ASSAY OF BLOOD LIPOPROTEINS") != null){
						value = resultLookup.get("ASSAY OF BLOOD LIPOPROTEINS").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","ASSAY OF BLOOD LIPOPROTEINS");
				}
				varLen = "ldlc_12_object".length();
				value=userVarMap.get("ldlc_12_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_12_object".endsWith("_value"))
				{
					variable = "ldlc_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_12_object".endsWith("_date"))
				{
					variable = "ldlc_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_12_object".endsWith("_object"))
				{
					variable = "ldlc_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_12_object") != null){
						value = resultLookup.get("ldlc_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_12_object");
				}
				Result result9 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result9",result9);
				varLen = "ldlc_1_object".length();
				value=userVarMap.get("ldlc_1_object");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("ldlc_1_object".endsWith("_value"))
				{
					variable = "ldlc_1_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_1_object".endsWith("_date"))
				{
					variable = "ldlc_1_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_1_object".endsWith("_object"))
				{
					variable = "ldlc_1_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_1_object") != null){
						value = resultLookup.get("ldlc_1_object").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","ldlc_1_object");
				}
				varLen = "ldlc_2_object".length();
				value=userVarMap.get("ldlc_2_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("ldlc_2_object".endsWith("_value"))
				{
					variable = "ldlc_2_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_2_object".endsWith("_date"))
				{
					variable = "ldlc_2_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_2_object".endsWith("_object"))
				{
					variable = "ldlc_2_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_2_object") != null){
						value = resultLookup.get("ldlc_2_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","ldlc_2_object");
				}
				varLen = "ldlc_3_object".length();
				value=userVarMap.get("ldlc_3_object");
				if(value != null){
					parameters.put("param3",value);
				}
				// It must be a result value or date
				else if("ldlc_3_object".endsWith("_value"))
				{
					variable = "ldlc_3_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_3_object".endsWith("_date"))
				{
					variable = "ldlc_3_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_3_object".endsWith("_object"))
				{
					variable = "ldlc_3_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_3_object") != null){
						value = resultLookup.get("ldlc_3_object").toString();
					}
				}
				if(value != null){
					parameters.put("param3",value);
				}
				else
				{
					parameters.put("param3","ldlc_3_object");
				}
				varLen = "ldlc_4_object".length();
				value=userVarMap.get("ldlc_4_object");
				if(value != null){
					parameters.put("param4",value);
				}
				// It must be a result value or date
				else if("ldlc_4_object".endsWith("_value"))
				{
					variable = "ldlc_4_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_4_object".endsWith("_date"))
				{
					variable = "ldlc_4_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_4_object".endsWith("_object"))
				{
					variable = "ldlc_4_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_4_object") != null){
						value = resultLookup.get("ldlc_4_object").toString();
					}
				}
				if(value != null){
					parameters.put("param4",value);
				}
				else
				{
					parameters.put("param4","ldlc_4_object");
				}
				varLen = "ldlc_5_object".length();
				value=userVarMap.get("ldlc_5_object");
				if(value != null){
					parameters.put("param5",value);
				}
				// It must be a result value or date
				else if("ldlc_5_object".endsWith("_value"))
				{
					variable = "ldlc_5_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_5_object".endsWith("_date"))
				{
					variable = "ldlc_5_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_5_object".endsWith("_object"))
				{
					variable = "ldlc_5_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_5_object") != null){
						value = resultLookup.get("ldlc_5_object").toString();
					}
				}
				if(value != null){
					parameters.put("param5",value);
				}
				else
				{
					parameters.put("param5","ldlc_5_object");
				}
				varLen = "ldlc_6_object".length();
				value=userVarMap.get("ldlc_6_object");
				if(value != null){
					parameters.put("param6",value);
				}
				// It must be a result value or date
				else if("ldlc_6_object".endsWith("_value"))
				{
					variable = "ldlc_6_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_6_object".endsWith("_date"))
				{
					variable = "ldlc_6_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_6_object".endsWith("_object"))
				{
					variable = "ldlc_6_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_6_object") != null){
						value = resultLookup.get("ldlc_6_object").toString();
					}
				}
				if(value != null){
					parameters.put("param6",value);
				}
				else
				{
					parameters.put("param6","ldlc_6_object");
				}
				varLen = "ldlc_7_object".length();
				value=userVarMap.get("ldlc_7_object");
				if(value != null){
					parameters.put("param7",value);
				}
				// It must be a result value or date
				else if("ldlc_7_object".endsWith("_value"))
				{
					variable = "ldlc_7_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_7_object".endsWith("_date"))
				{
					variable = "ldlc_7_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_7_object".endsWith("_object"))
				{
					variable = "ldlc_7_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_7_object") != null){
						value = resultLookup.get("ldlc_7_object").toString();
					}
				}
				if(value != null){
					parameters.put("param7",value);
				}
				else
				{
					parameters.put("param7","ldlc_7_object");
				}
				varLen = "ldlc_8_object".length();
				value=userVarMap.get("ldlc_8_object");
				if(value != null){
					parameters.put("param8",value);
				}
				// It must be a result value or date
				else if("ldlc_8_object".endsWith("_value"))
				{
					variable = "ldlc_8_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_8_object".endsWith("_date"))
				{
					variable = "ldlc_8_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_8_object".endsWith("_object"))
				{
					variable = "ldlc_8_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_8_object") != null){
						value = resultLookup.get("ldlc_8_object").toString();
					}
				}
				if(value != null){
					parameters.put("param8",value);
				}
				else
				{
					parameters.put("param8","ldlc_8_object");
				}
				varLen = "ldlc_9_object".length();
				value=userVarMap.get("ldlc_9_object");
				if(value != null){
					parameters.put("param9",value);
				}
				// It must be a result value or date
				else if("ldlc_9_object".endsWith("_value"))
				{
					variable = "ldlc_9_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_9_object".endsWith("_date"))
				{
					variable = "ldlc_9_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_9_object".endsWith("_object"))
				{
					variable = "ldlc_9_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_9_object") != null){
						value = resultLookup.get("ldlc_9_object").toString();
					}
				}
				if(value != null){
					parameters.put("param9",value);
				}
				else
				{
					parameters.put("param9","ldlc_9_object");
				}
				varLen = "result1_object".length();
				value=userVarMap.get("result1_object");
				if(value != null){
					parameters.put("param10",value);
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
					parameters.put("param10",value);
				}
				else
				{
					parameters.put("param10","result1_object");
				}
				varLen = "result2_object".length();
				value=userVarMap.get("result2_object");
				if(value != null){
					parameters.put("param11",value);
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
					parameters.put("param11",value);
				}
				else
				{
					parameters.put("param11","result2_object");
				}
				varLen = "result3_object".length();
				value=userVarMap.get("result3_object");
				if(value != null){
					parameters.put("param12",value);
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
					parameters.put("param12",value);
				}
				else
				{
					parameters.put("param12","result3_object");
				}
				varLen = "result4_object".length();
				value=userVarMap.get("result4_object");
				if(value != null){
					parameters.put("param13",value);
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
					parameters.put("param13",value);
				}
				else
				{
					parameters.put("param13","result4_object");
				}
				varLen = "result5_object".length();
				value=userVarMap.get("result5_object");
				if(value != null){
					parameters.put("param14",value);
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
					parameters.put("param14",value);
				}
				else
				{
					parameters.put("param14","result5_object");
				}
				varLen = "result6_object".length();
				value=userVarMap.get("result6_object");
				if(value != null){
					parameters.put("param15",value);
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
					parameters.put("param15",value);
				}
				else
				{
					parameters.put("param15","result6_object");
				}
				varLen = "ldlc_10_object".length();
				value=userVarMap.get("ldlc_10_object");
				if(value != null){
					parameters.put("param16",value);
				}
				// It must be a result value or date
				else if("ldlc_10_object".endsWith("_value"))
				{
					variable = "ldlc_10_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_10_object".endsWith("_date"))
				{
					variable = "ldlc_10_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_10_object".endsWith("_object"))
				{
					variable = "ldlc_10_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_10_object") != null){
						value = resultLookup.get("ldlc_10_object").toString();
					}
				}
				if(value != null){
					parameters.put("param16",value);
				}
				else
				{
					parameters.put("param16","ldlc_10_object");
				}
				varLen = "ldlc_11_object".length();
				value=userVarMap.get("ldlc_11_object");
				if(value != null){
					parameters.put("param17",value);
				}
				// It must be a result value or date
				else if("ldlc_11_object".endsWith("_value"))
				{
					variable = "ldlc_11_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ldlc_11_object".endsWith("_date"))
				{
					variable = "ldlc_11_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ldlc_11_object".endsWith("_object"))
				{
					variable = "ldlc_11_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ldlc_11_object") != null){
						value = resultLookup.get("ldlc_11_object").toString();
					}
				}
				if(value != null){
					parameters.put("param17",value);
				}
				else
				{
					parameters.put("param17","ldlc_11_object");
				}
				varLen = "result7_object".length();
				value=userVarMap.get("result7_object");
				if(value != null){
					parameters.put("param18",value);
				}
				// It must be a result value or date
				else if("result7_object".endsWith("_value"))
				{
					variable = "result7_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result7_object".endsWith("_date"))
				{
					variable = "result7_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result7_object".endsWith("_object"))
				{
					variable = "result7_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result7_object") != null){
						value = resultLookup.get("result7_object").toString();
					}
				}
				if(value != null){
					parameters.put("param18",value);
				}
				else
				{
					parameters.put("param18","result7_object");
				}
				varLen = "result8_object".length();
				value=userVarMap.get("result8_object");
				if(value != null){
					parameters.put("param19",value);
				}
				// It must be a result value or date
				else if("result8_object".endsWith("_value"))
				{
					variable = "result8_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result8_object".endsWith("_date"))
				{
					variable = "result8_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result8_object".endsWith("_object"))
				{
					variable = "result8_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result8_object") != null){
						value = resultLookup.get("result8_object").toString();
					}
				}
				if(value != null){
					parameters.put("param19",value);
				}
				else
				{
					parameters.put("param19","result8_object");
				}
				varLen = "result9_object".length();
				value=userVarMap.get("result9_object");
				if(value != null){
					parameters.put("param20",value);
				}
				// It must be a result value or date
				else if("result9_object".endsWith("_value"))
				{
					variable = "result9_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("result9_object".endsWith("_date"))
				{
					variable = "result9_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("result9_object".endsWith("_object"))
				{
					variable = "result9_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("result9_object") != null){
						value = resultLookup.get("result9_object").toString();
					}
				}
				if(value != null){
					parameters.put("param20",value);
				}
				else
				{
					parameters.put("param20","result9_object");
				}
				Result finalResult = logicService.eval(patient, "mostRecentResult",parameters);
				resultLookup.put("finalResult",finalResult);
			return true;
		}
	return false;	}

	public void initAction() {
		this.actions = new ArrayList<String>();
		actions.add("|| finalResult_date ||@ldlcResultDate");
		actions.add("|| finalResult_value ||@ldlcResultValue");
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