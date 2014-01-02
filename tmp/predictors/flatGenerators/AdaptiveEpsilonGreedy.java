package massim.agent.mind.harm.components.predictors.flatGenerators;

import java.util.Random;

import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.Motivation;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.PrimitiveAction;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.components.hierarchy.util.ActionVariableFilter;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.harm.components.predictors.SRP_eligibility_flat;
import massim.agent.mind.intentions.IntentionalStateSpace;
import massim.agent.motivationActionMapping.ConnectionManager;
import massim.framework.util.MyLogger;
import massim.shared.SharedData;

/**
 * 
 * 
 * @author jardavitku
 *
 */
public class AdaptiveEpsilonGreedy implements FlatActionSelector{
	
	private final int LEV = 15;
	private final MyLogger log;
	
	private final RootDecisionSpace root;
	private final PhysiologicalStateSpace space;
	private final HarmSystemSettings settings;
	private final ActionList allActions;
	private final ConnectionManager connections;
	private final SharedData shared;
	private final ActionVariableFilter filter;	// stores info about the last eecuted primitive act.
	
	private int step;
	private final double rand;
	
	private final double randInExecutionMode = 0.05;
	
	public AdaptiveEpsilonGreedy(RootDecisionSpace root,PhysiologicalStateSpace space, 
			IntentionalStateSpace ints, 
			HarmSystemSettings settings, MyLogger log, ActuatorHARM actuator,
			ActionList allActions, ConnectionManager connections, ActionVariableFilter filter,
			SharedData shared){
		
		this.log = log;
		this.root = root;
		this.space = space;
		this.filter = filter;
		this.settings = settings;
		this.shared = shared;
		this.allActions = allActions;
		this.connections = connections;
		
		this.rand = this.settings.flatSelectorRandomization;	// how much to randomize
	
		this.r= new Random();
		
		this.step = 0;
	}

	private void actRandomly(ActuatorHARM ac){
		int[] layer;
		layer = this.allActions.getActionsWithComplexity(0);
		
		// get random index of primitive action
		int ind = r.nextInt(layer.length);	
		
		ac.setActionToExecute(this.allActions.get(layer[ind]));
	}
	
	@Override
	public void selectAction(ActuatorHARM ac) {
		this.stats();
		
		
		if(this.somethingNotConnected()){
			this.mypl("------------ some phys variable not connected, acting randomly!" +
					" action is: "+ac.getActual());
			this.actRandomly(ac);
			return;
		}
		
		boolean selectMax = r.nextDouble() > this.rand;
		
		if(this.shared.inPlanExecutionMode())
			selectMax = r.nextDouble() > this.randInExecutionMode;
		
		if(!selectMax){
			this.actRandomly(ac);
			this.mypl("rand");
			return;
		}
		this.mypl("max");
		
		int[] layer;
		PrimitiveAction a;
		layer = this.allActions.getActionsWithComplexity(0);
		int max = -1;
		Action selected=null;
		
		for(int i=0; i<layer.length; i++){
			if(this.allActions.get(layer[i]) instanceof PrimitiveAction){
				a = (PrimitiveAction)this.allActions.get(layer[i]);
				if(a.getPriority() > max){
					selected = a;
					max = a.getPriority();
				}
			}else{
				System.err.println("wrong action complexity");
			}
				
		}
		/*
		if(selected.getPriority() == 0){
			System.out.println("rand also.. all are: 0");
			int sel = r.nextInt(layer.length);
			selected = (PrimitiveAction)this.allActions.get(layer[sel]);
		}
		// each dec. space when does not know, select randomly for himself
		*/
		ac.setActionToExecute(selected);
		this.mypl("just using greedy for now and selecting this one: "+ac.getActual());
	}

	private final Random r;
	//private final double epsilon = 0.1;
	
	@Override
	public void stats() {
		
		int[] layer;
		PrimitiveAction ac;
		layer = this.allActions.getActionsWithComplexity(0);
		this.mypl("Action complexity 0 reached, num of actions here is :"+layer.length);
		
		for(int i=0; i<layer.length; i++){
			if(! (this.allActions.get(layer[i]) instanceof PrimitiveAction))
				System.err.print("epsilon greedy cast action: bad complexitY!!");
			else{
				ac = (PrimitiveAction)this.allActions.get(layer[i]);
				this.mypl("Action called: "+ac.getName()+" priority: "+ac.getPriority());
			}
		}
	}

	
	/**
	 * some physiological state variable not connected to it's own action?
	 * @return
	 */
	private boolean somethingNotConnected(){
		Motivation m;
		for(int i=0; i<this.space.size(); i++){
			m = this.space.getMotivation(i);
			if(! this.connections.isConnected(m))
				return true;
		}
		return false;
	}
	
	
	@Override
	public void preSimulationStep() {
		this.step++;
	}

	@Override
	public void postSimulationStep() {		
	}
	
	private void mypl(String what){
		this.log.pl(LEV,"AdaptiveEpsilonGreedy: "+what);
	}
}
