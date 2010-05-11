/**
 * 
 */
package org.openmrs.module.nbsmodule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.dss.hibernateBeans.Rule;

/**
 * @author tmdugan
 */
public class NBSParameterHandler implements ParameterHandler {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.atd.ParameterHandler#addParameters(java.util.Map,
	 *      org.openmrs.module.dss.hibernateBeans.Rule)
	 */
	public void addParameters(Map<String, Object> parameters, Rule rule) {
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		String ruleType = rule.getRuleType();
		
		if (ruleType == null || formInstance == null) {
			return;
		}
		LogicService logicService = Context.getLogicService();
		
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, Field> fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		processParameters(parameters, fieldMap);
		
	}
	
	private void processParameters(Map<String, Object> parameters, HashMap<String, Field> fieldMap) {
		
		Iterator<String> iter = fieldMap.keySet().iterator();
		
		while (iter.hasNext()) {
			String tagName = iter.next();
			
			if (tagName != null) {
				String answer = fieldMap.get(tagName).getValue();
				if (answer != null) {
					answer = answer.trim();
					
					if (answer.equalsIgnoreCase("1")) {
						parameters.put("Box1", "true");
					}
					
					if (answer.equalsIgnoreCase("0")) {
						parameters.put("Box1", "false");
					}
				}
			}
		}
	}
	
}
