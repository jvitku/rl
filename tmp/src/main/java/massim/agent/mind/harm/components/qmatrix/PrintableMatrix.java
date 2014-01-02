package massim.agent.mind.harm.components.qmatrix;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.PhysilogicalAction;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.actions.SomeSpaceWithVariables;
import massim.agent.mind.harm.variables.VariableList;
import massim.framework.util.MyLogger;
import massim.agent.mind.harm.actions.ComplexAction;



/**
 * 
 * representation of Q(s,a) matrix for the (hierarchical) reinforcement learning system
 * 
 * -matrix has universal dimension and sizes of each dimension 
 * -dimensions can be added, removed or resized on the fly
 * -when the dimension is added, all of the old values are cloned to all specified 
 * values of this new dimension (that means when variable with vals {on,of,standby} is added, 
 * the old values are identical in all of three new values of the variable
 * -when the value of specified variable is referenced (by set(..) method), that is not contained
 * in the matrix, the size of particular dimension is increased by 1 
 * -dimension 0 represents the action set, from this dimension values (particular actions) 
 * can be removed and added arbitrary
 * 
 * -matrix is indexed by String values, or (better) by the current representation of the world 
 * (that means ActionList and VariableList) 
 * 
 * -as the variables can be added in arbitrary order, the values n the matrix are not 
 * physically sorted, dimensions for variables that are convertible to String are sorted
 * when the printSorted method is called  
 * 
 * @author jardavitku
 *
 */

public class PrintableMatrix extends ValsToMatrixMapping{

	public PrintableMatrix(VariableList vars, ActionList actions, MyLogger log) {
		super(vars, actions, log);
	}

	private final String W = "50";	// width of table row
	private final String H = "10";	// height
	
	
	private final String exportName = "maxUtility_";
	private final String append = ".txt";
	
	private MyLogger fileExporter;	// export of maximum utility distribution to file
	
