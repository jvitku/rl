package massim.agent.mind.harm.actions;

import java.util.ArrayList;

import massim.agent.mind.harm.components.predictors.StochasticReturnPredictor;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;
import massim.agent.motivationActionMapping.Connection;

public interface ComplexAction {
	
		/**
		 * update list of actions and variables that this action owns
		 * @param acts 
		 * @param vars
		 * @param actualStep
		 */
		public void updateActionsAndVars(ActionList acts, VariableList vars, int actualStep);
		
		public int getMyReinforcementVal();
		
		
		
		
		/**
		 * @return - variable which causes the reinforcement
		 */
		public Variable getReinforcementCausingVar();
		
		
		public ArrayList<Action> getChilds();
		
		public void addChild(Action a);
		
		public void updateProperties(VariableList vars, ActionList actions);
		
		/**
		 * complexity is bigger by one than the most complex child
		 */
		public void updateComplexity();
		
		public boolean complexityChanged();


		public ArrayList<Action> getActions();


		public String[] getActionNames();



		public VariableList getConstants();


		public VariableList getVariables();


		public long getSpaceSize();


		public int getSpaceDimension();


		public int getNumActions();

		public int getLastExecuted();

		public int getNumVariables();
		
		public QSAMatrix getQMatrix();

		public void setQMatrix(QSAMatrix m);


		public boolean justExecuted();

		public void discardExecution();

		public void setJustExecuted();

		public int lastExecuted();

		public void setExecutedAt(int when);

		public ArrayList<Variable> getListOfVariables();

		public int getPriority();

		public void addToPriority(int val);

		public void discardPriority();
		
		public String getName();

		public Connection getConnection();
		
		public void setConnection(Connection c);
		
		public StochasticReturnPredictor getSRP();

}
