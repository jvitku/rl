package massim.agent.mind.harm.variables;

import java.util.ArrayList;

/**
 * the same as action list, raw list of variables known so far
 * variables in this list are referenced from Actions (root and other decision spaces)
 * 
 * @author jardavitku
 *
 */
public class VariableList {
	
	public ArrayList<Variable> variables;
	//public ArrayList<StateVariable> constants;
	
	public VariableList(ArrayList<Variable> vrs){
		this.variables = vrs;
	}
	
	public VariableList(){
		this.variables = new ArrayList<Variable>();
		//this.constants = new ArrayList<StateVariable>();
	}
	
	public ArrayList<Variable> getArray(){ return this.variables; }
	public int size(){ return this.variables.size(); }
	public boolean isEmpty(){ return (this.variables.size() ==0); }
	
	public void remove(int what){
		if(this.variables.size()+1<what || this.variables.size() == 0){
			System.err.println("VariableList:remove: index out of bounds!");
			return;
		}
		this.variables.remove(what);
	}
	
	public Variable get(int ind){
		if(this.variables.size()+1<ind|| this.variables.size() == 0){
			System.err.println("VariableList:remove: index out of bounds!");
			return null;
		}
		return this.variables.get(ind);
	}
	
	public void add(Variable v){ this.variables.add(v); }

	public String toString(){
		String out = "List of Variables is: | ";
		
		for(int i=0; i<this.variables.size(); i++)
			out = out + this.variables.get(i).getName() +"; ";
		
		return out+" |";
	}
	
	
}
