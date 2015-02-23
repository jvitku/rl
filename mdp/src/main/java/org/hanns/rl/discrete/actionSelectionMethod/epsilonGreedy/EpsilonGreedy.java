package org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy;

import java.util.Random;

import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethod;
import org.hanns.rl.discrete.actionSelectionMethod.ActionSelectionMethodConfig;
import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.BasicEpsilonGeedyConf;
import org.hanns.rl.discrete.actions.ActionSetInt;

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

	private boolean wasgreedy;
	protected Random r;
	protected ActionSetInt acitons;
	protected BasicEpsilonGeedyConf config;

	public EpsilonGreedy(ActionSetInt actions, BasicEpsilonGeedyConf config){
		r = new Random();
		this.acitons = actions;
		this.config = config;
		this.wasgreedy = false;
	}

	@Override
	public int selectAction(E[] actionValues) {
		if(actionValues.length != acitons.getNumOfActions()){
			System.err.println("ERROR: incorrect size of actionValues array!");
			this.wasgreedy = false;
			return -1;
		}
		// if can explore, can choose randomly with p=epsilon 
		if(config.getExplorationEnabled()){
			if(r.nextDouble() <= config.getEpsilon()){
				this.wasgreedy = false;
				return r.nextInt(actionValues.length);
			}
		}
		// if all actions have equal value, select randomly
		if(this.allEqual(actionValues)){
			int ind = r.nextInt(acitons.getNumOfActions());
			this.wasgreedy = false;
			return ind;
		}
		int ind = 0;
		for(int i=1; i<actionValues.length; i++){
			if(this.better(actionValues[i], actionValues[ind])){
				ind = i;
			}
		}
		this.wasgreedy = true;
		return ind;
	}

	protected abstract boolean allEqual(E[] actionVals);

	protected abstract boolean better(E a, E b);

	@Override
	public void setActionSet(ActionSetInt actions) { this.acitons = actions;	}

	@Override
	public ActionSetInt getActionSet() { return this.acitons;	}

	@Override
	public BasicEpsilonGeedyConf getConfig(){ return this.config; }

	@Override
	public void setConfig(ActionSelectionMethodConfig config){
		if(!(config instanceof BasicEpsilonGeedyConf)){
			System.err.println("EpsilonGreedy: ERROR: BasicEpsilonGeedyConf class expected");
			return;
		}
		this.config = (BasicEpsilonGeedyConf)config;
	}

	@Override
	public boolean actionWasGreedy() { return this.wasgreedy; }
	
	@Override
	public void hardReset(boolean randomize) {
		this.wasgreedy = false;
	}

	@Override
	public void softReset(boolean randomize) {
		this.wasgreedy = false;
	}
}
