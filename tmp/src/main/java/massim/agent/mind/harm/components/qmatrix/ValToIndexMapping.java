package massim.agent.mind.harm.components.qmatrix;
import java.util.ArrayList;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import massim.agent.mind.harm.actions.Action;
import massim.agent.mind.harm.variables.Value;


/**
 * this class maps values of variables (specified by String value) onto array indexes
 * 
 * 	the problem was that we can have integer or String values of variable, and we can have
 * 	negative values of indexes, or values between i.e. <20587;20665>
 * 
 * 	so each value of variable is passed as a String and using HashMap mapped onto integer value
 * 	that specifies index in the array, so the values of variables can be arbitrary, but indexes 
 * 	are always starting from 0 
 * 
 * 	one single problem will be only in displaying such matrix, e.g. in 2D, when X values were discovered
 * 	in the following order: <5 6 4 7 2 1 0>, their indexes in the matrix will still be: <0 1 2 3 4 5 6>
 *    
 * --------
 * 
 * so if the value is in the map, return value, if not, add new mapping from val to new index
 * 
 * it does not support removing the specified value (this should not be necessary)
 * 
 * @author jardavitku
 *
 */
public class ValToIndexMapping {
	
	private int maxIndex = 0;
	
	private int actualIndex = -1;	// the actual index set to be read
	
	//private BidiMap map;
	private MyBidiMap map;
	
	private boolean hasIntVals = true;
	
	//public HashMap <String, Integer> map;
	private final String varName;
	
	public ValToIndexMapping(String varName){
		this.varName = varName;

		// map = new HashMap<String,Integer>();
		
		//map = new DualHashBidiMap();
		map = new MyBidiMap();
	}
	
	public ValToIndexMapping(String varName, String initVal){
		this.varName = varName;
		//map = new HashMap<String,Integer>(1);
		//map = new DualHashBidiMap();
		map = new MyBidiMap();
		
		this.updateIntVals(initVal);
		map.put(initVal, maxIndex++);
	}
	
	public ValToIndexMapping(String varName, String[] initVals){
		this.varName = varName;
		//map = new HashMap<String,Integer>(initVals.length);
		
		//map = new DualHashBidiMap();
		map = new MyBidiMap();
		
		for(int i=0; i<initVals.length; i++){
			this.updateIntVals(initVals[i]);
			map.put(initVals[i], maxIndex++);
		}
	}

	@SuppressWarnings("rawtypes")
	public ValToIndexMapping(String varName, ArrayList initVals, boolean actions){
		this.varName = varName;
		
		// if it is variable
		// check whether is convertible to int (store it) and add to map
		if(!actions){
			//map = new DualHashBidiMap();
			map = new MyBidiMap();
			
			for(int i=0; i<initVals.size(); i++){
				if(! ((Value)initVals.get(i)).isInt())
					this.hasIntVals = false;
				map.put(((Value)initVals.get(i)).getStringVal(), maxIndex++);
			}
		}
		// if it is action
		else if(actions){
			//map = new DualHashBidiMap();
			map = new MyBidiMap();

			for(int i=0; i<initVals.size(); i++){
				this.updateIntVals(((Action)initVals.get(i)).getName());
				/*
				System.out.println("putting this value: "+((Action)initVals.get(i)).getName()
				+" to this index "+maxIndex);
				*/
				map.put(((Action)initVals.get(i)).getName(), maxIndex++);
			}
		}
		// anything other is not supported
		else
			System.err.println("ValToIndexMapping: C: the second argument not supported");
	}
	
