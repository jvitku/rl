package massim.agent.mind.harm.components.predictors;

import java.util.ArrayList;
import java.util.Random;

import massim.agent.body.agentWorldInterface.actuatorLayer.Actuator;
import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.Motivation;
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
 * EDIT: this ASM is supposed to work under hierarchical selection mechanism,
 * so no randomization is used here, randomization will be implemented in the main flat ASM 
 * 
 * @author jardavitku
 *
 */
public class SRP_eligibility_HierarchyIII implements StochasticReturnPredictor{
	
	private final EligibilityQueueII elig;
	
	private final MyLogger log;
	private final String cn;
	private final int LEV = 115;
	private final int LEVII = 15;
	private final QSAMatrix Q;
	
	// this has to be here 
	// consider following situation: agent is near the food source, the water level is low
	// in this part of map the water behavior is not learned 
	// the action selector would tend to execute eating behavior
	private final int addition = 1;	  
	
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
	
	
	private final String name;
	
	PhysiologicalStateSpace phys;
	/**
	 * init with empty vars, these will be updated online
	 * for now (flat SRP) init with primitive actions, do not generate anything
	 *  
	 * @param log
	 * @param vars - variable list
	 * @param actions - action list
	 */
	public SRP_eligibility_HierarchyIII(MyLogger log, 
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
		//////this.Q = new QSAMatrix(vars, actions, log);
	//	this.Q.updateVarList(vars);
		
		this.epsilon = settings.minEpsilon; 
		this.r = new Random();
		
		this.elig = new EligibilityQueueII(
				settings.eligibilityLength, settings.gamma, settings.lambda, settings.alpha);

		this.name = "SRP: '"+this.me.getName()+"': ";
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

	
	/**
	 * epsilon greedy algorithm 
	 * when the motivation is small => epsilon is big 	(big randomization == exploration)
	 * 			motivation is big   => epsilon is small (small randomization == exploitation)
	 * 
	 * @return - value of epsilon
	 */
	private double getEpsilon(){
		double out = 0;
		
		Motivation m = this.me.myConnection.source();
		
		double max = m.getMaxMotivation();		// get max
		double actual = m.get();				// get actual
		
		double range = 1-settings.minEpsilon; // this should be less than 1
		
		// this is actual value decayed by the minEpsilon and in opposite direction
		out = range*(actual/max);
		// good direction
		out = 1-out;
		
		// System.out.println("stimulation value is: "+phys.getStimulation(0).get()+" and epsilon "+out);
		return out;
	}
	

	
	private Action selectNew(){
		ActionValsVector news = this.Q.getActualValues(vars);
		// select the best action
		Action selected = actions.getActionByName(news.getMaxAction().name);
		this.QsaVal = news.getSelectedVal();
		return selected;
	}

	private final double ee = 2.7;	// e na motivation
	private final int coeff = 10000; // TODO Try some other coefficients
	
	/**
	 * generate action:
	 * 	-from all of child actions
	 * 	-select one
	 * 	-add him priority as: \phi{child} += \phi{my} + Motivation*( Q(s,a) +1 ) 
	 */
	public void generateAction(){
		
		// get epsilon based on the actual value of motivation (small motivation => big epsilon) 
		epsilon = this.getEpsilon();
		
		selected = this.selectNew();
		
		//double motivation = this.me.myConnection.source().get() * coeff;
		
		double motivation = Math.pow(ee, this.me.myConnection.source().get())* coeff;
		
		int val = (int)(this.me.getPriority() + motivation +(this.QsaVal + this.addition));
		
		this.mypl("\t-------  \tee: "+ee+" \tsource "+
				this.me.myConnection.source().get()+" \tresult1: "+motivation+" " +
						"\toverall: "+val);
		
		/*
		this.mypl("my phi is this: "+this.me.getPriority()+" selected Qsa is "+this.QsaVal+" and it is "
				+this.selected.getName()+
				" my Motivation: "+motivation+" adding to action this: "+val);
		*/
		selected.addToPriority( val );
	}
	private int QsaVal;	// Q(s,a) value of selected action a
	
	private Action executed;
	Inds actual;
	
	
	public void updateKnowledge(ActuatorHARM actuator, int reinfNow){
		
		// get the previous action, if known, boolean is true
		// if not known, just ignore   
		
		boolean previousKnown = this.getExecutedChildAction();
		
		if(!previousKnown){
			this.mypl("previous action not known, ignoring this step...");
			return;
		}
		if(this.prev == null){
			this.mypl("prev state not known, will not learn");
			this.prev = this.Q.getInds(vars, executed, true);
			// TODO delete eligibility I think..
			return;
		}
		if(prev.size() != this.Q.getDimension()){
			this.mypl(" previous size has chenged, place here update of prev indexes..");
			this.prev = this.Q.getInds(vars, executed, true);
			// TODO eligibility
			return;
		}
		
		elig.makeStepHere(prev);
		// Q-matrix not changed, previous state known, go ahead!
		this.mypl("OK, and last executed is: "+this.executed.getName());
		
		this.prev = this.Q.getInds(vars, executed, true); // which one to delete? I think this one..
		this.actual = this.Q.getInds(vars, executed, false);
		
		this.mypl("learnning, prev and actual are: "+this.prev.toString()+" "+this.actual.toString()+
				" reinforcement strength is: "+reinfNow);
		
		this.updateKnowledgePrivate(reinfNow);
		
		this.prev = this.actual.clone();
	}

	
	/**
	 * check all child actions, get the one which is just executed and has the HIGHEST complexity
	 * store, the action into this.executed 
	 * @return - true if some justExecuted has been found
	 */
	private boolean getExecutedChildAction(){
		
		int maxAbst = -1;
		
		for(int i=0; i<this.actions.size(); i++){
			if(this.actions.get(i).justExecuted()){
				if(this.actions.get(i).getComplexity() > maxAbst){
					maxAbst = this.actions.get(i).getComplexity();
					this.executed = this.actions.get(i);
				}
			}
		}
		return (maxAbst > -1);
	}
	
	private void updateKnowledgePrivate(int re){
		
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
	
	/**
	 * my printing
	 * @param what - what to print
	 */
	private void mypl(String what){
		log.pl(LEVII, this.name+what);
	}
	
}
