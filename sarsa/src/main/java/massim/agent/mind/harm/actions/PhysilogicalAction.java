package massim.agent.mind.harm.actions;

import java.util.ArrayList;
import java.util.HashMap;

import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.Motivation;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.actions.rl.Memory;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.harm.components.predictors.SRP_eligibility_Hierarchy;
import massim.agent.mind.harm.components.predictors.SRP_eligibility_HierarchyII;
import massim.agent.mind.harm.components.predictors.SRP_eligibility_HierarchyIII;
import massim.agent.mind.harm.components.predictors.SRP_eligibility_flat;
import massim.agent.mind.harm.components.predictors.StochasticReturnPredictor;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.Value;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;
import massim.agent.motivationActionMapping.Connection;
import massim.framework.util.MyLogger;

/**
 * this represents action
 * action can be complex or primitive
 * 
 * @author jardavitku
 *
 */
public class PhysilogicalAction extends DecisionSpace implements ComplexAction{
	
	// ID of action (instead of the name, used in the GUI button listener for example) 
	protected String ID;	
	
	// index of variable which is reinforced in the state space (phys. or intentional) 
	protected int reinforcedVariable;
	// variable that has changed during reinforcing (variable from the environment)
	protected Variable reinforcedEnvVariable;	
	private boolean reinfFound; 	// whether the val has been found already
	
	// each decision space contains references to its child decision spaces (or primitive actions)
	protected ArrayList<Action> childActions;	// my child actions
	protected ArrayList<Variable> childVars;	// my variables
	
	public Connection myConnection;
	
	private boolean justExecuted;
	
	private int lastExecuted;	

	// for each action, count how many times it has been executed in our learning window
	private final HashMap<String, Integer> Acounts;	// action counts
	private final HashMap<String, Integer> Vcounts;	// variable vals counts
	
	private int numActions;
	private int numVars;
	
	// then use the trigger to ignore unwanted actions (in this DS will be only the best-needed)
	private final double trigger;
	
	private final HarmSystemSettings settings;
	
	private int prevComplexity;
	
	private int priority;
	
	// dim of Q
	
	private final MyLogger log;
	private final int LEV_DEBUG = 115;
	private final int LEVCOUNT = 115;
	private final int LEVVAR = 115;
	private final int LVCAUSE = 115;
	
	public SRP_eligibility_HierarchyII srp;
	
	
	private boolean initedSRP;
	
	private final ActuatorHARM actuator;
	
	private PhysiologicalStateSpace s;

	public PhysilogicalAction(String name, HarmSystemSettings settings, MyLogger log, 
			PhysiologicalStateSpace s,
			ActuatorHARM ac){
		super(false,name);
		this.settings = settings;
		this.s = s;
		
		this.ID = IDGenerator.generate(name);
		this.childActions = new ArrayList<Action>();
		this.justExecuted = false;
		
		this.lastExecuted = 0;
		this.trigger = this.settings.actionTrigger;
		
		this.actuator = ac;
		
		this.Acounts = new HashMap<String, Integer>();					// counting of actions
		this.Vcounts = new HashMap<String, Integer>(); // counting of variables
		this.numActions = 0;
		this.numVars = 0;
		this.prevComplexity = 0;
		this.log= log; 
		this.childVars = new ArrayList<Variable>();
		this.reinfFound = false;
		this.initedSRP = false;
		
		this.discardPriority();
	}

	
	private void initSRP(ActuatorHARM ac){
		this.srp = new SRP_eligibility_HierarchyII(log, childActions, childVars,s , settings, this, ac);
		this.initedSRP = true;
	}
	
