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
	}

	@Override
	protected void performSARSAstep(float reward, float[] state){
		this.decodeState(state);
		int action = this.learn(reward);
		this.executeAction(action);
		
		this.visualization.performStep(prevAction, reward, states.getValues(), action);
		
	}
	
	public Visualizer getVisualizer(){
		return this.visualization;
	}
	
}

