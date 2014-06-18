package org.hanns.rl.discrete.observer;

import ctu.nengoros.network.node.observer.stats.AbsProsperityObserver;

public abstract class AbsSardaProspObserver extends AbsProsperityObserver implements SarsaProsperityObserver{

	/**
	 * @see SarsaObserver
	 */
	@Override
	public abstract void observe(int prevAction, float reward, int[] currentState, int futureAction);

}
