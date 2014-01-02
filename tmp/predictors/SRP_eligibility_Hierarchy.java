package massim.agent.mind.harm.components.predictors;

import java.util.ArrayList;
import java.util.Random;

import massim.agent.body.agentWorldInterface.actuatorLayer.Actuator;
import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.PhysilogicalAction;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.components.predictors.eligibility.EligibilityQueue;
import massim.agent.mind.harm.components.predictors.eligibility.EligibilityQueueII;
import massim.agent.mind.harm.components.predictors.eligibility.Inds;
import massim.agent.mind.harm.components.qmatrix.ActionValsVector;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;
import massim.framework.util.MyLogger;

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
public class SRP_eligibility_Hierarchy implements StochasticReturnPredictor{
	
	private final EligibilityQueueII elig;
	
	private final MyLogger log;
	private final String cn;
	private final int LEV = 115;
	
	private final QSAMatrix Q;
	
	VariableList vars;
	ActionList actions;
	RootDecisionSpace space;
	
	private double epsilon; // (adaptive) epsilon.greedy
	private Random r;
	
	// contains all data that can be changed using XML config file
	private final HarmSystemSettings settings;
	
	Inds prev = new Inds();
	Action selected = null;
	
	private final PhysilogicalAction me;
	
	PhysiologicalStateSpace phys;
	/**
	 * init with empty vars, these will be updated online
	 * for now (flat SRP) init with primitive actions, do not generate anything
	 *  
	 * @param log
	 * @param vars - variable list
	 * @param actions - action list
	 */
	public SRP_eligibility_Hierarchy(MyLogger log, 
			ArrayList<Action> childActions,			 
			ArrayList<Variable> childVars, 
			PhysiologicalStateSpace p, HarmSystemSettings set, PhysilogicalAction me, 
			ActuatorHARM a){
		this.settings = set;
		this.me = me;
		
		// initialize variables and actions
		this.vars = new VariableList(childVars);
		this.actions = new ActionList(childActions);
		
		// TODO intentions
		this.phys = p;
		
		this.cn = this.getClass().getName();
		this.log = log;
		
		this.Q = new QSAMatrix(new VariableList(), actions, log);
	//	this.Q.updateVarList(vars);
		
		this.epsilon = settings.minEpsilon; 
		this.r = new Random();
		
		this.elig = new EligibilityQueueII(
				settings.eligibilityLength, settings.gamma, settings.lambda, settings.alpha);
		
		
		/*
		log.pl(LEV,"HIERARCHICAL SRP: inited for: '" +me.getName()+
				"', num of my actions is now: "+actions.size()+" and num vars: "+vars.size());
		log.pl(LEV,"SRP iited: variables: "+vars.toString());
		
		log.pl(LEV,"Q: dim: "+this.Q.getDimension()+" numactions: "+this.Q.getNumActions());
		log.pl(LEV, "num vars and num actions "+this.vars.size()+" "+this.actions.size());
		log.pl(LEV, this.vars.toString());

		/*
		System.out.println("REMOVING tESt "+this.vars.toString()+" "+this.Q.getDimension());
		this.Q.updateVarList(vars);
		this.Q.deleteVariable(vars.get(0));
		this.vars.remove(0);
		System.out.println("REMOVING tESt "+this.vars.toString()+" "+this.Q.getDimension());
		*/
	}
	
	public void stats(ArrayList<Action> ac){
		System.out.println("dim of Q: "+this.Q.getDimension()+" actions gna: "+this.Q.getNumActions());
		
		String[] a = Q.varNamesToArray();
		for(int i=0; i<a.length;i++)
			System.out.print(" "+a[i]);
		System.out.println(" ");
		
		System.out.println("num vars in srp "+vars.size()+" num actions in srp "+this.actions.size()
				+" and complex action child size: "+ac.size());
	}
	
	/**
	 * @param a - action (or variable below) to be removed from the DS
	 */
	public void removeAction(Action a){
		this.Q.removeAction(a);
	}
	
	public void removeVariable(Variable var){
		this.Q.deleteVariable(var);
	}
	
	/**
	 * adding of new actions and variables is buffered, call each once 
	 */
	public void updateNewActions(){
		this.Q.updateActionList(this.actions);
		
	}
	
	public void updateNewVariables(){
		this.Q.updateVarList(this.vars);
	}
	
	
	
	public QSAMatrix getMatrix(){ return this.Q; }
	
