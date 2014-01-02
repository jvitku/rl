package massim.agent.mind.harm.components.qmatrix;

import java.util.Arrays;
import java.util.Vector;


public class QMatrix<E>/*<Integer>*/ {

	protected Vector<QMatrix<Integer>> subMtr;
	protected Vector<Integer> vals;
	protected int dim;
	
	/**
	 * do not use this from the outside (zero dimensions should be initialized
	 * @param p_dim
	 */
	protected QMatrix(int p_dim){
		dim = p_dim;
		
	    if(p_dim == 1){
	    	vals = new Vector<Integer>();
	    }
	    else{
	    	subMtr = new Vector<QMatrix<Integer>>();
	    }
	}
	
	/**
	 * use this
	 * @param sizes - array of dimension sizes
	 */
	public QMatrix(int[] sizes){
		this.dim = sizes.length;
		
		/*
		for(int i=0; i<sizes.length; i++)
			sizes[i] = sizes[i]-1;
		*/
		if(this.dim == 1){
			vals = new Vector<Integer>(sizes[0]);
			vals.setSize(sizes[0]);
		}
		else{
			subMtr = new Vector<QMatrix<Integer>>();
			this.setZeroIndexDimension(sizes);
		}
	}
	
	
	/**
	 * this actually does not work, because I cannot clone the generic Element
	 */
	public QMatrix<Integer> clone(){
		
		int[] sizes = this.getDimensionSizes();
		
		int[] inds = new int[sizes.length];
		for(int i=0; i<inds.length; i++)
			inds[i] = 0;
		
		// allocate the new empty matrix.. 
		QMatrix<Integer> out = new QMatrix<Integer>(sizes);
		
		// search it using DFS and copy all initialized vectors (values or just initialized zero dim)
		this.dfsClone(out, sizes, inds, 0);
		
		return out;		
	}
	
	/**
	 * 
	 * @param m - output Matrix
	 * @param sizes - array of original matrix sizes
	 * @param inds - indexes specifying where we just now are
	 * @param where - actual depth in the matrix (DFS)
	 */
	private void dfsClone(QMatrix<Integer> m, int[] sizes, int[] inds, int where){
		
		// all indexes set up?
		if(where == inds.length){
			
			Integer value = this.get(inds);
			if( value != null)
				m.set(inds, value );
			
			return;
		}
		
		for(int i=0; i<sizes[where]; i++){
			inds[where] = i;
			this.dfsClone(m, sizes, inds, where+1);
		}
	}
	
	public int getDim(){ return this.dim; }
	
	public Integer get(int i[]) {
		if(i.length != dim){
			System.err.println("Matrix: get: delka pole neodpovida dimenzi");
	    }
	    if(dim == 1){
	    	if(i[0] >= vals.size()){
	    		return null;
	    	}
	    	return (Integer)vals.get(i[0]);
	    }else{
	    	// we have got the theoretical size of matrix, but this vector does not have to be initialized
	    	if(i[0] >= subMtr.size())
	    		return null;
	    	if(subMtr.get(i[0]) == null)
	    		return null;
	    	return (Integer)((QMatrix<Integer>)subMtr.get(i[0])).get(Arrays.copyOfRange(i, 1, i.length));
	    }
	}
	
	/**
	 * return the last dimension of the Matrix (array of actions is supposed to be here)
	 * @param i - entire array of indexes indexing even the last dimension (arbitrary first value)
	 * @return - vector representing the last dimension of the Matrix == Vector of action values in this state!!
	 */
	public Vector<Integer> getActionValsInThisState(int inds[]){
		Vector<Integer> out = new Vector<Integer>();
		int size;
		if(this.dim == 1)
			size = this.vals.size();
		else
			size = this.subMtr.size();
		
		out.setSize(size);
		
		for(int i=0; i<size; i++){
			inds[0] = i;
			out.set(i,this.get(inds));
		}
		
		return out;
	}
	
