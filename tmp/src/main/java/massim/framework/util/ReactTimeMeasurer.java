package massim.framework.util;

/**
 * this is able to measure the time that was needed to execute 
 * the processRequestAction method
 * 
 *  
 * @author jardavitku
 *
 */
public class ReactTimeMeasurer {
	
	private long prev;
	private long started;
	
	public ReactTimeMeasurer(){
		this.prev = 0;
		this.started = 0;
	}
	
	public void start(){
		started = System.currentTimeMillis();		
	}
	
	public void end(){
		prev = System.currentTimeMillis() - this.started;
		//System.out.println("Current time to execute is here : "+prev);
		
	}
	
	public long getPrev(){ return this.prev; }
	
	public long getTimeToThisPoint(){ return (System.currentTimeMillis() - this.started); }

}
