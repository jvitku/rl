package massim.agent.mind.harm.actions;

import java.util.ArrayList;

public interface Action {

	boolean isPrimitive();
	int getComplexity();
	void setComplexity(int val);

	ArrayList<Action> getChilds();
	void addChild(Action a);
	
	String getName();
	String getID();
//	boolean hasID();
	
	boolean justExecuted();
	void discardExecution();
	void setJustExecuted();
	
	int lastExecuted();
	void setExecutedAt(int when);
	
	// priority (marked as \phi in descriptions of algorithms in text)
	int getPriority();
	void addToPriority(int val);
	void discardPriority();
}