	/**
	 * removes the given value from the Matrix
	 * "value" is considered as a particular action from the action set
	 * the action set is considered to be in the dimension 0 !
	 * 
	 * @param which - index of the action to be removed
	 */
	public void removeIndexOfAction(int which){
		if(this.dim==1){///new + dole
			if(which<0 || which>=this.vals.size()){
				System.err.println("FinalQMatrix: removeIndexOfAction: index not accepted: "+which
						+" size of first dimension is: "+this.vals.size());
				return;
			}	
		}
		
		if(which<0 || which>=this.subMtr.size()){
			System.err.println("FinalQMatrix: removeIndexOfAction: index not accepted: "+which
					+" size of first dimension is: "+this.subMtr.size());
			return;
		}
		// have to reinitialize the dimension sizes?
		if(which == 0){
			// sizes have to be reinitialized all with value decreased by 1
			int[] sizes = this.getDimensionSizes();
			for(int i=0; i<sizes.length; i++)
				sizes[i] = sizes[i]-1;
			// decrease the dimension 0 by 1 (again:)
			sizes[0] = sizes[0]-1;
			
			// remove the zeros element
			if(this.dim==1)
				this.vals.remove(which);
			else
				this.subMtr.remove(which);
			
			// reinitialize sizes of the vectors (because vector 0 in the dim 0 is now different)
			this.setZeroIndexDimension(sizes);
			
		}else{
			if(this.dim ==1)
				this.vals.remove(which);
			else
				this.subMtr.remove(which);
		}
	}
	
	public void checkDimensions(){
		System.out.println("\n\n ooooooooooooooooooooooooooooooooooooooo  ");
		int[] sizes = this.getDimensionSizes();
		int[] i = new int[sizes.length];
		this.pi(sizes);
		
		
		this.dfsCcheck(i, sizes, 0);
		
		System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeend \n\n ");
		
	}
	
	public void dfsCcheck(int i[] , int[] sizes, int depth) {
		if(i.length != dim){
			System.err.println("Matrix: get: delka pole neodpovida dimenzi");
	    }
	    if(dim == 1){
	    	if(vals != null)
	    		System.out.println("depth: "+depth+" size: "+sizes[depth]+" == "+vals.size());
	    	else
	    		System.out.println("vals is null");
	    }else{
	    	if(subMtr == null)
	    		System.out.println("subMtr is null");
	    	else{
	    		System.out.println("depth: "+depth+" size: "+sizes[depth]+" == "+subMtr.size());
	    		
	    		for(int j = 0; j<subMtr.size(); j++){
			    	if(subMtr.get(j) == null)
			    		System.out.println("vector: "+j+" is null");
			    	else{
			    		//i[1] = j;
			    		System.out.println("entering th depth +1");	
			    		((QMatrix<Integer>)subMtr.get(j)).dfsCcheck(Arrays.copyOfRange(i, 1, i.length), sizes, depth+1);
			    	}
	    		}
	    	}
	    }
	}
	
	public void set(int[]i, Integer val){
		// set all necessary dimensions sizes with the index 0 (that is update the matrix theoretical size)
		this.setZeroIndexDimension(i);
		// set the value
		this.privateSet(i, val);
	}
	
	/**
	 * do not use this externally, use set(..)
	 * @param i - vector of indexes
	 * @param val - value to set
	 */
	private void privateSet(int []i, Integer val){
		if(i.length != dim){
			System.err.println("Matrix: privateSet: delka pole neodpovida dimenzi");
	    }
	    if(dim == 1){
	    	// if the last vector is not long enough, resize it and add the value
	    	if(i[0] >= vals.size()){
	    		vals.setSize(i[0] + 1);
	    	}
	    	vals.set(i[0], val);
	    }
	    else{
	    	if(i[0] >= subMtr.size()){
	    		// resize the actual vector
	    		subMtr.setSize(i[0] + 1);
	    	}
	    	// if not initialized, initialize it (Matrix with lover dimension than this has) and continue
	    	if(subMtr.get(i[0]) == null){
	    		subMtr.set(i[0], new QMatrix<Integer>(dim -1)); 
	    	}
	    	// set the value of deeper Matrix
	    	subMtr.get(i[0]).privateSet(Arrays.copyOfRange(i, 1, i.length), val);
	    }
	}
	
