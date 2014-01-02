package massim.agent.mind.harm.actions;

import java.util.ArrayList;

import massim.agent.body.actionset.ActionSet;
import massim.agent.body.agentWorldInterface.senzoricLayer.sensoricPerception.SimpleConvertorMDP;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;

import massim.agent.mind.harm.variables.PropertyList;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;

import org.w3c.dom.Element;


public class RootDecisionSpace implements SomeSpaceWithVariables{

	public String ID = "rootDecisionSpaceIsJustOne:-)";
	// list(s) of all world properties, this include all constants and variables
	private PropertyList worldProperties;
	
	// list of all primitive actions  (no actions will be added during the agents life)ˆ
	public final ActionList primitiveActions;
// TODO:-)
	//private massim.agent.body.agentWorldInterface.senzoricLayer.senzoricPerception.perceptConvertor perceptConvertor;
	
	// TODO something.. :-)
	public QSAMatrix qmatrix;
	
	/**
	 * create the List of primitive actions (all actions in the root decision space)
	 * initialize the empty list of known variables (properties more generally)
	 * 
	 * in the root decision space there will be ONLY primitive actions, but ALL known variables! 
	 *  
	 * @param set - set of primitive actions initialized from XML
	 */
	public RootDecisionSpace(ActionSet set, PropertyList worldProperties, PhysiologicalStateSpace space){
		
		this.primitiveActions = new ActionList();
		this.worldProperties = worldProperties;
		
		// get all possible primitive action
		String [] actions = set.actions();
		// for every action create its instance of Action class and add it to the list of actions
		for(int i=0; i<actions.length; i++){ 
			this.primitiveActions.actions.add(new PrimitiveAction(actions[i]));
		}
		
		// convertor of perception implementing the Markov Decision Process "sensor" ((almost)all knowing)
		//this.perceptConvertor = new massim.agent.body.agentWorldInterface.senzoricLayer.senzoricPerception.perceptConvertor(this.worldProperties);
	}

	/**
	 * this updates knowledge about the environment (worldProperties) based on the perception
	 *  
	 * @param e - (root) node in the perception XML
	 */
	/*
	public void processOuterPerception(Element perception){
		this.perceptConvertor.updateKnowledge(perception);
		// TODO implement some convertor simulating simple short-range sensor for neural net
		
	}
	**/
	
	/*
	public void processInnerPerception(PhysiologicalStateSpace space){
		
		if(space.considerInnerVariables()){
			for(int i=0; i<space.size(); i++){
				// note: I will store only (int) values of physiological state space variables!
				this.worldProperties.updateVariable(space.getName(i), (int)Math.round(space.getVal(i)));
			}
		}
	}
	
	*/
	
	/**
	 * which primitive action to execute
	 * @param which - index of action
	 * @return - String to be sent to the server
	 */
	public String execute(int which){
		this.primitiveActions.markExecution(which);
		return this.primitiveActions.actions.get(which).getName();
	}
	
	/**
	 * execute the primitive action by given name
	 * @param name
	 * @return
	 */
	public void markExecution(String name){
		this.primitiveActions.markExecution(name);
	}
	

	@Override
	public ArrayList<Action> getActions() { return this.primitiveActions.getActions(); }

	@Override
	public VariableList getConstants() { return this.worldProperties.getConstants(); }

	@Override
	public VariableList getVariables() { return this.worldProperties.getVariables(); }

	@Override
	public long getSpaceSize() { return this.worldProperties.getSpaceSize();	}

	@Override
	public int getSpaceDimension() { return this.worldProperties.getSpaceDimension();	}

	@Override
	public String[] getActionNames() {
		String[] names = new String[this.primitiveActions.getActions().size()];
		
		if(names.length == 0 || names == null)
			return null;
		
		for(int i=0; i<this.primitiveActions.getActions().size(); i++)
			names[i] = this.primitiveActions.getActions().get(i).getName();
		
		return names;
	}

	@Override
	public int getNumActions() { return this.primitiveActions.actions.size(); }

	// get index of last executed action
	@Override
	public int getLastExecuted() { return this.primitiveActions.lastExecuted(); }

	@Override
	public int getNumVariables() { return this.worldProperties.getVariables().size(); }
	
	public ActionList cloneActionList(){
		
		ActionList out = new ActionList();
		
		if(this.primitiveActions.isEmpty())
			return out;
		
		// hell :-)
		for(int i=0; i< this.primitiveActions.size(); i++){
			out.actions.add(((PrimitiveAction)this.primitiveActions.getActions().get(i)).clone());
		}
		return out;
	}

	@Override
	public String getID() { return this.ID; }

	@Override
	public QSAMatrix getQMatrix() { return this.qmatrix; }
	
	@Override
	public void setQMatrix(QSAMatrix m) { this.qmatrix = m; }
	
	public PropertyList getProperties(){ return this.worldProperties; }

	@Override
	public ArrayList<Variable> getListOfVariables() {
		// TODO Auto-generated method stub
		return null;
	}
}
