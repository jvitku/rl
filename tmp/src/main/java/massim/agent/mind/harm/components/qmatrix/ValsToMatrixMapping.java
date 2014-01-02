package massim.agent.mind.harm.components.qmatrix;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;
import massim.framework.util.MyLogger;

/**
 * this class is mapping variable names to maps (and those are mapping String values of variables
 * to indexes of particular columns/rows in the matrix..)
 *  
 * @author jardavitku
 *
 */
public class ValsToMatrixMapping {

	// name of variable (dimension) that belongs to action set (its values are particular actions)
	public final String ACTS = "actions";
	// maps variable name to mapping values to index in the MatrixII
	protected final Map<String, ValToIndexMapping> map;
	protected final MyLogger log;
	protected final String cn;
	
	protected boolean matrixChanged;	// shared between harm and GUI, whether the matrix dim.has been changed
	
	//TODO
	protected int[] previousIndexes;	// the array of previous indexes (to address value on Q(s,a) from Q(s',a'))
	
	public /*final*/ QMatrix<Integer> m;
	
	private final int LEV = 115;//000000;

	public ValsToMatrixMapping(VariableList vars, ActionList actions, MyLogger log){
		
		this.log = log;
		this.cn = this.getClass().getName();
		// initialize the map that has size == (num variables + one dimension for actions)
		this.map = (Map<String, ValToIndexMapping>)
			Collections.synchronizedMap(new LinkedHashMap<String, ValToIndexMapping>());

		// it is good to let the action set to be the last dimension in the MatrixII (faster indexing)
		// actually it is on the first dimension 
		this.map.put(ACTS, new ValToIndexMapping(ACTS, actions.actions, true)); ///new
		
		// add variables
		if(vars != null)
			this.addVars(vars);
		// or initialize the matrix without them 
		else{
			
			int[] indexes = new int[]{actions.actions.size()};
			this.m = new QMatrix<Integer>(indexes/*,false*/);
			
		}
		
		this.indicateMatrixChange();
	}
	
	public synchronized String printActions(){
		String out = "[";
		
		ValToIndexMapping mp = this.map.get(this.ACTS);
		
		for(int i=0; i<mp.size(); i++)
			out = out +" "+i+":"+ mp.getValName(i)+";";
		
		return out+"]";
	}
	
	private synchronized void addVars(VariableList vars){
		Variable v;
		
		for(int i=0; i<vars.size(); i++){
			v = vars.get(i);
			// create new mapping of MAP(value->index) and add it to mapping name->MAP
			if(map.containsKey(v.getName()))
				this.log.err(cn,"C: duplicate name of variable, ignoring this entry: "+v.getName());
			else
				this.map.put(v.getName(), new ValToIndexMapping(v.getName(), v.vals) );
		}
		// get sizes of each dimension
		int[] indexes = new int[map.size()];
		int i = 0;
		
		synchronized(map){
			Set<String> st = map.keySet();
			Iterator<String> iterator = st.iterator();
			
			// for all sorted nodes in the map, get the value of index
			while (iterator.hasNext())
				indexes[i++] = map.get(iterator.next()).maxIndexStored()+1;
		}
		//this.pi(indexes);
		this.m = new QMatrix<Integer>(indexes/*, false*/);
	}
	
	public synchronized void indicateMatrixChange(){ this.matrixChanged = true; }
	public synchronized void discardMatrixChange(){ this.matrixChanged = false; }
	public synchronized boolean matrixChanged(){ return this.matrixChanged; }
	
	/**
	 * so the algorithm is: 
	 * 		-for all variable names:
	 * 			get the name from map, store the appropriate value of index
	 * 		-create array of indexes
	 * 		-for all nodes in the hashMap (which are ordered)
	 * 			copy the value of stored index into the array
	 * 		-pass the index array to the MatrixII
	 * 			
	 * @param namesVals - 2D array: first column contains variable names, second their values
	 * @return - value contained in the MatrixII 
	 */
	public synchronized int get(String[][] namesVals){
		
		int[] indexes = this.getIndexes(namesVals);
		
		// pick up the value from the MatrixII!
		int a = -1;
		try {
			a = (Integer)m.get(indexes);
		} catch (Exception e) { /*e.printStackTrace();*/ }
		
		return a;
	}
	
	
	public synchronized  int get(int[] indexes){
		
		// pick up the value from the MatrixII!
		int aa = -1;
		try {
			aa = (Integer)m.get(indexes);
		} catch (Exception e) { /*e.printStackTrace();*/ }
		
		return aa;
	}
	
	public synchronized int get(VariableList vars, Action a){
		
		int[] indexes = this.getIndexes(vars, a, false);
		
		// pick up the value from the MatrixII!
		int aa = -1;
		try {
			aa = (Integer)m.get(indexes);
		} catch (Exception e) { /*e.printStackTrace();*/ }
		
		return aa;
	}
	
