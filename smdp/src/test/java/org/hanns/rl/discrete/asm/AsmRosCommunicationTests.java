package org.hanns.rl.discrete.asm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hanns.rl.discrete.ros.asm.impl.EpsilonGreedy;
import org.hanns.rl.discrete.ros.asm.impl.Greedy;
import org.hanns.rl.discrete.ros.testnodes.AsmTestNode;
import org.junit.Test;
import ctu.nengoros.RosRunner;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

public class AsmRosCommunicationTests extends RosCommunicationTest{

	public static final String RL = "org.hanns.rl.discrete.ros.learning.sarsa.QLambda";
	public static final String TESTER = "org.hanns.rl.discrete.ros.testnodes.AsmTestNode";
	
	public static final String GREEDY = "org.hanns.rl.discrete.ros.asm.impl.Greedy";
	public static final String EPS_GREEDY = "org.hanns.rl.discrete.ros.asm.impl.EpsilonGreedy";
	public static final String IMP_GREEDY = "org.hanns.rl.discrete.ros.asm.impl.ImportanceBased";
	
	
	public static final String RLPARAMS = "_importance:=0 _importance:=0";//_noOutputsConf:=4";// _noInputsConf:=4";
	
	
	public static final int noActions = 7;
	
	//public static final String[] rl = new String[]{ RL,"_importance:=0","_noOutputs:=4","_noInputs:=2","_sampleCount:=10"};
	public static final String[] greedy = new String[]{GREEDY,"_noInputs:="+noActions };
	public static final String[] epsGreedy = new String[]{EPS_GREEDY,"_noInputs:="+noActions };
	
	public static final String[] greedy_tester = new String[]{TESTER, "_randomization:=false", "_noInputs:="+noActions };
	public static final String[] epsGreedy_tester = new String[]{TESTER, "_randomization:=true", "_noInputs:="+noActions };
	
	/**
	 * Runs the GreedyAMS and the ASMTester, checks that all action selections were correct
	 */
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
		
		// simulate some steps
		while(tester.getStep() < 1000){
		}
		
		tester.pauseSimulation(true);
		
		assertTrue(tester.noIncorrect==0);		// testing is here
		
		rlr.stop();								// stop everything
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
		System.out.println("all done");
	}
	
	@Test
	public void epsilonGreedyAsmTest(){
		RosRunner rlr = super.runNode(epsGreedy_tester);		
		assertTrue(rlr.isRunning());
		
		RosRunner mapr = super.runNode(epsGreedy);	
		assertTrue(mapr.isRunning());
		
		assertTrue(mapr.getNode() instanceof EpsilonGreedy);
		EpsilonGreedy asm = (EpsilonGreedy)mapr.getNode();
		
		assertTrue(rlr.getNode() instanceof AsmTestNode);
		AsmTestNode tester = (AsmTestNode) rlr.getNode();
		
		// simulate some steps
		while(tester.getStep() < 1000){
		}
		
		tester.pauseSimulation(true);
		
		assertTrue(tester.noCorrect > tester.noIncorrect);		// testing is here
		
		rlr.stop();								// stop everything
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
		System.out.println("all done");
	}
	
}
