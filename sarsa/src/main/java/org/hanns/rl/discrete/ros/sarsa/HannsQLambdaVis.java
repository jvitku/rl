package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.visualizaiton.Visualizer;
import org.hanns.rl.discrete.visualizaiton.qMatrix.FinalStateSpaceVisDouble;
import org.ros.node.ConnectedNode;

/**
 * The same as {@link org.hanns.rl.discrete.ros.sarsa.HannsQLambda}, but with visualization 
 * available.
 * 
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambdaVis extends HannsQLambda{

	private FinalStateSpaceVisDouble visualization;

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);

		// initialize the visualizer
		this.visualization = new FinalStateSpaceVisDouble(
				states.getDimensionsSizes(), actions.getNumOfActions(), q);
		
		visualization.setVisPeriod(10);
		visualization.setTypeVisualization(0);
	}

	@Override
	protected void performSARSAstep(float reward, float[] state){
		// store the data into the int[]states
		super.decodeState(state);	
		// choose action and learn about it
		int action = super.learn(reward); 
		// use observer to log info
		o.observe(super.prevAction, reward, states.getValues(), action);
		
		o.getProsperity();
		
		// execute action
		super.executeAction(action);
		
		this.visualization.performStep(prevAction, reward, states.getValues(), action);
	}
	
	public Visualizer getVisualizer(){
		return this.visualization;
	}
	
}

