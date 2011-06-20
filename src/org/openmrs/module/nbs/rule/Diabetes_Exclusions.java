/********************************************************************
 Translated from - Diabetes_Exclusions.mlm on Thu Aug 05 11:24:58 EDT 2010

 Title:  Diabetes Exclusions
 Filename:  Diabetes_Exclusions
 Version: 1.0
 Institution:  Indiana University School of Medicine
 Author:  Tammy Dugan
 Specialist:  Pediatrics
 Date: 2010-08-03T11:00:00-0400
 Validation :
 Purpose:  Lookup up Exclusions for diabetic patients.
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
public class Diabetes_Exclusions implements Rule, DssRule{

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
		return "Lookup up Exclusions for diabetic patients.";
	}

	/*** @see org.openmrs.module.dss.DssRule#getSpecialist()*/
	public String getSpecialist(){
		return "Pediatrics";
	}

	/*** @see org.openmrs.module.dss.DssRule#getTitle()*/
	public String getTitle(){
		return "Diabetes Exclusions";
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
		return "If If || If || If || endif";
	}

	/*** @see org.openmrs.module.dss.DssRule#getAction()*/
	public String getAction(){
		return "write";
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

			Result polycystic=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("ovaries polycystic").last());
			resultLookup.put("polycystic",polycystic);
			Result hosp_proc=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hosp Procedures"));
			resultLookup.put("hosp_proc",hosp_proc);
			Result hosp_icd9_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Hosp ICD9 Dx"));
			resultLookup.put("hosp_icd9_dx",hosp_icd9_dx);
			Result dx_complaints=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Dx and Complaints"));
			resultLookup.put("dx_complaints",dx_complaints);
			Result clinic_billing_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Clinic Billing Diagnosis"));
			resultLookup.put("clinic_billing_dx",clinic_billing_dx);
			Result admitting_icd9_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Admitting ICD9 Dx"));
			resultLookup.put("admitting_icd9_dx",admitting_icd9_dx);
			Result admission_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Admission Diagnosis"));
			resultLookup.put("admission_dx",admission_dx);
			Result discharge_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("DISCHARGE DIAGNOSIS"));
			resultLookup.put("discharge_dx",discharge_dx);
			Result er_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("E.R. Diagnosis"));
			resultLookup.put("er_dx",er_dx);
			Result primary_care_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Primary Care Dx"));
			resultLookup.put("primary_care_dx",primary_care_dx);
			Result visit_dx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("VISIT DIAGNOSIS"));
			resultLookup.put("visit_dx",visit_dx);
			Result gyn_hx=context.read(
				patient,context.getLogicDataSource("RMRS"),
				new LogicCriteria("Gyn Hx"));
			resultLookup.put("gyn_hx",gyn_hx);}

			if(evaluate_logic(parameters)){
				Result ruleResult = new Result();
		Result dx_complaints = (Result) resultLookup.get("dx_complaints");
		Result admission_dx = (Result) resultLookup.get("admission_dx");
		Result gyn_hx = (Result) resultLookup.get("gyn_hx");
		Result hosp_proc = (Result) resultLookup.get("hosp_proc");
		Result hosp_icd9_dx = (Result) resultLookup.get("hosp_icd9_dx");
		Result clinic_billing_dx = (Result) resultLookup.get("clinic_billing_dx");
		Result admitting_icd9_dx = (Result) resultLookup.get("admitting_icd9_dx");
		Result er_dx = (Result) resultLookup.get("er_dx");
		Result visit_dx = (Result) resultLookup.get("visit_dx");
		Result primary_care_dx = (Result) resultLookup.get("primary_care_dx");
		Result polycystic = (Result) resultLookup.get("polycystic");
		Result discharge_dx = (Result) resultLookup.get("discharge_dx");

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


	private Result getResultList_hosp_proc(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_dx_complaints(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_admission_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_discharge_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_er_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_primary_care_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_visit_dx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_gyn_hx(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("256.4")));
		return aList;
	}

	private Result getResultList_hosp_proc__12(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__13(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_dx_complaints__14(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__15(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__16(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_admission_dx__17(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_discharge_dx__18(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_er_dx__19(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_primary_care_dx__20(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_visit_dx__21(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_gyn_hx__22(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("251.8")));
		return aList;
	}

	private Result getResultList_hosp_proc__23(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__24(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_dx_complaints__25(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__26(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__27(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_admission_dx__28(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_discharge_dx__29(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_er_dx__30(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_primary_care_dx__31(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_visit_dx__32(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_gyn_hx__33(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("POIS-CORTICOSTEROIDS")));
		return aList;
	}

	private Result getResultList_hosp_proc__34(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__35(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_dx_complaints__36(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__37(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__38(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_admission_dx__39(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_discharge_dx__40(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_er_dx__41(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_primary_care_dx__42(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_visit_dx__43(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_gyn_hx__44(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.8")));
		return aList;
	}

	private Result getResultList_hosp_proc__45(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__46(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_dx_complaints__47(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__48(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__49(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_admission_dx__50(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_discharge_dx__51(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_er_dx__52(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_primary_care_dx__53(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_visit_dx__54(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_gyn_hx__55(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.80")));
		return aList;
	}

	private Result getResultList_hosp_proc__56(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__57(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_dx_complaints__58(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__59(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__60(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_admission_dx__61(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_discharge_dx__62(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_er_dx__63(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_primary_care_dx__64(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_visit_dx__65(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_gyn_hx__66(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.81")));
		return aList;
	}

	private Result getResultList_hosp_proc__67(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__68(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_dx_complaints__69(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__70(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__71(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_admission_dx__72(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_discharge_dx__73(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_er_dx__74(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_primary_care_dx__75(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_visit_dx__76(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_gyn_hx__77(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.82")));
		return aList;
	}

	private Result getResultList_hosp_proc__78(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__79(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_dx_complaints__80(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__81(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__82(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_admission_dx__83(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_discharge_dx__84(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_er_dx__85(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_primary_care_dx__86(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_visit_dx__87(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_gyn_hx__88(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.83")));
		return aList;
	}

	private Result getResultList_hosp_proc__89(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_hosp_icd9_dx__90(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_dx_complaints__91(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_clinic_billing_dx__92(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_admitting_icd9_dx__93(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_admission_dx__94(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_discharge_dx__95(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_er_dx__96(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_primary_care_dx__97(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_visit_dx__98(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}

	private Result getResultList_gyn_hx__99(){
		ConceptService conceptService = Context.getConceptService();
		Result aList = new Result();
		aList.add(new Result(conceptService.getConcept("648.84")));
		return aList;
	}
	private boolean evaluate_logic(Map<String, Object> parameters) throws LogicException {

		Result Gender = new Result(userVarMap.get("Gender"));
		Result dx_complaints = (Result) resultLookup.get("dx_complaints");
		Result admission_dx = (Result) resultLookup.get("admission_dx");
		Result gyn_hx = (Result) resultLookup.get("gyn_hx");
		Result hosp_proc = (Result) resultLookup.get("hosp_proc");
		Result hosp_icd9_dx = (Result) resultLookup.get("hosp_icd9_dx");
		Result clinic_billing_dx = (Result) resultLookup.get("clinic_billing_dx");
		Result admitting_icd9_dx = (Result) resultLookup.get("admitting_icd9_dx");
		Result mode = (Result) resultLookup.get("mode");
		Result er_dx = (Result) resultLookup.get("er_dx");
		Result visit_dx = (Result) resultLookup.get("visit_dx");
		Result primary_care_dx = (Result) resultLookup.get("primary_care_dx");
		Result polycystic = (Result) resultLookup.get("polycystic");
		Result discharge_dx = (Result) resultLookup.get("discharge_dx");

		if((!mode.isNull()&&mode.toString().equalsIgnoreCase("PRODUCE"))){
		if((!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx(),gyn_hx))){
			//preprocess any || operator ;
			String val = doAction("true");
			userVarMap.put("exclude",  val);
		}
		if((!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__12(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__13(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__14(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__15(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__16(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__17(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__18(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__19(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__20(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__21(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__22(),gyn_hx))||
			(!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__23(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__24(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__25(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__26(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__27(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__28(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__29(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__30(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__31(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__32(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__33(),gyn_hx))){
			//preprocess any || operator ;
			String val = doAction("true");
			userVarMap.put("exclude",  val);
		}
		if((!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__34(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__35(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__36(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__37(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__38(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__39(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__40(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__41(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__42(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__43(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__44(),gyn_hx))||
			(!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__45(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__46(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__47(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__48(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__49(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__50(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__51(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__52(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__53(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__54(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__55(),gyn_hx))||
			(!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__56(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__57(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__58(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__59(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__60(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__61(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__62(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__63(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__64(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__65(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__66(),gyn_hx))||
			(!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__67(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__68(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__69(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__70(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__71(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__72(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__73(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__74(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__75(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__76(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__77(),gyn_hx))||
			(!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__78(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__79(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__80(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__81(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__82(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__83(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__84(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__85(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__86(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__87(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__88(),gyn_hx))||
			(!hosp_proc.isNull()&&containsIgnoreCase(getResultList_hosp_proc__89(),hosp_proc))||
			(!hosp_icd9_dx.isNull()&&containsIgnoreCase(getResultList_hosp_icd9_dx__90(),hosp_icd9_dx))||
			(!dx_complaints.isNull()&&containsIgnoreCase(getResultList_dx_complaints__91(),dx_complaints))||
			(!clinic_billing_dx.isNull()&&containsIgnoreCase(getResultList_clinic_billing_dx__92(),clinic_billing_dx))||
			(!admitting_icd9_dx.isNull()&&containsIgnoreCase(getResultList_admitting_icd9_dx__93(),admitting_icd9_dx))||
			(!admission_dx.isNull()&&containsIgnoreCase(getResultList_admission_dx__94(),admission_dx))||
			(!discharge_dx.isNull()&&containsIgnoreCase(getResultList_discharge_dx__95(),discharge_dx))||
			(!er_dx.isNull()&&containsIgnoreCase(getResultList_er_dx__96(),er_dx))||
			(!primary_care_dx.isNull()&&containsIgnoreCase(getResultList_primary_care_dx__97(),primary_care_dx))||
			(!visit_dx.isNull()&&containsIgnoreCase(getResultList_visit_dx__98(),visit_dx))||
			(!gyn_hx.isNull()&&containsIgnoreCase(getResultList_gyn_hx__99(),gyn_hx))){
			//preprocess any || operator ;
			String val = doAction("true");
			userVarMap.put("exclude",  val);
		}
}	return false;	}

	public void initAction() {
		this.actions = new ArrayList<String>();
		actions.add("|| exclude ||");
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