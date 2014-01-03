package org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.impl;

import java.util.LinkedList;

import org.hanns.rl.discrete.learningAlgorithm.sarsaLambda.StateTrace;

/**
 * Simple memory for just visited world states.
 *   
 * @author Jaroslav Vitku
 *
 */
public class StateTraceImpl implements StateTrace{

	private int n;
	LinkedList<int[]> states;

	public StateTraceImpl(int n){
		this.n = n;
		states = new LinkedList<int[]>();
	}

	public void push(int[] state){
		if(states.size()==n){
			states.remove(states.size()-1);	// drop the last one if necessary
		}
		states.push(state.clone());			// push the new one
	}

	@Override
	public void softReset(boolean randomize) {
		states = new LinkedList<int[]>();
	}

	@Override
	public void hardReset(boolean randomize) {
		states = new LinkedList<int[]>();
	}

	@Override
	public int size() { return states.size(); }

	@Override
	public int[] get(int i) { 
		if(i>=states.size()){
			System.err.println("StateTraceImpl: ERROR: size of the trace is " +
					"only "+states.size()+" currently!");
			return null;
		}
		return states.get(i);
	}

	@Override
	public int getCapacity() { return this.n; }

	@Override
	public void setCapacity(int n) { this.n = n; }


}
