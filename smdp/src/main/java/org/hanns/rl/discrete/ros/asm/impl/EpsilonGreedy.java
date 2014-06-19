package org.hanns.rl.discrete.ros.asm.impl;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.ImportanceEpsGreedyDouble;
import org.hanns.rl.discrete.actionSelectionMethod.greedy.GreedyDouble;
import org.hanns.rl.discrete.actions.ActionSetInt;
import org.hanns.rl.discrete.ros.asm.AbstractASMDouble;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.util.SL;

/**
 * Implementation of the Epsilon-Greedy ASM as a ROS node.
 *  
 * @author Jaroslav Vitku
 *
 */
public class EpsilonGreedy extends AbstractASMDouble{

	/**
	 * Importance based Epsilon-greedy ASM configuration
	 */
	public static final String epsilonConf="epsilon"; // TOOD change minEpsilon
	public static final String topicEpsilon = conf+epsilonConf;
	public static final double DEF_EPSILON=0.6;

	public static final String name = "EpsilonGreedyASM";

	private ImportanceBasedConfig config;
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);

	}

	protected void initializeASM(/*double epsilon*/){
		config = new ImportanceBasedConfig();
		asm = new ImportanceEpsGreedyDouble(actions, config);
		asm.getConfig().setExplorationEnabled(true);
		// this forces the agent to use only greedy ASM when importance is 1 
		//((ImportanceEpsGreedyDouble)asm).getConfig().setMinEpsilon(0);
	}

	
	@Override
	protected void buildASMSumbscribers(ConnectedNode connectedNode) {

		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(topicEpsilon, std_msgs.Float32MultiArray._TYPE);

		epsilonSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Epsilon config: Received message has " +
							"unexpected length of"+data.length+"!");
				else{

					logParamChange("RECEIVED chage of value EPSILON",
							((EpsilonGreedyDouble)asm).getConfig().getEpsilon(),data[0]);
					((EpsilonGreedyDouble)asm).getConfig().setEpsilon(data[0]);
				}
			}
		});
	}

	/**
	 * Instantiate the ProsperityObserver
	 * //TODO the prosperity
	@Override
	protected void registerProsperityObserver(){
		//o = new BinaryCoverageForgettingReward(this.states.getDimensionsSizes());
		//o = new KnowledgeChange(this.states.getDimensionsSizes(), q);
		//o = new ForgettingCoverageChangeReward(this.states.getDimensionsSizes(),q);
		o = new MCR();

		observers.add(o);
	}*/


	
	@Override
	public boolean isStarted(){
		if(!super.isStarted())
			return false;
		if(config==null)
			return false;
		return true;
	}


	@Override
	protected void registerParameters(){
		super.registerParameters();
		paramList.addParam(filterConf, ""+MessageDerivator.DEF_MAXLOOP, "The maximum" +
				" length (in sim. steps) of the closed loop: action->newState");
	}


	@Override
	protected void registerProsperityObserver() {
		// TODO prosperity of the ASM?
	}

}