	/**
	 * update list of actions and variables that this action owns
	 * @param acts 
	 * @param vars
	 * @param actualStep
	 */
	public void updateActionsAndVars(ActionList acts, VariableList vars, int actualStep){
		
		if(this.initedSRP){
			/*
			log.pl(LEV_DEBUG, "...................................................................  ");
			this.srp.stats(this.childActions);
			log.pl(LEV_DEBUG, ".................................................................... "+
					this.getName());
					*/
			this.srp.getMatrix().discardMatrixChange();
		}
		log.pl(LEV_DEBUG,"--------Updating my list of actions ad variables, my name is:"+this.getName());
		
		
		this.countAcitonsInWindow(acts, actualStep);
		this.updateMyActionsII(acts,actualStep);
		//this.updateComplexity();
		
		
		this.determineReinforcedCause(vars, actualStep);
		this.countVariablesInWindow(vars, actualStep);
		this.updateMyVariables(vars, actualStep);
		
		if(this.initedSRP){
			/*
			log.pl(LEV_DEBUG, ".................................................................. 2 ");
			this.srp.stats(this.childActions);
			log.pl(LEV_DEBUG, "................................................................. 2 "+
					this.getName());
					*/
		}
		
		log.pl(LEV_DEBUG,"--------Done");
	}
	
	public int getMyReinforcementVal(){
		if(this.myConnection == null){
			log.pl(0,"My connection is not set! ");
			return -1;
		}
		Motivation m = this.myConnection.source();
		if(!m.wasReinforced())
			return 0;
		return m.getReinforcementVal();
	}
	
	/**
	 * get all actions, count them and apply rules if they should be my childs
	 */
	private void updateMyVariables(VariableList vars, int actualStep){
		
		
		// this.deleteVars(); 
		Variable v;
		double n;
		double min = this.settings.variableTrigger;
				
		for(int i=0; i<vars.size(); i++){
			v = vars.get(i);
			
			// ignore the re variable
			if(this.reinforcedEnvVariable.getName().equalsIgnoreCase(v.getName()))
				continue;
			
			if(!this.Vcounts.containsKey(v.getName())){
				this.Vcounts.put(v.getName(), new Integer(1));
			}
			
			double nn = this.Vcounts.get(v.getName());
			n = this.Vcounts.get(v.getName()) / (double)this.numVars;
			
			log.pl(LEVVAR,"VARIABLE: "+v.getName()+" its NUM: "+nn+" "+n+" numvars: "+this.numVars);
			//log.pl(LEV,"A: "+this.getName()+" numA: "+this.numActions+" n: "+n+" min: "+min);
			// ame not found in the li
			if(n >= min){
				
				log.p(LEVVAR,"Variable "+v.getName()+" SHOULD be here, ");
				log.pl(LEVVAR, "Vars len is "+this.childVars.size());
				
				if(this.childVars.contains(v)){
					log.pl(LEVVAR, " and is here, so OK ");
				}else{
					log.pl(LEVVAR, " and is NOT here, so ADDING "+v.getName());
					this.childVars.add(v);
					this.srp.getMatrix().indicateMatrixChange();
					log.pl(LEV_DEBUG, "");
				}
			}else{
				log.p(LEVVAR,"Variable "+v.getName()+" should NOT be here, ");
				if(this.childVars.contains(v)){
					log.pl(LEVVAR, " and is here, so REMOVING "+v.getName());
					this.childVars.remove(v);
					this.srp.removeVariable(v);
					this.srp.getMatrix().indicateMatrixChange();
					log.pl(LEV_DEBUG, "");
				}else{
					log.pl(LEVVAR, " and is NOT here, so OK. ("+v.getName()+")");
				}
			}
		}
		this.srp.updateNewVariables();
	}
	

