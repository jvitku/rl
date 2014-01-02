package org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy;

import java.util.Random;

import org.hanns.rl.discrete.actionSelectionStrategy.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionStrategy.epsilonGreedy.config.EpsilonGreedyConfig;
import org.hanns.rl.discrete.actions.ActionSet;

/**
 * The Epsilon-Greedy action selection method weights between exploration and exploitation.
 * The parameter epsilon defines the probability of selecting action randomly with uniform distribution.
 * The action is selected according to the Greedy strategy with the probability of 1-epsilon.   
 *  
 * @author Jaroslav Vitku
 *
 * @param <E> parameter defining the utility of actions
 */
public abstract class EpsilonGreedy<E> implements ActionSelectionMethod<E>{

	Random r;
	ActionSet acitons;
	EpsilonGreedyConfig config;
	
	public EpsilonGreedy(ActionSet actions, EpsilonGreedyConfig config){
		r = new Random();
		this.acitons = actions;
		this.config = config;
	}
	
	@Override
	public int selectAction(E[] actionValues) {
		if(actionValues.length!=acitons.getNumOfActions()){
			System.err.println("ERROR: incorrect size of actionValues array!");
			return -1;
		}
		if(config.getExplorationEnabled()){
			if(r.nextDouble() <= config.getEpsilon()){
				return r.nextInt(actionValues.length);
			}
		}
		int ind = 0;
		for(int i=1; i<actionValues.length; i++){
			if(this.better(actionValues[ind], actionValues[i])){
				ind = i;
			}
		}
		return ind;
	}

	protected abstract boolean better(E a, E b);
	
	@Override
	public void setActionSet(ActionSet actions) { this.acitons = actions;	}

	@Override
	public ActionSet getActionSet() { return this.acitons;	}

	public EpsilonGreedyConfig getConfig(){ return this.config; }

	public void setConfig(EpsilonGreedyConfig config){ this.config = config; }
}
