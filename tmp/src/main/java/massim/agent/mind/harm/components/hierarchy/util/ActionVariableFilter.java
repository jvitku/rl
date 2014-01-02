package massim.agent.mind.harm.components.hierarchy.util;

import java.util.ArrayList;

import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.harm.variables.PropertyList;
import massim.agent.mind.harm.variables.Value;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;
import massim.framework.util.MyLogger;

public class ActionVariableFilter {
	
	private final HarmSystemSettings settings;
	private final int LEV = 5;
	private final MyLogger log;
	
	private int step;
	
	private int lastExecuted;
	private Action lastPrimitive;
	
	public ActionVariableFilter(HarmSystemSettings set, MyLogger log){
		
		this.settings = set;
		this.log = log;
		
		this.step = 0;
		
		this.lastExecuted = 0;
		this.lastPrimitive = null;
	}
	
	public void step(){
		this.step++;
	}
	
	/**
	 * mark given action that has been executed now 
	 * @param a
	 */
	// TODO setExecuted in non-primitive actions
	public void updateActionList(Action a, int which){
		
		log.pl(LEV+10, "Updating this action "+a.getName()+" step: "+this.step);
		a.setExecutedAt(this.step);
		a.setJustExecuted();
		this.lastExecuted = which;
		this.lastPrimitive = a;
	}
	
	public Action getLastPrimitiveExecuted(){ return this.lastPrimitive; }
	
	public int getLastPrimitiveInd(){ return this.lastExecuted; }
	
	/**
	 * update when the particular values have been seen (just variables)
	 * @param p - list of all environment properties
	 */
	public void updateProperties(PropertyList p){
		VariableList vars = p.getVariables();
		Variable v;
		
		// for all variables
		for(int i=0; i<vars.size(); i++){
			v = vars.get(i);
			v.vals.get(v.actual).usedInStep = this.step;	// save that this value is set now
			
			log.pl(LEV+10, "StateVariable: "+v.getName()+" value seen now: "+
					v.vals.get(v.actual).getStringVal());
		}
		
		VariableList consts = p.getConstants();
		for(int i=0; i<consts.size(); i++){
			v = consts.get(i);
			v.vals.get(0).usedInStep = this.step;	// save that this value is set now
			// for all constants, update info when they are still constants!
			v.constAtStep = this.step;
		}
	}
	
	/**
	 * go throw the list of variables, for each variable:
	 * 	-check whether more than one value has 1*decay^(step-itsStep) > trigger
	 *  -if yes, the variable has changed in the epoch and should be here (in DS)
	 *  
	 * @param p 		- list of all properties (variables and constants)
	 * @param numSteps	- if -1, then use trigger, if >0, then use this (hard) length of window
	 * @return 			- variables which are in the current "window"
	 */
	public ArrayList<Variable> getEpochVariables(PropertyList p, int numSteps){
		
		ArrayList<Variable> out = new ArrayList<Variable>();
		VariableList vars = p.getVariables();
		
		boolean oneFound;
		Variable v;
		
		// for all vars
		for(int i=0; i<vars.size(); i++){
			v = vars.get(i);
			
			// until two fulfilling values found
			for(int j=0; j<v.getNumValues(); j++){
				oneFound = false;
				
				// if should accept:
				if(this.shouldTake(v.vals.get(i), numSteps)){
					// if one OK value already found, add it to output
					if(oneFound){
						out.add(v);
						break;
					// else say that one has been found and continue
					}else{
						oneFound = true;
					}
				}
			}
		}
		return out;
	}

	private boolean shouldTake(Value val, int numSteps){
		// use the hard set window length
		if(numSteps > -1)
			return ( (this.step- val.lastSeen()) <= numSteps );
		
		// use trigger set in XML file  (each step decay by stepDecay..)
		double p = Math.pow(this.settings.stepDecay,  (this.step-val.lastSeen())  );
		
		return ( p > this.settings.trigger );
	}
	
	public ArrayList<Action> getEpochActions(ActionList acts, int numSteps){
		ArrayList<Action> out = new ArrayList<Action>();
		Action a;
		log.pl(LEV, "getting epoch actions, all action num is "+acts.size());
		
		// for all actions, if is OK, add to list
		for(int i=0; i<acts.size(); i++){
			a = acts.get(i);
			
			if(this.shouldTake(a, numSteps))
				out.add(a);
		}
		return out;
	}
	
	private boolean shouldTake(Action a, int numSteps){
		// use the hard set window length
		if(numSteps > -1)
			return ( (this.step-a.lastExecuted()) <= numSteps );
		
		log.pl(LEV, "Decay is: "+this.settings.stepDecay+" LE is: "+
				a.lastExecuted()+" step: "+this.step+" name is: "+a.getName());
		
		// use trigger set in XML file  (each step decay by stepDecay..)
		double p = 1-(this.settings.stepDecay * (this.step-a.lastExecuted())  );
		
		return ( p > this.settings.trigger );
	}

}
