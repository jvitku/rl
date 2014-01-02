package massim.agent.mind.harm.components.predictors.eligibility;

/**
 * represent indexes for the given matrix
 * 
 * @author jardavitku
 *
 */
public class Inds {
	
	private int[] indexes;
	private int step;
	
	public Inds(){
		this.step = -1;
		indexes = new int[0];
	}
	
	public Inds(int[] inds){
		this.set(inds);
		this.step = -1;
	}

	public Inds(int[] inds, int step){
		this.set(inds);
		this.step = step;
	}
	
	/**
	 * step is ignored
	 * @param to - compare TO
	 * @return - true if indexes are the same
	 */
	public boolean equals(Inds to){
		if(to.get().length != this.indexes.length)
			return false;
		
		for(int i=0; i<to.get().length; i++)
			if(to.get()[i] != this.indexes[i])
				return false;
		return true;
	}
	
	public void setStep(int step){
		this.step = step;
	}
	
	public int step(){
		return this.step;
	}
	
	public int[] get(){ 
		return this.indexes;
	}
	
	public void set(int[] inds){
		this.indexes = this.cloneArr(inds);
	}
	
	private int[] cloneArr(int[] src){
		
		int[] out = new int[src.length];
		for(int i=0; i<src.length; i++)
			out[i]= src[i];
		
		return out;
	}
	public int size(){ return indexes.length; }
	
	public Inds clone(){
		return new Inds(this.indexes, this.step);
	}
	
	public String toString(){
		String out = "{";
		for(int i=0; i<indexes.length; i++)
			if(i==0)
				out = out + indexes[i];
			else
				out = out + "; "+indexes[i];
		out = out+"}";
		return out;
	}
}
