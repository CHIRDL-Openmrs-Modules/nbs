/**
 * 
 */
package org.openmrs.module.nbs.datasource;

import java.util.HashMap;
import java.util.Set;

import org.openmrs.Obs;
import org.openmrs.logic.datasource.ObsDataSource;

/**
 * @author Tammy Dugan
 * 
 */
public class ObsNbsDatasource extends ObsDataSource
{

	private LogicNbsObsDAO logicNbsObsDAO;
	
	public void setLogicNbsObsDAO(LogicNbsObsDAO logicNbsObsDAO) {
		this.logicNbsObsDAO = logicNbsObsDAO;
	}
	
	public LogicNbsObsDAO getLogicNbsObsDAO() {
		return logicNbsObsDAO;
	}
	
	public void parseHL7ToObs(String hl7Message,Integer patientId,String mrn)
	{
		((LogicNbsObsDAO) this.getLogicObsDAO()).parseHL7ToObs(hl7Message,
				patientId,mrn);
	}

	public void deleteRegenObsByPatientId(Integer patientId)
	{
		((LogicNbsObsDAO) this.getLogicObsDAO())
				.deleteRegenObsByPatientId(patientId);
	}

	public Set<Obs> getRegenObsByConceptName(Integer patientId,
			String conceptName)
	{
		return ((LogicNbsObsDAO) this.getLogicObsDAO())
				.getRegenObsByConceptName(patientId, conceptName);
	}
	
	public void clearRegenObs() {
	    ((LogicNbsObsDAO) this.getLogicObsDAO()).clearRegenObs();
	}
	
	public HashMap<String, Set<Obs>> getRegenObs( Integer patientId) {
	    return ((LogicNbsObsDAO) this.getLogicObsDAO()).getRegenObs(patientId);
	}

}
