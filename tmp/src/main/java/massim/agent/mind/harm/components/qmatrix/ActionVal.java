package massim.agent.mind.harm.components.qmatrix;


/**
 * returned by the A matrix: couple containing Action name and value of Q(s,a)
 * 
 * @author jardavitku
 *
 */
public class ActionVal {

	public String name;
	public Integer val;
	
	public ActionVal(String name, Integer val){
		this.name = name;
		this.val = val;
	}
	
}
