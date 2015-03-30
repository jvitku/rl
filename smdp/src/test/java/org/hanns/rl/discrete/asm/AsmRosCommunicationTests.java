package org.hanns.rl.discrete.asm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hanns.rl.discrete.ros.asm.impl.EpsilonGreedy;
import org.hanns.rl.discrete.ros.asm.impl.Greedy;
import org.hanns.rl.discrete.ros.asm.impl.ImportanceBased;
import org.hanns.rl.discrete.ros.testnodes.AsmTestNode;
import org.junit.Test;
import ctu.nengoros.RosRunner;
import ctu.nengoros.network.common.exceptions.StartupDelayException;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

public class AsmRosCommunicationTests extends RosCommunicationTest{

	public static final String TESTER = "org.hanns.rl.discrete.ros.testnodes.AsmTestNode";
	
	public static final String GREEDY = "org.hanns.rl.discrete.ros.asm.impl.Greedy";
	public static final String EPS_GREEDY = "org.hanns.rl.discrete.ros.asm.impl.EpsilonGreedy";
	public static final String IMP_GREEDY = "org.hanns.rl.discrete.ros.asm.impl.ImportanceBased";
	
	public static final int noActions = 7;
	
	public static final String[] greedy = new String[]{GREEDY,"_noInputs:="+noActions };
	public static final String[] epsGreedy = new String[]{EPS_GREEDY,"_noInputs:="+noActions };
	public static final String[] impGreedy = new String[]{IMP_GREEDY,"_noInputs:="+noActions };
	
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
	
	// Run the node with big importance, reset and set small importance. Compare to the expected results. 
	@Test
	public void importanceBasedAsmTest(){
		RosRunner rlr = super.runNode(epsGreedy_tester);		
		assertTrue(rlr.isRunning());
		
		RosRunner mapr = super.runNode(impGreedy);	
		assertTrue(mapr.isRunning());
		
		assertTrue(mapr.getNode() instanceof ImportanceBased);
		ImportanceBased asm = (ImportanceBased)mapr.getNode();
		
		assertTrue(rlr.getNode() instanceof AsmTestNode);
		AsmTestNode tester = (AsmTestNode) rlr.getNode();
		
		try {
			tester.awaitStarted();
			asm.awaitStarted();
		} catch (StartupDelayException e1) {
			e1.printStackTrace();
		}
		
		
		tester.setImportance(1.0f);
		try {
			Thread.sleep(1000);
			
		} catch (InterruptedException e) {
			fail();
		}
		tester.setImportance(1.0f);
		
		tester.hardReset(true);
		
		// simulate some steps
		while(tester.getStep() < 1000){
		}
		tester.pauseSimulation(true);
		
		assertTrue(this.differenceAbove(tester.noCorrect, tester.noIncorrect, 80)); // minEpsilon=0.1
		
		// restart the tester, run another steps, but now the importance is low
		tester.hardReset(true);
		tester.setImportance(0);
		tester.pauseSimulation(false);
		while(tester.getStep() < 2000){
		}
		
		assertTrue(tester.noCorrect<tester.noIncorrect);
		
		rlr.stop();								// stop everything
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
		System.out.println("all done");
	}
	
	protected boolean differenceUnder(int a, int b, int percent){
		System.out.println("parameters are: a="+a+" b="+b+" difference shoudl be under "+percent+"% of a");
		int diff = Math.abs(b-a);
		float base = a;
		float per = Math.abs(base/100);
		float diffP = diff/per;		// how many percent it is
		
		return diffP<=percent;
	}
	
	private boolean differenceAbove(int a, int b, int percent){
		System.out.println("parameters are: a="+a+" b="+b+" difference shoudl be above "+percent+"% of a");
		int diff = Math.abs(b-a);
		float base = a;
		float per = Math.abs(base/100);
		float diffP = diff/per;		// how many percent it is
		return diffP>=percent;
	}
}
