package massim.agent.mind.harm;

import java.util.Random;

import massim.agent.body.actionset.ActionSet;
import massim.agent.body.agentWorldInterface.actuatorLayer.Actuator;
import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.PhysilogicalAction;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.components.hierarchy.Hierarchy;
import massim.agent.mind.harm.components.hierarchy.util.ActionVariableFilter;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.harm.components.predictors.SRP_eligibility_flat;
import massim.agent.mind.harm.variables.PropertyList;
import massim.agent.mind.harm.variables.VariableList;
import massim.agent.mind.intentions.ASimpleIntentions;
import massim.agent.mind.intentions.IntentionalStateSpace;
import massim.framework.util.MyLogger;
import massim.shared.SharedData;

/**
 * entire representation of HARM system
 * 
 * @author jardavitku
 *
 */
public class HarmSystem {
	
	// all known action s and variables 
	public ActionList allActions;
	public VariableList allVariables; 

	
	public RootDecisionSpace root;
	public PhysiologicalStateSpace physiological;
	
	private ActuatorHARM actuator;
	
	// TODO flat SRP
	//SRP_eligibility srp;
	
	private final MyLogger log;
	private final int LEV = 1118; 
	
	private final HarmSystemSettings settings;
	// tmp
	private Queue q;
	
	// new
	private final Hierarchy hierarchy;
	
	public HarmSystem(ActionSet actuators, PropertyList worldProperties, HarmSystemSettings set,
			PhysiologicalStateSpace space, IntentionalStateSpace ints, MyLogger log, 
			SharedData shared){
		
		this.log = log;
		this.physiological = space;
		this.settings = set;
		
		// collect information about all variables, constants, agents abilities etc
		// and store them in the ROOT DECISION SPACE
		this.root = new RootDecisionSpace(actuators, worldProperties, physiological);
		
		// get all variables (they are managed by the root d.s.) and all actions (primitive so far)
		this.allVariables = worldProperties.getVariables();	
		this.allActions = this.root.cloneActionList();
		
		// even before logging into the simulation, the agent is able to process his physical state
		//this.root.processInnerPerception(physiological);
	
	//	log.pl(LEV,"HARMSystem: initing SRP with matrix");
	//	this.srp = new SRP_eligibility(log, this.root, physiological, set);
		//this.root.qmatrix = this.srp.getMatrix();
		
		this.actuator = new ActuatorHARM();
		
		q = new Queue();
		
		ASimpleIntentions its = (ASimpleIntentions)ints;
		
		// setup the hierarchy of abstract actions.. the core of HARM system 
		this.hierarchy = new Hierarchy(root, space, its, set, log, actuator, allActions, shared);
		this.root.qmatrix = this.hierarchy.getRootMatrix();	// ?
	}
	
	
	public HarmSystemSettings getSettings(){ return this.settings; }
	
	public Actuator getActuator(){ return this.actuator; }
	/*
	public Action getActionGenerated(){ return this.srp.getActionGenerated(); }
	*/
	public void work(){
		
		// mark what has been executed
		root.markExecution(actuator.getActionExecuted());
		this.allActions.markExecution(actuator.getActionExecuted());
		/*
		this.root.processOuterPerception(perception);
		this.root.processInnerPerception(physiological);
		*/
		// add the previously executed action
		//q.step(this.root.primitiveActions.lastExecuted());
		
		this.hierarchy.processSimulationStep();
		
		//this.srp.infer(this.actuator);
	}
	
	
	public void detachDecisionSpace(String name){
		
		PhysilogicalAction a = new PhysilogicalAction("_"+name+"_", this.settings, this.log, 
				this.physiological, actuator);
		// for all primitive actions used: 
		for(int i=0; i<q.size(); i++){
			if(q.get(i) != -1){
				log.pl(LEV, "detaching with this index this: "+i + " "+q.get(i));
				a.addChild(root.primitiveActions.getActions().get(q.get(i)));
				
				// add random action (hierarchy test
				Random r1 = new Random();
				a.addChild(allActions.get(r1.nextInt(allActions.size()-1)));
			}
		}
		a.updateComplexity();
		log.pl(LEV,"the complexity of action is this!! "+a.getComplexity()+" and ID: "+a.getID());
		this.allActions.add(a);
	}
	
	
	public void stats(){
		log.pl(LEV," ------------------------------------------");
		log.pl(LEV, this.allActions.toString());
		log.pl(LEV,this.allVariables.toString());
	}
}
