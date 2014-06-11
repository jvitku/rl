package org.hanns.rl.discrete.infrastructure;

import org.hanns.rl.discrete.ros.sarsa.QLambda;
import org.ros.node.ConnectedNode;

/**
 * QLambda for testing purposes
 * 
 * @author Jaroslav Vitku
 *
 */
public class QLambdaTest extends QLambda{
	
	public static final String name = "QLambdaTest";
	
	public boolean hardResetted = false;
	public boolean softResetted = false;
	
	private ConnectedNode connectedNode;
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		this.connectedNode = connectedNode;
		super.onStart(connectedNode);
	}
	
	@Override
	public void hardReset(boolean randomize) {
		super.hardReset(randomize);
		this.hardResetted = true;
	}
	
	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		this.softResetted = true;
	}
	
	public int getStep(){ return super.step; }
	
	public String getNamespace(){
		return this.connectedNode.getResolver().getNamespace().toString();
	}

}
