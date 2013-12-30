package massim.agent.mind.harm.actions;

import java.util.ArrayList;

import massim.framework.util.MyLogger;


/**
 * this is list of all known actions so far
 * 
 * @author jardavitku
 *
 */
public class ActionList {
	
	public ArrayList<Action> actions;
	private int lastExecuted;
	private boolean changed;	//  whether the hierarchy has changed
	
	public ActionList(ArrayList<Action> acts){
		this.actions = acts;
	}
	
	public ActionList(){
		this.actions = new ArrayList<Action>();
		this.lastExecuted = -1;
	}
	public int size(){ return actions.size(); }
	
	public boolean isEmpty(){ return actions.size()==0; }
	
	public ArrayList<Action> getActions(){ return this.actions; }
	
	public int lastExecuted(){ return this.lastExecuted; }
	
	public Action get(int i){
		if(i>= this.actions.size()){
			System.err.println("ActionList: get: index out of bounds!");
			return null;
		}
		else
			return this.actions.get(i);
	}
	
	public synchronized void discardChangeLabel(){ this.changed = false; }
	public boolean hierarchyChanged(){ return this.changed; }
	
	public void discardAllExecuted(){
		for(int i=0; i<this.actions.size(); i++){
			this.actions.get(i).discardExecution();
		}
	}
	
	
	public synchronized Action getActionByName(String name){
		if(actions == null || actions.size() == 0){
			System.err.println("ActionList: getActionByName: action list is empty");
			return null;
		}
		for(int i=0; i<actions.size(); i++){
			if(this.actions.get(i).getName().equalsIgnoreCase(name))
				return this.actions.get(i);
		}
		System.err.println("ActionList: getActionByName: this name not found in the list: "+name);
		return null;
	}
	
	/**
	 * is mainly for remembering the last executed action
	 * 
	 * @param which - index of action to execute 
	 * @return - name of action (in case of primitive actions, just send it to server
	 */
	public String markExecution(int which){
		if(this.actions.size() == 0 || this.lastExecuted > this.actions.size()-1){
			System.err.println("ActionList:execute: action index to execute is out of bounds" +
					" or action list is empty...");
			return null;
		}
		this.lastExecuted = which;
		this.actions.get(which).setJustExecuted();
		return this.actions.get(which).getName();
	}
	
	public String markExecution(String which){
		if(this.actions.size() == 0 || this.lastExecuted > this.actions.size()-1){
			System.err.println("ActionList:execute: action index to execute is out of bounds" +
					" or action list is empty...");
			return null;
		}
		int whichh = this.findIndexByName(which);
		// if the action has not been found, just ignore it! (for skip and action that are not in list)
		if(whichh == -1)
			return null;
		this.lastExecuted = whichh;
		this.actions.get(whichh).setJustExecuted();
		return this.actions.get(whichh).getName();
	}
	
	/**
	 * find the index of an action by the given name
	 * @param name
	 * @return - index in the aciton list
	 */
	private int findIndexByName(String name){
		for(int i=0; i<this.actions.size(); i++){
			if(this.actions.get(i).getName().equalsIgnoreCase(name))
				return i;
		}
		//System.err.println("ActionList: findIndexByName: action wit this name was not found! "+name);
		return -1;
	}
	
	/**
	 * watch out, these actions are not cloned..
	 * @return ActionList containing only primitive actions
	 */
	public ActionList getPrimitiveActions(){
		ActionList out = new ActionList();
		
		for(int i=0; i<this.actions.size(); i++){
			if( this.actions.get(i).isPrimitive())
				out.add(this.actions.get(i));
		}
		return out;
	}
	
	public ActionList getNonPrimitiveAtions(){
		ActionList out = new ActionList();
		
		for(int i=0; i<this.actions.size(); i++){
			if( ! this.actions.get(i).isPrimitive())
				out.add(this.actions.get(i));
		}
		return out;
	}
	
	public int getMaxAxtionComplexity(){
		int max = 0;
		for(int i=0; i<this.actions.size(); i++){
			if( this.actions.get(i).getComplexity() > max ) 
				max = this.actions.get(i).getComplexity();
		}
		return max;
	}
	
	public int[] getActionsInPlanningWindow(int cpx){
		// the max num of such actions
		int [] actions = new int[this.actions.size()];
		int found = 0;
		
		for(int i=0; i<this.actions.size(); i++){
			if(this.actions.get(i).getComplexity() >= cpx)
				actions[found++] = i;
		}
		
		// create the new array with this length
		int[] out = new int[found];
		
		for(int i=0; i<found; i++)
			out[i] = actions[i];
		
		return out;
	}
	
	public int[] getActionsWithComplexity(int cpx){
		// the max num of such actions
		int [] actions = new int[this.actions.size()];
		int found = 0;
		
		for(int i=0; i<this.actions.size(); i++){
			if(this.actions.get(i).getComplexity() == cpx)
				actions[found++] = i;
		}
		
		// create the new array with this length
		int[] out = new int[found];
		
		for(int i=0; i<found; i++)
			out[i] = actions[i];
		
		return out;
	}
	
	public String toString(){
		String out = "List of Actions is: | ";
		
		for(int i=0; i<this.actions.size(); i++)
			out = out+this.actions.get(i).getName()+"; ";
		
		return out+"|";
	}
	
	// TODO !!! check the properties of action and determine whether to add, or just extend ..
	public void add(Action a){
		this.changed = true;
		actions.add(a);
	}
	
	/**
	 * set all priorities to 0
	 */
	public void discardAllPriorities(){
		for(int i=0; i<this.actions.size(); i++)
			this.actions.get(i).discardPriority();
	}
	
}