	/**
	 * this prints out two selected dimensions (by names of their variables) of the Q(s,a)
	 * the variables are sorted by their values, if the values are convertible to integer 
	 * @param firstName - first dimension variable (Y) 
	 * @param secondName - second dimension variable (X)
	 * @return - HTML code of the resulting table
	 */
	public synchronized String printSorted(String firstName, String secondName){

		int[] inds = new int[m.getDim()];
		for(int i=0; i<inds.length; i++)
			inds[i] = 0;
		
		return this.printSorted(firstName, secondName, inds);
		
	}
	
	
	/**
	 * print max utility (values) to file (for post processing in Matlab)
	 * @param firstName - name of first dimension selected (y)
	 * @param secondName - second (x)
	 * @param inds - selected values for other dimensions
	 * @param space - some space with variables to which this matrix belongs (root or cpx action) 
	 */
	public synchronized void printMaxUtilityToFile(String firstName, String secondName, int[] inds, 
			SomeSpaceWithVariables space){
		
		if(inds.length != super.getDimension()-1){
			System.out.println("If you want to print Maximum Utilities Distribution to file, you have to " +
					"check the checkbox Utility M. on the right side! (and select two main dimensions to print)");
			return;
		}
		String name = this.exportName;
		
    	if(space instanceof RootDecisionSpace)
    		name = name +"ROOT"+this.append;
    	else
    		name = name + ((ComplexAction)space).getName()+this.append;
    	
		System.out.println("printing the maximum utility distribution to file named: "+name);
		
		this.fileExporter = new MyLogger(name);
		this.fileExporter.printToFile(true);
		this.fileExporter.pl(0,firstName+" "+secondName+"  (Names of displayed dimensions: y and x)");
		

		int i=0;
		int first=-1, second=-1;
		String key;
		boolean oneFound = false;	// on value found
		
		// go throw the entire map and find the first and second name, store their dim number
		Set<String> st = map.keySet();
		Iterator<String> iterator = st.iterator();
		
		while (iterator.hasNext()){
			key = iterator.next();
			if(key.equalsIgnoreCase(firstName)){
				first = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			if(key.equalsIgnoreCase(secondName)){
				second = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			i++;
		}	
		if(first==-1 || second == -1){
			log.err(cn,"ValsToMatrixMapping: printMaxUtilityToFile(String,String): var name not found! ");
			if(first==-1)
				log.err(cn,"Var name: "+firstName);
			else
				log.err(cn,"Var name: "+secondName);
			return;
		}
		int[] sizes = m.getDimensionSizes();
		
		// extend the inds vector with the zeros dimension..
		int[] indsTMP = new int[inds.length+1];
		for(int t=0; t<inds.length; t++)
			indsTMP[t+1] = inds[t];
		indsTMP[0] = 0;
		inds = indsTMP;

		int[] firstSorted; 
		int[] secondSorted;
		
		// try to sort x and y axis values
		secondSorted = this.getSortedValueInds(secondName);
		firstSorted = this.getSortedValueInds(firstName);
		
		// get actions map
		//ValToIndexMapping mp = super.map.get(super.ACTS);
		int best;
		
		// here starts print to file
		String out = "";
		
		// for first dimension values
		for(i=0; i<sizes[first]; i++){
			inds[first] = firstSorted[i];
			
			// for the second dimension values
			for(int j=0; j<sizes[second]; j++){
				inds[second] = secondSorted[j];
				// find the best action in the given state
				best = this.getIndexOfBestAction(m.getActionValsInThisState(inds));
				
				if(best == -1)
					out = out + " -1";
				else if(best == -2)
					out = out + " 0";
				else{
					int bestVal = this.getValOfBestAction(m.getActionValsInThisState(inds));
					out = out+" "+bestVal;
				}
			}
			out = out+"\n";
		}
		// export data
		this.fileExporter.pl(0, out);
	}
	
	public synchronized String printSorted(String firstName, String secondName, int[] inds){
		
		
		if(inds.length == super.getDimension()){
		
			if(!this.m.checkDimensionSizes(inds))
				return null;
			return this.printSortedMatrix(firstName, secondName, inds);
		}
		
		else if(inds.length == super.getDimension()-1){
			if(!this.checkDimSizesII(inds))
				return null;
			return this.printSortedUtil(firstName, secondName, inds);
		}
		
		log.err(cn, "printSorted: incorrect length of inds vector!");
		return null;
	}
	
	private boolean checkDimSizesII(int[] inds){
		int[] tmp = new int[inds.length+1];
		tmp[0] = this.getNumActions()-1;
		for(int i=1;i<tmp.length; i++){
			tmp[i] = inds[i-1];
		}
		return this.m.checkDimensionSizes(tmp);
	}
	
	private synchronized String printSortedUtil(String firstName, String secondName, int[] inds){
		
		int i=0;
		int first=-1, second=-1;
		String key;
		boolean oneFound = false;	// on value found
		
		// go throw the entire map and find the first and second name, store their dim number
		Set<String> st = map.keySet();
		Iterator<String> iterator = st.iterator();
		
		while (iterator.hasNext()){
			key = iterator.next();
			if(key.equalsIgnoreCase(firstName)){
				first = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			if(key.equalsIgnoreCase(secondName)){
				second = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			i++;
		}	
		if(first==-1 || second == -1){
			log.err(cn,"ValsToMatrixMapping: printTMPHTML(String,String): var name not found! ");
			if(first==-1)
				log.err(cn,"Var name: "+firstName);
			else
				log.err(cn,"Var name: "+secondName);
			return null;
		}
		int[] sizes = m.getDimensionSizes();
		
		/*
		System.out.println("\t\t\t printing... sizes of first and second are: "
				+sizes[first]+" "+sizes[second]);
		*/
		// extend the inds vector with the zeros dimension..
		int[] indsTMP = new int[inds.length+1];
		for(int t=0; t<inds.length; t++)
			indsTMP[t+1] = inds[t];
		indsTMP[0] = 0;
		inds = indsTMP;

		int[] firstSorted; 
		int[] secondSorted;
		
		// try to sort x and y axis values
		secondSorted = this.getSortedValueInds(secondName);
		firstSorted = this.getSortedValueInds(firstName);
		
		// get actions map
		//ValToIndexMapping mp = super.map.get(super.ACTS);
		int best;
		String actName;
		
		// here starts print to HTML
		String out = "<html><table border=\"1\">";
		//String out = "<html><table border=\"1\" width=\"150\">";
		
		//out = out+"<tr>"+"<th> Q(s,a) </th>";
		out = out +"<tr><th> <img src=\"file:../resources/util.png\" ALT=\"Utility\"> </th>";
		//out = out +"<tr><th> <img src=\"file:../resources/drink.png\" ALT=\"Q(s,a)\"> </th>";
		
		// for all indexes of this variable, print x-header
		for(int j=0; j<sizes[second]; j++){
			out = out + "<th>"+ this.valueIndexToName(secondName, secondSorted[j]) +"</th>";
			//out = out + "<th>"+ this.valueIndexToName(secondName, j) +"</th>";
			
		}
		
		// for first dimension values
		for(i=0; i<sizes[first]; i++){
			inds[first] = firstSorted[i];
			
			out = out+"<tr>";
			// for the second dimension values
			for(int j=0; j<sizes[second]; j++){
				inds[second] = secondSorted[j];
				
				// y header
				if(j==0)
					out = out+"<th>"+ this.valueIndexToName(firstName,firstSorted[i])+"</th>";
					//out = out+"<th>"+ this.valueIndexToName(firstName,i)+"</th>";
				
				// find the best action in the given state
				best = this.getIndexOfBestAction(m.getActionValsInThisState(inds));
				
				if(best == -1)
					out = out + "<td width=\""+W+"\" height=\""+H+"\" ALIGN=\"CENTER\">" + ".."+ "</td>";
				else if(best == -2)
					out = out + "<td width=\""+W+"\" height=\""+H+"\" ALIGN=\"CENTER\">" +
					"<img src=\"file:../resources/zero.png\" ALT=\" 0 \"> </td>";
				else{
					actName = this.valueIndexToName(super.ACTS, best);
					out = out + "<td width=\""+W+"\" height=\""+H+"\" ALIGN=\"CENTER\">" +
					"<img src=\"file:../resources/"+
					actName
					+".png\" ALT=\""+actName+"\"> </td>";
				}
			}
			out = out+"</tr>";
		}		
		return out;
		
	}
	/**
	 * get the index of action which has the maximum value, -1 if all actions hav null values 
	 * @param vals - vector of values (according to order of indexes) from the getActionValsInThisState()
	 * @return - index of the best action or -1 if all have null vals, od -2 if all valueas are zero
	 */
	private int getIndexOfBestAction(Vector<Integer> vals){
		int max = -1;
		int ind = -1;
		
		for(int i=0; i<vals.size(); i++){
			if(vals.get(i) != null)
				if(vals.get(i)>max){
					max = vals.get(i);
					ind = i;
				}
		}
		if(max == -1)
			return -1;
		if(max == 0)
			return -2;
		return ind;
	}
	
	private int getValOfBestAction(Vector<Integer> vals){
		int max = -1;
		
		for(int i=0; i<vals.size(); i++){
			if(vals.get(i) != null)
				if(vals.get(i)>max){
					max = vals.get(i);
				}
		}
		// not found
		if(max == -1)
			return -1;
		return max;
	}
	
	/**
	 * prints out Q-matrix with two selected dimensions (=variables by their name) 
	 * and the rest of variables is set according to the integer array 
	 * @param firstName - name of variable on Y axis
	 * @param secondName - name of dimension (variable) on X axis 
	 * @param inds - array of indexes for other variables (use method this.getIndexes(String[][] s))
	 * @return - HMTL code of the resulting table!
	 */
	private synchronized String printSortedMatrix(String firstName, String secondName, int[] inds){
		int i=0;
		int first=-1, second=-1;
		String key;
		boolean oneFound = false;	// on value found
		// go throw the entire map and find the first and second name, store their dim number
		Set<String> st = map.keySet();
		Iterator<String> iterator = st.iterator();
		while (iterator.hasNext()){
			key = iterator.next();
			if(key.equalsIgnoreCase(firstName)){
				first = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			if(key.equalsIgnoreCase(secondName)){
				second = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			i++;
		}
	
		if(first==-1 || second == -1){
			log.err(cn,"ValsToMatrixMapping: printTMPHTML(String,String): var name not found! ");
			if(first==-1)
				log.err(cn,"Var name: "+firstName);
			else
				log.err(cn,"Var name: "+secondName);
			return null;
		}

		int[] sizes = m.getDimensionSizes();
		
		/*
		System.out.println("Dimension sizes are: ");
		this.pi(sizes);
		*/
		int[] firstSorted; 
		int[] secondSorted;
		
		// try to sort x and y axis values
		secondSorted = this.getSortedValueInds(secondName);
		firstSorted = this.getSortedValueInds(firstName);
		
		// here starts print to HTML
		String out = "<html><table border=\"1\">";
		//String out = "<html><table border=\"1\" width=\"150\">";
		
		//out = out+"<tr>"+"<th> Q(s,a) </th>";
		out = out +"<tr><th> <img src=\"file:../resources/qsa.png\" ALT=\"Q(s,a)\"> </th>";
		//out = out +"<tr><th> <img src=\"file:../resources/drink.png\" ALT=\"Q(s,a)\"> </th>";
		
		// for all indexes of this variable, print x-header
		for(int j=0; j<sizes[second]; j++){
			out = out + "<th>"+ this.valueIndexToName(secondName, secondSorted[j]) +"</th>";
			//out = out + "<th>"+ this.valueIndexToName(secondName, j) +"</th>";
		}
		
		// for first dimension values
		for(i=0; i<sizes[first]; i++){
			inds[first] = firstSorted[i];
			
			out = out+"<tr>";
			// for the second dimension values
			for(int j=0; j<sizes[second]; j++){
				inds[second] = secondSorted[j];
				
				// y header
				if(j==0)
					out = out+"<th>"+ this.valueIndexToName(firstName,firstSorted[i])+"</th>";
					//out = out+"<th>"+ this.valueIndexToName(firstName,i)+"</th>";
				
				Integer el = (Integer)m.get(inds);
				if(el == null)
					out = out + "<td width=\""+W+"\" height=\""+H+"\" ALIGN=\"CENTER\">" + ".."+ "</td>";
				else
					out = out + "<td width=\""+W+"\" height=\""+H+"\" ALIGN=\"CENTER\"> "+el+"</td>";
			}
			out = out+"</tr>";
		}		
		return out;
		
	}
	
	/**
	 * get array of sorted values of variable of given name .. of of
	 * @param name - name of variable
	 * @return - sorted array of variable values
	 */
	public synchronized String[] getSortedValues(String name){
		int[] sortedInds = this.getSortedValueInds(name);
		
		String[] out = new String[sortedInds.length];
		
		for(int i=0; i<sortedInds.length; i++)
			out[i] = this.valueIndexToName(name, sortedInds[i]);
		
		return out;
	}
	
	/**
	 * tries to sort all possible values of variable of given name
	 *   
	 * @param name - name of variable
	 * @return - sorted indexes of values in the matrix
	 */
	public synchronized int[] getSortedValueInds(String name){
		
		// get number of values
		int size = this.map.get(name).size();
		// out
		int[] secondSorted = new int[size];
		
		if(this.map.get(name).convertableToInt()){
		
			// for all values of second variable
			for(int j=0; j<size; j++){
				try{
					secondSorted[j] = Integer.parseInt(this.valueIndexToName(name, j));
					
				}catch(Exception e){
					log.err(cn, "getSortedValueInds: error: cannot convert secondVar vals to int! " +
							"value that cannot be converted is: "+ this.valueIndexToName(name,j));
				}
			}
			secondSorted = Sorter.sort(secondSorted);
		}else{
			for(int j=0; j<size; j++)
				secondSorted[j] = j;
		}
		return secondSorted;
	}

	public synchronized String printNonSorted(String firstName, String secondName){

		int i=0;
		int first=-1, second=-1;
		String key;
		boolean oneFound = false;	// on value found
		// go throw the entire map and find the first and second name, store their dim number
		Set<String> st = map.keySet();
		Iterator<String> iterator = st.iterator();
		while (iterator.hasNext()){
			key = iterator.next();
			if(key.equalsIgnoreCase(firstName)){
				first = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			if(key.equalsIgnoreCase(secondName)){
				second = i;
				if(oneFound)
					break;
				else
					oneFound = true;
			}
			i++;
		}
	
		if(first==-1 || second == -1){
			log.err(cn,"ValsToMatrixMapping: printTMPHTML(String,String): var name not found! ");
			if(first==-1)
				log.err(cn,"Var name: "+firstName);
			else
				log.err(cn,"Var name: "+secondName);
			return null;
		}
		
		int[] inds = new int[m.getDim()];
		for(i=0; i<m.getDim(); i++)
			inds[i] = 0;

		return this.printToHtml(first, second, firstName, secondName, inds);
	}
	
	
	public String printTMPHTML(int firstDim, int secondDim){

		int[] inds = new int[m.getDim()];
		for(int i=0; i<m.getDim(); i++)
			inds[i] = 0;

		String firstName = this.getVarName(firstDim);
		String secondName = this.getVarName(secondDim);
		
		return this.printToHtml(firstDim, secondDim, firstName, secondName, inds);
	}
	
	/**
	 * should be the same as print, but it generates table in HTML language :-)
	 * @return - string to be pasted to some web page or JLabel
	 */
	private String printToHtml(int firstDim, int secondDim,
			String firstName, String secondName, int[] inds){
		String out = "<html><table border=\"1\">";
		
		int[] sizes = m.getDimensionSizes();

		//out = out+"<tr>"+"<th> Q(s,a) </th>";
		out = out +"<tr><th> <img src=\"file:../resources/qsa.png\" ALT=\"Q(s,a)\"> </th>";
		
		// for all indexes of this variable, print x-header
		for(int j=0; j<sizes[secondDim]; j++){
			out = out + "<th>"+ this.valueIndexToName(secondName, j) +"</th>";
		}
		
		// for first dimension values
		for(int i=0; i<sizes[firstDim]; i++){
			inds[firstDim] = i;
			
			out = out+"<tr>";
			// for the second dimension values
			for(int j=0; j<sizes[secondDim]; j++){
				inds[secondDim] = j;
				
				// y header
				if(j==0)
					out = out+"<th>"+ this.valueIndexToName(firstName,i)+"</th>";
				
				Integer el = (Integer)m.get(inds);
				if(el == null)
					out = out + "<td ALIGN=\"CENTER\">" + ".."+ "</td>";
				else
					out = out + "<td ALIGN=\"CENTER\"> "+el+"</td>";
			}
			out = out+"</tr>";
		}		
		return out;
	}
	
	protected void pi(int[] indexes){
		System.out.println(" generated indexes areeeeeeeee: (pm)");
		for(int i=0; i<indexes.length; i++){
			System.out.print(indexes[i]+" ");			
		}
		System.out.println(" ");
	}
	

}
