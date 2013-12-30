package massim.agent.mind.harm.actions.rl;

/**
 * RL memory should be contained in every decision space
 * (which represents some nonprimitive action) except the root decision space
 * 
 * @author jardavitku
 *
 */
public class Memory {
	
	public Object R;
	
	public double gamma;
	
	// TODO how to initialize matrix with general number of dimensions
	// TODO how to unambiguously address each dimension in the matrix
	public Memory(int numDimensions){
		R = new Integer(numDimensions);
		
	}

}
