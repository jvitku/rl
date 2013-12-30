package massim.agent.mind.harm.components.predictors.eligibility;

import java.util.ArrayList;

import massim.agent.mind.harm.components.qmatrix.QSAMatrix;

/**
 * this simplifies eligibility traces implementation
 * 
 * instead of updating of all the possible environment states, he agent will remember 
 * only N last action-state positions (actions), these couples are stored 
 * as position in the Q(s,a) matrix - indexes Inds
 * 
 *  as the Queue has the stationary size, we can also pre-count the gamma*lambda*error values 
 *  
 *  so we have two vectors, one with alpha*gamma*error values (based just on the distance 
 *  from the actual state) and vector of previous states (can be shorter)
 *  
 * @author jardavitku
 *
 */
public class EligibilityQueue {
	
	private ArrayList<Inds> inds;
	private ArrayList<Double> eligibility;
	
	private int len;
	private double gamma, lambda, alpha;
	/**
	 * 
	 * @param length - length of agents memory (how many steps back agent remembers)
	 * @param gamma - forgetting coefficient: 0-immediate reward 1-future consideration
	 * @param lambda - decay speed   eligibility of states is:  1 lambda lambda^2 lambda^3 ...
	 * @param alpha - learning rate
	 */
	public EligibilityQueue(int length, double gamma, double lambda, double alpha){
		this.len = length;
		this.gamma = gamma;
		this.lambda = lambda;
		this.alpha = alpha;
	
		inds = new ArrayList<Inds>();
		eligibility = new ArrayList<Double>();
		
		this.initErrors();
	}
	
	public void setAlpha(int al){ this.alpha = al; }
	
	/**
	 * init the vector of gamma*lambda*error values
	 */
	private void initErrors(){
		
		double e = 1;
		double actualLambda = lambda*e;
		
		for(int i=0; i<this.len; i++){
			// set the value and continue ..
			eligibility.add(i, gamma*actualLambda);
			actualLambda = actualLambda*lambda;
		}
	}
	
	public String eligibilitiesToString(){
		String out = "";
		for(int i=0; i<this.len; i++)
			out = out+" ["+i+": "+this.eligibility.get(i)+"]";
		return out;
	}
	
	/**
	 * empty the Inds vector (for instance when the Q matrix changes its dimension)
	 */
	private void deleteAll(){
		while(!inds.isEmpty())
			inds.remove(0);
	}
	
	/**
	 * the new step has been made to this place
	 * @param here - indexes in the Q(s,a) matrix 
	 */
	public void makeStepHere(Inds here){
		// append as a first action
		inds.add(0, here.clone());
		// eventually forget the last one 
		if(inds.size()>this.len)
			inds.remove(inds.size()-1);
			
	}
	
	/**
	 * update the knowledge based on the one step error and the previous steps
	 * @param delta - one step error value
	 * @param q - Q(s,a) matrix
	 */
	public void learn(double delta, QSAMatrix q){
		
		// no values?
		if(inds.size() == 0)
			return;
		
		int actual;
		
		// for all of remembered steps, add the appropriately decayed delta value (to the actual one) 
		for(int i=0; i<inds.size(); i++){
			
			if(inds.get(i).size() != q.getDimension()){
				this.deleteAll();
				return;
			}
			
		//	System.out.println("getting.. "+i+" "+inds.get(i).size()+" "+q.getDimension());
			actual = q.get(inds.get(i));
			
			if(actual == -1){	// not visited yet?
				System.err.println("no co to tady jako tohle dela?!?!? :-) ");
				actual = 0;
			}
//			System.out.print("delta is: "+delta+" prev is: "+actual);
			// solve the equation
			actual = (int)Math.round(actual + alpha*delta*eligibility.get(i));
			
			//System.out.println("final is? "+actual);
			
			if(actual < 0)
				System.out.println("\t\t\t !!!!!!!!!!!!!!!!!!!!!!!!!!! "+actual);
			
			q.set(inds.get(i), actual);
		}
	}
}




