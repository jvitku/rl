package org.hanns.rl.discrete.ros;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.ros.sarsa.HannsQLambda;
import org.hanns.rl.discrete.ros.testnodes.GridWorldNode;
import org.junit.Test;

import ctu.nengoros.RosRunner;
import ctu.nengoros.nodes.RosCommunicationTest;

public class RosInteractionVis extends RosCommunicationTest{

	public static final String RL = "org.hanns.rl.discrete.ros.sarsa.HannsQLambdaVis";
	public static final String MAP = "org.hanns.rl.discrete.ros.testnodes.GridWorldNode";
	
	//public static final String RLPARAMS = "_importance:=0 _noOutputsConf:=4 _noInputsConf:=4";
	public static final String RLPARAMS = "_importance:=0 _importance:=0";//_noOutputsConf:=4";// _noInputsConf:=4";
	
	public static final String[] rl = new String[]{
		RL,"_importance:=0","_noOutputs:=4","_noInputs:=2","_sampleCount:=10"};
	
	@Test
	public void runMapAndRL(){
		RosRunner rlr = super.runNode(rl);		// run the RL
		assertTrue(rlr.isRunning());
		
		RosRunner mapr = super.runNode(MAP);	// run the map
		assertTrue(mapr.isRunning());
		
		assertTrue(mapr.getNode() instanceof GridWorldNode);
		GridWorldNode map = (GridWorldNode)mapr.getNode();
		
		assertTrue(rlr.getNode() instanceof HannsQLambda);
		HannsQLambda rl = (HannsQLambda) rlr.getNode();
		
		
		// simulate 2000 steps
		while(map.getStep() < 500){
			sleep(100);
		}
		map.setSimPaused(true);
		
		
		
		rlr.stop();								// stop everything
		mapr.stop();
		assertFalse(rlr.isRunning());
		assertFalse(mapr.isRunning());
	}
	
}