	/**
	 * basically: it counts variables in window 
	 * variable is defined as variable (globally) which had at least two different vals. in the window 
	 * @param vars
	 * @param actualStep
	 */
	private void countVariablesInWindow(VariableList vars, int actualStep){
		Variable v;
		
		log.pl(LEVVAR,"ACTION: "+this.getName()+" " +
				"getting epoch variables, all vars num is "+vars.size()+" winLen is: "+
				this.settings.actionWinInitLength);
		
		
		// for all vars, if is OK, add to list
		for(int i=0; i<vars.size(); i++){
			v = vars.get(i);
			
			// ignore the re variable
			if(this.reinforcedEnvVariable.getName().equalsIgnoreCase(v.getName()))
				continue;
			
			if(this.shouldTake(v, this.settings.actionWinInitLength, actualStep)){
				this.numVars++;
				// if not in the map, put there with number 1
				if(!this.Vcounts.containsKey(v.getName())){
					this.Vcounts.put(v.getName(), new Integer(1));
					
				// if found, get, add 1 and put back
				}else{
					
					Integer num = this.Vcounts.get(v.getName());
					log.pl(LEVVAR,"getting this variable with this num "+v.getName()+ " " +num);
					this.Vcounts.remove(v.getName());
					this.Vcounts.put(v.getName(),new Integer(num+1));
				}
			}
		}
	}
	
	/**
	 * add and remove separately
	 */
	private void updateMyActionsII(ActionList acts, int actualStep){
		
		
		if(this.initedSRP)
			log.pl(LEV_DEBUG, "xxxxxxx START "+this.srp.getMatrix().getNumActions()+" "+
					this.srp.getMatrix().printActions());
		else
			log.pl(LEV_DEBUG, "xxxxxxx START  srp not inited");
		
		//this.deleteActions(); 
		Action a;
		double n;
		double min = this.settings.actionTrigger;
		
		// removing part
		for(int i=0; i<acts.size(); i++){
			a = acts.get(i);
			// action is me
			if(a.getName().equalsIgnoreCase(this.getName()))
				continue;
			
			if(!this.Acounts.containsKey(a.getName())){
				this.Acounts.put(a.getName(), new Integer(1));
			}
			
			double nn = this.Acounts.get(a.getName());
			
			//log.pl(LEV+10, "countVarsInWIndow: actual step; "+actualStep+" A count: "+nn);
			
			n = this.Acounts.get(a.getName()) / (double)this.numActions;
			
			log.pl(LEVCOUNT,"ACTION: "+a.getName()+" its NUM: "+nn+" "+n+" numactions: "+this.numActions);
			//log.pl(LEV,"A: "+this.getName()+" numA: "+this.numActions+" n: "+n+" min: "+min);
			
			if(n < min){
				if(this.childActions.contains(a)){
					log.pl(LEV_DEBUG, "Action should NOT be here and IS, so REMOVING!!: ("+a.getName()+")");
					
					if(this.initedSRP){
						this.srp.removeAction(a);
						this.childActions.remove(a);
						this.srp.getMatrix().indicateMatrixChange();
					}
				}else{
					log.pl(200, "and is NOT here, OK ("+a.getName()+")");
				}
			}
		}
		// adding part
		for(int i=0; i<acts.size(); i++){
			a = acts.get(i);
			// action is me
			if(a.getName().equalsIgnoreCase(this.getName()))
				continue;
			
			if(!this.Acounts.containsKey(a.getName())){
				this.Acounts.put(a.getName(), new Integer(1));
			}
			
			double nn = this.Acounts.get(a.getName());
			
			//log.pl(LEV+10, "countVarsInWIndow: actual step; "+actualStep+" A count: "+nn);
			
			n = this.Acounts.get(a.getName()) / (double)this.numActions;
			
			log.pl(LEVCOUNT,"ACTION: "+a.getName()+" its NUM: "+nn+" "+n+" numactions: "+this.numActions);
			//log.pl(LEV,"A: "+this.getName()+" numA: "+this.numActions+" n: "+n+" min: "+min);
			
			if(n >= min){
				// if there is, OK, if not, add
				if(this.childActions.contains(a)){
					log.pl(200, "Action SHOULD be here, and IS here, OK ("+a.getName()+")");
				}else{
					log.pl(LEV_DEBUG, "Action SHOULD be here, and is NOT here, ADDING! ("+a.getName()+")");
					this.childActions.add(a);
					if(this.initedSRP)
						this.srp.getMatrix().indicateMatrixChange();
				}
			}
		}
		
		
		if(!this.initedSRP)
			this.initSRP(actuator);
		
		log.pl(LEV_DEBUG, "xxxxxxx ADDING all variables meant above! "+this.srp.getMatrix().getNumActions());
		this.srp.updateNewActions();
		log.pl(LEV_DEBUG, "xxxxxxx ADDING all variables meant above-end! "+this.srp.getMatrix().getNumActions()
				+" "+log.getIds(this.srp.getMatrix().getDimensionSizes())
				+" "+this.srp.getMatrix().printActions());
	}
	
