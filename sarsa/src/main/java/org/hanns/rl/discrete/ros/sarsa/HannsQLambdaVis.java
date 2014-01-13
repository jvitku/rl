package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.observer.stats.ProsperityObserver;
import org.ros.node.ConnectedNode;

import ctu.nengoros.util.SL;

/**
 * The same as {@link org.hanns.rl.discrete.ros.sarsa.HannsQLambda}, but with visualization 
 * available. Also, this node publishes its prosperity, which is composed of coverage and reward/step.
 * 
 * @author Jaroslav Vitku
 *
 */
@Deprecated
public class HannsQLambdaVis extends AbstractQLambda{

	SL sl;
/*
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);

		this.buildProsperityPublisher(connectedNode);

		sl = new SL("filex");
		sl.printToFile(true);

	}

	@Override
	protected void performSARSAstep(float reward, float[] state){
		// store the data into the int[] states
		super.decodeState(state);	
		// choose action and learn about it
		int action = super.learn(reward); 
		// use observer to log info
		o.observe(super.prevAction, reward, states.getValues(), action);

		// execute action
		super.executeAction(action);
		this.publishProsperity();

		//if(this.visualization!=null)
			//this.visualization.observe(prevAction, reward, states.getValues(), action);

		this.log();
	}
*/

	private void log(){
		ProsperityObserver[] childs = o.getChilds(); 
		sl.pl(step+" "+o.getProsperity()+" "+childs[0].getProsperity()
				+" "+childs[1].getProsperity()); // log data to file each step

		if(step%logPeriod ==0)
			SL.sinfol("step: "+step+" "+o.getProsperity()+"\tcoverage="+childs[0].getProsperity()
					+" \treward/step="+childs[1].getProsperity()); // log data
	}

	@Override
	protected void onNewDataReceived(float[] data) {
		// TODO Auto-generated method stub
		
	}

}

