package massim.agent.mind.harm.components.qmatrix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * mapping of String -> Integer
 * 
 * it has to be bidirectional, BidiMap does not work (size 3, contains 4 keys)
 * 
 * so I will use normal HashMap, and search in it "bidirectionaly"
 * 
 * 
 * @author jardavitku
 */
public class MyBidiMap {
	
	private HashMap<String, Integer> map;
	
	public MyBidiMap(){
		this.map = new HashMap<String,Integer>();
		
	}

	
	public synchronized void put(String key, Integer value){
		this.map.put(key, value);
	}
	
	
	public synchronized int size(){ return this.map.size(); }
	
	public synchronized boolean containsKey(String key){ return this.map.containsKey(key); }
	
	public synchronized boolean containsValue(int value){
		Iterator it = this.map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			
			if(value == ((Integer)pairs.getValue()).intValue())
				return true;	
		}
		return false;
	}
	
	public synchronized Integer get(String key){ return this.map.get(key); }
	
	public synchronized String getKey(int value){
		Iterator it = this.map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			
			if(value == ((Integer)pairs.getValue()).intValue())
				return (String)pairs.getKey();	
		}
		System.err.println("MyBidiMap: key to this value: "+value+" not found! (giving null)");
		return null;
	}
	
	public synchronized void remove(int value){
		Iterator it = this.map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			
			if(value == ((Integer)pairs.getValue()).intValue()){
				this.map.remove((String)pairs.getKey());
				return;
			}
		}
		System.err.println("MyBidiMap: key to this value: "+value+" not found! (not removing!)");
	}

	
	
	
}
