package massim.shared;

import massim.agent.mind.AgentsMap;
import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.SomeSpaceWithVariables;
import massim.agent.mind.harm.variables.Variable;
import massim.framework.util.ReactTimeMeasurer;


public class SharedData {
	
	
	public ReactTimeMeasurer timer;
	
	// waiting for the GUI
	private boolean waitForGUI = false;
	private boolean waiting = false;
	private boolean actionValid = false; 	// in the manual mode, whether the execute action string
	private boolean manualMode = false;
	private String action;
	
	private boolean planningMode;	// in the planning mode, all intentions ARE 0 !!!!!
	private boolean executionMode;	// one can be set to max
	
	private boolean complexityChanged;	// some complexity changed, reload win and refresh other gui
	
	// for displaying the information about space and Q(s,a) matrix
	private SomeSpaceWithVariables actualSpaceSelected;
	
	public int agentsSteps=0; 
	
	private boolean stopAndSave = false;
	private boolean shouldReload = true;
	
	private Variable[] planningVariables;	// list of variables in the window (actual and desired state)
	private Action[] planningActions;		// list of actions in the window
	
	public SharedData(){
		this.timer = new ReactTimeMeasurer();
	}
	
	public synchronized String getAction(){ return this.action; }
	
	public synchronized void setAction(String action){ 
		this.action = action;
		this.actionValid = true;
	}
	
	/**
	 * stop agent thread and save it s data?
	 */
	public synchronized void stopAndSave(){ this.stopAndSave = true; }
	
	public synchronized boolean shouldStop(){ return this.stopAndSave; }
	
	/**
	 * this is for sharing the information what decision space is selected now
	 * @param space - pointer to decision space that is selected now (or planning window)
	 */
	public synchronized void setActualDecisionSpace(SomeSpaceWithVariables space){
		this.actualSpaceSelected = space; }
	public synchronized SomeSpaceWithVariables getActualSpaceSelected(){ return this.actualSpaceSelected; }
	
	/**
	 * whether the agent should wait for GUI operations between each simulation step
	 * or between defined steps (for instance planning window movement etc..)
	 * @param wait
	 */
	public synchronized void setWaitingConditionForGUI(boolean wait){ this.waitForGUI = wait; }
	public synchronized boolean shouldWaitForGUI(){ return this.waitForGUI; }
	public synchronized boolean waiting(){ return this.waiting; }
	public synchronized void stepMade(){ this.waiting = true; }
	public synchronized void step(){ this.waiting = false; }
	
	public synchronized boolean actionReady(){ return this.actionValid; }
	public synchronized void setActionReady(boolean ready){ this.actionValid = false; }
	public synchronized void setManualMode(boolean on){
		this.waitForGUI = on;
		this.manualMode = on;
		this.waiting = true;
	}
	public synchronized void gettingAction(){ this.actionValid = false; }
	public synchronized boolean inManualMode(){ return this.manualMode; }
	
	public synchronized boolean shouldReloadMatrix(){ return this.shouldReload; }
	public synchronized void requestMatrixReload(){ this.shouldReload = true; }
	public synchronized void discardReloadRequest(){ this.shouldReload = false; }
	
	public synchronized void setCPXChanged(){ this.complexityChanged = true; }		// call from HARM
	public synchronized void discardCPXChange(){ this.complexityChanged = false; } // call from GUI
	public synchronized boolean cPXChanged(){ return this.complexityChanged; }		// update GUI?
	
	
	public synchronized boolean inPlanningMode(){ return this.planningMode; }
	public synchronized void enablePlanning(){ this.planningMode = true; }
	public synchronized void disablePlanning(){ this.planningMode = false; }
	
	public synchronized boolean inPlanExecutionMode(){ return this.executionMode; }
	public synchronized void enablePlanExecutionMode(){ this.executionMode = true; }
	public synchronized void disablePlanExecutionMode(){ this.executionMode = false; }
	
	// variables to be passed to the planning engine
	public synchronized Variable[] getPlanningVariables(){ return this.planningVariables; }
	public synchronized Action[] getPlanningActions(){ return this.planningActions; }
	public synchronized void setPlanningVariablesAndActions(Variable[] variables, Action[] actions){
		this.planningVariables = variables;
		this.planningActions = actions;
	}

	private boolean planningRequest;
	public synchronized void setPlanningReq(){ this.planningRequest = true; }
	public synchronized void discardPlanningReq(){ this.planningRequest = false; }
	public synchronized boolean planningRequested(){ return this.planningRequest; }
	
}
