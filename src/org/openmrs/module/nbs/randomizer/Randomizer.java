/**
 * 
 */
package org.openmrs.module.nbs.randomizer;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.nbs.hibernateBeans.Study;

/**
 * @author tmdugan
 *
 */
public interface Randomizer
{
	public void randomize(Study study,Patient patient,Encounter encounter);
}
