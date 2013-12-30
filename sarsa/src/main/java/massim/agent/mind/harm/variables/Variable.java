package massim.agent.mind.harm.variables;

import java.util.ArrayList;

/**
 * this represents variable of surrounding world
 * this can be either variable or constant
 * it holds all values of variable found so far
 * 
 * @author jardavitku
 *
 */
public class Variable {
	
	private String name;
	
	public ArrayList<Value> vals;
	
	private boolean isConstant;
	// the last sim step when this has been constant
	// if it is the actual one, should be constant, if not, should be variable 
	public int constAtStep;		 
	
	public int actual;
	public int previous;
	
	// data structures for the planner
	public int desired;		// index of desired value
	public int temp;		// actual state in the planning engine (DFS)
	
	private int indexFound = -1;
	
	public Variable(String name, String val){
		this.name = name;
		vals = new ArrayList<Value>();
		vals.add(new Value(val));
		this.isConstant = true;
		this.actual = 0;
		this.previous = -1;
	}
	
	public Variable(String name, int val){
		this.name = name;
		vals = new ArrayList<Value>();
		vals.add(new Value(val));
		this.isConstant = true;
		this.actual = 0;
		this.previous = -1;
	}
	
	public int lastSeenAsConst(){ return this.constAtStep; }
	
	/**
	 * tries to add new value to the variable
	 * @param val
	 * @return - true if the new value has been added
	 */
	public boolean addValue(String val){
		if(this.containsString(val)){
			this.previous = this.actual;
			this.actual = this.indexFound;
			return false;
		}
		this.vals.add(new Value(val));
		this.isConstant = false;
		
		// when adding new value, I suppose that it is the actual one
		this.previous = this.actual;
		this.actual = this.vals.size()-1;
		
		return true;
	}
	
	public boolean addValue(int val){
		if(this.containsInt(val)){
			this.previous = this.actual;
			this.actual = this.indexFound;
			return false;
		}
		this.vals.add(new Value(val));
		this.isConstant = false;
		
		// when adding new value, I suppose that it is the actual one
		this.previous = this.actual;
		this.actual = this.vals.size()-1;
		
		return true;
	}
	
	public void setActualValue(String val){
		if(this.containsString(val)){
			this.previous = this.actual;
			this.actual = this.indexFound;
		}
	}
	
	public void setActualValue(int val){
		if(this.containsInt(val)){
			this.previous = this.actual;
			this.actual = this.indexFound;
		}
	}
	
	public String actualValue(){ return vals.get(this.actual).getStringVal(); }
	
	public String previousValue(){ return vals.get(this.previous).getStringVal(); }
	
	public boolean hasChanged(){  return (this.actual != this.previous); }
	
	/**
	 * whether the variable with this index is the actual one
	 * @param i - index of variable in the arrayList
	 * @return - true if it is the actual value 
	 */
	public boolean isActual(int i){ return this.actual == i; }
	public boolean isPrevious(int i){ return this.previous == i; }
	
	public int getNumValues(){ return this.vals.size(); }
	
	public String getValuesToString(){
		String s = new String("");
		
		for(int i=0; i<this.vals.size(); i++)
			s = s+" "+this.vals.get(i).getStringVal();
		
		return s;	
	}
	
	/**
	 * whether the variable is constant (that is, we have found one value so far)
	 * @return
	 */
	public boolean isConstant(){ return this.isConstant; }

	public String getName(){ return this.name; }
	
	// TODO here we should store just Strings really..
	private boolean containsString(String what){
		if(this.vals.isEmpty())
			return false;
		
		for(int i=0; i<this.vals.size(); i++){
				if(this.vals.get(i).getStringVal().equalsIgnoreCase(what)){
					this.indexFound = i;
					return true;
				}
		}
		return false;
	}
	
	private boolean containsInt(int what){
		if(this.vals.isEmpty())
			return false;
		
		String st = Integer.toString(what);
		
		for(int i=0; i<this.vals.size(); i++){
			if(this.vals.get(i).isInt()){
				if(this.vals.get(i).getIntVal() == what){
					this.indexFound = i;
					return true;
				}
			}
			else
				if(this.vals.get(i).getStringVal().equals(st)){
					this.indexFound = i;
					return true;
				}
		}
		return false;
	}
	
	/**
	 * flip the binary value (using the temp variable) to the other value
	 */
	public void flipVal(){
		if(this.vals.size() > 2)
			System.err.println("Variable: flipVal: variable is not binary!! ");
		else{
			if(this.temp == 0)
				this.temp = 1;
			else 
				this.temp = 0;
		}
	}
}
