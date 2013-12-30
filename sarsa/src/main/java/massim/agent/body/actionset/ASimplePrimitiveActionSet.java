package massim.agent.body.actionset;

/**
 * this class serves really just for initialization of ActionList 
 * in fact, it is definition of agents actuators-abilities how to interact with environment
 *   
 * @author jardavitku
 */
public class ASimplePrimitiveActionSet implements ActionSet {

	public String[] actionSet = null;
	
	private int numActions;
	
	@Override
	public void showActions() {
		System.out.println("Agents action set consist of these actions: ");
		for(int i=0; i<actionSet.length; i++)
			System.out.print(" | "+actionSet[i]);
		System.out.println(" ");
	}

	@Override
	public int len() { return this.numActions; }

	@Override
	public String[] actions() { return actionSet; }

	//@Override
	public void init() {
		this.numActions = actionSet.length;
	}
	
}
