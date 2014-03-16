package org.hanns.rl.discrete.ros.asm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

import ctu.nengoros.RosRunner;
import ctu.nengoros.network.common.exceptions.StartupDelayException;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

public class ImportanceBasedGreedyTest extends RosCommunicationTest {

	public static final String EASM = "org.hanns.rl.discrete.ros.asm.ImportanceEpsilonGreedy";	// tested node
	public static final String TEST = "org.hanns.rl.discrete.ros.asm.ActionImportanceReceiver";	// tester

	public static final String[] EASM_NORAND = 	new String[]{EASM, "_importance:=1","_noInputs:=4"};
	public static final String[] EASM_RAND = 	new String[]{EASM, "_importance:=0","_noInputs:=4"};

	private boolean debug = false;

	/**
	 * just start two nodes, check their instances, wait and stop them 
	 */
	@Test
	public void startStopGreedyASMTest(){
		RosRunner rlr = super.runNode(EASM);		// run the greedy ASM node
		assertTrue(rlr.isRunning());

		RosRunner mapr = super.runNode(TEST);	// run the tester node
		assertTrue(mapr.isRunning());

		assertTrue(mapr.getNode() instanceof ActionImportanceReceiver);
		assertTrue(rlr.getNode() instanceof ImportanceEpsilonGreedy);

		// let them run some time
		super.sleep(1000);

		rlr.stop();								// stop everything
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
	}

	/**
	 * Run the ImportanceEps.Greedy with low importance, check results, increase the importance, check again 
	 */
	@Test
	public void testImportanceInput(){
		RosRunner rlr = super.runNode(EASM_RAND);	// run the greedy ASM node
		assertTrue(rlr.isRunning());

		RosRunner mapr = super.runNode(TEST);		// run the tester node
		assertTrue(mapr.isRunning());

		assertTrue(mapr.getNode() instanceof ActionImportanceReceiver);
		ActionImportanceReceiver tester = (ActionImportanceReceiver)mapr.getNode();

		assertTrue(rlr.getNode() instanceof ImportanceEpsilonGreedy);
		ImportanceEpsilonGreedy gr = (ImportanceEpsilonGreedy) rlr.getNode();

		try {
			tester.awaitStarted();
			gr.awaitStarted();
			super.sleep(200);
		} catch (StartupDelayException e) {
			e.printStackTrace();
			fail();
		}

		int noTests = 500;
		int noRandoms = 0;

		/**
		 * Check ASM with low importance
		 */
		tester.setImportanceToNode(0);	// high exploration
		super.sleep(10);
		for(int i=0; i < noTests; i++){
			if(randomActionSelected(4, tester))
				noRandoms++;
		}
		System.out.println("No of greedy actions is: "+(noTests-noRandoms)+" and no randoms: "+noRandoms);
		// the number of randomized actions should be very high
		assertTrue(noRandoms > (noTests-noRandoms));

		/**
		 * Check ASM with high importance
		 */
		noRandoms = 0;
		tester.setImportanceToNode(1);	// high exploration
		super.sleep(10);
		for(int i=0; i < noTests; i++){
			if(randomActionSelected(4, tester))
				noRandoms++;
		}
		System.out.println("No of greedy actions is: "+(noTests-noRandoms)+" and no randoms: "+noRandoms);
		// the number of randomized actions should be much smaller than those Greedy ones
		assertTrue((10*noRandoms) < (noTests-noRandoms));

		/**
		 * stop everything
		 */
		rlr.stop();								
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
	}

	Random r = new Random();
	/**
	 * Return true if a random action was selected
	 * @param noActions number of actions
	 * @return true if the random action was selected
	 */
	private boolean randomActionSelected(int noActions, ActionReceiver tester){
		int range = 100;	// action utilities e.g. from: -50 to 50

		float [] actionUtils = new float[noActions];
		float highest = -10*range;
		float highestInd = 0;


		for(int i=0; i<actionUtils.length; i++){

			actionUtils[i] = -range/2+(range*r.nextFloat());
			//System.out.println("comparing: "+highest+" actionUtils "+actionUtils[i]+" and actionUtils is bigger? "+(actionUtils[i]>highest));
			if(actionUtils[i] > highest){
				highest = actionUtils[i];
				highestInd = i;
			}
		}
		//System.out.println("sending this array: "+SL.toStr(actionUtils));
		float[] response = tester.getResponseFor(actionUtils);
		//System.out.println("sent: "+SL.toStr(actionUtils)+" and received: "+SL.toStr(response));

		int noNonzeroActions = 0;
		int index = 0;

		for(int i=0; i<response.length; i++){
			if(response[i] != 0)
				noNonzeroActions ++;
			if(response[index] < response[i])
				index = i;
		}

		assertTrue(noNonzeroActions == 1);	// assert encoding 1ofN

		if(debug)
			System.out.println("Index of selected action is: "+index+" and the highest one was: "+highestInd);

		// highest action selected?
		if(index == highestInd)
			return false;
		// randomized?
		return true;
	}


}