	/**
	 * get all actions, count them and apply rules if they should be my childs
	 */
	private void updateMyActions(ActionList acts, int actualStep){
		
		if(this.initedSRP)
			log.pl(LEV_DEBUG, "xxxxxxx START "+this.srp.getMatrix().getNumActions());
		else
			log.pl(LEV_DEBUG, "xxxxxxx START  srp not inited");
		
		//this.deleteActions(); 
		Action a;
		double n;
		double min = this.settings.actionTrigger;
		
		for(int i=0; i<acts.size(); i++){
			a = acts.get(i);
			// action is me
			if(a.getName().equalsIgnoreCase(this.getName()))
				continue;
			
			if(!this.Acounts.containsKey(a.getName())){
				this.Acounts.put(a.getName(), new Integer(1));
			}
			
			double nn = this.Acounts.get(a.getName());
			
			//log.pl(LEV+10, "countVarsInWIndow: actual step; "+actualStep+" A count: "+nn);
			
			n = this.Acounts.get(a.getName()) / (double)this.numActions;
			
			log.pl(LEVCOUNT,"ACTION: "+a.getName()+" its NUM: "+nn+" "+n+" numactions: "+this.numActions);
			//log.pl(LEV,"A: "+this.getName()+" numA: "+this.numActions+" n: "+n+" min: "+min);
			
			if(n >= min){
				// if there is, OK, if not, add
				if(this.childActions.contains(a)){
					log.pl(200, "Action SHOULD be here, and IS here, OK ("+a.getName()+")");
				}else{
					log.pl(LEV_DEBUG, "Action SHOULD be here, and is NOT here, ADDING! ("+a.getName()+")");
					this.childActions.add(a);
				}
			}else{
				if(this.childActions.contains(a)){
					log.pl(LEV_DEBUG, "Action should NOT be here and IS, so REMOVING!!: ("+a.getName()+")");
					
					if(this.initedSRP)
						this.srp.removeAction(a);
					this.childActions.remove(a);
				}else{
					log.pl(200, "and is NOT here, OK ("+a.getName()+")");
				}
			}
		}
		if(!this.initedSRP)
			this.initSRP(actuator);
		
		log.pl(LEV_DEBUG, "xxxxxxx ADDING all variables meant above! "+this.srp.getMatrix().getNumActions());
		this.srp.updateNewActions();
		log.pl(LEV_DEBUG, "xxxxxxx ADDING all variables meant above-end! "+this.srp.getMatrix().getNumActions()
				+" "+log.getIds(this.srp.getMatrix().getDimensionSizes()));
	}
	
	
	// Some decision space selected
	
