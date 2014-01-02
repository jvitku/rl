package massim.agent.mind.harm.components.predictors.flatGenerators;

import java.util.Random;

import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
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

/**
 * 
 * 
 * @author jardavitku
 *
 */
public class EpsilonGreedy implements FlatActionSelector{
	
	private final int LEV = 5;
	private final MyLogger log;
	
	private final RootDecisionSpace root;
	private final PhysiologicalStateSpace space;
	private final HarmSystemSettings settings;
	private final ActionList allActions;
	private final ConnectionManager connections;
	
	private final ActionVariableFilter filter;	// stores info about the last eecuted primitive act.
	
	private int step;
	
	public EpsilonGreedy(RootDecisionSpace root,PhysiologicalStateSpace space, 
			IntentionalStateSpace ints, 
			HarmSystemSettings settings, MyLogger log, ActuatorHARM actuator,
			ActionList allActions, ConnectionManager connections, ActionVariableFilter filter){
		
		this.log = log;
		this.root = root;
		this.space = space;
		this.filter = filter;
		this.settings = settings;
		this.allActions = allActions;
		this.connections = connections;
	
		this.r= new Random();
		
		this.step = 0;
	}

	@Override
	public void selectAction(ActuatorHARM ac) {
		this.stats();
		
		int[] layer;
		PrimitiveAction a;
		layer = this.allActions.getActionsWithComplexity(0);
		int max = -1;
		Action selected=null;
		
		for(int i=0; i<layer.length; i++){
			a = (PrimitiveAction)this.allActions.get(layer[i]);
			if(a.getPriority() > max){
				selected = a;
				max = a.getPriority();
			}
		}
		
		boolean selectMax = r.nextDouble()>this.epsilon;
		if(selectMax){
			System.out.println("max ");
		}else{
			System.out.println("rand ");
			int sel = r.nextInt(layer.length);
			selected = (PrimitiveAction)this.allActions.get(layer[sel]);
		}
		if(selected.getPriority() == 0){
			System.out.println("rand also.. 0");
			int sel = r.nextInt(layer.length);
			selected = (PrimitiveAction)this.allActions.get(layer[sel]);
		}
			
		ac.setActionToExecute(selected);
		this.mypl("just using greedy for now and selecting this one: "+ac.getActual());
	}

	private final Random r;
	private final double epsilon = 0.5;
	
	@Override
	public void stats() {
		
		int[] layer;
		PrimitiveAction ac;
		layer = this.allActions.getActionsWithComplexity(0);
		this.mypl("Action complexity 0 reached, num of actions here is :"+layer.length);
		
		for(int i=0; i<layer.length; i++){
			ac = (PrimitiveAction)this.allActions.get(layer[i]);
			this.mypl("Action called: "+ac.getName()+" priority: "+ac.getPriority());
		}
	}

	@Override
	public void preSimulationStep() {
		this.step++;
	}

	@Override
	public void postSimulationStep() {		
	}
	
	private void mypl(String what){
		this.log.pl(LEV,"EpsilonGreedy: "+what);
	}
}
