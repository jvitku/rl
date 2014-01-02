package massim.agent.mind.harm.components.qmatrix;

/**
 * 
 * @author jardavitku, thanks to algoritmy.net
 *
 */
public class Sorter {

	
	/**
	 * sorts the given int[] array
	 * @param array - array to be sorted
	 * @return - int[][] array, array[0][:] - original positions; array[1][:] - sorted values  
	 */
	public static int[][] sortNotUsed(int[] array){
		
		// init the array to be sorted 
		int[][] ar = new int[2][array.length];
		ar[0] = new int[array.length];
		ar[1] = new int[array.length];
		// store the original order
		for(int i=0; i<array.length; i++){
			ar[0][i] = i;
			ar[1][i] = array[i];
		}
		
		quicksort(ar,0,ar[0].length);
		
		return ar;
	}
	
	
	/**
	 * sort array and return positions of values 
	 * @param array - array of values
	 * @return - array of positions (indexes where it was in the array before sorting)
	 */
	public static int[] sort(int[] array){
		
		// init the array of positions
		int[] inds = new int[array.length];
		for(int i=0; i<array.length; i++){
			inds[i] = i;
		}

		quicksort(array,inds,0,array.length);
	
		return inds;
	}
	
	
	/**
	 * just returns the same array as sorting fcn
	 * @param array
	 * @return
	 */
	public static int[][] doNotSort(int[] array){
		// init the array to be sorted 
		int[][] ar = new int[2][array.length];
		ar[0] = new int[array.length];
		ar[1] = new int[array.length];
		// store the original order
		for(int i=0; i<array.length; i++){
			ar[0][i] = i;
			ar[1][i] = array[i];
		}
		return ar;
	}
	

	
	/**
	 * Quicksort - rychle razeni (pivotem je prvni prvek), radi od nejnizsiho prvku
	 * @param array pole k serazeni
	 * @param left index prvniho prvku, na ktery muzeme sahnout (leva mez (vcetne))
	 * @param right index prvniho prvku, na ktery nemuzeme sahnout (prava mez (bez))
	 */
	private static void quicksort(int[] array, int[] inds, int left, int right){
	    if(left < right){ 
	        int boundary = left;
	        for(int i = left + 1; i < right; i++){ 
	            if(array[i] < array[left]){ 
	                swap(array, inds, i, ++boundary);
	            }
	        }
	        swap(array, inds, left, boundary);
	        quicksort(array, inds, left, boundary);
	        quicksort(array, inds, boundary + 1, right);
	    }     
	}

	/**
	 * Prohodi prvky v zadanem poli
	 * @param array pole
	 * @param left prvek 1
	 * @param right prvek 2
	 */
	private static void swap(int[] array, int[] inds, int left, int right){
	    int tmp = array[right]; 
	    int tmp2 = inds[right];
	    array[right] = array[left];
	    inds[right] = inds[left];
	    array[left] = tmp;
	    inds[left] = tmp2;
	}
	
	
	/**
	 * Quicksort - rychle razeni (pivotem je prvni prvek), radi od nejnizsiho prvku
	 * @param array pole k serazeni
	 * @param left index prvniho prvku, na ktery muzeme sahnout (leva mez (vcetne))
	 * @param right index prvniho prvku, na ktery nemuzeme sahnout (prava mez (bez))
	 */
	private static void quicksort(int[][] array, int left, int right){
	    if(left < right){ 
	        int boundary = left;
	        for(int i = left + 1; i < right; i++){ 
	            if(array[1][i] < array[1][left]){ 
	                swap(array, i, ++boundary);
	            }
	        }
	        swap(array, left, boundary);
	        quicksort(array, left, boundary);
	        quicksort(array, boundary + 1, right);
	    }     
	}

	/**
	 * Prohodi prvky v zadanem poli
	 * @param array pole
	 * @param left prvek 1
	 * @param right prvek 2
	 */
	private static void swap(int[][] array, int left, int right){
	    int tmp = array[1][right]; 
	    int tmp2 = array[0][right];
	    array[0][right] = array[0][left];
	    array[1][right] = array[1][left];
	    array[1][left] = tmp;
	    array[0][left] = tmp2;
	}
}
