package massim.agent.mind.harm.variables;

/**
 * value of variable found in the world
 * it can be String or integer
 * 
 * @author jardavitku
 *
 */
public class Value {
	
	private String val;
	private int ival;
	
	private boolean isInt;
	
	public int usedInStep;	// when this value was used
	
	public Value(String val){
		this.val = val;
		this.isInt = false;
		this.usedInStep = 0;
	}
	
	public Value(int val){
		this.val = Integer.toString(val);
		this.ival = val;
		this.isInt = true;
		this.usedInStep = 0;
	}

	public boolean isInt(){ return this.isInt; }
	
	
	public String getStringVal(){
		return this.val;
	}
	
	public int getIntVal(){
		if(this.isInt)
			return this.ival;
		else{
			System.err.println("class Value:getIntVal() the value is String! returning -1");
			return -1;
		}
	}
	
	public int lastSeen(){ return this.usedInStep; }
	
	
	
}
