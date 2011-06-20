/********************************************************************
 Translated from - Diabetes_Micro.mlm on Thu Dec 02 14:00:53 EST 2010

 Title:  Diabetes_Micro
 Filename:  Diabetes_Micro
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Tammy Dugan
 Specialist:  Pediatrics
 Date: 2010-08-03T11:00:00-0400
 Validation :
 Purpose:  Lookup up Microalbumin results for diabetic patients.
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
public class Diabetes_Micro implements Rule, DssRule{

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
		return "Lookup up Microalbumin results for diabetic patients.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Pediatrics";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "Diabetes_Micro";
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
		return "read If read read read read read read read read read read read read read read read read read read read read read read read read read read read read read read read read read read endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getLogic()*/
	public String getLogic(){
		return "If call call call call call call call call call call call call call call conclude endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "write write write write write write write write";
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
		System.out.println("test this");

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

			Result micro_1=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein 12H Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_1",micro_1);
			Result micro_2=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Alb/Creat 24H Ur Mass Rto").within(Duration.years(-1)).last());
			resultLookup.put("micro_2",micro_2);
			Result micro_3=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein/creat 24H Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_3",micro_3);
			Result micro_4=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin/Min Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_4",micro_4);
			Result micro_5=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin 24H Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_5",micro_5);
			Result micro_6=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin R-Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_6",micro_6);
			Result micro_7=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_7",micro_7);
			Result micro_8=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_8",micro_8);
			Result micro_9=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin Ur Qn mg/dL").within(Duration.years(-1)).last());
			resultLookup.put("micro_9",micro_9);
			Result micro_10=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin/Creat 24H Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_10",micro_10);
			Result micro_11=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin/Creat Ur mcg/mg Cr").within(Duration.years(-1)).last());
			resultLookup.put("micro_11",micro_11);
			Result micro_12=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin/Creat Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_12",micro_12);
			Result micro_13=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin Ur QL").within(Duration.years(-1)).last());
			resultLookup.put("micro_13",micro_13);
			Result micro_14=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin R-Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_14",micro_14);
			Result micro_15=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin Ur Qn (POC)").within(Duration.years(-1)).last());
			resultLookup.put("micro_15",micro_15);
			Result micro_16=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin 24H Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_16",micro_16);
			Result micro_17=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein 24H Cnc'").within(Duration.years(-1)).last());
			resultLookup.put("micro_17",micro_17);
			Result micro_18=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein Timed Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_18",micro_18);
			Result micro_19=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein R-Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_19",micro_19);
			Result micro_20=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein-Lcf Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_20",micro_20);
			Result micro_21=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein-Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_21",micro_21);
			Result micro_22=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein (Ur Elp)").within(Duration.years(-1)).last());
			resultLookup.put("micro_22",micro_22);
			Result micro_23=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein 24H Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_23",micro_23);
			Result micro_24=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein 24H Ur Qn Elp").within(Duration.years(-1)).last());
			resultLookup.put("micro_24",micro_24);
			Result micro_25=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein TV Ur Qn").within(Duration.years(-1)).last());
			resultLookup.put("micro_25",micro_25);
			Result micro_26=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein R-Ur Qn mg/gm Cr").within(Duration.years(-1)).last());
			resultLookup.put("micro_26",micro_26);
			Result micro_27=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Protein/Creat Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_27",micro_27);
			Result micro_28=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin 24H Ur Cnc").within(Duration.years(-1)).last());
			resultLookup.put("micro_28",micro_28);
			Result micro_29=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin/Creat Rnd Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_29",micro_29);
			Result micro_30=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin/Creat Ur").within(Duration.years(-1)).last());
			resultLookup.put("micro_30",micro_30);
			Result micro_31=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Albumin/Creat Ur Ratio (POC)").within(Duration.years(-1)).last());
			resultLookup.put("micro_31",micro_31);
			Result micro_32=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin Ur Excretion Rate").within(Duration.years(-1)).last());
			resultLookup.put("micro_32",micro_32);
			Result micro_33=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Microalbumin Ur Panel").within(Duration.years(-1)).last());
			resultLookup.put("micro_33",micro_33);
			Result micro_34=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("R-UR ALBUMIN/CREAT").within(Duration.years(-1)).last());
			resultLookup.put("micro_34",micro_34);
			Result micro_35=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hosp Procedures").within(Duration.years(-1)));
			resultLookup.put("micro_35",micro_35);}

			if(evaluate_logic(parameters)){
				Result ruleResult = new Result();
		Result micro_14 = (Result) resultLookup.get("micro_14");
		Result micro_15 = (Result) resultLookup.get("micro_15");
		Result micro_12 = (Result) resultLookup.get("micro_12");
		Result micro_13 = (Result) resultLookup.get("micro_13");
		Result micro_10 = (Result) resultLookup.get("micro_10");
		Result micro_11 = (Result) resultLookup.get("micro_11");
		Result micro_33 = (Result) resultLookup.get("micro_33");
		Result micro_32 = (Result) resultLookup.get("micro_32");
		Result micro_31 = (Result) resultLookup.get("micro_31");
		Result micro_30 = (Result) resultLookup.get("micro_30");
		Result micro_35 = (Result) resultLookup.get("micro_35");
		Result micro_34 = (Result) resultLookup.get("micro_34");
		Result micro_29 = (Result) resultLookup.get("micro_29");
		Result micro_27 = (Result) resultLookup.get("micro_27");
		Result micro_28 = (Result) resultLookup.get("micro_28");
		Result micro_7 = (Result) resultLookup.get("micro_7");
		Result micro_8 = (Result) resultLookup.get("micro_8");
		Result micro_9 = (Result) resultLookup.get("micro_9");
		Result micro_3 = (Result) resultLookup.get("micro_3");
		Result micro_4 = (Result) resultLookup.get("micro_4");
		Result micro_5 = (Result) resultLookup.get("micro_5");
		Result micro_6 = (Result) resultLookup.get("micro_6");
		Result micro_20 = (Result) resultLookup.get("micro_20");
		Result micro_1 = (Result) resultLookup.get("micro_1");
		Result micro_22 = (Result) resultLookup.get("micro_22");
		Result micro_2 = (Result) resultLookup.get("micro_2");
		Result micro_21 = (Result) resultLookup.get("micro_21");
		Result micro_24 = (Result) resultLookup.get("micro_24");
		Result micro_23 = (Result) resultLookup.get("micro_23");
		Result micro_26 = (Result) resultLookup.get("micro_26");
		Result micro_25 = (Result) resultLookup.get("micro_25");
		Result micro_16 = (Result) resultLookup.get("micro_16");
		Result micro_17 = (Result) resultLookup.get("micro_17");
		Result micro_18 = (Result) resultLookup.get("micro_18");
		Result micro_19 = (Result) resultLookup.get("micro_19");

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
		Result micro_14 = (Result) resultLookup.get("micro_14");
		Result micro_15 = (Result) resultLookup.get("micro_15");
		Result micro_12 = (Result) resultLookup.get("micro_12");
		Result micro_13 = (Result) resultLookup.get("micro_13");
		Result micro_10 = (Result) resultLookup.get("micro_10");
		Result micro_11 = (Result) resultLookup.get("micro_11");
		Result micro_33 = (Result) resultLookup.get("micro_33");
		Result micro_32 = (Result) resultLookup.get("micro_32");
		Result micro_31 = (Result) resultLookup.get("micro_31");
		Result micro_30 = (Result) resultLookup.get("micro_30");
		Result micro_35 = (Result) resultLookup.get("micro_35");
		Result micro_34 = (Result) resultLookup.get("micro_34");
		Result mode = (Result) resultLookup.get("mode");
		Result micro_29 = (Result) resultLookup.get("micro_29");
		Result micro_27 = (Result) resultLookup.get("micro_27");
		Result micro_28 = (Result) resultLookup.get("micro_28");
		Result micro_7 = (Result) resultLookup.get("micro_7");
		Result micro_8 = (Result) resultLookup.get("micro_8");
		Result micro_9 = (Result) resultLookup.get("micro_9");
		Result micro_3 = (Result) resultLookup.get("micro_3");
		Result micro_4 = (Result) resultLookup.get("micro_4");
		Result micro_5 = (Result) resultLookup.get("micro_5");
		Result micro_6 = (Result) resultLookup.get("micro_6");
		Result micro_20 = (Result) resultLookup.get("micro_20");
		Result micro_1 = (Result) resultLookup.get("micro_1");
		Result micro_22 = (Result) resultLookup.get("micro_22");
		Result micro_2 = (Result) resultLookup.get("micro_2");
		Result micro_21 = (Result) resultLookup.get("micro_21");
		Result micro_24 = (Result) resultLookup.get("micro_24");
		Result micro_23 = (Result) resultLookup.get("micro_23");
		Result micro_26 = (Result) resultLookup.get("micro_26");
		Result micro_25 = (Result) resultLookup.get("micro_25");
		Result micro_16 = (Result) resultLookup.get("micro_16");
		Result micro_17 = (Result) resultLookup.get("micro_17");
		Result micro_18 = (Result) resultLookup.get("micro_18");
		Result micro_19 = (Result) resultLookup.get("micro_19");

				Object value = null;
				String variable = null;
				int varLen = 0;
		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
				varLen = "POS MICROALBUMINURIA REV".length();
				value=userVarMap.get("POS MICROALBUMINURIA REV");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("POS MICROALBUMINURIA REV".endsWith("_value"))
				{
					variable = "POS MICROALBUMINURIA REV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("POS MICROALBUMINURIA REV".endsWith("_date"))
				{
					variable = "POS MICROALBUMINURIA REV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("POS MICROALBUMINURIA REV".endsWith("_object"))
				{
					variable = "POS MICROALBUMINURIA REV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("POS MICROALBUMINURIA REV") != null){
						value = resultLookup.get("POS MICROALBUMINURIA REV").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","POS MICROALBUMINURIA REV");
				}
				varLen = "micro_35_object".length();
				value=userVarMap.get("micro_35_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_35_object".endsWith("_value"))
				{
					variable = "micro_35_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_35_object".endsWith("_date"))
				{
					variable = "micro_35_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_35_object".endsWith("_object"))
				{
					variable = "micro_35_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_35_object") != null){
						value = resultLookup.get("micro_35_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_35_object");
				}
				Result result1 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result1",result1);
				varLen = "NEG MICROALBUMINURIA REV".length();
				value=userVarMap.get("NEG MICROALBUMINURIA REV");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("NEG MICROALBUMINURIA REV".endsWith("_value"))
				{
					variable = "NEG MICROALBUMINURIA REV".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("NEG MICROALBUMINURIA REV".endsWith("_date"))
				{
					variable = "NEG MICROALBUMINURIA REV".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("NEG MICROALBUMINURIA REV".endsWith("_object"))
				{
					variable = "NEG MICROALBUMINURIA REV".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("NEG MICROALBUMINURIA REV") != null){
						value = resultLookup.get("NEG MICROALBUMINURIA REV").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","NEG MICROALBUMINURIA REV");
				}
				varLen = "micro_35_object".length();
				value=userVarMap.get("micro_35_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_35_object".endsWith("_value"))
				{
					variable = "micro_35_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_35_object".endsWith("_date"))
				{
					variable = "micro_35_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_35_object".endsWith("_object"))
				{
					variable = "micro_35_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_35_object") != null){
						value = resultLookup.get("micro_35_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_35_object");
				}
				Result result2 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result2",result2);
				varLen = "ASSAY OF URINE ALBUMIN".length();
				value=userVarMap.get("ASSAY OF URINE ALBUMIN");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("ASSAY OF URINE ALBUMIN".endsWith("_value"))
				{
					variable = "ASSAY OF URINE ALBUMIN".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ASSAY OF URINE ALBUMIN".endsWith("_date"))
				{
					variable = "ASSAY OF URINE ALBUMIN".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ASSAY OF URINE ALBUMIN".endsWith("_object"))
				{
					variable = "ASSAY OF URINE ALBUMIN".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ASSAY OF URINE ALBUMIN") != null){
						value = resultLookup.get("ASSAY OF URINE ALBUMIN").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","ASSAY OF URINE ALBUMIN");
				}
				varLen = "micro_35_object".length();
				value=userVarMap.get("micro_35_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_35_object".endsWith("_value"))
				{
					variable = "micro_35_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_35_object".endsWith("_date"))
				{
					variable = "micro_35_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_35_object".endsWith("_object"))
				{
					variable = "micro_35_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_35_object") != null){
						value = resultLookup.get("micro_35_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_35_object");
				}
				Result result3 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result3",result3);
				varLen = "MICROALBUMIN, QUANTITATIVE".length();
				value=userVarMap.get("MICROALBUMIN, QUANTITATIVE");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("MICROALBUMIN, QUANTITATIVE".endsWith("_value"))
				{
					variable = "MICROALBUMIN, QUANTITATIVE".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("MICROALBUMIN, QUANTITATIVE".endsWith("_date"))
				{
					variable = "MICROALBUMIN, QUANTITATIVE".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("MICROALBUMIN, QUANTITATIVE".endsWith("_object"))
				{
					variable = "MICROALBUMIN, QUANTITATIVE".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("MICROALBUMIN, QUANTITATIVE") != null){
						value = resultLookup.get("MICROALBUMIN, QUANTITATIVE").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","MICROALBUMIN, QUANTITATIVE");
				}
				varLen = "micro_35_object".length();
				value=userVarMap.get("micro_35_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_35_object".endsWith("_value"))
				{
					variable = "micro_35_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_35_object".endsWith("_date"))
				{
					variable = "micro_35_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_35_object".endsWith("_object"))
				{
					variable = "micro_35_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_35_object") != null){
						value = resultLookup.get("micro_35_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_35_object");
				}
				Result result4 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result4",result4);
				varLen = "MICROALBUMIN, SEMIQUANT".length();
				value=userVarMap.get("MICROALBUMIN, SEMIQUANT");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("MICROALBUMIN, SEMIQUANT".endsWith("_value"))
				{
					variable = "MICROALBUMIN, SEMIQUANT".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("MICROALBUMIN, SEMIQUANT".endsWith("_date"))
				{
					variable = "MICROALBUMIN, SEMIQUANT".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("MICROALBUMIN, SEMIQUANT".endsWith("_object"))
				{
					variable = "MICROALBUMIN, SEMIQUANT".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("MICROALBUMIN, SEMIQUANT") != null){
						value = resultLookup.get("MICROALBUMIN, SEMIQUANT").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","MICROALBUMIN, SEMIQUANT");
				}
				varLen = "micro_35_object".length();
				value=userVarMap.get("micro_35_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_35_object".endsWith("_value"))
				{
					variable = "micro_35_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_35_object".endsWith("_date"))
				{
					variable = "micro_35_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_35_object".endsWith("_object"))
				{
					variable = "micro_35_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_35_object") != null){
						value = resultLookup.get("micro_35_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_35_object");
				}
				Result result5 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result5",result5);
				varLen = "ASSAY OF PROTEIN, URINE".length();
				value=userVarMap.get("ASSAY OF PROTEIN, URINE");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("ASSAY OF PROTEIN, URINE".endsWith("_value"))
				{
					variable = "ASSAY OF PROTEIN, URINE".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("ASSAY OF PROTEIN, URINE".endsWith("_date"))
				{
					variable = "ASSAY OF PROTEIN, URINE".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("ASSAY OF PROTEIN, URINE".endsWith("_object"))
				{
					variable = "ASSAY OF PROTEIN, URINE".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("ASSAY OF PROTEIN, URINE") != null){
						value = resultLookup.get("ASSAY OF PROTEIN, URINE").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","ASSAY OF PROTEIN, URINE");
				}
				varLen = "micro_35_object".length();
				value=userVarMap.get("micro_35_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_35_object".endsWith("_value"))
				{
					variable = "micro_35_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_35_object".endsWith("_date"))
				{
					variable = "micro_35_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_35_object".endsWith("_object"))
				{
					variable = "micro_35_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_35_object") != null){
						value = resultLookup.get("micro_35_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_35_object");
				}
				Result result6 = logicService.eval(patient, "mostRecentResultWithAnswer",parameters);
				resultLookup.put("result6",result6);
				varLen = "micro_1_object".length();
				value=userVarMap.get("micro_1_object");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("micro_1_object".endsWith("_value"))
				{
					variable = "micro_1_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_1_object".endsWith("_date"))
				{
					variable = "micro_1_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_1_object".endsWith("_object"))
				{
					variable = "micro_1_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_1_object") != null){
						value = resultLookup.get("micro_1_object").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","micro_1_object");
				}
				varLen = "micro_2_object".length();
				value=userVarMap.get("micro_2_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_2_object".endsWith("_value"))
				{
					variable = "micro_2_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_2_object".endsWith("_date"))
				{
					variable = "micro_2_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_2_object".endsWith("_object"))
				{
					variable = "micro_2_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_2_object") != null){
						value = resultLookup.get("micro_2_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_2_object");
				}
				varLen = "micro_3_object".length();
				value=userVarMap.get("micro_3_object");
				if(value != null){
					parameters.put("param3",value);
				}
				// It must be a result value or date
				else if("micro_3_object".endsWith("_value"))
				{
					variable = "micro_3_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_3_object".endsWith("_date"))
				{
					variable = "micro_3_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_3_object".endsWith("_object"))
				{
					variable = "micro_3_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_3_object") != null){
						value = resultLookup.get("micro_3_object").toString();
					}
				}
				if(value != null){
					parameters.put("param3",value);
				}
				else
				{
					parameters.put("param3","micro_3_object");
				}
				varLen = "micro_4_object".length();
				value=userVarMap.get("micro_4_object");
				if(value != null){
					parameters.put("param4",value);
				}
				// It must be a result value or date
				else if("micro_4_object".endsWith("_value"))
				{
					variable = "micro_4_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_4_object".endsWith("_date"))
				{
					variable = "micro_4_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_4_object".endsWith("_object"))
				{
					variable = "micro_4_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_4_object") != null){
						value = resultLookup.get("micro_4_object").toString();
					}
				}
				if(value != null){
					parameters.put("param4",value);
				}
				else
				{
					parameters.put("param4","micro_4_object");
				}
				varLen = "micro_5_object".length();
				value=userVarMap.get("micro_5_object");
				if(value != null){
					parameters.put("param5",value);
				}
				// It must be a result value or date
				else if("micro_5_object".endsWith("_value"))
				{
					variable = "micro_5_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_5_object".endsWith("_date"))
				{
					variable = "micro_5_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_5_object".endsWith("_object"))
				{
					variable = "micro_5_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_5_object") != null){
						value = resultLookup.get("micro_5_object").toString();
					}
				}
				if(value != null){
					parameters.put("param5",value);
				}
				else
				{
					parameters.put("param5","micro_5_object");
				}
				varLen = "micro_6_object".length();
				value=userVarMap.get("micro_6_object");
				if(value != null){
					parameters.put("param6",value);
				}
				// It must be a result value or date
				else if("micro_6_object".endsWith("_value"))
				{
					variable = "micro_6_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_6_object".endsWith("_date"))
				{
					variable = "micro_6_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_6_object".endsWith("_object"))
				{
					variable = "micro_6_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_6_object") != null){
						value = resultLookup.get("micro_6_object").toString();
					}
				}
				if(value != null){
					parameters.put("param6",value);
				}
				else
				{
					parameters.put("param6","micro_6_object");
				}
				varLen = "micro_7_object".length();
				value=userVarMap.get("micro_7_object");
				if(value != null){
					parameters.put("param7",value);
				}
				// It must be a result value or date
				else if("micro_7_object".endsWith("_value"))
				{
					variable = "micro_7_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_7_object".endsWith("_date"))
				{
					variable = "micro_7_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_7_object".endsWith("_object"))
				{
					variable = "micro_7_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_7_object") != null){
						value = resultLookup.get("micro_7_object").toString();
					}
				}
				if(value != null){
					parameters.put("param7",value);
				}
				else
				{
					parameters.put("param7","micro_7_object");
				}
				varLen = "micro_8_object".length();
				value=userVarMap.get("micro_8_object");
				if(value != null){
					parameters.put("param8",value);
				}
				// It must be a result value or date
				else if("micro_8_object".endsWith("_value"))
				{
					variable = "micro_8_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_8_object".endsWith("_date"))
				{
					variable = "micro_8_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_8_object".endsWith("_object"))
				{
					variable = "micro_8_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_8_object") != null){
						value = resultLookup.get("micro_8_object").toString();
					}
				}
				if(value != null){
					parameters.put("param8",value);
				}
				else
				{
					parameters.put("param8","micro_8_object");
				}
				varLen = "micro_9_object".length();
				value=userVarMap.get("micro_9_object");
				if(value != null){
					parameters.put("param9",value);
				}
				// It must be a result value or date
				else if("micro_9_object".endsWith("_value"))
				{
					variable = "micro_9_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_9_object".endsWith("_date"))
				{
					variable = "micro_9_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_9_object".endsWith("_object"))
				{
					variable = "micro_9_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_9_object") != null){
						value = resultLookup.get("micro_9_object").toString();
					}
				}
				if(value != null){
					parameters.put("param9",value);
				}
				else
				{
					parameters.put("param9","micro_9_object");
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
				varLen = "micro_10_object".length();
				value=userVarMap.get("micro_10_object");
				if(value != null){
					parameters.put("param16",value);
				}
				// It must be a result value or date
				else if("micro_10_object".endsWith("_value"))
				{
					variable = "micro_10_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_10_object".endsWith("_date"))
				{
					variable = "micro_10_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_10_object".endsWith("_object"))
				{
					variable = "micro_10_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_10_object") != null){
						value = resultLookup.get("micro_10_object").toString();
					}
				}
				if(value != null){
					parameters.put("param16",value);
				}
				else
				{
					parameters.put("param16","micro_10_object");
				}
				varLen = "micro_11_object".length();
				value=userVarMap.get("micro_11_object");
				if(value != null){
					parameters.put("param17",value);
				}
				// It must be a result value or date
				else if("micro_11_object".endsWith("_value"))
				{
					variable = "micro_11_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_11_object".endsWith("_date"))
				{
					variable = "micro_11_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_11_object".endsWith("_object"))
				{
					variable = "micro_11_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_11_object") != null){
						value = resultLookup.get("micro_11_object").toString();
					}
				}
				if(value != null){
					parameters.put("param17",value);
				}
				else
				{
					parameters.put("param17","micro_11_object");
				}
				varLen = "micro_12_object".length();
				value=userVarMap.get("micro_12_object");
				if(value != null){
					parameters.put("param18",value);
				}
				// It must be a result value or date
				else if("micro_12_object".endsWith("_value"))
				{
					variable = "micro_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_12_object".endsWith("_date"))
				{
					variable = "micro_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_12_object".endsWith("_object"))
				{
					variable = "micro_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_12_object") != null){
						value = resultLookup.get("micro_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param18",value);
				}
				else
				{
					parameters.put("param18","micro_12_object");
				}
				varLen = "micro_13_object".length();
				value=userVarMap.get("micro_13_object");
				if(value != null){
					parameters.put("param19",value);
				}
				// It must be a result value or date
				else if("micro_13_object".endsWith("_value"))
				{
					variable = "micro_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_13_object".endsWith("_date"))
				{
					variable = "micro_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_13_object".endsWith("_object"))
				{
					variable = "micro_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_13_object") != null){
						value = resultLookup.get("micro_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param19",value);
				}
				else
				{
					parameters.put("param19","micro_13_object");
				}
				varLen = "micro_14_object".length();
				value=userVarMap.get("micro_14_object");
				if(value != null){
					parameters.put("param20",value);
				}
				// It must be a result value or date
				else if("micro_14_object".endsWith("_value"))
				{
					variable = "micro_14_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_14_object".endsWith("_date"))
				{
					variable = "micro_14_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_14_object".endsWith("_object"))
				{
					variable = "micro_14_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_14_object") != null){
						value = resultLookup.get("micro_14_object").toString();
					}
				}
				if(value != null){
					parameters.put("param20",value);
				}
				else
				{
					parameters.put("param20","micro_14_object");
				}
				varLen = "micro_15_object".length();
				value=userVarMap.get("micro_15_object");
				if(value != null){
					parameters.put("param21",value);
				}
				// It must be a result value or date
				else if("micro_15_object".endsWith("_value"))
				{
					variable = "micro_15_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_15_object".endsWith("_date"))
				{
					variable = "micro_15_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_15_object".endsWith("_object"))
				{
					variable = "micro_15_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_15_object") != null){
						value = resultLookup.get("micro_15_object").toString();
					}
				}
				if(value != null){
					parameters.put("param21",value);
				}
				else
				{
					parameters.put("param21","micro_15_object");
				}
				varLen = "micro_16_object".length();
				value=userVarMap.get("micro_16_object");
				if(value != null){
					parameters.put("param22",value);
				}
				// It must be a result value or date
				else if("micro_16_object".endsWith("_value"))
				{
					variable = "micro_16_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_16_object".endsWith("_date"))
				{
					variable = "micro_16_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_16_object".endsWith("_object"))
				{
					variable = "micro_16_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_16_object") != null){
						value = resultLookup.get("micro_16_object").toString();
					}
				}
				if(value != null){
					parameters.put("param22",value);
				}
				else
				{
					parameters.put("param22","micro_16_object");
				}
				varLen = "micro_17_object".length();
				value=userVarMap.get("micro_17_object");
				if(value != null){
					parameters.put("param23",value);
				}
				// It must be a result value or date
				else if("micro_17_object".endsWith("_value"))
				{
					variable = "micro_17_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_17_object".endsWith("_date"))
				{
					variable = "micro_17_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_17_object".endsWith("_object"))
				{
					variable = "micro_17_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_17_object") != null){
						value = resultLookup.get("micro_17_object").toString();
					}
				}
				if(value != null){
					parameters.put("param23",value);
				}
				else
				{
					parameters.put("param23","micro_17_object");
				}
				varLen = "micro_18_object".length();
				value=userVarMap.get("micro_18_object");
				if(value != null){
					parameters.put("param24",value);
				}
				// It must be a result value or date
				else if("micro_18_object".endsWith("_value"))
				{
					variable = "micro_18_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_18_object".endsWith("_date"))
				{
					variable = "micro_18_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_18_object".endsWith("_object"))
				{
					variable = "micro_18_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_18_object") != null){
						value = resultLookup.get("micro_18_object").toString();
					}
				}
				if(value != null){
					parameters.put("param24",value);
				}
				else
				{
					parameters.put("param24","micro_18_object");
				}
				varLen = "micro_19_object".length();
				value=userVarMap.get("micro_19_object");
				if(value != null){
					parameters.put("param25",value);
				}
				// It must be a result value or date
				else if("micro_19_object".endsWith("_value"))
				{
					variable = "micro_19_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_19_object".endsWith("_date"))
				{
					variable = "micro_19_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_19_object".endsWith("_object"))
				{
					variable = "micro_19_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_19_object") != null){
						value = resultLookup.get("micro_19_object").toString();
					}
				}
				if(value != null){
					parameters.put("param25",value);
				}
				else
				{
					parameters.put("param25","micro_19_object");
				}
				varLen = "micro_20_object".length();
				value=userVarMap.get("micro_20_object");
				if(value != null){
					parameters.put("param26",value);
				}
				// It must be a result value or date
				else if("micro_20_object".endsWith("_value"))
				{
					variable = "micro_20_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_20_object".endsWith("_date"))
				{
					variable = "micro_20_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_20_object".endsWith("_object"))
				{
					variable = "micro_20_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_20_object") != null){
						value = resultLookup.get("micro_20_object").toString();
					}
				}
				if(value != null){
					parameters.put("param26",value);
				}
				else
				{
					parameters.put("param26","micro_20_object");
				}
				varLen = "micro_21_object".length();
				value=userVarMap.get("micro_21_object");
				if(value != null){
					parameters.put("param27",value);
				}
				// It must be a result value or date
				else if("micro_21_object".endsWith("_value"))
				{
					variable = "micro_21_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_21_object".endsWith("_date"))
				{
					variable = "micro_21_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_21_object".endsWith("_object"))
				{
					variable = "micro_21_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_21_object") != null){
						value = resultLookup.get("micro_21_object").toString();
					}
				}
				if(value != null){
					parameters.put("param27",value);
				}
				else
				{
					parameters.put("param27","micro_21_object");
				}
				varLen = "micro_22_object".length();
				value=userVarMap.get("micro_22_object");
				if(value != null){
					parameters.put("param28",value);
				}
				// It must be a result value or date
				else if("micro_22_object".endsWith("_value"))
				{
					variable = "micro_22_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_22_object".endsWith("_date"))
				{
					variable = "micro_22_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_22_object".endsWith("_object"))
				{
					variable = "micro_22_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_22_object") != null){
						value = resultLookup.get("micro_22_object").toString();
					}
				}
				if(value != null){
					parameters.put("param28",value);
				}
				else
				{
					parameters.put("param28","micro_22_object");
				}
				varLen = "micro_23_object".length();
				value=userVarMap.get("micro_23_object");
				if(value != null){
					parameters.put("param29",value);
				}
				// It must be a result value or date
				else if("micro_23_object".endsWith("_value"))
				{
					variable = "micro_23_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_23_object".endsWith("_date"))
				{
					variable = "micro_23_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_23_object".endsWith("_object"))
				{
					variable = "micro_23_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_23_object") != null){
						value = resultLookup.get("micro_23_object").toString();
					}
				}
				if(value != null){
					parameters.put("param29",value);
				}
				else
				{
					parameters.put("param29","micro_23_object");
				}
				varLen = "micro_24_object".length();
				value=userVarMap.get("micro_24_object");
				if(value != null){
					parameters.put("param30",value);
				}
				// It must be a result value or date
				else if("micro_24_object".endsWith("_value"))
				{
					variable = "micro_24_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_24_object".endsWith("_date"))
				{
					variable = "micro_24_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_24_object".endsWith("_object"))
				{
					variable = "micro_24_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_24_object") != null){
						value = resultLookup.get("micro_24_object").toString();
					}
				}
				if(value != null){
					parameters.put("param30",value);
				}
				else
				{
					parameters.put("param30","micro_24_object");
				}
				varLen = "micro_25_object".length();
				value=userVarMap.get("micro_25_object");
				if(value != null){
					parameters.put("param31",value);
				}
				// It must be a result value or date
				else if("micro_25_object".endsWith("_value"))
				{
					variable = "micro_25_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_25_object".endsWith("_date"))
				{
					variable = "micro_25_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_25_object".endsWith("_object"))
				{
					variable = "micro_25_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_25_object") != null){
						value = resultLookup.get("micro_25_object").toString();
					}
				}
				if(value != null){
					parameters.put("param31",value);
				}
				else
				{
					parameters.put("param31","micro_25_object");
				}
				varLen = "micro_26_object".length();
				value=userVarMap.get("micro_26_object");
				if(value != null){
					parameters.put("param32",value);
				}
				// It must be a result value or date
				else if("micro_26_object".endsWith("_value"))
				{
					variable = "micro_26_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_26_object".endsWith("_date"))
				{
					variable = "micro_26_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_26_object".endsWith("_object"))
				{
					variable = "micro_26_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_26_object") != null){
						value = resultLookup.get("micro_26_object").toString();
					}
				}
				if(value != null){
					parameters.put("param32",value);
				}
				else
				{
					parameters.put("param32","micro_26_object");
				}
				varLen = "micro_27_object".length();
				value=userVarMap.get("micro_27_object");
				if(value != null){
					parameters.put("param33",value);
				}
				// It must be a result value or date
				else if("micro_27_object".endsWith("_value"))
				{
					variable = "micro_27_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_27_object".endsWith("_date"))
				{
					variable = "micro_27_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_27_object".endsWith("_object"))
				{
					variable = "micro_27_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_27_object") != null){
						value = resultLookup.get("micro_27_object").toString();
					}
				}
				if(value != null){
					parameters.put("param33",value);
				}
				else
				{
					parameters.put("param33","micro_27_object");
				}
				varLen = "micro_28_object".length();
				value=userVarMap.get("micro_28_object");
				if(value != null){
					parameters.put("param34",value);
				}
				// It must be a result value or date
				else if("micro_28_object".endsWith("_value"))
				{
					variable = "micro_28_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_28_object".endsWith("_date"))
				{
					variable = "micro_28_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_28_object".endsWith("_object"))
				{
					variable = "micro_28_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_28_object") != null){
						value = resultLookup.get("micro_28_object").toString();
					}
				}
				if(value != null){
					parameters.put("param34",value);
				}
				else
				{
					parameters.put("param34","micro_28_object");
				}
				varLen = "micro_29_object".length();
				value=userVarMap.get("micro_29_object");
				if(value != null){
					parameters.put("param35",value);
				}
				// It must be a result value or date
				else if("micro_29_object".endsWith("_value"))
				{
					variable = "micro_29_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_29_object".endsWith("_date"))
				{
					variable = "micro_29_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_29_object".endsWith("_object"))
				{
					variable = "micro_29_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_29_object") != null){
						value = resultLookup.get("micro_29_object").toString();
					}
				}
				if(value != null){
					parameters.put("param35",value);
				}
				else
				{
					parameters.put("param35","micro_29_object");
				}
				varLen = "micro_30_object".length();
				value=userVarMap.get("micro_30_object");
				if(value != null){
					parameters.put("param36",value);
				}
				// It must be a result value or date
				else if("micro_30_object".endsWith("_value"))
				{
					variable = "micro_30_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_30_object".endsWith("_date"))
				{
					variable = "micro_30_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_30_object".endsWith("_object"))
				{
					variable = "micro_30_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_30_object") != null){
						value = resultLookup.get("micro_30_object").toString();
					}
				}
				if(value != null){
					parameters.put("param36",value);
				}
				else
				{
					parameters.put("param36","micro_30_object");
				}
				varLen = "micro_31_object".length();
				value=userVarMap.get("micro_31_object");
				if(value != null){
					parameters.put("param37",value);
				}
				// It must be a result value or date
				else if("micro_31_object".endsWith("_value"))
				{
					variable = "micro_31_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_31_object".endsWith("_date"))
				{
					variable = "micro_31_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_31_object".endsWith("_object"))
				{
					variable = "micro_31_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_31_object") != null){
						value = resultLookup.get("micro_31_object").toString();
					}
				}
				if(value != null){
					parameters.put("param37",value);
				}
				else
				{
					parameters.put("param37","micro_31_object");
				}
				varLen = "micro_32_object".length();
				value=userVarMap.get("micro_32_object");
				if(value != null){
					parameters.put("param38",value);
				}
				// It must be a result value or date
				else if("micro_32_object".endsWith("_value"))
				{
					variable = "micro_32_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_32_object".endsWith("_date"))
				{
					variable = "micro_32_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_32_object".endsWith("_object"))
				{
					variable = "micro_32_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_32_object") != null){
						value = resultLookup.get("micro_32_object").toString();
					}
				}
				if(value != null){
					parameters.put("param38",value);
				}
				else
				{
					parameters.put("param38","micro_32_object");
				}
				varLen = "micro_33_object".length();
				value=userVarMap.get("micro_33_object");
				if(value != null){
					parameters.put("param39",value);
				}
				// It must be a result value or date
				else if("micro_33_object".endsWith("_value"))
				{
					variable = "micro_33_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_33_object".endsWith("_date"))
				{
					variable = "micro_33_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_33_object".endsWith("_object"))
				{
					variable = "micro_33_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_33_object") != null){
						value = resultLookup.get("micro_33_object").toString();
					}
				}
				if(value != null){
					parameters.put("param39",value);
				}
				else
				{
					parameters.put("param39","micro_33_object");
				}
				varLen = "micro_34_object".length();
				value=userVarMap.get("micro_34_object");
				if(value != null){
					parameters.put("param40",value);
				}
				// It must be a result value or date
				else if("micro_34_object".endsWith("_value"))
				{
					variable = "micro_34_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_34_object".endsWith("_date"))
				{
					variable = "micro_34_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_34_object".endsWith("_object"))
				{
					variable = "micro_34_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_34_object") != null){
						value = resultLookup.get("micro_34_object").toString();
					}
				}
				if(value != null){
					parameters.put("param40",value);
				}
				else
				{
					parameters.put("param40","micro_34_object");
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
				varLen = "micro_1_object".length();
				value=userVarMap.get("micro_1_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("micro_1_object".endsWith("_value"))
				{
					variable = "micro_1_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_1_object".endsWith("_date"))
				{
					variable = "micro_1_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_1_object".endsWith("_object"))
				{
					variable = "micro_1_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_1_object") != null){
						value = resultLookup.get("micro_1_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","micro_1_object");
				}
				varLen = "micro_2_object".length();
				value=userVarMap.get("micro_2_object");
				if(value != null){
					parameters.put("param3",value);
				}
				// It must be a result value or date
				else if("micro_2_object".endsWith("_value"))
				{
					variable = "micro_2_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_2_object".endsWith("_date"))
				{
					variable = "micro_2_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_2_object".endsWith("_object"))
				{
					variable = "micro_2_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_2_object") != null){
						value = resultLookup.get("micro_2_object").toString();
					}
				}
				if(value != null){
					parameters.put("param3",value);
				}
				else
				{
					parameters.put("param3","micro_2_object");
				}
				varLen = "micro_3_object".length();
				value=userVarMap.get("micro_3_object");
				if(value != null){
					parameters.put("param4",value);
				}
				// It must be a result value or date
				else if("micro_3_object".endsWith("_value"))
				{
					variable = "micro_3_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_3_object".endsWith("_date"))
				{
					variable = "micro_3_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_3_object".endsWith("_object"))
				{
					variable = "micro_3_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_3_object") != null){
						value = resultLookup.get("micro_3_object").toString();
					}
				}
				if(value != null){
					parameters.put("param4",value);
				}
				else
				{
					parameters.put("param4","micro_3_object");
				}
				varLen = "micro_4_object".length();
				value=userVarMap.get("micro_4_object");
				if(value != null){
					parameters.put("param5",value);
				}
				// It must be a result value or date
				else if("micro_4_object".endsWith("_value"))
				{
					variable = "micro_4_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_4_object".endsWith("_date"))
				{
					variable = "micro_4_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_4_object".endsWith("_object"))
				{
					variable = "micro_4_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_4_object") != null){
						value = resultLookup.get("micro_4_object").toString();
					}
				}
				if(value != null){
					parameters.put("param5",value);
				}
				else
				{
					parameters.put("param5","micro_4_object");
				}
				varLen = "micro_5_object".length();
				value=userVarMap.get("micro_5_object");
				if(value != null){
					parameters.put("param6",value);
				}
				// It must be a result value or date
				else if("micro_5_object".endsWith("_value"))
				{
					variable = "micro_5_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_5_object".endsWith("_date"))
				{
					variable = "micro_5_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_5_object".endsWith("_object"))
				{
					variable = "micro_5_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_5_object") != null){
						value = resultLookup.get("micro_5_object").toString();
					}
				}
				if(value != null){
					parameters.put("param6",value);
				}
				else
				{
					parameters.put("param6","micro_5_object");
				}
				varLen = "micro_6_object".length();
				value=userVarMap.get("micro_6_object");
				if(value != null){
					parameters.put("param7",value);
				}
				// It must be a result value or date
				else if("micro_6_object".endsWith("_value"))
				{
					variable = "micro_6_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_6_object".endsWith("_date"))
				{
					variable = "micro_6_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_6_object".endsWith("_object"))
				{
					variable = "micro_6_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_6_object") != null){
						value = resultLookup.get("micro_6_object").toString();
					}
				}
				if(value != null){
					parameters.put("param7",value);
				}
				else
				{
					parameters.put("param7","micro_6_object");
				}
				varLen = "micro_7_object".length();
				value=userVarMap.get("micro_7_object");
				if(value != null){
					parameters.put("param8",value);
				}
				// It must be a result value or date
				else if("micro_7_object".endsWith("_value"))
				{
					variable = "micro_7_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_7_object".endsWith("_date"))
				{
					variable = "micro_7_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_7_object".endsWith("_object"))
				{
					variable = "micro_7_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_7_object") != null){
						value = resultLookup.get("micro_7_object").toString();
					}
				}
				if(value != null){
					parameters.put("param8",value);
				}
				else
				{
					parameters.put("param8","micro_7_object");
				}
				varLen = "micro_8_object".length();
				value=userVarMap.get("micro_8_object");
				if(value != null){
					parameters.put("param9",value);
				}
				// It must be a result value or date
				else if("micro_8_object".endsWith("_value"))
				{
					variable = "micro_8_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_8_object".endsWith("_date"))
				{
					variable = "micro_8_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_8_object".endsWith("_object"))
				{
					variable = "micro_8_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_8_object") != null){
						value = resultLookup.get("micro_8_object").toString();
					}
				}
				if(value != null){
					parameters.put("param9",value);
				}
				else
				{
					parameters.put("param9","micro_8_object");
				}
				varLen = "micro_9_object".length();
				value=userVarMap.get("micro_9_object");
				if(value != null){
					parameters.put("param10",value);
				}
				// It must be a result value or date
				else if("micro_9_object".endsWith("_value"))
				{
					variable = "micro_9_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_9_object".endsWith("_date"))
				{
					variable = "micro_9_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_9_object".endsWith("_object"))
				{
					variable = "micro_9_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_9_object") != null){
						value = resultLookup.get("micro_9_object").toString();
					}
				}
				if(value != null){
					parameters.put("param10",value);
				}
				else
				{
					parameters.put("param10","micro_9_object");
				}
				varLen = "result1_object".length();
				value=userVarMap.get("result1_object");
				if(value != null){
					parameters.put("param11",value);
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
					parameters.put("param11",value);
				}
				else
				{
					parameters.put("param11","result1_object");
				}
				varLen = "result2_object".length();
				value=userVarMap.get("result2_object");
				if(value != null){
					parameters.put("param12",value);
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
					parameters.put("param12",value);
				}
				else
				{
					parameters.put("param12","result2_object");
				}
				varLen = "result3_object".length();
				value=userVarMap.get("result3_object");
				if(value != null){
					parameters.put("param13",value);
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
					parameters.put("param13",value);
				}
				else
				{
					parameters.put("param13","result3_object");
				}
				varLen = "result4_object".length();
				value=userVarMap.get("result4_object");
				if(value != null){
					parameters.put("param14",value);
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
					parameters.put("param14",value);
				}
				else
				{
					parameters.put("param14","result4_object");
				}
				varLen = "result5_object".length();
				value=userVarMap.get("result5_object");
				if(value != null){
					parameters.put("param15",value);
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
					parameters.put("param15",value);
				}
				else
				{
					parameters.put("param15","result5_object");
				}
				varLen = "result6_object".length();
				value=userVarMap.get("result6_object");
				if(value != null){
					parameters.put("param16",value);
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
					parameters.put("param16",value);
				}
				else
				{
					parameters.put("param16","result6_object");
				}
				varLen = "micro_10_object".length();
				value=userVarMap.get("micro_10_object");
				if(value != null){
					parameters.put("param17",value);
				}
				// It must be a result value or date
				else if("micro_10_object".endsWith("_value"))
				{
					variable = "micro_10_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_10_object".endsWith("_date"))
				{
					variable = "micro_10_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_10_object".endsWith("_object"))
				{
					variable = "micro_10_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_10_object") != null){
						value = resultLookup.get("micro_10_object").toString();
					}
				}
				if(value != null){
					parameters.put("param17",value);
				}
				else
				{
					parameters.put("param17","micro_10_object");
				}
				varLen = "micro_11_object".length();
				value=userVarMap.get("micro_11_object");
				if(value != null){
					parameters.put("param18",value);
				}
				// It must be a result value or date
				else if("micro_11_object".endsWith("_value"))
				{
					variable = "micro_11_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_11_object".endsWith("_date"))
				{
					variable = "micro_11_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_11_object".endsWith("_object"))
				{
					variable = "micro_11_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_11_object") != null){
						value = resultLookup.get("micro_11_object").toString();
					}
				}
				if(value != null){
					parameters.put("param18",value);
				}
				else
				{
					parameters.put("param18","micro_11_object");
				}
				varLen = "micro_12_object".length();
				value=userVarMap.get("micro_12_object");
				if(value != null){
					parameters.put("param19",value);
				}
				// It must be a result value or date
				else if("micro_12_object".endsWith("_value"))
				{
					variable = "micro_12_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_12_object".endsWith("_date"))
				{
					variable = "micro_12_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_12_object".endsWith("_object"))
				{
					variable = "micro_12_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_12_object") != null){
						value = resultLookup.get("micro_12_object").toString();
					}
				}
				if(value != null){
					parameters.put("param19",value);
				}
				else
				{
					parameters.put("param19","micro_12_object");
				}
				varLen = "micro_13_object".length();
				value=userVarMap.get("micro_13_object");
				if(value != null){
					parameters.put("param20",value);
				}
				// It must be a result value or date
				else if("micro_13_object".endsWith("_value"))
				{
					variable = "micro_13_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_13_object".endsWith("_date"))
				{
					variable = "micro_13_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_13_object".endsWith("_object"))
				{
					variable = "micro_13_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_13_object") != null){
						value = resultLookup.get("micro_13_object").toString();
					}
				}
				if(value != null){
					parameters.put("param20",value);
				}
				else
				{
					parameters.put("param20","micro_13_object");
				}
				varLen = "micro_14_object".length();
				value=userVarMap.get("micro_14_object");
				if(value != null){
					parameters.put("param21",value);
				}
				// It must be a result value or date
				else if("micro_14_object".endsWith("_value"))
				{
					variable = "micro_14_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_14_object".endsWith("_date"))
				{
					variable = "micro_14_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_14_object".endsWith("_object"))
				{
					variable = "micro_14_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_14_object") != null){
						value = resultLookup.get("micro_14_object").toString();
					}
				}
				if(value != null){
					parameters.put("param21",value);
				}
				else
				{
					parameters.put("param21","micro_14_object");
				}
				varLen = "micro_15_object".length();
				value=userVarMap.get("micro_15_object");
				if(value != null){
					parameters.put("param22",value);
				}
				// It must be a result value or date
				else if("micro_15_object".endsWith("_value"))
				{
					variable = "micro_15_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_15_object".endsWith("_date"))
				{
					variable = "micro_15_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_15_object".endsWith("_object"))
				{
					variable = "micro_15_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_15_object") != null){
						value = resultLookup.get("micro_15_object").toString();
					}
				}
				if(value != null){
					parameters.put("param22",value);
				}
				else
				{
					parameters.put("param22","micro_15_object");
				}
				varLen = "micro_16_object".length();
				value=userVarMap.get("micro_16_object");
				if(value != null){
					parameters.put("param23",value);
				}
				// It must be a result value or date
				else if("micro_16_object".endsWith("_value"))
				{
					variable = "micro_16_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_16_object".endsWith("_date"))
				{
					variable = "micro_16_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_16_object".endsWith("_object"))
				{
					variable = "micro_16_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_16_object") != null){
						value = resultLookup.get("micro_16_object").toString();
					}
				}
				if(value != null){
					parameters.put("param23",value);
				}
				else
				{
					parameters.put("param23","micro_16_object");
				}
				varLen = "micro_17_object".length();
				value=userVarMap.get("micro_17_object");
				if(value != null){
					parameters.put("param24",value);
				}
				// It must be a result value or date
				else if("micro_17_object".endsWith("_value"))
				{
					variable = "micro_17_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_17_object".endsWith("_date"))
				{
					variable = "micro_17_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_17_object".endsWith("_object"))
				{
					variable = "micro_17_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_17_object") != null){
						value = resultLookup.get("micro_17_object").toString();
					}
				}
				if(value != null){
					parameters.put("param24",value);
				}
				else
				{
					parameters.put("param24","micro_17_object");
				}
				varLen = "micro_18_object".length();
				value=userVarMap.get("micro_18_object");
				if(value != null){
					parameters.put("param25",value);
				}
				// It must be a result value or date
				else if("micro_18_object".endsWith("_value"))
				{
					variable = "micro_18_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_18_object".endsWith("_date"))
				{
					variable = "micro_18_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_18_object".endsWith("_object"))
				{
					variable = "micro_18_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_18_object") != null){
						value = resultLookup.get("micro_18_object").toString();
					}
				}
				if(value != null){
					parameters.put("param25",value);
				}
				else
				{
					parameters.put("param25","micro_18_object");
				}
				varLen = "micro_19_object".length();
				value=userVarMap.get("micro_19_object");
				if(value != null){
					parameters.put("param26",value);
				}
				// It must be a result value or date
				else if("micro_19_object".endsWith("_value"))
				{
					variable = "micro_19_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_19_object".endsWith("_date"))
				{
					variable = "micro_19_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_19_object".endsWith("_object"))
				{
					variable = "micro_19_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_19_object") != null){
						value = resultLookup.get("micro_19_object").toString();
					}
				}
				if(value != null){
					parameters.put("param26",value);
				}
				else
				{
					parameters.put("param26","micro_19_object");
				}
				varLen = "micro_20_object".length();
				value=userVarMap.get("micro_20_object");
				if(value != null){
					parameters.put("param27",value);
				}
				// It must be a result value or date
				else if("micro_20_object".endsWith("_value"))
				{
					variable = "micro_20_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_20_object".endsWith("_date"))
				{
					variable = "micro_20_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_20_object".endsWith("_object"))
				{
					variable = "micro_20_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_20_object") != null){
						value = resultLookup.get("micro_20_object").toString();
					}
				}
				if(value != null){
					parameters.put("param27",value);
				}
				else
				{
					parameters.put("param27","micro_20_object");
				}
				varLen = "micro_21_object".length();
				value=userVarMap.get("micro_21_object");
				if(value != null){
					parameters.put("param28",value);
				}
				// It must be a result value or date
				else if("micro_21_object".endsWith("_value"))
				{
					variable = "micro_21_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_21_object".endsWith("_date"))
				{
					variable = "micro_21_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_21_object".endsWith("_object"))
				{
					variable = "micro_21_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_21_object") != null){
						value = resultLookup.get("micro_21_object").toString();
					}
				}
				if(value != null){
					parameters.put("param28",value);
				}
				else
				{
					parameters.put("param28","micro_21_object");
				}
				varLen = "micro_22_object".length();
				value=userVarMap.get("micro_22_object");
				if(value != null){
					parameters.put("param29",value);
				}
				// It must be a result value or date
				else if("micro_22_object".endsWith("_value"))
				{
					variable = "micro_22_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_22_object".endsWith("_date"))
				{
					variable = "micro_22_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_22_object".endsWith("_object"))
				{
					variable = "micro_22_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_22_object") != null){
						value = resultLookup.get("micro_22_object").toString();
					}
				}
				if(value != null){
					parameters.put("param29",value);
				}
				else
				{
					parameters.put("param29","micro_22_object");
				}
				varLen = "micro_23_object".length();
				value=userVarMap.get("micro_23_object");
				if(value != null){
					parameters.put("param30",value);
				}
				// It must be a result value or date
				else if("micro_23_object".endsWith("_value"))
				{
					variable = "micro_23_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_23_object".endsWith("_date"))
				{
					variable = "micro_23_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_23_object".endsWith("_object"))
				{
					variable = "micro_23_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_23_object") != null){
						value = resultLookup.get("micro_23_object").toString();
					}
				}
				if(value != null){
					parameters.put("param30",value);
				}
				else
				{
					parameters.put("param30","micro_23_object");
				}
				varLen = "micro_24_object".length();
				value=userVarMap.get("micro_24_object");
				if(value != null){
					parameters.put("param31",value);
				}
				// It must be a result value or date
				else if("micro_24_object".endsWith("_value"))
				{
					variable = "micro_24_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_24_object".endsWith("_date"))
				{
					variable = "micro_24_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_24_object".endsWith("_object"))
				{
					variable = "micro_24_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_24_object") != null){
						value = resultLookup.get("micro_24_object").toString();
					}
				}
				if(value != null){
					parameters.put("param31",value);
				}
				else
				{
					parameters.put("param31","micro_24_object");
				}
				varLen = "micro_25_object".length();
				value=userVarMap.get("micro_25_object");
				if(value != null){
					parameters.put("param32",value);
				}
				// It must be a result value or date
				else if("micro_25_object".endsWith("_value"))
				{
					variable = "micro_25_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_25_object".endsWith("_date"))
				{
					variable = "micro_25_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_25_object".endsWith("_object"))
				{
					variable = "micro_25_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_25_object") != null){
						value = resultLookup.get("micro_25_object").toString();
					}
				}
				if(value != null){
					parameters.put("param32",value);
				}
				else
				{
					parameters.put("param32","micro_25_object");
				}
				varLen = "micro_26_object".length();
				value=userVarMap.get("micro_26_object");
				if(value != null){
					parameters.put("param33",value);
				}
				// It must be a result value or date
				else if("micro_26_object".endsWith("_value"))
				{
					variable = "micro_26_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_26_object".endsWith("_date"))
				{
					variable = "micro_26_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_26_object".endsWith("_object"))
				{
					variable = "micro_26_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_26_object") != null){
						value = resultLookup.get("micro_26_object").toString();
					}
				}
				if(value != null){
					parameters.put("param33",value);
				}
				else
				{
					parameters.put("param33","micro_26_object");
				}
				varLen = "micro_27_object".length();
				value=userVarMap.get("micro_27_object");
				if(value != null){
					parameters.put("param34",value);
				}
				// It must be a result value or date
				else if("micro_27_object".endsWith("_value"))
				{
					variable = "micro_27_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_27_object".endsWith("_date"))
				{
					variable = "micro_27_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_27_object".endsWith("_object"))
				{
					variable = "micro_27_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_27_object") != null){
						value = resultLookup.get("micro_27_object").toString();
					}
				}
				if(value != null){
					parameters.put("param34",value);
				}
				else
				{
					parameters.put("param34","micro_27_object");
				}
				varLen = "micro_28_object".length();
				value=userVarMap.get("micro_28_object");
				if(value != null){
					parameters.put("param35",value);
				}
				// It must be a result value or date
				else if("micro_28_object".endsWith("_value"))
				{
					variable = "micro_28_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_28_object".endsWith("_date"))
				{
					variable = "micro_28_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_28_object".endsWith("_object"))
				{
					variable = "micro_28_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_28_object") != null){
						value = resultLookup.get("micro_28_object").toString();
					}
				}
				if(value != null){
					parameters.put("param35",value);
				}
				else
				{
					parameters.put("param35","micro_28_object");
				}
				varLen = "micro_29_object".length();
				value=userVarMap.get("micro_29_object");
				if(value != null){
					parameters.put("param36",value);
				}
				// It must be a result value or date
				else if("micro_29_object".endsWith("_value"))
				{
					variable = "micro_29_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_29_object".endsWith("_date"))
				{
					variable = "micro_29_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_29_object".endsWith("_object"))
				{
					variable = "micro_29_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_29_object") != null){
						value = resultLookup.get("micro_29_object").toString();
					}
				}
				if(value != null){
					parameters.put("param36",value);
				}
				else
				{
					parameters.put("param36","micro_29_object");
				}
				varLen = "micro_30_object".length();
				value=userVarMap.get("micro_30_object");
				if(value != null){
					parameters.put("param37",value);
				}
				// It must be a result value or date
				else if("micro_30_object".endsWith("_value"))
				{
					variable = "micro_30_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_30_object".endsWith("_date"))
				{
					variable = "micro_30_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_30_object".endsWith("_object"))
				{
					variable = "micro_30_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_30_object") != null){
						value = resultLookup.get("micro_30_object").toString();
					}
				}
				if(value != null){
					parameters.put("param37",value);
				}
				else
				{
					parameters.put("param37","micro_30_object");
				}
				varLen = "micro_31_object".length();
				value=userVarMap.get("micro_31_object");
				if(value != null){
					parameters.put("param38",value);
				}
				// It must be a result value or date
				else if("micro_31_object".endsWith("_value"))
				{
					variable = "micro_31_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_31_object".endsWith("_date"))
				{
					variable = "micro_31_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_31_object".endsWith("_object"))
				{
					variable = "micro_31_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_31_object") != null){
						value = resultLookup.get("micro_31_object").toString();
					}
				}
				if(value != null){
					parameters.put("param38",value);
				}
				else
				{
					parameters.put("param38","micro_31_object");
				}
				varLen = "micro_32_object".length();
				value=userVarMap.get("micro_32_object");
				if(value != null){
					parameters.put("param39",value);
				}
				// It must be a result value or date
				else if("micro_32_object".endsWith("_value"))
				{
					variable = "micro_32_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_32_object".endsWith("_date"))
				{
					variable = "micro_32_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_32_object".endsWith("_object"))
				{
					variable = "micro_32_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_32_object") != null){
						value = resultLookup.get("micro_32_object").toString();
					}
				}
				if(value != null){
					parameters.put("param39",value);
				}
				else
				{
					parameters.put("param39","micro_32_object");
				}
				varLen = "micro_33_object".length();
				value=userVarMap.get("micro_33_object");
				if(value != null){
					parameters.put("param40",value);
				}
				// It must be a result value or date
				else if("micro_33_object".endsWith("_value"))
				{
					variable = "micro_33_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_33_object".endsWith("_date"))
				{
					variable = "micro_33_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_33_object".endsWith("_object"))
				{
					variable = "micro_33_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_33_object") != null){
						value = resultLookup.get("micro_33_object").toString();
					}
				}
				if(value != null){
					parameters.put("param40",value);
				}
				else
				{
					parameters.put("param40","micro_33_object");
				}
				varLen = "micro_34_object".length();
				value=userVarMap.get("micro_34_object");
				if(value != null){
					parameters.put("param41",value);
				}
				// It must be a result value or date
				else if("micro_34_object".endsWith("_value"))
				{
					variable = "micro_34_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("micro_34_object".endsWith("_date"))
				{
					variable = "micro_34_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("micro_34_object".endsWith("_object"))
				{
					variable = "micro_34_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("micro_34_object") != null){
						value = resultLookup.get("micro_34_object").toString();
					}
				}
				if(value != null){
					parameters.put("param41",value);
				}
				else
				{
					parameters.put("param41","micro_34_object");
				}
				Result finalResults = logicService.eval(patient, "getAllWithSameDate",parameters);
				resultLookup.put("finalResults",finalResults);
				varLen = "0".length();
				value=userVarMap.get("0");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("0".endsWith("_value"))
				{
					variable = "0".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("0".endsWith("_date"))
				{
					variable = "0".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("0".endsWith("_object"))
				{
					variable = "0".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("0") != null){
						value = resultLookup.get("0").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","0");
				}
				varLen = "finalResults_object".length();
				value=userVarMap.get("finalResults_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("finalResults_object".endsWith("_value"))
				{
					variable = "finalResults_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("finalResults_object".endsWith("_date"))
				{
					variable = "finalResults_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("finalResults_object".endsWith("_object"))
				{
					variable = "finalResults_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("finalResults_object") != null){
						value = resultLookup.get("finalResults_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","finalResults_object");
				}
				Result element1 = logicService.eval(patient, "getResultElement",parameters);
				resultLookup.put("element1",element1);
				varLen = "1".length();
				value=userVarMap.get("1");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("1".endsWith("_value"))
				{
					variable = "1".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("1".endsWith("_date"))
				{
					variable = "1".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("1".endsWith("_object"))
				{
					variable = "1".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("1") != null){
						value = resultLookup.get("1").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","1");
				}
				varLen = "finalResults_object".length();
				value=userVarMap.get("finalResults_object");
				if(value != null){
					parameters.put("param2",value);
				}
				// It must be a result value or date
				else if("finalResults_object".endsWith("_value"))
				{
					variable = "finalResults_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("finalResults_object".endsWith("_date"))
				{
					variable = "finalResults_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("finalResults_object".endsWith("_object"))
				{
					variable = "finalResults_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("finalResults_object") != null){
						value = resultLookup.get("finalResults_object").toString();
					}
				}
				if(value != null){
					parameters.put("param2",value);
				}
				else
				{
					parameters.put("param2","finalResults_object");
				}
				Result element2 = logicService.eval(patient, "getResultElement",parameters);
				resultLookup.put("element2",element2);
				varLen = "element1_object".length();
				value=userVarMap.get("element1_object");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("element1_object".endsWith("_value"))
				{
					variable = "element1_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("element1_object".endsWith("_date"))
				{
					variable = "element1_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("element1_object".endsWith("_object"))
				{
					variable = "element1_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("element1_object") != null){
						value = resultLookup.get("element1_object").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","element1_object");
				}
				Result finalResultConceptName1 = logicService.eval(patient, "conceptNameResult",parameters);
				resultLookup.put("finalResultConceptName1",finalResultConceptName1);
				varLen = "finalResultConceptName1".length();
				value=userVarMap.get("finalResultConceptName1");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("finalResultConceptName1".endsWith("_value"))
				{
					variable = "finalResultConceptName1".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("finalResultConceptName1".endsWith("_date"))
				{
					variable = "finalResultConceptName1".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("finalResultConceptName1".endsWith("_object"))
				{
					variable = "finalResultConceptName1".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("finalResultConceptName1") != null){
						value = resultLookup.get("finalResultConceptName1").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","finalResultConceptName1");
				}
				Result finalResultUnits1 = logicService.eval(patient, "getConceptUnits",parameters);
				resultLookup.put("finalResultUnits1",finalResultUnits1);
				varLen = "element2_object".length();
				value=userVarMap.get("element2_object");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("element2_object".endsWith("_value"))
				{
					variable = "element2_object".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("element2_object".endsWith("_date"))
				{
					variable = "element2_object".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("element2_object".endsWith("_object"))
				{
					variable = "element2_object".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("element2_object") != null){
						value = resultLookup.get("element2_object").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","element2_object");
				}
				Result finalResultConceptName2 = logicService.eval(patient, "conceptNameResult",parameters);
				resultLookup.put("finalResultConceptName2",finalResultConceptName2);
				varLen = "finalResultConceptName2".length();
				value=userVarMap.get("finalResultConceptName2");
				if(value != null){
					parameters.put("param1",value);
				}
				// It must be a result value or date
				else if("finalResultConceptName2".endsWith("_value"))
				{
					variable = "finalResultConceptName2".substring(0, varLen-6); // -6 for _value
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).toString();
					}
				}
				else if("finalResultConceptName2".endsWith("_date"))
				{
					variable = "finalResultConceptName2".substring(0, varLen-5); // -5 for _date
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable).getResultDate().toString();
					}
				}
				else if("finalResultConceptName2".endsWith("_object"))
				{
					variable = "finalResultConceptName2".substring(0, varLen-7); // -5 for _object
					if (resultLookup.get(variable) != null){
						value = resultLookup.get(variable);
					}
				}
				else
				{
					if (resultLookup.get("finalResultConceptName2") != null){
						value = resultLookup.get("finalResultConceptName2").toString();
					}
				}
				if(value != null){
					parameters.put("param1",value);
				}
				else
				{
					parameters.put("param1","finalResultConceptName2");
				}
				Result finalResultUnits2 = logicService.eval(patient, "getConceptUnits",parameters);
				resultLookup.put("finalResultUnits2",finalResultUnits2);
				
			return true;
		}
	return false;	}

	public void initAction() {
		this.actions = new ArrayList<String>();
		actions.add("|| element1_date ||@microResultDate1");
		actions.add("|| element1_value ||@microResultValue1");
		actions.add("|| finalResultConceptName1_value ||@microConceptName1");
		actions.add("|| finalResultUnits1_value ||@microResultUnit1");
		actions.add("|| element2_date ||@microResultDate2");
		actions.add("|| element2_value ||@microResultValue2");
		actions.add("|| finalResultConceptName2_value ||@microConceptName2");
		actions.add("|| finalResultUnits2_value ||@microResultUnit2");
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