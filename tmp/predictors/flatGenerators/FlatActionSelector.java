package massim.agent.mind.harm.components.predictors.flatGenerators;

import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;

/**
 * selector of primitive action
 * 
 *  -selection is based on the action priorities
 *  -some randomization has to be utilized
 *  
 * @author jardavitku
 *
 */
public interface FlatActionSelector {

	/**
	 * select one primitive action and move with the actuator
	 * @param ac
	 */
	public void selectAction(ActuatorHARM ac);
	
	/**
	 * some statistics about selection
	 * @return
	 */
	public void stats();
	
	
	public void preSimulationStep();
	
	public void postSimulationStep();
}