	/**
	 * this initializes the zeros dimensions, but do not sets any value
	 * it is just for measuring sizes of Matrix sizes
	 * @param i - array of indexes
	 */
	public void setZeroIndexDimension(int i[]){
		if(i.length != dim){
			System.err.println("Matrix: setZeroIndexDimension: delka pole neodpovida dimenzi");
	    }
	    if(dim == 1){
	    	// if the last vector is not long enough, resize it and we are done
	    	if(i[0] >= vals.size()){
	    		vals.setSize(i[0] + 1);
	    	}
	    }
	    else{
	    	if(i[0] >= subMtr.size()){
	    		// resize the actual vector
	    		subMtr.setSize(i[0] + 1);
	    	}
	    	// if not initialized, initialize the zeros index and go further
	    	if(subMtr.get(0) == null){
	    		subMtr.set(0, new QMatrix<Integer>(dim -1));
	    	}
	    	// set the value of deeper Matrix
	    	subMtr.get(0).setZeroIndexDimension(Arrays.copyOfRange(i, 1, i.length));
	    }
	}
	
	/**
	 * returns the array of dimension sizes
	 * the deal is that when initializing any dimension, the vectors with coordinates {0,0,0,..}
	 * are also initialized
	 * 
	 * @return - array of dimension sizes (that means the biggest one found here)
	 */
	public int[] getDimensionSizes(){
		if(dim == 1)
	    	return new int[]{vals.size()};
		
		return this.appendArr(subMtr.size(), subMtr.get(0).getDimensionSizes());
	}
	
	/**
	 * if the indexes are out of bounds, the even length of vector is incorrect, we will know 
	 * @param inds - indexes to index in the matrix
	 * @return - true if OK, false if problem
	 */
	public boolean checkDimensionSizes(int[] inds){
		int[] sizes = this.getDimensionSizes();
		if(sizes.length!=inds.length){
			System.err.println("FinalQMatrix: checkDimensionSizes: incorrect num of variables!!!!!!!!!!  "+
					" dimension sizes are: ["+this.dimSizes(sizes)+"] , but you are indexing with: ["+
					this.dimSizes(inds)+"]");
			return false;
		}
		//for(int i=0; i<inds.length; i++){
			if(inds[0] >= sizes[0]){
				System.err.println("FinalQMatrix: checkDimensionSizes: incorrect indexing! "+
						" dimension sizes are: ["+this.dimSizes(sizes)+"] , but you are indexing with: ["+
						this.dimSizes(inds)+ "]   ..(problem with actions!! )");
				return false;
			}
		//}
		/*
		System.out.println("FinalQMatrix: checkDimensionSizes: dimension sizes are: ["+this.dimSizes(sizes)+
				"] , and you are indexing with: ["+ this.dimSizes(inds)+ "] ");
				*/
		return true;	
	}
	
	private String dimSizes(int[] inds){
		String out = "";
		for(int i=0; i<inds.length; i++)
			out = out+" "+inds[i];
		return out;
	}
	
	private int[] appendArr(int a, int[] b){
		int[] out = new int[1+b.length];
		out[0] = a;
		for(int i=0; i<b.length; i++)
			out[i+1] = b[i];
		return out;
	}

	
	/**
	 * add one dimension to the end 
	 * @param varName - name of the variable to be added 
	 * @param initSize - initial size of new dimension 
	 */
	public void addDimension(int initSize){
		
		this.dfsAddAndCopyValues(initSize); // clone old data across the new dimension
		//  this.dfsAdd(initSize);			// add dimension and delete all data
	
		// update the matrix size
		int[] sizes = new int[this.dim];
		for(int i=0;i<this.dim; i++)
			sizes[i] = 0;
		sizes[sizes.length-1] = initSize-1;
		
		this.setZeroIndexDimension(sizes);
	}
	

