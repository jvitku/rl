package massim.agent.body.actionset;

//import massim.framework.util.xml.test.Loadable;

public interface ActionSet {//extends Loadable{

	
	public void showActions();
	
	// get the number of actions
	public int len();
	
	// get actions
	public String[] actions();
	
}
