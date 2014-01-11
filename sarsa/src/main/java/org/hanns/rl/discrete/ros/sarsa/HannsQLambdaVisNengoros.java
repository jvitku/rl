package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.observer.Observer;
import org.hanns.rl.discrete.visualizaiton.Visualizer;
import org.hanns.rl.discrete.visualizaiton.qMatrix.FinalStateSpaceVisDouble;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

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
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambdaVisNengoros extends HannsQLambda{

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

		//System.out.println("fuuuuu \t\t\talpha"+rl.getConfig().getAlpha()+" "+rl.getConfig().getGamma()+" "+rl.getConfig().getLambda());

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
		Observer[] childs = o.getChilds(); 
		std_msgs.Float32MultiArray fl = prospPublisher.newMessage();	
		fl.setData(new float[]{o.getProsperity(),childs[0].getProsperity()
				,childs[1].getProsperity()});								
		prospPublisher.publish(fl);
	}

}

