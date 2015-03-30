package org.hanns.rl.discrete.asm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.hanns.rl.discrete.ros.asm.impl.Greedy;
import org.hanns.rl.discrete.ros.testnodes.AsmTestNode;
import org.junit.Test;
import ctu.nengoros.RosRunner;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

public class AsmRosCommunicationTests extends RosCommunicationTest{

	public static final String RL = "org.hanns.rl.discrete.ros.learning.sarsa.QLambda";
	public static final String TESTER = "org.hanns.rl.discrete.ros.testnodes.AsmTestNode";
	
	public static final String GREEDY = "org.hanns.rl.discrete.ros.asm.impl.Greedy";
	
	//public static final String MAP = "org.hanns.rl.discrete.ros.testnodes.GridWorldNode";
	
	//public static final String RLPARAMS = "_importance:=0 _noOutputsConf:=4 _noInputsConf:=4";
	public static final String RLPARAMS = "_importance:=0 _importance:=0";//_noOutputsConf:=4";// _noInputsConf:=4";
	
	
	//public static final String[] rl = new String[]{ RL,"_importance:=0","_noOutputs:=4","_noInputs:=2","_sampleCount:=10"};
	public static final String[] greedy = new String[]{GREEDY,"_noActions:=2"};
	
	public static final String[] greedy_tester = new String[]{TESTER, "_randomization:=false", "_noInputs:=2"};
	
	/**
	 * The tests does the following:
	 * -runs the RL node and MAP node
	 * -simulates agents interaction with the environment and logs it prosperity
	 * -after 10 000 steps, the agent should have explored the entire map
	 * -this information is contained in the prosperity value
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void greedyAsmTest(){
		RosRunner rlr = super.runNode(greedy_tester);		
		assertTrue(rlr.isRunning());
		
		RosRunner mapr = super.runNode(greedy);	
		assertTrue(mapr.isRunning());
		
		assertTrue(mapr.getNode() instanceof Greedy);
		Greedy asm = (Greedy)mapr.getNode();
		
		assertTrue(rlr.getNode() instanceof AsmTestNode);
		AsmTestNode tester = (AsmTestNode) rlr.getNode();
		
		// simulate 2000 steps
		while(tester.getStep() < 10000){
			//sleep(100);
		}
		
		tester.pauseSimulation(true);
		
		assertTrue(tester.noIncorrect==0);		// testing is here
		
		rlr.stop();								// stop everything
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
		System.out.println("all done");
	}
	
}
