package org.hanns.rl.discrete.ros.sarsaAlgorithms;

public interface SarsaAlgorithm {

	//public void setAlg
	
	public void learn(); 		// TODO pass the action
	
	public void selectAction();	// TODO return action, pass the learned model
	
}
