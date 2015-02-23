package org.hanns.rl.discrete.infrastructure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import ctu.nengoros.RosRunner;
import ctu.nengoros.network.common.exceptions.StartupDelayException;
import ctu.nengoros.network.node.infrastructure.simulation.testnodes.SimulationControlsNode;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

public class Reset extends RosCommunicationTest{
	
	@Test
	public void testReset(){
		
		// TODO: some ROSjava network problem, starting these two in different order causes exception
		// "no publisher for the topic /simulation (when calling hardReset) 
		RosRunner slaveNode =super.runNode(
				"org.hanns.rl.discrete.infrastructure.QLambdaTest");
		RosRunner masterNode =super.runNode(
				"ctu.nengoros.network.node.infrastructure.simulation.testnodes.SimulationControlsNode");		
		

		SimulationControlsNode master = null;
		QLambdaTest node = null;

		if(!(masterNode.getNode() instanceof SimulationControlsNode))
			fail("Wrong class launched");
		master = (SimulationControlsNode)masterNode.getNode();

		if(!(slaveNode.getNode() instanceof QLambdaTest))
			fail("Wrong class launched");
		node = (QLambdaTest)slaveNode.getNode();

		try {
			node.awaitStarted();
			master.awaitStarted();
		} catch (StartupDelayException e1) {
			e1.printStackTrace();
			System.out.println("(At least one of) nodes not started fast enough!");
			fail();
		}
		System.out.println("namesace of slave node is: "+node.getNamespace());
		
		System.out.println("ready");
		assertTrue(node.isStarted());
		assertTrue(master.isStarted());
		
		assertFalse(node.hardResetted);
		assertFalse(node.softResetted);
		
		// wait for publishers/subscribers to be operational
		sleep(100);					// TODO communicationAware startup 

		master.callHardReset(false);
		
		// wait for message to be delivered
		sleep(100);					// TODO use services instead of pub/sub
		assertTrue(node.hardResetted);
		assertFalse(node.softResetted);

		master.callSoftReset(false);
		sleep(100);
		assertTrue(node.hardResetted);
		assertTrue(node.softResetted);

		masterNode.stop();
		slaveNode.stop();
	}

}
