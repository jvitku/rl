package org.hanns.rl.discrete.ros.sarsa.ioHelper;


public class MessageDerivator implements MessageDerivationFilter{

	public static final int DEF_MAXLOOP = 2;	// wait max 2 steps by default

	private int unchangedFor;
	private float[] prevMessage;
	private int maxLoop;

	public MessageDerivator(){
		this.maxLoop = DEF_MAXLOOP;
		this.unchangedFor = 0;
	}

	public MessageDerivator(int maxloop){
		this.maxLoop = maxloop;
		this.unchangedFor = 0;
	}

	@Override
	public void softReset(boolean randomize) { 
		this.prevMessage= null;
		this.unchangedFor = 0;
	}

	@Override
	public void hardReset(boolean randomize) { this.softReset(false);   }

	@Override
	public void setMaxClosedLoopLength(int len) {
		if(len<0){
			System.err.println("MessageDerivator: ERROR: will not set max closed loop len"
					+ "less than zero, setting zero");
			len = 0;
		}
		this.maxLoop = len;
		this.softReset(false);
	}

	@Override
	public int getMaxloopLength() { return this.maxLoop; }

	@Override
	public boolean newMessageShouldBePassed(float[] message) {

		// after constructor/reset?
		if(this.prevMessage == null){
			this.prevMessage = message.clone();
			this.unchangedFor = 0;
			return true;
		}
		
		if(!this.messagesEqual(this.prevMessage, message)){
			this.unchangedFor = 0;
			this.prevMessage = message.clone();
			return true;
		}
		// message the same, if should send already, reset counter (will wait again after this)
		if(this.unchangedFor >= this.maxLoop){
			this.unchangedFor = 0;
			return true;
		}
		
		this.unchangedFor++;
		return false;
	}

	private boolean messagesEqual(float[]a, float[] b){
		if(a.length!=b.length)
			return false;
		for(int i=0; i<a.length; i++)
			if(a[i]!=b[i])
				return false;
		return true;
	}

}
