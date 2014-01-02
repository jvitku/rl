package massim.agent.mind.harm.components.predictors;

import java.util.Random;

import massim.agent.body.agentWorldInterface.actuatorLayer.Actuator;
import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.components.predictors.eligibility.EligibilityQueue;
import massim.agent.mind.harm.components.predictors.eligibility.Inds;
import massim.agent.mind.harm.components.qmatrix.ActionValsVector;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
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
public class SRP_eligibility_flat {
	
	private final EligibilityQueue elig;
	
	private final MyLogger log;
	private final String cn;
	private final int LEV = 115;
	
	private final QSAMatrix Q;
	
	VariableList vars;
	ActionList actions;
	RootDecisionSpace space;
	
	private double epsilon; // (adaptive) epsilon.greedy
	private Random r;
	
	// contains all data that can be changed using XML config gfile
	private final HarmSystemSettings s;
	
	Inds prev = new Inds();
	Action selected = null;
	
	PhysiologicalStateSpace phys;
	/**
	 * init with empty vars, these will be updated online
	 * for now (flat SRP) init with primitive actions, do not generate anything
	 *  
	 * @param log
	 * @param vars - variable list
	 * @param actions - action list
	 */
	public SRP_eligibility_flat(MyLogger log, RootDecisionSpace space, PhysiologicalStateSpace p,
			HarmSystemSettings set){
		this.s = set;
		this.space = space;
		this.vars = space.getVariables();
		this.actions = space.primitiveActions;
		this.phys = p;
		
		this.cn = this.getClass().getName();
		this.log = log;
		this.Q = new QSAMatrix(vars, actions, log);
		
		this.epsilon = s.minEpsilon; 
		this.r = new Random();
		
		this.elig = new EligibilityQueue(s.eligibilityLength, s.gamma, s.lambda, s.alpha);
		
		log.pl(LEV, "SRP: inited OK num oactions: "+actions.size()+" num vars: "+vars.size());
	}
	
	// tmp
	public QSAMatrix getMatrix(){ return this.Q; }
	
	public Action getActionGenerated(){ return this.selected; }
	
	
	public void infer(ActuatorHARM a){

		// update matrix dimensions
		this.Q.updateVarList(vars);
		
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
		
		selected = this.selectNew();
		
		log.pl(LEV+15,"END: "+this.Q.getInds(vars, selected, false).toString());
		
		// move with the actuator
		a.setActionToExecute(selected);
		
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
		
		double range = 1-s.minEpsilon; // this should be less than 1
		
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
		ActionValsVector news = this.Q.getActualValues(vars);
		Action selected;
		// select using greedy?
		if(r.nextDouble() > this.epsilon){
			
//			System.out.println("greedy!");
			selected = actions.getActionByName(news.getMaxAction().name);
			
		// select randomly?
		}else{
			
		//	System.out.println("\t\trandom!");
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
		
		double result = qsa + s.alpha*(re + s.gamma*qsaa - qsa);
		
		double delta = re + s.gamma*qsaa-qsa;
	
		// write out the previous step
		Q.set(prev, (int)result);
		//System.out.println("calling the learn and prev is: "+prev.toString());
		// write the rest of them
		elig.learn(delta, Q);
		//System.out.println("traces are: "+elig.eligibilitiesToString());
	}
	
}
