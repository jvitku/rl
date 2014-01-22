package org.hanns.rl.discrete.ros;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.learningAlgorithm.models.qMatrix.FinalQMatrix;
import org.hanns.rl.discrete.ros.sarsa.QLambda;
import org.hanns.rl.discrete.ros.testnodes.GridWorldNode;
import org.hanns.rl.discrete.ros.testnodes.worlds.GridWorld;
import org.junit.Test;

import ctu.nengoros.RosRunner;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

/**
 * Test message filtering, the new ROS node should work also with delay in the closed loop. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class NengorosMessageFilter extends RosCommunicationTest{

	public static final String RL = "org.hanns.rl.discrete.ros.sarsa.QLambda";
	public static final String MAP = "org.hanns.rl.discrete.ros.testnodes.GridWorldNode";
	
	//public static final String RLPARAMS = "_importance:=0 _noOutputsConf:=4 _noInputsConf:=4";
	public static final String RLPARAMS = "_importance:=0 _importance:=0";//_noOutputsConf:=4";// _noInputsConf:=4";
	
	public static final String[] rl = new String[]{
		RL,"_importance:=0","_noOutputs:=4","_noInputs:=2","_sampleCount:=10"};
	
	/**
	 * The tests does the following:
	 * -runs the RL node and MAP node
	 * -simulates agents interaction with the environment and logs it prosperity
	 * -after 10 000 steps, the agent should have explored the entire map
	 * -this information is contained in the prosperity value
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void runMapAndRL(){
		RosRunner rlr = super.runNode(rl);		// run the RL
		assertTrue(rlr.isRunning());
		
		RosRunner mapr = super.runNode(MAP);	// run the map
		assertTrue(mapr.isRunning());
		
		assertTrue(mapr.getNode() instanceof GridWorldNode);
		GridWorldNode map = (GridWorldNode)mapr.getNode();
		
		assertTrue(rlr.getNode() instanceof QLambda);
		QLambda rl = (QLambda) rlr.getNode();
		
		// simulate 2000 steps
		while(map.getStep() < 10000){
			sleep(100);
		}
		
		map.setSimPaused(true);
		
		// prosperity is measured here 50/50 of:
		// binary coverage: how many tales of the map agent visited (has to be 1.0)
		// binary reward per step: typically something like 0.0115
		System.out.println("prosperity "+rl.getProsperityObserver().getProsperity());
		assertTrue(rl.getProsperityObserver().getProsperity()>0.1);
		
		System.out.println(GridWorld.visqm((FinalQMatrix<Double>)rl.rl.getMatrix(), 0));
		
		rlr.stop();								// stop everything
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
	}
	
}
