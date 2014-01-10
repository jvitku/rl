package org.hanns.rl.discrete.actions.impl;

import java.util.LinkedList;

import org.hanns.rl.discrete.actions.ActionBufferInt;

/**
 * Simple implementation of the {@link ActionBufferInt} for remembering n past executed
 * actions. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class ActionBuffer implements ActionBufferInt{

	public static final int EMPTY = -1; // no action remembered so far

	public static final int DEF_LEN=1;	// no buffering by default
	private int len;

	private LinkedList<Integer> buff;

	public ActionBuffer(){
		this.len = DEF_LEN;
		this.buff = new LinkedList<Integer>();
	}

	public ActionBuffer(int length){
		if(length<1){
			System.err.println("ERROR: the smallest size of the aciton buffer"
					+ "is 1, setting to 1");
			length=1;
		}
		this.len = length;
		this.buff = new LinkedList<Integer>();
	}

	@Override
	public void softReset(boolean randomize) {
		this.buff = new LinkedList<Integer>();
	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}

	@Override
	public synchronized void push(int action) {
		if(this.buff.size() == this.len){
			this.buff.removeLast();
		}
		this.buff.addFirst(action);
	}

	@Override
	public int read() {
		if(!this.isEmpty())
			return this.buff.getLast();

		return EMPTY;
	}

	@Override
	public void setLength(int length) {
		if(length<1){
			System.err.println("BasicConfiguration: ERROR: probably want action buffer of size 1, "
					+ "the size 1 actually does no buffering (push, then get)");
			length = 1;
		}
		this.len = length;
		this.softReset(false);
	}

	@Override
	public int getLength() { return this.len; }

	@Override
	public boolean isEmpty() { return this.buff.isEmpty(); }

}