	public Action getActionGenerated(){ return this.selected; }
	
	
	public void infer(ActuatorHARM a){

		// update matrix dimensions
		this.Q.updateVarList(vars);
		log.pl(LEV,"SRP infering: variables: "+vars.toString());
		
		// get the action that has been executed, the previous state of the world and inds in Q(s,a)
		boolean previousKnown = this.getExecutedAction(a);
		
		log.pl(LEV+15,"--------------- acting");
		
		// read some reinforcement
		int re = this.getReinforcement();
		
		// get the epsilon value
		epsilon = this.getEpsilon();
		
		// if the previous action can be found in the matrix and has been initialized
		if(prev.size() == this.Q.getDimension() && previousKnown){
			// solve the Q-Learning equation
			this.updateKnowledge(re);
			//this.updateOld(re);
		}
		//System.out.println("selecting........");
		selected = this.selectNew();
		
		log.pl(LEV,"END: "+this.Q.getInds(vars, selected, false).toString());
		
		// move with the actuator
		//a.setActionToExecute(selected);
		
	}
	
	
	/**
	 * we must check whether our action has been really executed
	 * if yes, we know indexes (of state,action) in the matrix
	 * if no, we must get new indexes  
	 * @param a - actuator who knows what action has been executed
	 */
	private boolean getExecutedAction(Actuator a){
		
		//System.out.println(" action is this: "+a.getActionExecuted());
		
		// read the action executed
		Action executed = actions.getActionByName(a.getActionExecuted());
		
		// action is not known to this system 
		if(executed == null){
			log.pl(LEV, "unknown action, ignoring..");
			return false;
		}
		if(selected == null){
			log.pl(LEV, "action not selected, hope next time....");
			return false;
		}
		//System.out.println("executed action is: "+selected.getName());
		
		if(executed.equals(selected)){
			log.pl(LEV+10, "Actually executed action is that mine! "+executed.getName());
			
			// get the position in the matrix (previous state and action that was really executed) 
			prev = this.Q.getInds(vars, executed, true); // executed == selected
			
			log.pl(LEV+15,"START       "+prev.toString());
			log.pl(LEV+15,"START actual"+Q.getInds(vars, executed, false).toString());
			
			log.pl(LEV+15,"var indexes are: prev+actual:");
			for(int i=0; i<vars.size(); i++)
				log.p(LEV+15," [ "+vars.get(i).previous+" ->"+vars.get(i).actual+" ] ");
			log.pl(LEV+15," ");
			
			
		}else{
			log.pl(LEV+10, "Actualy executed action is different from that mine! "
					+executed.getName()+" "+selected.getName());
			prev = this.Q.getInds(vars, executed, true);
		}
		elig.makeStepHere(prev);
		
		return true;
	}
	
	
	private double getEpsilon(){
		double out = 0;
		
		if(phys.size() != 1){
			//System.out.println("TODOOOOOOOOOOOOOOOOO  ");
		}
		double max = phys.getMaxMotivation(0);
		// now, for max stimulation the epsilon is zero, for min stimulation epsilon is 1
		double actual = phys.getMotivation(0).get();
		
		double range = 1-settings.minEpsilon; // this should be less than 1
		
		// this is actual value decayed by the minEpsilon and in opposite direction
		out = range*(actual/max);
		// good direction
		out = 1-out;
		
		// System.out.println("stimulation value is: "+phys.getStimulation(0).get()+" and epsilon "+out);
		return out;
	}
	
	private int getReinforcement(){
		// read some reinforcement
		int re = 0;
		for(int i=0; i<this.phys.size(); i++){
			if(this.phys.reinforced(i)){
				
				re = this.phys.getReinforcementStrength(i);
				System.out.println("RRRRRRRRRRRRRRreeeeeeeeeeeee , stength: "+re);
			}
		}
		return re;
	}
	
	private Action selectNew(){
		//System.out.println("a");
		ActionValsVector news = this.Q.getActualValues(vars);
		//System.out.println("b");
		Action selected;
		// select using greedy?
		if(r.nextDouble() > this.epsilon){
			
		//	System.out.println("greedy!");
			selected = actions.getActionByName(news.getMaxAction().name);
			
		// select randomly?
		}else{
			
			//System.out.println("\t\trandom!");
			selected = actions.getActionByName(news.getRandAction().name);
			
		}
		return selected;
	}

	
	
	private void updateKnowledge(int re){
		
	//	System.out.println("update: "+prev.size()+" "+Q.getDimension());
		// read the previous value in the matrix
		int qsa = Q.get(prev);
		if(qsa == -1)
			qsa = 0;
		// read the value of the best actual action
		int qsaa;
		Integer x = Q.getActualValues(vars).getMaxAction().val;
		if(x==null)
			qsaa = 0;
		else
			qsaa = x;
		
		double result = qsa + settings.alpha*(re + settings.gamma*qsaa - qsa);
		
		double delta = re + settings.gamma*qsaa-qsa;
	
		// write out the previous step
		Q.set(prev, (int)result);
		//System.out.println("calling the learn and prev is: "+prev.toString());
		// write the rest of them
		elig.learn(delta, Q);
		//System.out.println("traces are: "+elig.eligibilitiesToString());
	}

	@Override
	public void generateAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateKnowledge(ActuatorHARM actuator, int reinfNow) {
		// TODO Auto-generated method stub
		
	}
	
}
