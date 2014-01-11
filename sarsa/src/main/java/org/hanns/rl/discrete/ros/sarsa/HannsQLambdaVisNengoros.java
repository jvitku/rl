package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.ros.sarsa.ioHelper.MessageDerivator;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.util.SL;

/**
 * The same as {@link org.hanns.rl.discrete.ros.sarsa.HannsQLambdaVis}, but here the 
 * filtering of input data is made in the following way:
 * <ul>
 * <li>Only state changes are registered as new state (that is e.g. response from the GridWorld)</li>
 * <li>There is specified maximum numbed of steps without response (to executed action). 
 * This defines the maximum length of closed loop where the RL is (max. delay between 
 * action->new state)</li>
 * <li>If the response is not received in the predefined number of steps, the situation 
 * is evaluated as the following case: the action executed did not have effect, RL&ASM: continue.</li>
 * </ul>
 * 
 * @see {@link org.hanns.rl.discrete.ros.sarsa.ioHelper.MessageDerivationFilter}
 * 
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambdaVisNengoros extends HannsQLambdaVis{
	
	protected MessageDerivator filter;
	
	public static final String filterConf = "filterLength";

	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		super.parseParameters(connectedNode);
		
		int len = r.getMyInteger(filterConf, MessageDerivator.DEF_MAXLOOP);
		filter = new MessageDerivator(len);
	}

	/**
	 * Well, filter messages directly after receiving them
	 */
	@Override
	protected void buildDataIO(ConnectedNode connectedNode){
		/**
		 * Action publisher
		 */
		actionPublisher =connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		/**
		 * State receiver
		 */
		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != states.getNumVariables()+1)
					log.error(me+":"+topicDataIn+": Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+
							(states.getNumVariables()+1));
				else{
					
					// check whether the message should be passed through
					boolean shouldPass = filter.newMessageShouldBePassed(data);
					
					// here, the state description is decoded and one SARSA step executed
					if(step % logPeriod==0)
						myLog(me+"<-"+topicDataIn+" Received new reinforcement &" +
								" state description "+SL.toStr(data)+" Message should be "
										+ "processed?: "+shouldPass);
					
					if(!shouldPass)
						return;

					// decode data (first value is reinforcement..
					// ..the rest are values of state variables
					float reward = data[0];
					float[] state = new float[data.length-1];
					for(int i=0; i<state.length; i++){
						state[i] = data[i+1];
					}
					// perform the SARSA step
					performSARSAstep(reward, state);
				}
			}
		});
	}

}

