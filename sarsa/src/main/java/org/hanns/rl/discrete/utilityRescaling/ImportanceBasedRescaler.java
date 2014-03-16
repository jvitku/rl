package org.hanns.rl.discrete.utilityRescaling;

/**
 * Utility re-scaling based on current value of Importance.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ImportanceBasedRescaler extends UtilityRescaler{
	
	public float getImportance();
	
	public void setImportance(float importance);

}
