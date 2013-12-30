package massim.agent.mind.harm.actions;

import java.util.Random;

public class IDGenerator {

	/**
	 * generates 
	 * @param name - the name of an action 
	 * @return	- ID for the action containing its name
	 */
	public static String generate(String name){
		String out = name+"_";
		
	    Random randomGenerator = new Random();
	    long l = randomGenerator.nextLong();
	    
		return out+Long.toString(l);
	}
	
}