	/**
	 * basically: count all action in the window and store their counts 
	 * @param acts
	 * @param actualStep
	 * @return
	 */
	private void countAcitonsInWindow(ActionList acts, int actualStep){
		Action a;
		
		log.pl(LEVCOUNT,"ACTION: "+this.getName()+" " +
				"getting epoch actions, all action num is "+acts.size()+" winLen is: "+
				this.settings.actionWinInitLength);
		
		
		// for all actions, if is OK, add to list
		for(int i=0; i<acts.size(); i++){
			a = acts.get(i);
			
			if(this.getName().equalsIgnoreCase(a.getName()))
				continue;
			
			if(this.shouldTake(a, this.settings.actionWinInitLength, actualStep)){
				this.numActions++;
				// if not in the map, put there with number 1
				if(!this.Acounts.containsKey(a.getName())){
					this.Acounts.put(a.getName(), new Integer(1));
				// if found, get, add 1 and put back
				}else{
					
					Integer num = this.Acounts.get(a.getName());
					log.pl(LEVCOUNT,"getting this action with this num "+a.getName()+ " " +num);
					this.Acounts.remove(a.getName());
					this.Acounts.put(a.getName(),new Integer(num+1));
				}
			}
				
		}
	}
	
	/**
	 * get the environment variable which caused the reinforcement
	 * @param vars - all variables
	 * @return - the one
	 */
	private void determineReinforcedCause(VariableList vars, int step){
		if(this.reinfFound)
			return;
		
		Variable v;
		int ind;
		
		String prev="", actual="";	// prev value and the actual one
		boolean pFound, aFound;
		
		log.pl(LVCAUSE,"determining the reinforcement cause (searching for var) "
				+" my name is: "+this.getName()+" and actual step is: "+step);
		for(int i=0; i<vars.size(); i++){
			v = vars.get(i);
			aFound = false;
			pFound = false;
			log.pl(LVCAUSE, "--------------var: "+v.getName()+": ");
			
			for(int j=0; j<v.getNumValues(); j++){
				
				log.pl(LVCAUSE,"considering: "+v.vals.get(j).getStringVal()+" "+v.vals.get(j).lastSeen());
				
				// if this value last seen previous step 
				if((step-v.vals.get(j).lastSeen()) == 1){
					log.pl(LVCAUSE, "val: "+v.vals.get(j).getStringVal()+
							"  and last seen at: "+v.vals.get(j).lastSeen());
					prev = v.vals.get(j).getStringVal();
					pFound = true;
					if(aFound){
						if(!prev.equalsIgnoreCase(actual)){
							this.reinfFound(v);
						}
						break;
					}
				}
				// if this value is actual
				else if((step - v.vals.get(j).lastSeen()) == 0){
					log.pl(LVCAUSE, "val: "+v.vals.get(j).getStringVal()+
							"  and last seen at: "+v.vals.get(j).lastSeen());
					actual = v.vals.get(j).getStringVal();
					aFound = true;
					// if found previous also, we have found our variable
					if(pFound){
						if(!prev.equalsIgnoreCase(actual)){
							this.reinfFound(v);
						}
						break;
					}
						
				}
			}
		}
		if(!this.reinfFound)
			log.err("CompexAction:","determineReinforcedVariable: no variable changed this step!!");
	}
	
	private void reinfFound(Variable v){
		this.reinfFound = true;
		log.pl(LEV_DEBUG, "variable found! it is: "+v.getName());
		this.reinforcedEnvVariable = v;
	}
	
	/**
	 * @return - variable which causes the reinforcement
	 */
	public Variable getReinforcementCausingVar(){
		return this.reinforcedEnvVariable;
	}
	
	/**
	 * whether the variable is assumed as variable in our window, 
	 * if the variable had more than one value in the window, then it is variable
	 * 
	 * @param v
	 * @param numSteps
	 * @param step
	 * @return
	 */
	private boolean shouldTake(Variable v, int numSteps, int step){
		/*
		log.pl(LEV,"SHOULD take: numSteps: "+numSteps+" actual "+step+" last "+a.lastExecuted()
				+"      diff: "+((step-a.lastExecuted())));
				*/
		boolean found = false;
		
		// for all vals
		for(int i=0; i<v.getNumValues(); i++){
			Value val = v.vals.get(i);
			
			if((step-val.lastSeen()) < numSteps){
				if(found)
					return true;
				found = true;
			}
		}
		return false;
	}
	
