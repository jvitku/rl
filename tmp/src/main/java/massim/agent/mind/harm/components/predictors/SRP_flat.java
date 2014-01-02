package massim.agent.mind.harm.components.predictors;

import java.util.ArrayList;
import java.util.Random;

import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.PhysiologicalStateSpace;

import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.RootDecisionSpace;
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
public class SRP_flat implements StochasticReturnPredictor{
	
	private final MyLogger log;
	private final String cn;
	private final int LEV = 5;
	
	private final QSAMatrix Q;
	
	VariableList vars;
	ActionList actions;
	RootDecisionSpace space;
	
	private double epsilon; // (adaptive) epsilon.greedy
	private final double gamma = 0.9;	// ??
	private final double alpha = 0.5;  	// ..
	private Random r;
	
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
	public SRP_flat(MyLogger log, RootDecisionSpace space, PhysiologicalStateSpace p){
		this.space = space;
		this.vars = space.getVariables();
		this.actions = space.primitiveActions;
		this.phys = p;
		
		this.cn = this.getClass().getName();
		this.log = log;
		
		this.Q = new QSAMatrix(vars, actions, log);
		
		this.epsilon = 0.2; 
		this.r = new Random();
		
		
		log.pl(LEV, "SRP: inited OK num oactions: "+actions.size()+" num vars: "+vars.size());
	}
	
	// tmp
	public QSAMatrix getMatrix(){ return this.Q; }
	
	public Action getActionGenerated(){ return this.selected; }
	
	
	public void infer(){
		
		// update matrix dimensions
		this.Q.updateVarList(vars);
		
		// read some reinforcement
		int re = this.getReinforcement();
		
		// get the epsilon value
		epsilon = this.getEpsilon();
		
		
		// if the previous action can be found in the matrix and has been initialized
		if(prev.size() == this.Q.getDimension()){
			// solve the Q-Learning equation
			this.updateOld(re);
		}
		
		selected = this.selectNew();
		
		prev = this.Q.getInds(vars, selected, false);
		
	}
	
	private double getEpsilon(){
		double out = 0;
		
		if(phys.size() != 1){
			System.out.println("TODOOOOOOOOOOOOOOOOO  ");
		}
		double max = phys.getMaxMotivation(0);
		// now, for max stimulation the epsilon is zero, for min stimulation epsilon is 1
		
		double actual = phys.getMotivation(0).get();
		
		out = 1- (actual/max);
		
		// System.out.println("stimulation value is: "+phys.getStimulation(0).get()+" and epsilon "+out);
		return out;
	}
	
	private int getReinforcement(){
		// read some reinforcement
		int re = 0;
		for(int i=0; i<this.phys.size(); i++){
			if(this.phys.reinforced(i)){
				re = 10;
				System.out.println("RRRRRRRRRRRRRRreeeeeeeeeeeee");
			}
		}
		return re;
	}
	
	private Action selectNew(){
		ActionValsVector news = this.Q.getActualValues(vars);
		Action selected;
		// select using greedy?
		if(r.nextDouble()>this.epsilon){
			
			System.out.println("greedy!");
			selected = actions.getActionByName(news.getMaxAction().name);
			
		// select randomly?
		}else{
			
			System.out.println("\t\trandom!");
			selected = actions.getActionByName(news.getRandAction().name);
			
		}
		return selected;
	}
	
	private void updateOld(int re){
		
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
		//int qssaa = Q.getActualValues(vars).getMaxAction().val;
		
		double result = qsa + alpha*(re + gamma*qsaa - qsa);
	
		// write out
		Q.set(prev, (int)result);
	}

	@Override
	public void stats(ArrayList<Action> ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAction(Action a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeVariable(Variable var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNewActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNewVariables() {
		// TODO Auto-generated method stub
		
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
