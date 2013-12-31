package org.hanns.rl.discrete.learningAlgorithm.qlearning.model.impl;

public class Dimension<E>{

	private E val;
	private Dimension<E>[] childs;

	public Dimension(int[] sizes, E val){
		//Dimension(sizes,0,val);
	}
	

	@SuppressWarnings("unchecked")
	public Dimension(int[] sizes, int index, E val){

		System.out.println("--- hi level "+index);
		// if not the last dimension, make array of childs and recurse
		if(index<sizes.length){


			int numChilds = sizes[index];
			System.out.println("------creating this no of childs: "+numChilds);

			childs = new Dimension[numChilds];
			for(int i=0; i<numChilds; i++){
				childs[i] = new Dimension<E>(sizes,index+1,val);
			}
		}else{
			System.out.println("STOP, setting my value to!"+val);
			this.val = val;
		}
	}

	public void setValue(int[] coords, E value){
		this.setVal(coords, 0, value);
	}
	
	private void setVal(int[] coords, int depth, E value){
		// traversing recursively across the coordinates
		if(depth<coords.length){
			System.out.println("rolling deepere "+depth);
			
			this.checkDims(coords, depth);
			childs[coords[depth]].setVal(coords, depth+1, value);
			
		// we are in the place (all coords. applied)
		}else{
			System.out.println("SETTING this value "+value.toString());
			val = value;
		}
	}
	
	public E readValue(int[] coords){
		return this.readValue(coords, 0);
	}
	
	private E readValue(int[] coords, int depth){
		if(depth<coords.length){
			System.out.println("rolling deepere "+depth);
			this.checkDims(coords, depth);
			return (E) childs[coords[depth]].readValue(coords, depth+1);
		}else{
			System.out.println("READING this value "+val.toString());
			return val;
		}
	}
	
	private void checkDims(int [] coords, int depth){
		if(coords[depth]<0)
			System.err.println("Dimension: negative index ");
		if(coords[depth]>=this.childs.length){
			System.err.println("Dimension: index out of range, this one: "+coords[depth]);
		}
	}
}
