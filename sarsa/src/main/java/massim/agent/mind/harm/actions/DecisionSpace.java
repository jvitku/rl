package massim.agent.mind.harm.actions;

import java.util.ArrayList;

import massim.agent.mind.harm.variables.Variable;

/**
 * defines action
 * there will be two kinds of action: 
 * 	-(generally)abstract    complexity >0
 * 	-primitive  			complexity =0
 * 
 * 
 * this is supposed to be stored in some arrayList of Actions
 * 
 * @author jardavitku
 *
 */
public abstract class DecisionSpace implements Action, SomeSpaceWithVariables {
	
	protected final boolean isPrimtive;
	
	protected int complexity;
	
	// action name, when the action is primitive, the name is equal to the action string
	protected final String name;
	
	// list of variables that are changed when using this action 
	protected ArrayList<Variable> variables; // TODO...maybe?
	
	
	protected DecisionSpace(boolean isPrimitive, String name){
		
		this.name = name;
		this.isPrimtive = isPrimitive;
		this.complexity = 0;	// primitive by def
		
	}
	
	
	
	// whether the action is primitive ( => no childs, no R matrix etc..)
	@Override
	public boolean isPrimitive(){ return this.isPrimtive; }
	
	@Override
	public int getComplexity(){ return this.complexity; }
	
	@Override
	public void setComplexity(int val){
		if(!this.isPrimtive){
			this.complexity = val;
			return;
		}
		else
			System.err.println("Action:setComplexity: cannot change complexity of primitive action!");
	}

	@Override
	public String getName(){ return this.name; }


}
