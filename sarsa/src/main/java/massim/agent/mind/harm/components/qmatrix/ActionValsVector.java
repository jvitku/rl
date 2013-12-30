package massim.agent.mind.harm.components.qmatrix;

import java.util.Random;


/**
 * vector of all admissible actions in the given state with their values
 * array of couples {actionName,actionVal}
 * 
 * typically returned from the matrix
 *  
 * @author jardavitku
 *
 */
public class ActionValsVector {

	public ActionVal[] array;
	private int size;
	private Random r;
	
	private int selectedVal;
	
	public ActionValsVector(int len){
		this.size = len;
		array = new ActionVal[len];
		this.r = new Random();
		this.selectedVal = 0;
	}
	
	/**
	 * value (Q(s,a)) of action that is selected now  
	 *	(randomly or just as max action)
	 * @return
	 */
	public int getSelectedVal(){
		return this.selectedVal;
	}
	
	/**
	 * return action with the maximum value
	 * action with the null value are ignored
	 * if all action values are null, then the random action is returned 
	 * 
	 * @return ActionVal couple
	 */
	public ActionVal getMaxAction(){
		int actual = -1;
		int max = 0;
		
		for(int i=0; i<size; i++){
			if(array[i].val != null){
				if(array[i].val > max){
					actual = i;
					max = array[i].val; 
				}
			}
		}
		if(actual == -1){
			actual = r.nextInt(size);
			this.selectedVal = this.getVal(array[actual]);
			return array[actual];
		}
		else{
			this.selectedVal = this.getVal(array[actual]);
			return array[actual];
		}
	}
	
	public ActionVal getRandAction(){
		int rr = r.nextInt(size);
		this.selectedVal = this.getVal(array[rr]);
		return array[rr];
	}
	
	public String toString(){
		String out = " ";
		for(int i=0; i<size; i++){
			out = out + " ["+array[i].name+" "+array[i].val+"] ";
		}
		return out;
	}
	
	/**
	 * get actual value
	 * deal with null pointers
	 * @return
	 */
	private int getVal(ActionVal where){
		
		if(where.val==null)
			return 0;
		return where.val;
	}
	
	public int size(){
		return this.size;
	}
	
}
