package org.hanns.rl.discrete.ros.sarsa;

import org.hanns.rl.discrete.observer.Observer;

/**
 * The same as {@link HannsQLambdaVis}, but this one publishes two types of importance: coverage and reward.
 * 
 * @author Jaroslav Vitku
 *
 */
public class HannsQLambdaVisProsperity extends HannsQLambdaVis{
	

	/**
	 * Publish the complete prosperity and separately for both childs (coverage and rew./step) 
	 */
	@Override
	protected void publishProsperity(){
		
		float[] observed = new float[3];
		observed[0] = o.getProsperity();
		observed[1] = o.getChilds()[0].getProsperity();
		observed[2] = o.getChilds()[1].getProsperity();
		
		Observer[] childs = o.getChilds(); 
		sl.pl(step+" "+o.getProsperity()+" "+childs[0].getProsperity()
				+" "+childs[1].getProsperity()); // log data
		
		std_msgs.Float32MultiArray fl = prospPublisher.newMessage();	
		fl.setData(observed);								
		prospPublisher.publish(fl);
	}
	
	
}