	/**
	 * should take if in window
	 * @param a - action
	 * @param numSteps - win length
	 * @param step - actual step
	 * @return
	 */
	private boolean shouldTake(Action a, int numSteps, int step){
		/*
		log.pl(LEV,"SHOULD take: numSteps: "+numSteps+" actual "+step+" last "+a.lastExecuted()
				+"      diff: "+((step-a.lastExecuted())));
				*/
		if(((step-a.lastExecuted()) <= numSteps)){
			return true;
		}
		return false;
	}
	
	@Override
	public ArrayList<Action> getChilds() {
		return this.childActions;
	}
	
	@Override
	public void addChild(Action a){
		this.Acounts.put(a.getName(), new Integer(0));
		this.childActions.add(a);
	}
	
	public void updateProperties(VariableList vars, ActionList actions){
		
		this.updateChildActionSet(actions);
		this.updateVariableSet(vars);
		
	}
	
	private void updateChildActionSet(ActionList actoins){
		
		
	}
	
	private void updateVariableSet(VariableList vars){
		
	}
	/**
	 * complexity is bigger by one than the most complex child
	 */
	public void updateComplexity(){
		this.prevComplexity = this.getComplexity();
		
		if(this.childActions.isEmpty()){
			this.complexity = 0;
			return;
		}
		int max = 0;
		for(int i=0; i<this.childActions.size(); i++){
			if(max < this.childActions.get(i).getComplexity())
				max = this.childActions.get(i).getComplexity();
		}
		this.complexity = max + 1;
		return;
	}
	
	public boolean complexityChanged(){
		if(this.prevComplexity!=this.getComplexity()){
			this.prevComplexity = this.getComplexity();
			return true;
		}
		return false;
	}
	
	public int getReinforcerVar(){ return this.reinforcedVariable; }

	public void setReinforcedStateSpaceVar(int var){ this.reinforcedVariable = var; } 
	
	@Override
	public String getID() { return this.ID; }


	@Override
	public ArrayList<Action> getActions() {
		return this.childActions;
	}


	@Override
	public String[] getActionNames() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public VariableList getConstants() {
		return new VariableList();
	}


	@Override
	public VariableList getVariables() {
		return new VariableList(this.childVars);
	}


	@Override
	public long getSpaceSize() {
		if(this.variables == null)
			return 0;
		
		if(this.variables.size()==0)
			return 0;
		
		if(this.variables.size() == 1)
			return this.variables.get(0).getNumValues(); 
		
		long sum = this.variables.get(0).getNumValues();
		
		for(int i=1; i<this.variables.size(); i++)
			sum = sum * this.variables.get(i).getNumValues();
		return sum;
	}


	@Override
	public int getSpaceDimension() {
		if(this.variables == null)
			return 0;
		return this.variables.size();
	}


	@Override
	public int getNumActions() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getLastExecuted() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getNumVariables() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public QSAMatrix getQMatrix() { return this.srp.getMatrix(); }


	@Override
	public void setQMatrix(QSAMatrix m) { /*this.qmatrix = m;*/ }



	@Override
	public boolean justExecuted() { return this.justExecuted; }
	@Override
	public void discardExecution() { this.justExecuted= false; }
	@Override
	public void setJustExecuted() { this.justExecuted = true; }
	@Override
	public int lastExecuted() { return this.lastExecuted; }
	@Override
	public void setExecutedAt(int when) { this.lastExecuted = when;}

	@Override
	public ArrayList<Variable> getListOfVariables() {
		return this.childVars;
	}


	@Override
	public int getPriority() {
		return this.priority;
	}


	@Override
	public void addToPriority(int val) {
		this.priority += val;
	}


	@Override
	public void discardPriority() {
		this.priority = 0;
	}


	@Override
	public Connection getConnection() {
		return this.myConnection;
	}


	@Override
	public void setConnection(Connection c) {
		this.myConnection = c;
	}


	@Override
	public StochasticReturnPredictor getSRP() {
		return this.srp;
	}
	
	
}
