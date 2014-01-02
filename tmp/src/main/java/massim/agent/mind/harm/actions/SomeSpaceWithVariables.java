package massim.agent.mind.harm.actions;

import java.util.ArrayList;

import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;

public interface SomeSpaceWithVariables {
	
	// return all known actions contained in this (decision) space
	public ArrayList<Action> getActions();
	public String[] getActionNames();
	
	
	// return all known constants in this space
	public VariableList getConstants();
	
	// return all known variables in this space 
	public VariableList getVariables();
	
	public ArrayList<Variable> getListOfVariables();
		
	public long getSpaceSize();
	public int getSpaceDimension();
	
	public int getNumActions();
	
	public int getLastExecuted();
	
	public int getNumVariables();

	public String getID();
	
	public QSAMatrix getQMatrix();
	
	public void setQMatrix(QSAMatrix m);
	
}