	/**
	 * go to the bottom, for all values, clone them to the all indexes of new dimension
	 * the new dimension is added to the bottom  
	 * 
	 * @param initSize - initial size of the last dimension
	 */
	private void dfsAddAndCopyValues(int initSize){

		// initialized vector of values found?
		if(this.dim == 1){

			// create Vector of Matrixes instead of it
			this.subMtr = new Vector<QMatrix<Integer>>(this.vals.size());
			this.subMtr.setSize(this.vals.size());
			
			// clone all values to all new Vectors of values (in the new child Matrix) 
			for(int i=0; i<this.subMtr.size(); i++){
				// if the value has been initialized
				if(this.vals.get(i) != null){
					// init the subMatrix of dimension 1
					this.subMtr.set(i, new QMatrix<Integer>(new int[]{initSize}));
					for(int j=0; j<initSize; j++){
						// for all values of its Vals vector, set them to the same value
						this.subMtr.get(i).vals.set(j, this.vals.get(i));
					}
				}
			}
			// increase the dimension by 1
			this.dim++;
			// vector of values is not necessary anymore
			this.vals = null;	
			return;
		}
		
		// find all initialized Vectors of values 
		for(int i=0; i<subMtr.size(); i++){
			
			if(this.subMtr.get(i) != null){
				subMtr.get(i).dfsAddAndCopyValues(initSize);
			}
		}
		// on the return from DFS, increase dimension of each matrix.. huh
		this.dim++;
	}
	
	/**
	 * this simply increases the dimension of matrix by adding the new dimension to the bottom
	 * all the old data are deleted
	 * @param initSize - initial size of new dimension
	 */
	private void dfsAdd(int initSize){

		// initialized vector of values found?
		if(this.dim == 1){

			// create Vector of Matrixes instead of it
			this.subMtr = new Vector<QMatrix<Integer>>(this.vals.size());
			this.subMtr.setSize(this.vals.size());
			
			// increase the dimension by 1
			this.dim++;
			// vector of values is not necessary anymore
			this.vals = null;	
			return;
		}
		
		// find all initialized Vectors of values 
		for(int i=0; i<subMtr.size(); i++){
			
			if(this.subMtr.get(i) != null){
				subMtr.get(i).dfsAdd(initSize);
			}
		}
		// on the return from DFS, increase dimension of each matrix.. huh
		this.dim++;
	}
	
	/**
	 * this should delete the given dimension
	 * @param which - number of dimension to delete (from 0 to this.dim-1)
	 */
	public void deleteDimension(int which){
		
		if(dim==1){
			System.err.println("Matrix: deleteDimension: will not delelte the last dimension, ignoring");
		}else if(which > this.dim){
			System.err.println("Matrix: deleteDimension: dimension number is bigger than num dims");
			return;
		}else if(which <0){
			System.err.println("Matrix: deleteDimension: dimension number is under the zero: "+which);
			return;
		// deal with the zeros dimension 
		}else if(which ==0){
			System.out.println("FinalQMatrix: deleteDImension: will not delete the dimension 0 = action set");
				return;
		}else{
			int[] sizes = this.getDimensionSizes();
			//System.out.println("\n\nNENTERING THE DELETE DIMENSION METHOD, deleting this: "+which+" , sizes are:");
			//this.pi(sizes);
			
			//this.dfsGoToDelete(0,which);
			this.dfsGoToDeleteAndAverage(0,which, sizes);
		}
		/*
		System.out.println("========================== deleted! \n\n\n\n\n");
		int[]sizes = this.getDimensionSizes();
		this.pi(sizes);
		*/
	}
	
