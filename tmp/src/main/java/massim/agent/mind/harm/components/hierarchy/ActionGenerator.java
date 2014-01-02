package massim.agent.mind.harm.components.hierarchy;

///import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.ComplexAction;
import massim.agent.mind.harm.actions.PhysilogicalAction;
import massim.agent.mind.harm.actions.PrimitiveAction;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.components.hierarchy.util.ActionVariableFilter;
import massim.agent.mind.harm.components.hierarchy.util.HierarchyComponent;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.harm.components.predictors.SRP_eligibility_flat;
import massim.agent.mind.harm.components.predictors.flatGenerators.AdaptiveEpsilonGreedy;
import massim.agent.mind.harm.components.predictors.flatGenerators.EpsilonGreedy;
import massim.agent.mind.harm.components.predictors.flatGenerators.FlatActionSelector;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.intentions.IntentionalStateSpace;
import massim.agent.motivationActionMapping.ConnectionManager;
import massim.framework.util.MyLogger;
import massim.shared.SharedData;

public class ActionGenerator implements HierarchyComponent {

	private final int LEV = 15;
	private final MyLogger log;
	
	private final RootDecisionSpace root;
	private final PhysiologicalStateSpace space;
	private final HarmSystemSettings settings;
	private final ActionList allActions;
	private final ConnectionManager connections;
	
	
	private final ActionVariableFilter filter;	// stores info about the last eecuted primitive act.
	
	private final FlatActionSelector selector;
	
	private int step;
	
	public ActionGenerator(RootDecisionSpace root,PhysiologicalStateSpace space, 
			IntentionalStateSpace ints, 
			HarmSystemSettings settings, MyLogger log, /*ActuatorHARM actuator*/
			ActionList allActions, ConnectionManager connections, ActionVariableFilter filter, 
			SharedData shared){
		
		this.log = log;
		this.root = root;
		this.space = space;
		this.filter = filter;
		this.settings = settings;
		this.allActions = allActions;
		this.connections = connections;
		
		log.pl(LEV,"Hierarchy: initializing flat action selector");
		
		this.selector = null;
		
		// !!!!!
		//this.selector = new AdaptiveEpsilonGreedy(root,space,ints, settings, log, 
				//actuator,allActions, connections, filter, shared);
		
		this.step = 0;
	}
	
	/*
	public void generateAction(ActuatorHARM actuator){
		
		this.inferPrimitiveActionPrioritiesFromHierarchy();
		
		this.selector.selectAction(actuator);
	}
	*/
	
	/**
	 * go throw the hierarchy from top to the bottom, layer by layer
	 * for each abstract action, use it's own RL engine to generate action that it wants to execute
	 * (abstract or primitive) and add it's \phi_{a}*Q(s,a) value to priority: \phi_{child}
	 *   
	 * as a result, each primitive action will have set overall priority, 
	 * telling how much the agent wants to execute it
	 */
	private void inferPrimitiveActionPrioritiesFromHierarchy(){
		
		this.allActions.discardAllPriorities();

		int mx = this.allActions.getMaxAxtionComplexity();
		this.mypl("max action complexity is: "+mx);
		int[] layer;
		ComplexAction a;
		
		// for all complexities from the highest one
		for(int cpx=mx; cpx>0; cpx--){
			
			// for all actions on this layer
			layer = this.allActions.getActionsWithComplexity(cpx);
			this.mypl("L: "+cpx+", num actions  here is: "+layer.length);
			
			for(int l=0; l<layer.length; l++){
				a = (ComplexAction)this.allActions.get(layer[l]);
				this.mypl("action: "+a.getName()+" infering..");
				a.getSRP().generateAction();
			}
		}
	}
	
	public QSAMatrix getRootMatrix(){
		//System.err.println("Noone should want my matrix, I don't have one.. ");
		return null;
	}

	@Override
	public void preSimulationStep() {
		this.step++;
	}

	@Override
	public void postSimulationStep() {	
	}
	
	
	private void mypl(String what){
		this.log.pl(LEV,"ActionGenerator: "+what);
	}
}