	public synchronized Vector<Integer> getActionValsInState(VariableList vars){
		Vector<Integer> out;
		
		int[] inds = this.getIndexes(vars, null, false);
		//System.out.println("getting indexes and they are num: "+inds.length);
		//System.out.println("dimension sizes are!! "+this.pii(m.getDimensionSizes()));
		
		m.checkDimensionSizes(inds);
		
		out = this.m.getActionValsInThisState(inds);
		
		return out;
	}
	
	
	public synchronized int getNumActions(){
		return map.get(this.ACTS).size();
	}
	
	/**
	 * set the int value to the Q(s,a) matrix, if the value of variable has not been found
	 * (in the method getIndexes) the new one is added to the mapping (it obtains new index
	 * and thus the size of matrix will be increased by 1)
	 *  
	 * @param namesVals - array of variable names and their values 
	 * @param val - value to be added to the matrix on the specified position
	 */
	public synchronized void set(String[][] namesVals, int val){
		
		int[] indexes = this.getIndexes(namesVals);
		
		// set the data to the the Matrix!
		try {
			m.set(indexes, val);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public synchronized void set(int[] indexes, int val){
		
		// set the data to the the Matrix!
		try {
			m.set(indexes, val);
		} catch (Exception e) { e.printStackTrace(); }
	}

	public synchronized void set(VariableList vars, Action a, int val){
		
		int[] indexes = this.getIndexes(vars, a, false);
		
		// set the data to the the Matrix!
		try {
			m.set(indexes, val);
		} catch (Exception e) { e.printStackTrace(); }
	}

	
	public synchronized int getDimension(){ return m.getDim(); }
	
	public synchronized void makeStep(int [] indexes){ this.previousIndexes = indexes; }
	
	/**
	 * get array of indexes in the MatrixII, specified by given name; value array
	 * @param namesVals  array of couples <name; value> specified both by String 
	 * @return array of indexes of length n, where n is the dimension of MatrixII  
	 */
	public synchronized int[] getIndexes(String[][] namesVals){
		
		if(namesVals.length != m.getDim())
			log.err(cn,"getIndexes: number of variables is not the same as dimension of Matrix");
		
		ValToIndexMapping mp;
		
		// number of found unitialized nodes, if one, we suppose that it is action and set some default
		int foundNulls= 0;
		
		// for all variables
		for(int i=0; i<namesVals.length; i++){
			
			// if the entry is not initialized, we suppose that it is action, and selection is arbitrary
			if(namesVals[i][0] == null){
				if(foundNulls>0){
					log.err(cn,"getIndexes: found more than one uninitialized entry!");
					return null;
				}
				foundNulls++;
				mp = this.map.get(this.ACTS);
				mp.setIndex(0);
			// or just map the String value of String variable to index in the matrix
			}else{
				// get the reference for the class mapping value names to indexes, and set the actual index
				mp = this.map.get(namesVals[i][0]);
				if(mp == null){
					log.err(cn,"getIndexes: key not retreived from the map!");
					return null;
				}
				mp.setValTo(namesVals[i][1]);
			}
		}
		
		int[] indexes = new int[m.getDim()];
		int i = 0;
	
		// get Set of keys contained in Map using Set keySet() method of LinkedHashMap class
		Set<String> st = map.keySet();
		// iterate through the Set of keys, ordered from the first to last one added
		Iterator<String> iterator = st.iterator();
		
		// for all sorted nodes in the map, get the value of index
		while (iterator.hasNext()){
			mp = map.get(iterator.next());
			//indexes[i++] = map.get(iterator.next()).getIndex();
			indexes[i++] = mp.getIndex();	
		}
		return indexes;
	}

	
	/**
	 * convert variables and action to to indexes in the matrix 
	 * @param vars - variable list of vars and their actual values
	 * @param a - action to be read, or null
	 * @param prevoius - whether to get previous state of world (if false, the actual one is dealed with)
	 * @return 
	 */
	public synchronized int[] getIndexes(VariableList vars, Action a, boolean previous){
		
		if((vars.size()+1) != m.getDim())
			log.err(cn,"getIndexes: number of variables+1 is not the same as dimension of FinalQMatrix");
		
		ValToIndexMapping mp;
		
		// for all variables
		for(int i=0; i<vars.size(); i++){
			// get the reference for the class mapping value names to indexes, and set the actual index
			mp = this.map.get(vars.get(i).getName());
			if(mp == null){
				log.err(cn,"getIndexes: key not retreived from the map!");
				return null;
			}
			if(!previous)
				mp.setValTo(vars.get(i).actualValue());
			else
				mp.setValTo(vars.get(i).previousValue());
		}
		
		// set the action
		mp = this.map.get(this.ACTS);
		if(mp == null){
			log.err(cn,"getIndexes: action key not retreived from the map!");
			return null;
		}
		// if action specified, select it, if not, select the action 0 (arbitrary)
		if(a!= null)
			mp.setValTo(a.getName());
		else
			mp.setIndex(0);
		
		int[] indexes = new int[m.getDim()];
		int i = 0;
	
		// get Set of keys contained in Map using Set keySet() method of LinkedHashMap class
		Set<String> st = map.keySet();
		// iterate through the Set of keys, ordered from the first to last one added
		Iterator<String> iterator = st.iterator();
		
		// for all sorted nodes in the map, get the value of index
		while (iterator.hasNext()){
			mp = map.get(iterator.next());
			//indexes[i++] = map.get(iterator.next()).getIndex();
			indexes[i++] = mp.getIndex();	
		}
		return indexes;
	}
	
	/**
	 * add one dimension to the matrix
	 * (action lists are not allowed to be added from the definition of Q-matrix)
	 * @param var - variable to be added 
	 */
	public synchronized void addAction(Action a){
		
		ValToIndexMapping ac = this.map.get(this.ACTS);
		
		if(ac.containsValName(a.getName())){
			log.err(cn, "addAction: action '"+a.getName()+"' already in the map, ignoring");
			return;
		}
		log.pl(LEV, "gna is before ac.add: "+this.getNumActions());
		ac.add(a.getName());
		log.pl(LEV, "gna is after ac.add: "+this.getNumActions());
		
		log.pl(LEV, "action named "+a.getName()+" added to the matrix");
		
		int[] sz = m.getDimensionSizes();
		log.pl(LEV, "readed dim sizes are: "+log.getIds(sz)+ " gna is "+this.getNumActions());
		if((sz[0]) != this.getNumActions()){
			log.pl(LEV,"xxx NNNum actions is: "+this.getNumActions()+" BUT IS: "+(sz[0]+1));
			sz[0] = this.getNumActions()-1;
		
			for(int i=1; i<sz.length; i++)
				if(sz[i] > 0)
					sz[i] = sz[i] -1;
			
			m.setZeroIndexDimension(sz);
			
			log.pl(LEV,"\t\t\t\t so the actual SITUATION is: "+this.vsv());
			
		}
		this.indicateMatrixChange();
	}
	
	private String vsv(){
		String out = "";
		int[] ids = this.m.getDimensionSizes();
		for(int i=0; i<ids.length; i++)
			out = out+" "+ids[i];
		return out;
	}
	
	/**
	 * add one dimension to the matrix
	 * (action lists are not allowed to be added from the definition of Q-matrix)
	 * @param var - variable to be added 
	 */
	public synchronized void addVariable(Variable var){
		if(map.containsKey(var.getName())){
			log.err(cn, "addVariable: this variable is already contained in the matrix: "+
					var.getName());
			return;
		}
		
		// create new mapping of MAP(value->index) and add it to mapping name->MAP
		this.map.put(var.getName(), new ValToIndexMapping(var.getName(), var.vals) );
		
		//System.out.println("addin var with num values: "+var.getNumValues());
		m.addDimension(var.getNumValues());
		this.indicateMatrixChange();
		
		log.pl(LEV, "variable named "+var.getName()+" added to the matrix");
	}
	// numatctions
	/**
	 * delete one dimension from the matrix
	 * (action list cannot be deleted..) 
	 * @param var - variable to be deleted
	 */
	public synchronized void deleteVariable(Variable var){
		
		int index;
		
		if((index = this.variableNameToIndex(var.getName())) == -1){
			log.err(cn, "delete StateVariable: variable not found in the map!" );
			return;
		}
		m.deleteDimension(index);
		map.remove(var.getName());
		this.indicateMatrixChange();
		
		log.pl(LEV,"dimension number: "+index+" (var called: "+var.getName()+" ) deleted!");
	}
 
	/**
	 * removes the specified value of the given variable from the matrix
	 * note: addValue does not exist, values are added automatically by the method set()..
	 * note: maybe it should exist... TODO
	 * @param name - name of the variable
	 * @param value - name of the value
	 */
	public synchronized void removeAction(String name){
		
		ValToIndexMapping mp = this.variableNameToValMap(this.ACTS);
		
		if(mp.size() ==1){
			log.err(cn, "will not delete the last remaining action in the action set!");
			return;
		}
		if(!mp.containsValName(name)){
			log.err(cn, "removeOneValue: this action : "+name+" not found in the map for actions");
			return;
		}
		// get the index in the map (number of row(column) in the matrix of this value
		int pivot = mp.getIndex(name);
		
		log.pl(LEV,"REMOVING num action by getnumactions is: "+this.getNumActions()+
				" and dim sizes: "+log.getIds(this.m.getDimensionSizes()));
		
		this.m.removeIndexOfAction(pivot);
		
		// for all indexes after the pivot, decrease them by 1
		mp.removeFromTheMap(pivot);
		
		log.pl(LEV,"REMOVING num action by getnumactions is: "+this.getNumActions()+" II"+
				" and dim sizes: "+log.getIds(this.m.getDimensionSizes()));
				
		this.indicateMatrixChange();
		
		log.pl(LEV, " this: \""+name+"\" action has been deleted");
	}
	
	public void removeAction(Action a){
		this.removeAction(a.getName());
	}
	
	protected void pi(int[] indexes){
		System.out.println(" generated indexes are: ");
		for(int i=0; i<indexes.length; i++){
			System.out.print(indexes[i]+" ");			
		}
		System.out.println(" ");
	}
	
	/**
	 * converts the variable names to String array (for JComboBoxes)
	 * @return
	 */
	public synchronized String[] varNamesToArray(){
		
		String[] keys = new String[this.map.size()];
		keys = (String[])( this.map.keySet().toArray( keys ) );
		
		return keys;
		
	}
	
	/**
	 * convert variable name to index of dimension in the matrix
	 * @param name - name of variable to address
	 * @return - number of dimension that is addressed in the matrix class
	 */
	public synchronized int variableNameToIndex(String name){
		
		if(!map.containsKey(name)){
			log.err(cn, "variableNameToIndex: this name: "+name+" not in the map, returning -1");
			return -1;
		}
		
		//log.pl(LEV, "variableNameToIndex "+name );
		
		synchronized(map){
			Set<String> st = map.keySet();
			Iterator<String> iterator = st.iterator();
			int i=0;
			String s;
			// for all sorted nodes in the map, get the name, if is equal, return index
			while (iterator.hasNext()){
				s = map.get(iterator.next()).getName();
			//	log.pl(2*LEV,"sarching: "+i+" "+s+" ");
				//if( map.get(iterator.next()).getName().equalsIgnoreCase(name))
				if( s.equalsIgnoreCase(name))
					return i;
				i++;
			}
		}
		log.err(cn,"variableNameToIndex: some big problem, found in the map, but not by the iterator ");
		return -1;
	}
	
	public synchronized ValToIndexMapping variableNameToValMap(String name){
		
		if(!map.containsKey(name)){
			log.err(cn, "variableNameToValMap: this name: "+name+" not in the map, returning null");
			return null;
		}
		
		//log.pl(LEV, "variableNameToValMap "+name );
		
		synchronized(map){
			Set<String> st = map.keySet();
			Iterator<String> iterator = st.iterator();
			int i=0;
			
			// for all sorted nodes in the map, get the name, if is equal, return index
			while (iterator.hasNext()){
				ValToIndexMapping m = map.get(iterator.next());
				
				if( m.getName().equalsIgnoreCase(name))
					return m;
				i++;
			}
		}
		log.err(cn,"variableNameToValMap: some big problem, found in the map, but not by the iterator ");
		return null;
	}
	
	/**
	 * return the name of variable 
	 * @param index - index (dimension) of variable in the matrix 
	 * @return - name of variable
	 */
	public synchronized String getVarName(int index){
		String[] keys = new String[this.map.size()];
		keys = (String[])( this.map.keySet().toArray( keys ) );
		return keys[index];
	}
	
	
	/**
	 * specify variable name and name of value, get its index
	 * @param varName - name of variable
	 * @param value - name of value
	 * @return - index in the matrix
	 */
	public synchronized int valueNameToIndex(String varName, String value){
		if(!this.map.containsKey(varName)){
			log.err(cn,"valueNameToIndex: varName not contained in the map!");
			return -1;
		}
		if(!this.map.get(varName).containsValName(value)){
			log.err(cn, "valueNameToIndex: this value name not found ");
			return -1;
		}
		return this.map.get(varName).getIndex(value);
	}
	
	/**
	 * specify variable name and index in the matrix, get name of value
	 * @param varName - name of variable
	 * @param index - index in the matrix
	 * @return - name of value
	 */
	public synchronized String valueIndexToName(String varName, int index){
		if(!this.map.containsKey(varName)){
			log.err(cn,"valueIndexToName: varName not contained in the map!");
			return null;
		}
		if(!this.map.get(varName).containsIndex(index)){
			log.err(cn, "valueNameToIndex: this value name not found ");
			return null;
		}
		return (String)this.map.get(varName).getValName(index);
	}
	
		
	
}