	/**
	 * this is able to delete dimension selected by its index
	 * here the dimension is counted from the top (max dimension) to the bottom 
	 * dimension 0 means the oldest one (probably for the action set)
	 * 
	 * @param actual - actual depth (called with 0)
	 * @param which - which one (starting from zero) to delete
	 */
	private void dfsGoToDelete(int actual, int which){
		
		// parent of desired dimension reached?
		if((actual+1) == which){
			// deal just with the vals vector? (deleting the next dimension means that this is the last) 
			if(this.dim == 2 ){
				vals = new Vector<Integer>();
				vals.setSize(subMtr.size());
				subMtr = null;
				dim--;
				return;
			}else{
				/*
				int sz = subMtr.size();
				System.out.println("-----------xxxx----------- dim is: "+dim+
						" and subMtr size is: "+sz);
						*/
				// for all nodes of the vector
				for(int i=0; i<subMtr.size(); i++){
					//System.out.println("and we are here: "+i+" and is null: "+(subMtr.get(i)==null)+" dim: "+dim);
					// get the child of the actual node, append its child to the parent
					if(subMtr.get(i) != null){
						QMatrix<Integer> child = subMtr.get(i);
						
						if(child.subMtr.get(0) != null){
							subMtr.set(i, child.subMtr.get(0));
						}else{
							subMtr.set(i,null);
						}
					}
				}
				dim--;
			}
		}else{
			// go down for all possible sub-vectors until the desired dimension is reached 
			for(int i=0; i<this.subMtr.size(); i++){
				if(subMtr.get(i) != null)
					subMtr.get(i).dfsGoToDelete(actual+1,which);
			}
			// decrease all dimensions on the path to here..
			dim--;
		}
	}
	
	private void dfsGoToDeleteAndAverage(int actual, int which, int[] sizes){

		//System.out.println("e------------------------- entering gotodelete: actual: "+actual+" dim sizes are: ");
		// this.pi(sizes);
		
		// parent of desired dimension reached?
		if((actual+1) == which){
			
			// deal just with the vals vector? (deleting the next dimension means that this is the last) 
			if(this.dim == 2 ){
				
				//System.out.println("SUMMING - deleting THE LAST DIMENSION- THIS IS OK!");
				vals = new Vector<Integer>();
				vals.setSize(subMtr.size());
				
				boolean found;
				double sum;
				// for all nodes in the subMtr
				for(int i=0; i<sizes[sizes.length-2]; i++){
					sum = 0;
					found = false;
					
					for(int j=0; j<sizes[sizes.length-1]; j++){
						try{
							sum = sum + this.subMtr.get(i).vals.get(j);
							found = true;
						}catch(Exception e){
							
						}
					}
					// ignore if no data found in the entire vector
					if(found){
				//		System.out.println("SUMING this: "+sum+" poradi: "+i);
						sum = Math.round(sum / sizes[sizes.length-1]);
						
					//	System.out.println("SUMMING: "+sum+" size of the last vec  is: "+sizes[sizes.length-1]);
						this.vals.set(i, (int)sum);
					}else{
			//			System.out.println("!Summing...... not "+i);
					}
				}
				subMtr = null;
				dim--;
				//System.out.println("--------------------------- exiting!!");

				return;
			}else{
				/*
				System.out.println("PARENT HAS BEEN FOUND, WILL CALL sumTheseIII, depth: "+actual);
				System.out.println("SIZES control: "+subMtr.size()+" == "+sizes[actual]);
				*/
				// for all nodes of the parent vector
				for(int i=0; i<subMtr.size(); i++){
					// get the child of the actual node, append its child to the parent
					if(subMtr.get(i) != null){
						QMatrix<Integer> child = subMtr.get(i);
						/*
						System.out.println("SIZES CONTROL: "+i+"  "+subMtr.get(i).subMtr.size()+" == "+sizes[actual+1]);
						System.out.println("PARENT OF DIM TO DELETE FOUND! NUM IS: "+actual+"  ..calling with "+(actual+1)
								+"  len of vector List is: "+child.subMtr.size());
						*/
						
						this.sumThese(child.subMtr, sizes, actual+1);   
				
						subMtr.set(i, child.subMtr.get(0));

					}
				}
				dim--;
			}
		}else{
			// go down for all possible sub-vectors until the desired dimension is reached 
			for(int i=0; i<this.subMtr.size(); i++){
				if(subMtr.get(i) != null)
					subMtr.get(i).dfsGoToDeleteAndAverage(actual+1,which,/*Arrays.copyOfRange(sizes, 1, sizes.length)*/sizes);
			}
			// decrease all dimensions on the path to here..
			dim--;
		}
		//System.out.println("--------------------------- exiting!!");
	}
	

