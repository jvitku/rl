package massim.agent.mind.harm;

import java.util.ArrayList;

public class Queue {
	
	ArrayList<Integer> q;
	public int numActions = 6;
	
	public Queue(){
		q = new ArrayList<Integer>();
	}

	private void add(int a){ q.add(new Integer(a)); }
	
	private void delete(){
		if(q.size() >= this.numActions)
				q.remove(0);
	}
	
	public void step(int a){
		if(!this.contains(a))
			add(a);
		delete();
	}
	
	public ArrayList<Integer> getActual(){ return this.q; }
	
	public int size(){ return q.size(); }
	
	private boolean contains(int a){
		if(q.isEmpty())
			return false;
		for(int i=0; i<q.size(); i++)
			if(q.get(i) == a)
				return true;
		return false;
	}
	
	public int get(int no){ return q.get(no); }
}
