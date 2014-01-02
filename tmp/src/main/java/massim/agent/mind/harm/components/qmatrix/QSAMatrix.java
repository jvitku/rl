package massim.agent.mind.harm.components.qmatrix;

import java.util.Vector;

import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.components.predictors.eligibility.Inds;
import massim.agent.mind.harm.variables.VariableList;
import massim.framework.util.MyLogger;

/**
 * extends the printable matrix with ability to work with the particular world states
 * 
 * that means we can access values of all possible actions in the given state
 * or we can pick the best action from the given state
 * 
 * the main problem was in representation of actions, we will return the actions and corresponding vals 
 * 
 * the action selection methods should be provided here
 *  
 * @author jardavitku
 *
 */
public class QSAMatrix extends PrintableMatrix{

	// xx NNNu
	private final int LEV = 115;
	
	public QSAMatrix(VariableList vars, ActionList actions, MyLogger log) {
		super(vars, actions, log);

	}
	// mapping:  adding this action:
	public synchronized ActionValsVector getActualValues(VariableList vars){
		
		ActionValsVector out = new ActionValsVector(super.getNumActions());
		ValToIndexMapping mp = super.map.get(this.ACTS);
		
		// vector of (action) values in this state
		Vector<Integer> values = super.getActionValsInState(vars);
		
		//System.out.println("aaaaa getActualValues: "+values.size()+" "+out.size());

		if(values.size() != out.size()){
			log.err(cn, "getActualValues: number of values not the same as length of output vector!" +
					" values in this state is: "+values.size()+" but num actions: "+super.getNumActions());
			log.pl(LEV, "the dim sizes are "+log.getIds(this.m.getDimensionSizes()));
		}
		
		for(int i=0; i<out.size(); i++){
			out.array[i] = new ActionVal(mp.getValName(i), values.get(i));
		}
		
		return out;
	}
	
	public synchronized int get(Inds inds){
		return super.get(inds.get());
	}
	
	public synchronized void set(Inds inds, int val){
		super.set(inds.get(), val);
	}
	
	/**
	 * return the indexes of given position in the matrix
	 * @param vars - variables and their actual state
	 * @param a - action to be performed
	 * @return - Inds - storage of indexes in the matrix 
	 */
	public synchronized Inds getInds(VariableList vars, Action a, boolean previous){
		return new Inds(super.getIndexes(vars, a, previous));
	}

	/**
	 * will be called each simulation step, the variable list could be changed, 
	 * so each step check whether all variables are here, if some not, add it
	 * // TODO: do this also in the opposite direction (if var not in the list, remove from Q) [solved]
	 *    
	 * @param vars - list of variables to be in the Q(s,a)
	 */
	public synchronized void updateVarList(VariableList vars){
		for(int i=0; i<vars.size(); i++){
			// some variable is not there?
			if(!this.map.containsKey(vars.get(i).getName())){
				log.pl(LEV,"Q mapping:  adding this var: "+vars.get(i).getName());
				this.addVariable(vars.get(i));
				this.indicateMatrixChange();
			}
			// some variable has some new (less) value? 
			else if((this.map.get(vars.get(i).getName()).maxIndexStored()+1) 
					!= vars.get(i).getNumValues()){
				
				log.pl(LEV,"Q mapping:  ne value of variable registered, indicating update Q");
				this.indicateMatrixChange();
			}
		}
	}
	
	// TODO the same as the method above
	public synchronized void updateActionList(ActionList actions){
		
		ValToIndexMapping ac = this.map.get(this.ACTS);
		
		for(int i=0; i<actions.size(); i++){
			
			log.pl(LEV, "Checking this in map "+actions.get(i).getName() +" "
					+" and it is: "+ac.containsValName(actions.get(i).getName())+
					" and size of map is: "+ac.size());
			
			if(!ac.containsValName(actions.get(i).getName())){
				log.pl(LEV,"Q mapping:  adding this action: "+actions.get(i).getName());
				
				//// tady je chyba, getdimensionsizes obcas opomiji akci!
				log.pl(LEV,"\t\tnumatctions "+this.getNumActions()+" "
						+log.getIds(this.m.getDimensionSizes()));
				this.addAction(actions.get(i));
				log.pl(LEV,"\t\tnumatctions "+this.getNumActions()+" II"+" "
						+log.getIds(this.m.getDimensionSizes()));	
				
				// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!! [solved hopefuly]
				this.indicateMatrixChange();
			}
		}
		
	}
	public synchronized int[] getDimensionSizes() {
		return this.m.getDimensionSizes();
	}
	
}