	private void sumThese(Vector<QMatrix<Integer>> list, int[] sizes, int depth){
		
		//System.out.println("\t\t\tEntering this depth: "+depth);

		// max depth? sum it
		if(depth+1 == sizes.length-1){
			this.sumLastDim(list, sizes, depth+1, sizes[depth]);
			return;
		}
		
		Vector<QMatrix<Integer>> out;	// vector of data to be summed 
		boolean found;					// some not null vector found? if not,do not continue!

		//System.out.println("OK, WE ARE HERE, num of vectors to sum is: "+sizes[depth+1]+" and list size is: "
			//	+list.size());
		
		// create this number of lists
		for(int i=0; i<sizes[depth+1]; i++){
			out = new Vector<QMatrix<Integer>>();
			out.setSize(list.size());
			//System.out.println("initializing the new list with size: "+sizes[depth+1]);
			found = false;
			
			// initialize the out data 
			for(int j=0; j<out.size(); j++){
				//System.out.println("actual dimension is BTW: "+list.get(j).dim);
				try{
					out.set(j, list.get(j).subMtr.get(i));
					found = true;
				
				}catch(Exception e){
					//System.out.println("null..");
				}
			}
			if(found){
				//System.out.println("uaaaaaaa, something found, going to depth: "+(depth+1));
				this.sumThese(out,sizes,depth+1);
			}else{
				//System.out.println("Nothing found, discarded, continue");
			}
		}
	}
	
	private void sumLastDim(Vector<QMatrix<Integer>> list, int[] sizes, int depth, int listLen){

		boolean found;
		double sum;
		
		// for all values in the vector list(0)
		if(depth != sizes.length-1)
			System.err.println("FinalQMatrix: sumLastDim: shpould sum last dim, but the depth is incorrect !!");
		
		for(int i=0; i<sizes[depth]; i++){
			
			found = false;
			sum = 0;
			
			// for all vectors in the list (get the value on the position i and sum it)
			for(int j=0; j<listLen/*list.size()*/; j++){ //// just this list (one vector in m.) is 5 instead of 3!!
				
				try{
					//System.out.println("dim on the botom is: "+list.get(j).dim+" and j: "+j+" and i: "+i+" found: ");
					//System.out.println((Integer)list.get(j).vals.get(i));
					sum = sum + (Integer)list.get(j).vals.get(i);
					found = true;
				}catch(Exception e){
				//	System.out.println("null");
				}
			}
			if(!found){
				//System.out.println("nothing found in the val vectors, continue");
				continue;
			}
			
			//System.out.println("something found, sum is now: "+sum+" and lis tsize is: "+list.size());
			sum = Math.round(sum / listLen);
			if(list.get(0) == null || list.get(0).vals == null){
				list.set(0, new QMatrix<Integer>(new int[]{sizes[sizes.length-2]}));
				list.setSize(sizes[sizes.length-2]);
			}
			//System.out.println("setting to size: "+(sizes[sizes.length-1])+" ------------ this sum:"+sum);
			//System.out.println("getting list.get: "+list.get(0).toString());
			//System.out.println("getting:list.get.vals "+list.get(0).vals.toString());
			//System.out.println("getting:list.get.vals.get(i) where i is: "+i+" "+list.get(0).vals.get(i).intValue());
			if(list.size()==0)
				System.out.println("error, empty list");
			
			if(i>= list.get(0).vals.size())
				list.get(0).vals.add((int)sum);
			else if(list.get(0).vals.get(i)==null)
				list.get(0).vals.add(i, (int)sum);
			else
				list.get(0).vals.set(i, (int)sum);
		}
	}


	private void pi(int[] indexes){
		System.out.print("indexes are: {");
		for(int i=0; i<indexes.length; i++){
			System.out.print(indexes[i]+", ");			
		}
		System.out.println("}");
	}
}



