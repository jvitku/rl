package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.observer.Observer;
import org.hanns.rl.discrete.visualizaiton.Visualizer;
import org.hanns.rl.discrete.visualizaiton.qMatrix.FinalStateSpaceVisDouble;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import ctu.nengoros.util.SL;

/**
 * The same as {@link org.hanns.rl.discrete.ros.sarsa.HannsQLambda}, but with visualization 
 * available. Also, this node publishes its prosperity, which is composed of coverage and reward/step.
 * 
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambdaVis extends HannsQLambda{

	protected FinalStateSpaceVisDouble visualization;
	protected Publisher<std_msgs.Float32MultiArray> prospPublisher;
	public static final String topicProsperity = ns+"prosperity";
	
	SL sl;

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);
		
		this.registerProsperityPublisher(connectedNode);
		
		sl = new SL("filex");
		sl.printToFile(true);

		// initialize the visualizer
		this.visualization = new FinalStateSpaceVisDouble(
				states.getDimensionsSizes(), actions.getNumOfActions(), q);

		visualization.setVisPeriod(this.logPeriod);
		visualization.setTypeVisualization(2);
		visualization.setActionRemapping(new String[]{"<",">","^","v"});
	}

	@Override
	protected void performSARSAstep(float reward, float[] state){
		// store the data into the int[]states
		super.decodeState(state);	
		// choose action and learn about it
		int action = super.learn(reward); 
		// use observer to log info
		o.observe(super.prevAction, reward, states.getValues(), action);
		
		// execute action
		super.executeAction(action);
		this.publishProsperity();

		if(this.visualization!=null)
			this.visualization.performStep(prevAction, reward, states.getValues(), action);

		this.log();
	}

	public Visualizer getVisualizer(){
		return this.visualization;
	}

	private void log(){
		Observer[] childs = o.getChilds(); 
		sl.pl(step+" "+o.getProsperity()+" "+childs[0].getProsperity()
				+" "+childs[1].getProsperity()); // log data to file each step
		
		if(step%logPeriod ==0)
			SL.sinfol("step: "+step+" "+o.getProsperity()+"\tcoverage="+childs[0].getProsperity()
				+" \treward/step="+childs[1].getProsperity()); // log data
	}
	
	protected void registerProsperityPublisher(ConnectedNode connectedNode){
		prospPublisher =connectedNode.newPublisher(topicProsperity, std_msgs.Float32MultiArray._TYPE);
	}
	
	protected void publishProsperity(){
		std_msgs.Float32MultiArray fl = prospPublisher.newMessage();	
		fl.setData(new float[]{o.getProsperity()});								
		prospPublisher.publish(fl);
	}

}

