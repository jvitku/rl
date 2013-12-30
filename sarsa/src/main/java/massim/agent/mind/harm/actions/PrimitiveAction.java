package massim.agent.mind.harm.actions;

import java.util.ArrayList;

import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;

public class PrimitiveAction extends DecisionSpace {

	protected String ID;
	private boolean justExecuted;
	private int lastExecuted;
	
	private int priority;
	
	/**
	 * create the primitive action
	 * @param name - equals to the action string sent to the server
	 */
	public PrimitiveAction(String name) {
		super(true, name);
		this.ID = IDGenerator.generate(name);
		this.justExecuted= false;
		this.lastExecuted = 0;
		this.discardPriority();
	}
	
	
	
	// everything the primitive action does not support..
	@Override
	public ArrayList<Action> getChilds(){
		System.err.println("PrimitiveAction: getChilds(): primitive action have no childs");
		return null;
	}

	@Override
	public void addChild(Action a) {
		System.err.println("PrimitiveAction: setChilds(): primitive action have no childs");
	}

	public Action clone(){ return new PrimitiveAction(this.name); }



	@Override
	public String getID() { return this.ID; }



	@Override
	public ArrayList<Action> getActions() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String[] getActionNames() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public VariableList getConstants() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public VariableList getVariables() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public long getSpaceSize() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getSpaceDimension() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getNumActions() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getLastExecuted() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getNumVariables() {
		return 0;
	}



	@Override
	public QSAMatrix getQMatrix() {
		System.err.println("PrimitiveAction: getQMatrix: primitive actions does not contain Q(s,a)");
		return null;
	}


	@Override
	public void setQMatrix(QSAMatrix m) {
	}



	@Override
	public boolean justExecuted() { return this.justExecuted; }
	@Override
	public void discardExecution() { this.justExecuted= false; }
	@Override
	public void setJustExecuted() { this.justExecuted = true; }
	@Override
	public int lastExecuted() { return this.lastExecuted; }
	@Override
	public void setExecutedAt(int when) { this.lastExecuted = when; }



	@Override
	public ArrayList<Variable> getListOfVariables() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void addToPriority(int val) {
		this.priority += val;
	}

	@Override
	public void discardPriority() {
		this.priority = 0;
	}
	
}
