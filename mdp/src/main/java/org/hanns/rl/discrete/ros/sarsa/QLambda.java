package org.hanns.rl.discrete.ros.sarsa;


import org.hanns.rl.common.exceptions.MessageFormatException;
import org.hanns.rl.discrete.actions.ActionSet;
import org.hanns.rl.discrete.observer.SarsaObserver;
import org.hanns.rl.discrete.observer.stats.impl.MCR;
import org.hanns.rl.discrete.ros.sarsa.ioHelper.MessageDerivator;
import org.ros.node.ConnectedNode;

/**
 * Implements QLambda algorithm in the ROS node. This algorithm uses:
 * <ul>
 * <li>Q(lambda) discrete RL Q-learning algorithm with eligibility trace of given length.</li>
 * <li>ASM (action selection method) is modified epsilon-greedy, where the epsilon is 
 * affected by the value of importance, if action importance is high, epsilon gets small.</li>
 * <li>ROS actions are encoded by the 1ofN encoding (one value 1 on the output vector means 
 * a particular action)</li>
 * <li>ROS input is evaluated as follows: first value is reward received, the rest of values
 * determines the current state of the environment (states are encoded on the specified interval
 * ([0,1] by default) with given number of samples. Number of samples determines number of
 * values of given variable, therefore also size of the resulting state space.</li>
 * <li>In addition, this algorithm uses {@link #filter} on input data, see below.</li>
 * </ul>
 * 
 * 
 * The filtering of input data is made in the following way:
 * <ul>
 * <li>Only state changes are registered as new state (that is e.g. response from the GridWorld)</li>
 * <li>There is specified maximum numbed of steps without response (to executed action). 
 * This defines the maximum length of closed loop where the RL is (max. delay between 
 * action->new state)</li>
 * <li>If the response is not received in the predefined number of steps, the situation 
 * is evaluated as the following case: the action executed did not have effect, RL&ASM: continue.</li>
 * </ul>
 * 
 * @see {@link org.hanns.rl.discrete.ros.sarsa.ioHelper.MessageDerivationFilter}
 * 
 * @author Jaroslav Vitku
 *
 */
public class QLambda extends AbstractQLambda{

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
			//System.out.println("NOOP");
			dataPublisher.publish(fl);
			this.publishProsperity();
			return;
		}

		// decode data (first value is reinforcement..
		// ..the rest are values of state variables
		float reward = data[0];
		float[] state = new float[data.length-1];
		for(int i=0; i<state.length; i++){
			state[i] = data[i+1];
		}
		//System.out.println("SARSA step!");
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

		observers.add(o);
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



