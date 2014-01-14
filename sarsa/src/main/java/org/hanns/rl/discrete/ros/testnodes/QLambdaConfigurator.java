package org.hanns.rl.discrete.ros.testnodes;

import org.apache.commons.logging.Log;
import org.hanns.rl.discrete.ros.sarsa.QLambda;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * ROS node which publishes configuration for the QLambda ROS node in an open-loop.
 * 
 * @author Jaroslav Vitku
 *
 */
public class QLambdaConfigurator extends AbstractNodeMain{

	private Publisher<std_msgs.Float32MultiArray> alphaPub, gammaPub, lambdaPub, 
	/*epsilonPub,*/ importancePub;

	public float alpha = 0.5f, gamma = 0.3f, lambda = 0.7f, epsilon = 0.6f, 
			importance = 0.5f;

	public int sleeptime = 1000;

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("QLambdaConfigurator"); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		System.out.println("Node started, initializing ROS publishers");

		final Log log = connectedNode.getLog();

		// define the publishers defining the nodes configuration
		alphaPub= connectedNode.newPublisher(QLambda.topicAlpha,std_msgs.Float32MultiArray._TYPE);
		gammaPub= connectedNode.newPublisher(QLambda.topicGamma, std_msgs.Float32MultiArray._TYPE);
		lambdaPub= connectedNode.newPublisher(QLambda.topicLambda, std_msgs.Float32MultiArray._TYPE);
		//epsilonPub= connectedNode.newPublisher(QLambda.topicEpsilon, std_msgs.Float32MultiArray._TYPE);
		importancePub = connectedNode.newPublisher(QLambda.topicImportance, std_msgs.Float32MultiArray._TYPE);
		
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			private int poc;

			@Override
			protected void setup() {
				poc = 0;
			}
			
			@Override
			protected void loop() throws InterruptedException {

				std_msgs.Float32MultiArray mess = alphaPub.newMessage();

				mess.setData(new float[]{alpha});								
				alphaPub.publish(mess);							

				mess.setData(new float[]{gamma});								
				gammaPub.publish(mess);

				mess.setData(new float[]{lambda});								
				lambdaPub.publish(mess);

				//mess.setData(new float[]{epsilon});
				//epsilonPub.publish(mess);

				mess.setData(new float[]{importance});
				importancePub.publish(mess);
				
				log.info("Step no. "+(poc++)+" Setting these vlaues:"
						+"\nalpha="+alpha
						+"\ngamma="+gamma
						+"\nlambda="+lambda
						//+"\nepsilon="+epsilon
						+"\nimportance="+importance);
						

				Thread.sleep(sleeptime);
			}
		});
		log.info("Node ready, starting to publish the configuration each "+sleeptime+"ms");
	}
	
	public void setAlpha(float val){ this.alpha = val; }
	public float getAlpha(){ return this.alpha; }
	
	public void setGamma(float val){ this.gamma= val; }
	public float getGamma(){ return this.gamma; }
	
	public void setLambda(float val){ this.lambda= val; }
	public float getLambda(){ return this.lambda; }
	/*
	public void setEpsilon(float val){ this.epsilon= val; }
	public float getEpsilon(){ return this.epsilon; }
	*/
}
