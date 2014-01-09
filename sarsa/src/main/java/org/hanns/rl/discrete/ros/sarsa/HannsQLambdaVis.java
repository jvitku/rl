package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.observer.Observer;
import org.hanns.rl.discrete.visualizaiton.Visualizer;
import org.hanns.rl.discrete.visualizaiton.qMatrix.FinalStateSpaceVisDouble;
import org.ros.node.ConnectedNode;

import ctu.nengoros.util.SL;

/**
 * The same as {@link org.hanns.rl.discrete.ros.sarsa.HannsQLambda}, but with visualization 
 * available.
 * 
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambdaVis extends HannsQLambda{

	private FinalStateSpaceVisDouble visualization;

	SL sl;

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);

		sl = new SL("filex");
		sl.printToFile(true);

		// initialize the visualizer
		this.visualization = new FinalStateSpaceVisDouble(
				states.getDimensionsSizes(), actions.getNumOfActions(), q);

		visualization.setVisPeriod(this.logPeriod);
		visualization.setTypeVisualization(1);
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
				+" "+childs[1].getProsperity()); // log data
		
		if(step%logPeriod ==0)
			SL.sinfol(step+" "+o.getProsperity()+" \tcoverage:"+childs[0].getProsperity()
				+" \treward/step:"+childs[1].getProsperity()); // log data
	}

}