	@SuppressWarnings("rawtypes")
	public ValToIndexMapping(String varName, ArrayList initVals){
		this.varName = varName;
		
		// if it is variable
		// check whether is convertible to int (store it) and add to map
		if(initVals.get(0) instanceof Value){
			//map = new DualHashBidiMap();
			map = new MyBidiMap();
			
			for(int i=0; i<initVals.size(); i++){
				if(! ((Value)initVals.get(i)).isInt())
					this.hasIntVals = false;
				map.put(((Value)initVals.get(i)).getStringVal(), maxIndex++);
			}
		}
		// if it is action
		else if(initVals.get(0) instanceof Action){
			//map = new DualHashBidiMap();
			map = new MyBidiMap();

			for(int i=0; i<initVals.size(); i++){
				this.updateIntVals(((Action)initVals.get(i)).getName());
				/*
				System.out.println("putting this value: "+((Action)initVals.get(i)).getName()
				+" to this index "+maxIndex);
				*/
				map.put(((Action)initVals.get(i)).getName(), maxIndex++);
			}
		}
		// anything other is not supported
		else
			System.err.println("ValToIndexMapping: C: the second argument not supported");
	}
	
	private void updateIntVals(String name){
		if(!this.isInt(name))
			this.hasIntVals = false;
	}
	
	public String printAllValuesInMap(){
		String out = "";
		for(int i=0; i<this.map.size(); i++){
//			out = out +" "+map.get(key)
		}
		return out;
	}
	
	/**
	 * whether all values of this variable are convertible to int
	 * @return
	 */
	public synchronized boolean convertableToInt(){ return this.hasIntVals; }
	
	/**
	 * whether the given string is convertible to int
	 * @param val - string representing int?
	 * @return - if it is int
	 */
	private boolean isInt(String val){
		try{
			Integer.parseInt(val);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	
	public synchronized int maxIndexStored(){ return this.maxIndex-1; }
	public synchronized int size(){
		if(map.size()  != this.maxIndex)
		System.out.println("beeeeeeeeeeeeeeeeeeeeeeeeeeee   "+map.size() +" map size: "+this.maxIndex);
		//return this.maxIndex;
		return this.map.size();
		}
	
	public synchronized String getName(){ return this.varName; }
	
	public synchronized boolean containsValName(String val){ 
		return map.containsKey(val);
	}
	
	public synchronized boolean containsIndex(int index){ return map.containsValue(index); }
	
	public synchronized int getIndex(String val) {
		
		if(!map.containsKey(val)){
			System.err.println("StringToIndexMap: get: requested value not in the map: "+val);
			return -1;
		}
		
		return (Integer)map.get(val);
	}

	public synchronized String getValName(int index){
		
		if(!map.containsValue(index)){
			System.err.println("StringToIndexMap: getValName: requested index not in the map: "+index);
			return null;
		}
		return (String)map.getKey(index);
	}
	
	public synchronized void add(String val) {
		
		if(map.containsKey(val))
			System.err.println("StringToIndexMap: add: value to be added is in the map already! "+val);
		
		this.updateIntVals(val);
		
		map.put(val, maxIndex++);
	}
	
	/**
	 * this prepares the mapped index in the tmp variable, 
	 * it can be obtained by the getIndex() method
	 * 
	 * if the value not found in the map, then is added to the mapping (matrix)
	 *  
	 * @param val - String value of the variable
	 */
	public synchronized void setValTo(String val) {
		
		if(!map.containsKey(val))
			this.add(val);
		
		this.actualIndex = (Integer)map.get(val);
	}
	
	public synchronized void setIndex(int what){ this.actualIndex = what; }
	
	public synchronized int getIndex(){ return this.actualIndex; }
	
	/**
	 * some value of variable (concretely one action) has been deleted
	 * all indexes after this variable has to be decreased by 1
	 * 
	 * note: in the map: VALUE is int, KEY is String
	 * and it is map<String, int>()
	 * @param pivot - original index of value in the map 
	 */
	public synchronized void removeFromTheMap(int pivot){
		
		map.remove(pivot);
		
		for(int i=pivot+1; i<=this.maxIndexStored(); i++){
			
			if(!map.containsValue(i))
				System.err.println("ValToIndexMapping: removeFromTheMap: value with this index: " +
						+i+" not found in the map! ");
			
			// decrease index of each value by 1
			String tmp = (String) map.getKey(i);
			map.remove(i);
			map.put(tmp, i-1);
		}
		this.maxIndex --;
	}
}

