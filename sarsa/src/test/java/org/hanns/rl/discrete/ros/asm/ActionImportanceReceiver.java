package org.hanns.rl.discrete.ros.asm;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * The same as ActionReceiver, but this is able to modify the importance online (as the NengoROS can).
 *  
 * @author Jaroslav Vitku
 */
public class ActionImportanceReceiver extends ActionReceiver{

	protected Publisher<std_msgs.Float32MultiArray> importancePublisher;

	protected void buildConfigIO(ConnectedNode connectedNode){
		importancePublisher = connectedNode.newPublisher(
				ImportanceEpsilonGreedy.topicImportance, std_msgs.Float32MultiArray._TYPE);
	}

	/**
	 * Sets the importance value to the remote node.
	 * @param imporatnce value of the importance parameter (0,1)
	 */
	public void setImportanceToNode(float importance){
		if(importance>1 || importance<0)
			System.out.println("Warning: importance should be between 0,1");
		
		std_msgs.Float32MultiArray fl = importancePublisher.newMessage();	
		fl.setData(new float[]{importance});								
		importancePublisher.publish(fl);
	}
	
	@Override
	public boolean isStarted() { return this.importancePublisher !=null; }

	@Override
	public void onStart(ConnectedNode connectedNode) {
		super.onStart(connectedNode);
		this.buildConfigIO(connectedNode);
	}

}
