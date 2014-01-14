package org.hanns.rl.discrete.observer.stats;

/**
 * Abstract ProsperityObserver..
 *  
 * @author Jaroslav Vitku
 *
 */
public abstract class AbsProsperityObserver implements ProsperityObserver{

	public final String name = "AbstProsperityObserver (subclass name not defined)";
	public final String explanation = "Meaning of observed prosperity" +
			"not defined.";
	
	public static final boolean DEF_SHOULDVIS = false;
	public static final int DEF_VISPERIOD = 1000;
	protected boolean shouldVis;
	protected int step;
	protected int visPeriod;

	public AbsProsperityObserver(){

		this.shouldVis = DEF_SHOULDVIS;
		this.visPeriod = DEF_VISPERIOD;
		this.step = 0;
	}

	@Override
	public void softReset(boolean randomize) {
		this.step = 0;
	}
	
	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}

	@Override
	public void setShouldVis(boolean visualize) {this.shouldVis = visualize; }

	@Override
	public boolean getShouldVis() { return this.shouldVis; }

	@Override
	public void setVisPeriod(int period) {
		if(period < 0){
			System.err.println("invalid vis period, accepted is [1,inf] " +
					"setting default of: "+DEF_VISPERIOD);
			period = DEF_VISPERIOD;
		}
		this.visPeriod = period;
	}

	@Override
	public int getVisPeriod() { return this.visPeriod; }
	
	@Override
	public ProsperityObserver[] getChilds() {
		System.err.println("ERROR: no childs available");
		return null;
	}


}
