package org.hanns.rl.discrete.ros.testnodes;

import java.util.LinkedList;

import org.hanns.rl.common.exceptions.MessageFormatException;
import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.AbstractFinalModelNStepLambda;
import org.hanns.rl.discrete.learningAlgorithm.lambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.qLearning.FinalModelQLambda;
import org.hanns.rl.discrete.observer.SarsaObserver;
import org.hanns.rl.discrete.observer.qMatrix.visualizaiton.FinalStateSpaceVisDouble;
import org.hanns.rl.discrete.observer.stats.combined.KnowledgeCoverageReward;
import org.hanns.rl.discrete.ros.common.ioHelper.MessageDerivator;
import org.hanns.rl.discrete.ros.learning.qLearning.AbstractQLambda;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

/**
 * Should test all the 3 ASM nodes. TODO implement this.
 * 
 * @author Jaroslav Vitku
 *
 */
public class AsmTestNode  extends AbstractConfigurableHannsNode{

	public static final String name = "AsmTestNode";

	protected ActionSelectionMethod<Double> asm;// action selection methods

	protected OneOfNEncoder actionEncoder;		// encode actions to ROS
	protected BasicFinalActionSet actions;		// set of agents actions

	protected BasicFinalStateSet states;		// state variables (each has encoder)

	protected int step = 0;

	protected ProsperityObserver o;						// observes the prosperity of node
	protected LinkedList<Observer> observers;	// logging, visualization & observing data

	protected final String randomizationConf = "Randomize";
	protected final boolean DEF_RANDOMIZATION = false;
	protected boolean rand = DEF_RANDOMIZATION;
	
	
	private int noCorrect, noIncorrect;
	private int bestPrevActionInd;
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();

		log.info(me+"started, parsing parameters");
		this.registerParameters();
		paramList.printParams();
		this.parseParameters(connectedNode);
		this.registerObservers();

		System.out.println(me+"initializing ROS Node IO");

		this.registerSimulatorCommunication(connectedNode);
		//this.buildProsperityPublisher(connectedNode);
		//this.buildConfigSubscribers(connectedNode);
		this.buildDataIO(connectedNode);

		super.fullName = super.getFullName(connectedNode);
		
		noCorrect=0;
		noIncorrect=0;
		this.bestPrevActionInd = 0;
		
		System.out.println(me+"Node configured and ready now!");
	}

	/**
	 * Adds arbitrary observers/visualizators to the node/algorithms
	 */
	protected void registerObservers(){
		observers = new LinkedList<Observer>();

		this.registerProsperityObserver();
		
		/*
		FinalStateSpaceVisDouble visualization = new FinalStateSpaceVisDouble(
				states.getDimensionsSizes(), actions.getNumOfActions(), q);

		visualization.setVisPeriod(this.logPeriod);
		visualization.setTypeVisualization(2);
		visualization.setActionRemapping(new String[]{"<",">","^","v"});

		observers.add(visualization);

		// configure observers to log/visualize in as selected in the node
		for(int i=0; i<observers.size(); i++){
			System.out.println("willLogToFile "+this.logToFile+ " period: "+this.logPeriod+
					" "+observers.get(i).getName());
			observers.get(i).setShouldVis(this.logPeriod>=0);
			observers.get(i).setVisPeriod(this.logPeriod);
		}*/
	}
	
	/**
	 * Instatntiate the Observer {@link #o} to the resider one. 
	 */
	protected void registerProsperityObserver(){}

	/**
	 * Execute action selected by the ASM and publish over the ROS network
	 * 
	 * @param action index of selected action, this action is encoded and sent
	 */
	protected void executeAction(int action){
		if((step++) % logPeriod==0) 
			log.info(me+"Step: "+step+"-> responding with the following action: "
					+SL.toStr(actionEncoder.encode(action)));

		// publish action selected by the ASM
		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();	
		fl.setData(actionEncoder.encode(action));								
		dataPublisher.publish(fl);
		this.publishProsperity();
	}

	@Override
	protected void registerParameters(){
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Number of state variables");
		paramList.addParam(randomizationConf, ""+DEF_RANDOMIZATION, "Does the tested node implement a randomization?");
		
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		System.out.println(me+"parsing parameters");

		// dimensionality of the RL task 
		int noActions = r.getMyInteger(noInputsConf, DEF_NOINPUTS);
		
		rand = r.getMyBoolean(randomizationConf, DEF_RANDOMIZATION);

		/**
		 * Build the action set & action encoder
		 */
		String[] names = new String[noActions];
		for(int i=0; i<noActions; i++)
			names[i] = actionPrefix+i;
		actions = new BasicFinalActionSet(names);
		actionEncoder = new OneOfNEncoder(actions);

	}

	/**
	 * Check the performance of the node
	 */
	protected void onNewDataReceived(float[] data){
	
		int selected = this.getSelectedActionInd(data);
		if(selected != bestPrevActionInd){
			noIncorrect++;
		}else{
			noCorrect++;
		}
		
	}

	private void evaluateCorrectness(){
		if(this.rand){
			if(this.step>100){
				
			}
		}else{
			if(noIncorrect>0){
				//TODO call fail here
			}
		}
	}
	
	private int getSelectedActionInd(float[] data){
		int selected = -1;
		boolean found = false;
		
		for(int i=0; i<data.length; i++){
			if(data[i]==1){
				if(found){
					// TODO call fail here
					return 0;
				}else{
					found = true;
					selected = i;
				}
			}else if(data[i]!=0){
				// TODO call fail here
				return 0;
			}
		}
		if(!found){
			// TODO call fail here
			return -0;
		}
		return selected;
	}
	
	private void generateUtilsAndSend(){
		
	}
	
	/**
	 * Register the {@link #actionPublisher} for publishing actions 
	 * and the state subscriber for receiving state+reward data.
	 * 
	 * @param connectedNode
	 */
	protected void buildDataIO(ConnectedNode connectedNode){
		// publish vector of action utilities 
		dataPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		// check the 1ofN response, either randomized or returning the index of max action
		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != actions.getNumOfActions())
					log.error(me+":"+topicDataIn+": Received vector of actions of " +
							"unexpected length of"+data.length+"! Expected: "+
							(actions.getNumOfActions()));
				else{
					// implement this
					onNewDataReceived(data);
				}
			}
		});
	}

	/**
	 * Register subscribers for the RL configuration (alpha & gamma)
	 * @param connectedNode
	 */
	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode){}



	/**
	 * If the prosperity observer has no childs, publish its value. 
	 * If the prosperity observer has childs, publish its value on the first
	 * position and values of its childs in the vector.
	 */
	@Override
	public void publishProsperity(){
	}

	@Override
	public ProsperityObserver getProsperityObserver() { return o; }

	@Override
	public boolean isStarted() {
		if(log==null)
			return false;
		if(dataPublisher==null)
			return false;
		if(asm==null)
			return false;
		if(observers==null)
			return false;
		return true;
	}
	
	@Override
	public String getFullName() { return this.fullName; }

	@Override
	public LinkedList<Observer> getObservers() { return observers; }
	
	
	private boolean lg = false;
	public void logg(String what) {
		if(lg)
			System.out.println(" ------- "+what);		
	}

	@Override
	public String listParams() {
		return null;
	}

	@Override
	public void hardReset(boolean arg0) {}

	@Override
	public void softReset(boolean arg0) {}

	@Override
	public float getProsperity() { return 0; }
}


