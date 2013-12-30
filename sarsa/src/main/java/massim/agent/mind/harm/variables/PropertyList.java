package massim.agent.mind.harm.variables;

/**
 * the has two lists, list of constants and variables
 * 
 * @author jardavitku
 *
 */
public class PropertyList {
	
	// all known properties of environment (that is constants and variables)
	// the difference between constant and variable is that the constant has 
	// only one known value so far, if some new second value is found, 
	// this constant is moved from constants list to variables
	private VariableList variables;
	private VariableList constants;

	private int indexFound;
	
	public PropertyList(){
		this.variables = new VariableList();
		this.constants = new VariableList();
		
		this.indexFound = -1;
	}

	public VariableList getVariables(){ return this.variables; }
	public VariableList getConstants(){ return this.constants; }
	

	/**
	 * get total size of search space
	 * @return
	 */
	public long getSpaceSize(){
		
		if(this.variables.getArray().isEmpty())
			return 0;
		
		if(this.variables.size() == 1)
			return this.variables.getArray().get(0).getNumValues(); 
		
		long sum = this.variables.getArray().get(0).getNumValues();
		
		for(int i=1; i<this.variables.size(); i++)
			sum = sum * this.variables.getArray().get(i).getNumValues();
		
		return sum;
	}
	

	/**
	 * get dimension of search space
	 * @return
	 */
	public int getSpaceDimension(){
		return this.variables.size();
	}
	

	/**
	 * this tries to find the variable or constant, if not found, the new constant is added
	 * @param name
	 * @param val
	 */
	public void updateVariable(String name, int val){
		
		// if it is found between variables
		if(this.findVariable(name)){
			// try to add this new value
			this.variables.get(this.indexFound).addValue(val);
			return;
		}
		// if it is already known constant
		else if(this.findConstant(name)){
			// if we have added new value
			if(this.constants.get(this.indexFound).addValue(val)){
				// move it from constants to variables
				this.variables.add(this.constants.get(this.indexFound));
				this.constants.remove(this.indexFound);
			}
			return;
		}
		// the name is unknown, add it between constants
		else{
			this.constants.add(new Variable(name, val));
			return;
		}
	}
	
	public void updateVariable(String name, String val){
		
		// if it is found between variables
		if(this.findVariable(name)){
			// try to add this new value
			this.variables.get(this.indexFound).addValue(val);
			return;
		}
		// if it is already known constant
		else if(this.findConstant(name)){
			// if we have added new value
			if(this.constants.get(this.indexFound).addValue(val)){
				// move it from constants to variables
				this.variables.add(this.constants.get(this.indexFound));
				this.constants.remove(this.indexFound);
			}
			return;
		}
		// the name is unknown, add it between constants
		else{
			this.constants.add(new Variable(name, val));
			return;

		}
	}
	
	public Variable getByName(String name){
		
		// if it is found between variables
		if(this.findVariable(name)){
			// try to add this new value
			return this.variables.get(this.indexFound);
			
		}
		// if it is already known constant
		else if(this.findConstant(name)){
			// if we have added new value
			return this.constants.get(this.indexFound);
		}
		// the name is unknown, add it between constants
		else{
			return null;
		}
	}

	// we could use HashMaps here, but I hope that the complexity will not be so big to complicate this
	/**
	 * try to find the constant by given name
	 * @param name - name of constant (that is nameOfObjectID_parameter)
	 * @return - true if the constant has been found, and index has been stored
	 */
	private boolean findConstant(String name){
		
		if(this.constants.isEmpty())
			return false;
		
		// for all constants
		for(int i=0; i<this.constants.size(); i++){
			// if the name is the same, we have found it
			if(name.equalsIgnoreCase(this.constants.get(i).getName())){
				this.indexFound = i;
				return true;
			}
		}
		return false;
	}
	
	private boolean findVariable(String name){
		
		if(this.variables.isEmpty())
			return false;
		
		// for all variables
		for(int i=0; i<this.variables.size(); i++){
			// if the name is the same, we have found it
			if(name.equalsIgnoreCase(this.variables.get(i).getName())){
				this.indexFound = i;
				return true;
			}
		}
		return false;
	}

	public String varsToBetterString(){
		String out = "-------List of Variables is:  (size: "+this.variables.size()+" )";
		out = out+"\n\ttotal search space dimension is: "+this.getSpaceDimension()+"\n"+
			"\ttotal search space size is: "+this.getSpaceSize();
		
		// for each variable print out their values
		for(int i=0; i<this.variables.size(); i++){
			out = out + "\n" + this.variables.get(i).getName();
			// for all values, write out it
			out = out+"\t| "+this.variables.get(i).getValuesToString();
		}
		return out+"\n";
	}
	
	public String constsToBetterString(){
		String out = "-------List of Constants is:  (size: "+this.constants.size()+" )";
		
		// for each variable print out their values
		for(int i=0; i<this.constants.size(); i++){
			out = out + "\n" + this.constants.get(i).getName();
			// for all values, write out it
				out = out+"\t| "+this.constants.get(i).getValuesToString();
		}
		return out+"\n";
	}
	
	public String varsToString(){
		String out = "List of Variables is: |";
		
		for(int i=0; i<this.variables.size(); i++)
			out = out+this.variables.get(i).getName();
		
		return out+"|";
	}
	
	public String constsToString(){
		String out = "List of Constants is: |";
		
		for(int i=0; i<this.variables.size(); i++)
			out = out+this.variables.get(i).getName();
		
		return out+"|";
	}
	
	
	
}
