package massim.agent.mind.harm.components.hierarchy;

import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.body.physiological.StateSpace;
import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.ComplexAction;
import massim.agent.mind.harm.actions.PhysilogicalAction;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.components.hierarchy.util.ActionVariableFilter;
import massim.agent.mind.harm.components.hierarchy.util.HierarchyComponent;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.intentions.IntentionalStateSpace;
import massim.agent.motivationActionMapping.ConnectionManager;
import massim.framework.util.MyLogger;

public class Learner implements HierarchyComponent{
	
	private final int LEV = 15;
	private final MyLogger log;
	
	private final RootDecisionSpace root;
	private final HarmSystemSettings settings;
	private final ActionList allActions;
	private final PhysiologicalStateSpace space;
	private final IntentionalStateSpace ints;
	
	private final ActionVariableFilter filter;	// stores info about the last executed primitive act.
	
	private final ActuatorHARM actuator;
	
	private int step;

	private final ConnectionManager connections;	// holds connections between motivations and actions

	public Learner(RootDecisionSpace root,PhysiologicalStateSpace space, IntentionalStateSpace ints, 
			HarmSystemSettings settings, MyLogger log, ActuatorHARM actuator,
			ActionList allActions, ConnectionManager connections, ActionVariableFilter filter){
	
		this.log = log;
		this.root = root;
		this.ints = ints;
		this.space = space;
		this.filter = filter;
		this.settings = settings;
		this.actuator = actuator;
		this.allActions = allActions;
		this.connections = connections;
	
		log.pl(LEV,"Learner: initializing learner");
		
		this.step = 0;
	}

	/**
	 * learn some subset of abstract actions in the hierarchy
	 * actions that fulfill: 
	 * 	-the movement in the decision space has been just made
	 *  -some of the known actions (in my actions) has been just executed
	 */
	public void learn(){
		// iterate over all non primitive actions
		ComplexAction a;
		for(int i=0; i<this.allActions.size(); i++){
			if(!this.allActions.get(i).isPrimitive()){
				a = (ComplexAction)this.allActions.get(i);
				
				if(a.getConnection() == null){
					log.pl(LEV, "Some problem, complex action not connected!: "+a.getName());
					continue;
				}
				a.getSRP().updateKnowledge(actuator, a.getMyReinforcementVal());
			}
		}
	}

	@Override
	public void preSimulationStep() {
		this.step++;
	}

	@Override
	public void postSimulationStep() {
	}
}
