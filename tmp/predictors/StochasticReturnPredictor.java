package massim.agent.mind.harm.components.predictors;

import java.util.ArrayList;

import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.Variable;

public interface StochasticReturnPredictor {

	/**
	 * SRP stands for Stochastic Return Predictor :-)
	 * 
	 * SRP implements Q-learning with epsilon-greedy action selection with variable epsilon
	 * 
	 * the value of epsilon is based on the actual motivation of the action 
	 * 
	 * (action with small motivation produces big epsilon, which provides bigger exploration ability, 
	 * in contrast when the motivation is big, the epsilon goes to 0 and exploitation is preferred)
	 * 
	 * 
	 * 
	 * @author jardavitku
	 *
	 */

		
		public void stats(ArrayList<Action> ac);
		
		/**
		 * @param a - action (or variable below) to be removed from the DS
		 */
		public void removeAction(Action a);
		
		public void removeVariable(Variable var);
		
		/**
		 * adding of new actions and variables is buffered, call each once 
		 */
		public void updateNewActions();
		
		public void updateNewVariables();
		
		public QSAMatrix getMatrix();
		
		public Action getActionGenerated();
		/**
		 * generate action:
		 * 	-from all of child actions
		 * 	-select one
		 * 	-add him priority as: \phi{child} += \phi{my} + Motivation*( Q(s,a) +1 ) 
		 */
		public void generateAction();
		public void updateKnowledge(ActuatorHARM actuator, int reinfNow);

}
