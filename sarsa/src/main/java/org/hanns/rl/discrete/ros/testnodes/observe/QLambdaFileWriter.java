package org.hanns.rl.discrete.ros.testnodes.observe;


import org.hanns.rl.common.exceptions.MessageFormatException;
import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.discrete.observer.SarsaObserver;
import org.hanns.rl.discrete.observer.qMatrix.stats.FinalMaxActionUtilDouble;
import org.hanns.rl.discrete.observer.stats.impl.MCR;
import org.hanns.rl.discrete.ros.sarsa.AbstractQLambda;
import org.hanns.rl.discrete.ros.sarsa.ioHelper.MessageDerivator;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.observer.Observer;

/**
 * Implements QLambda algorithm in the ROS node. Used for testing fileWrite observer.
 * 
 * @see {@link org.hanns.rl.discrete.ros.sarsa.QLambda}
 * @see {@link org.hanns.rl.discrete.ros.sarsa.ioHelper.MessageDerivationFilter}
 * 
 * @author Jaroslav Vitku
 *
 */
public class QLambdaFileWriter extends AbstractQLambda{

	public MessageDerivator filter;
	public static final String filterConf = "filterLength";

	/**
	 * Called by the asynchronous data subscriber when new data
	 * sample is received. Typically, the first value is reward, 
	 * the rest is state description.
	 */
	@Override
	protected void onNewDataReceived(float[] data) {

		// check whether the message should be passed through
		boolean shouldPass = filter.newMessageShouldBePassed(data);

		// if the message should not be processed, send NOOP and wait for new state
		if(!shouldPass){
			// publish the NOOP 
			std_msgs.Float32MultiArray fl = dataPublisher.newMessage();	
			fl.setData(actionEncoder.encode(ActionSet.NOOP));								
			dataPublisher.publish(fl);
			return;
		}

		// decode data (first value is reinforcement..
		// ..the rest are values of state variables
		float reward = data[0];
		float[] state = new float[data.length-1];
		for(int i=0; i<state.length; i++){
			state[i] = data[i+1];
		}
		// perform the SARSA step
		performSARSAstep(reward, state);		
	}

	protected void performSARSAstep(float reward, float[] state){
		this.decodeState(state);
		int action = this.learn(reward);
		this.executeAction(action);
	}

	protected void decodeState(float[] state){
		// encode the raw float[] values into state variables
		try {
			states.setRawData(state);
		} catch (MessageFormatException e) {
			log.error(me+"ERROR: Could not encode state description into state variables");
			e.printStackTrace();
		}
	}

	/**
	 * Instantiate the ProsperityObserver
	 */
	@Override
	protected void registerProsperityObserver(){
		//o = new BinaryCoverageForgettingReward(this.states.getDimensionsSizes());
		//o = new KnowledgeChange(this.states.getDimensionsSizes(), q);
		//o = new ForgettingCoverageChangeReward(this.states.getDimensionsSizes(),q);
		o = new MCR();
		//o = new 

		// write to file TODO
		Observer fw = new FinalMaxActionUtilDouble(super.states.getDimensionsSizes(), 
				super.actions.getNumOfActions(), 
				super.q, "generated-data/test.txt");
				//super.q, "test.txt");
		
		
		observers.add(o);
		observers.add(fw);
	}

	/**
	 * Select action, perform learning step, return selected action.
	 * 
	 * @param reward reward received during the previous simulation step
	 * @return selected action
	 */
	protected int learn(float reward){

		int action = asm.selectAction(q.getActionValsInState(states.getValues()));
		rl.performLearningStep(prevAction, reward, states.getValues(), action);

		// run all observers
		for(int i=0; i<observers.size(); i++)
			((SarsaObserver)observers.get(i)).observe(prevAction, reward, states.getValues(), action);

		return action;
	}

	/**
	 * Parse configuration of the filter.
	 */
	@Override
	protected void parseParameters(ConnectedNode connectedNode){
		super.parseParameters(connectedNode);

		int len = r.getMyInteger(filterConf, MessageDerivator.DEF_MAXLOOP);
		filter = new MessageDerivator(len);
	}

	@Override
	protected void registerParameters(){
		super.registerParameters();
		paramList.addParam(filterConf, ""+MessageDerivator.DEF_MAXLOOP, "The maximum" +
				" length (in sim. steps) of the closed loop: action->newState");
	}

	@Override
	public boolean isStarted(){
		if(!super.isStarted())
			return false;
		if(filter==null)
			return false;
		return true;
	}

	@Override
	public float getProsperity() { return o.getProsperity(); }

	@Override
	public String listParams() { return this.paramList.listParams(); }

	@Override
	public void hardReset(boolean randomize) {
		if(!this.randomizeAllowed)
			randomize = false;
			
		System.out.println(me+"hardReset called, discarding all data");
		filter.hardReset(randomize);
		rl.hardReset(randomize);
		asm.hardReset(randomize);
		for(int i=0; i<observers.size(); i++){
			observers.get(i).hardReset(randomize);
		}
		o.hardReset(randomize);
	}

	@Override
	public void softReset(boolean randomize) {
		if(!this.randomizeAllowed)
			randomize = false;
		
		System.out.println(me+"softReset called, returning to the initial state.");
		filter.softReset(randomize);
		rl.softReset(randomize);
		asm.softReset(randomize);
		for(int i=0; i<observers.size(); i++){
			observers.get(i).softReset(randomize);
		}
		o.softReset(randomize);
	}
}



