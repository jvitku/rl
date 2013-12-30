package massim.agent.mind.harm.components.qmatrix;



import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
//import org.apache.commons.collections.bidimap.UnmodifiableBidiMap;


public class Demo {


	  public static void main(String args[]) {

		  
		  
	    BidiMap agentToCode = new DualHashBidiMap();
	    
	   // agentToCode.put(new Integer(17), "to chci videt");
	    
	    
	    agentToCode.put("007", "Bond");
	    agentToCode.put("006", "Joe");

	    
	   // agentToCode = UnmodifiableBidiMap.decorate(agentToCode);
	    agentToCode.put("002", "Fairbanks"); // throws Exception
	    agentToCode.remove("007"); // throws Exception
	    agentToCode.removeValue("Bond"); // throws Exception
	    
	    System.out.println("ahoj tohle je klic a pak objekt: "+
	    		agentToCode.get("002")+" "+agentToCode.getKey("Bond"));
	    

	  }

	     
	
}
