package org.hanns.rl.discrete.observer.asm;

public abstract class AbstractAsmObserver implements AsmObserver{

	public final String name;
	
	protected boolean shouldVis = false;
	protected int visPeriod = 100;
	protected int step = 0;

	public AbstractAsmObserver(String name){
		this.name = name;
		this.hardReset(true);
	}

	@Override
	public String getName() { return this.name; }

	@Override
	public boolean getShouldVis() { return this.shouldVis; }

	@Override
	public int getVisPeriod() { return this.visPeriod; }

	@Override
	public void setShouldVis(boolean vis) { this.shouldVis = vis; }

	@Override
	public void setVisPeriod(int per) { this.visPeriod = per; }

	@Override
	public void hardReset(boolean randomize) { this.softReset(randomize); }

	@Override
	public void softReset(boolean randomize) { this.step = 0; }
}
