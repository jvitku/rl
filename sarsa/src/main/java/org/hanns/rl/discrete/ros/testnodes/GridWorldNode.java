package org.hanns.rl.discrete.ros.testnodes;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.BasicConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.impl.EpsilonGreedyDouble;
import org.hanns.rl.discrete.actions.impl.BasicFinalActionSet;
import org.hanns.rl.discrete.actions.impl.OneOfNEncoder;
import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.FinalModelNStepQLambda;
import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl.NStepQLambdaConfImpl;
import org.hanns.rl.discrete.ros.sarsa.QLambda;
import org.hanns.rl.discrete.states.impl.BasicFinalStateSet;
import org.hanns.rl.discrete.states.impl.BasicStateVariable;
import org.hanns.rl.discrete.states.impl.BasicVariableEncoder;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.rosparam.impl.PrivateRosparam;
import ctu.nengoros.util.SL;

/**
 * Provides very similar map to the one from src/test/java (used for testing) in for of a ROS node
 * compatible with RL ROS nodes. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class GridWorldNode extends AbstractNodeMain{

	public static final String name = "GridWorldNode";
	public final String me = "["+name+"]";

	private PrivateRosparam r;


	public static final String shouldLog = "shouldLog";
	public static final boolean DEF_LOG = true;

	private Log log;
	private Publisher<std_msgs.Float32MultiArray> statePublisher;
	private Publisher<std_msgs.Float32MultiArray> actionSubscriber;

	private float[][] map;		// map of rewards
	private int sizex, sizey;	// default dimensions of the map
	private int logPeriod;		// how often to log

	private final int noActions = 4;	// 4 actions -> {<,>,^,v}
	private final int stateLen = 2;		// 2 state variables -> x,y (published as raw floats from [0,1])

	public static final int DEF_SIZEX =10, DEF_SIZEY=10;
	public static final String sizexConf = "sizex";
	public static final String sizeyConf = "sizey";

	public static final int DEF_LOGPERIOD =10;			// how often to log, each 10 sim steps? 
	public static final String logPeriodConf = "logPeriod";


	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters. \n\nInfo: \n-This is simple 2D grid world."
				+ "\n-An agent has four actions: {<,>,^,v}."
				+ "\n-Each tale defines vlaue of reinforcement (mostly zeros)"
				+ "\n-By stepping on a tale, the reinforcement (value of a tale) is received."
				+ "\n-This node is subscribed to agents actions, it responds with "
				+ "a reinforcement and a new state immediatelly after receiving the action."
				+ "\n-Response is composed as follows: [float reward, float varX, float varY]\n\n");

		this.parseParameters(connectedNode);
		log.info(me+"Parameters parsed, creating the map of size: "+sizex+"x"+sizey+"\n\n");

		this.registerROSCommunication(connectedNode);

		map = GridWorld.simpleRewardMap(sizex, sizey, null, 15);


		// publish action selected by the ASM
		std_msgs.Float32MultiArray fl = statePublisher.newMessage();
		fl.setData(new float[]{0,0,1});								
		statePublisher.publish(fl);
		statePublisher.publish(fl);
		statePublisher.publish(fl);
		
		log.info(me+"Node configured and ready to provide simulator services!");
	}


	private void registerROSCommunication(ConnectedNode connectedNode){

		/**
		 * State publisher - connect to the input-data topic of (.e.g.) QLambda
		 */
		statePublisher =connectedNode.newPublisher(QLambda.topicDataIn, std_msgs.Float32MultiArray._TYPE);

		/**
		 * Action subscriber = subscribe to agents actions, process agents requests for simulating one step
		 */
		Subscriber<std_msgs.Float32MultiArray> epsilonSub = 
				connectedNode.newSubscriber(QLambda.topicDataOut, std_msgs.Float32MultiArray._TYPE);

		epsilonSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != noActions)
					log.error(me+"Received action description has" +
							"unexpected length of"+data.length+"! Expected number "
							+ "of actions (coding 1ofN) is "+noActions);
				else{

					log.info(me+"Received gents action, this one: "+SL.toStr(data));

					// TODO, process action and response
					

					// publish action selected by the ASM
					std_msgs.Float32MultiArray fl = statePublisher.newMessage();
					fl.setData(new float[]{0,0,1});								
					statePublisher.publish(fl);
					log.info(me+"Node configured and ready to provide simulator services!");
					
				}
			}
		});

	}

	private void parseParameters(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);

		// parse size of the map 
		sizex = r.getMyInteger(sizexConf, DEF_SIZEX);
		sizex = r.getMyInteger(sizeyConf, DEF_SIZEY);

		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

	}

}
