package massim.gui.subwindows;

import java.util.ArrayList;

import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.PrimitiveAction;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;
import massim.framework.util.MyLogger;

public class QMatrixTest {
	

	/**
	 * get array for indexing the matrix
	 * @param names - array of variable names
	 * @param vals - array of variable values
	 * @return - array for indexing the matrix
	 */
	public static String[][] getNamesVals(String[] names, String[] vals){
		if(names.length != vals.length){
			System.err.println(" getNamesVals: arrays must have the same length!");
			return null;
		}
		String[][] par = new String[names.length][names.length];
		for(int i=0; i<names.length; i++){
			par[i][0] = names[i];
			par[i][1] = vals[i];
		}
		return par;
	}
	


public static boolean testSizesA(QSAMatrix m, VariableList val, ActionList al, int divider, int poc){
	
	boolean front = false;
	String[][] par;
	
		if((divider %8 ==0) && (poc<40)){
			System.out.println("\nAaaaaaaaaddding this: addedVar"+poc);
    		
	    	Variable z = new Variable("addedVar"+poc, poc);
	    	val.add(z);
	    	z.addValue(poc+1);
	    	z.addValue(poc+2);
	    	z.addValue(poc+3);
	    	/*
			for(int i=0; i<poc; i++){
				//System.out.println("ading this nmber? "+(poc-i));
				z.addValue(poc-i);
			}
			*/
			m.addVariable(z);
			
  //  		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"123","down","8","-960"});
//    		m.set(par, 7);
			
			return true;
    	}
    	if(poc>41 && (divider%8)==0){
    		if(front){
	    		if(val.size() > 0){
	    			/*
		    		System.out.println("\nDelete variable from the end!! Its num is:"+(val.size()));
		    		System.out.println("deleting this variable: "+val.get(val.size()-1).getName()+" "
		    				+"and its num is "+(val.size()));
		    		
		    		
		    		System.out.println("situation before: ");
		    		String names[] = m.varNamesToArray();
		            pi(names);
		    		*/
		    		m.deleteVariable(val.get(val.size()-1));
		    		//this.space.getQMatrix().deleteVariable(vars.get(vars.size()-1));
		    		
		    		val.remove(val.size()-1);
		    		//vars.remove(vars.size()-1);
		    	
	    		}
    		}else{
    			if(val.size() > 0){
    				/*
		    		System.out.println("\nDelete variable from the start!! Its num is:"+(1));
		    		System.out.println("deleting this variable: "+val.get(0).getName()+" "
		    				+"and its num is "+1);
		    		
		    		System.out.println("situation before: ");
		    		String names[] = m.varNamesToArray();
		            pi(names);
		    		*/
		    		m.deleteVariable(val.get(1));
		    		//this.space.getQMatrix().deleteVariable(vars.get(vars.size()-1));
		    		
		    		val.remove(1);
		    		//vars.remove(vars.size()-1);
	    		
	    		}
    		}
    		return true;
    	}
    	return false;
	}
	

	public static boolean testSizesB(QSAMatrix m, VariableList val, 
			ActionList al, int divider, int poc){
	
		boolean front = true;
		String[][] par;
	
		
		if(poc==15){
			System.out.println("\nAaaaaaaaaddding 2 variables: addedVar"+poc);
    		
			
			
			
	    	Variable z = new Variable("addedVar_A", 10);
	    	z.addValue(11);
	    	z.addValue(12);
	    	val.add(z);
	    	m.addVariable(z);
	    	
	    	Variable x = new Variable("addedVar_B", 17);
	    	x.addValue(18);
	    	x.addValue(19);
	    	x.addValue(20);
	    	val.add(x);
	    	m.addVariable(x);
	    	
	    	
	    	par = getNamesVals(
    				new String[]{"xPos",m.ACTS, "addedVar_A", "addedVar_B"}, 
    				new String[]{"0","eating", "12","19"});
    		m.set(par, 1129);
    		/*
    		System.out.println("x_ some variables added");
    		m.m.checkDimensions();
    		*/
    		
    		
	    	/*
			for(int i=0; i<poc; i++){
				//System.out.println("ading this nmber? "+(poc-i));
				z.addValue(poc-i);
			}
			*/
			
			
  //  		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"123","down","8","-960"});
//    		m.set(par, 7);
			
			return true;
		}
		if(poc>16 && (divider%8)==0){
    		if(front){
	    		if(val.size() > 0){
		    		System.out.println("\nDelete variable from the end!! Its num is:"+(val.size()));
		    		System.out.println("deleting this variable: "+val.get(val.size()-1).getName()+" "
		    				+"and its num is "+(val.size()));
		    		
		    		System.out.println("situation before: ");
		    		String names[] = m.varNamesToArray();
		            pi(names);
		    		
		    		m.deleteVariable(val.get(val.size()-1));
		    		//this.space.getQMatrix().deleteVariable(vars.get(vars.size()-1));
		    		
		    		val.remove(val.size()-1);
		    		//vars.remove(vars.size()-1);
		    	
		    		/*
		    		System.out.println("x_ some var removed");
		    		m.m.checkDimensions();
		    		*/
	    		}
    		}else{
    			if(val.size() > 0){
		    		System.out.println("\nDelete variable from the start!! Its num is:"+(1));
		    		System.out.println("deleting this variable: "+val.get(0).getName()+" "
		    				+"and its num is "+1);
		    		
		    		System.out.println("situation before: ");
		    		String names[] = m.varNamesToArray();
		            pi(names);
		    		
		            if(val.size() >1){
		            	m.deleteVariable(val.get(1));
		            	val.remove(1);
		            }else{
		            		m.deleteVariable(val.get(0));
		            		val.remove(0);
		            }
		            /*
		            System.out.println("x_ some var removed");
		    		m.m.checkDimensions();
		    		*/
		            /*
		            par = getNamesVals(
		    				new String[]{"xPos",m.ACTS, "addedVar_B"}, 
		    				new String[]{"0","eating", "19"});
		            try{
		    		m.set(par, 1129);
		            }catch(Exception e){}
		    		
		    		*/
	    		}
    		}
    		return true;
		}
    	return false;
	}
	
	

	public static boolean testSizesC(QSAMatrix m, VariableList val, 
			ActionList al, int divider, int poc){
	
		boolean front = true;
		String[][] par;
	
		
		if(poc==15){
			System.out.println("\nAaaaaaaaaddding 2 variables: addedVar"+poc);
    		
			
	    	Variable z = new Variable("addedVar_A", 10);
	    	z.addValue(11);
	    	z.addValue(12);
	    	val.add(z);
	    	m.addVariable(z);
	    	
	    	Variable x = new Variable("addedVar_B", 17);
	    	x.addValue(18);
	    	x.addValue(19);
	    	x.addValue(20);
	    	val.add(x);
	    	m.addVariable(x);
	    	
	    	
	    	par = getNamesVals(
    				new String[]{"xPos",m.ACTS, "addedVar_A", "addedVar_B"}, 
    				new String[]{"0","eating", "12","19"});
    		m.set(par, 1129);
    		/*
    		System.out.println("x_ some variables added");
    		m.m.checkDimensions();
    		*/
    		
    		
	    	/*
			for(int i=0; i<poc; i++){
				//System.out.println("ading this nmber? "+(poc-i));
				z.addValue(poc-i);
			}
			*/
			
			
  //  		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"123","down","8","-960"});
//    		m.set(par, 7);
			
			return true;
		}
		
		// deletion of some action
		if(poc ==30 ){
			String actionName;
			
			actionName = "up";
			System.out.println("removing this action! "+actionName);
			m.removeAction(actionName);
						
			
		}
		if(poc ==35){
			String actionName = "drinking";
			System.out.println("removing this action! "+actionName);
			m.removeAction(actionName);
			
		}
		if(poc==40){
			String actionName = "down";
			System.out.println("removing this action! "+actionName);
			m.removeAction(actionName);
			
		}
		if(poc == 45){
			String actionName = "right";
			System.out.println("removing this action! "+actionName);
			m.removeAction(actionName);
			
		}
		if(poc == 50){
			String actionName = "left";
			System.out.println("removing this action! "+actionName);
			m.removeAction(actionName);
			
		}
		if(poc == 55){
			String actionName = "eating";
			System.out.println("removing this action! "+actionName);
			m.removeAction(actionName);
			
		}
		
		
		if(poc>60 && (divider%8)==0){
    		if(front){
	    		if(val.size() > 0){
	    			/*
		    		System.out.println("\nDelete variable from the end!! Its num is:"+(val.size()));
		    		System.out.println("deleting this variable: "+val.get(val.size()-1).getName()+" "
		    				+"and its num is "+(val.size()));
		    		
		    		System.out.println("situation before: ");
		    		String names[] = m.varNamesToArray();
		            pi(names);
		    		*/
		    		m.deleteVariable(val.get(val.size()-1));
		    		//this.space.getQMatrix().deleteVariable(vars.get(vars.size()-1));
		    		
		    		val.remove(val.size()-1);
		    		//vars.remove(vars.size()-1);
		    	
		    		/*
		    		System.out.println("x_ some var removed");
		    		m.m.checkDimensions();
		    		*/
	    		}
    		}else{
    			if(val.size() > 0){
    				/*
		    		System.out.println("\nDelete variable from the start!! Its num is:"+(1));
		    		System.out.println("deleting this variable: "+val.get(0).getName()+" "
		    				+"and its num is "+1);
		    		
		    		System.out.println("situation before: ");
		    		String names[] = m.varNamesToArray();
		            pi(names);
		    		*/
		            if(val.size() >1){
		            	m.deleteVariable(val.get(1));
		            	val.remove(1);
		            }else{
		            		m.deleteVariable(val.get(0));
		            		val.remove(0);
		            }
		            /*
		            System.out.println("x_ some var removed");
		    		m.m.checkDimensions();
		    		*/
		            /*
		            par = getNamesVals(
		    				new String[]{"xPos",m.ACTS, "addedVar_B"}, 
		    				new String[]{"0","eating", "19"});
		            try{
		    		m.set(par, 1129);
		            }catch(Exception e){}
		    		
		    		*/
	    		}
    		}
    		return true;
		}
    	return false;
	}
	
	
	
	
public static QSAMatrix setupTestA( MyLogger log, VariableList val, ActionList al, 
		int divider, int poc){
		
		PrimitiveAction up = new PrimitiveAction("up");
		PrimitiveAction down = new PrimitiveAction("down");
		PrimitiveAction eat = new PrimitiveAction("eating");
		PrimitiveAction left = new PrimitiveAction("left");
		PrimitiveAction right = new PrimitiveAction("right");
		PrimitiveAction drink = new PrimitiveAction("drinking");
		//ActionList al = new ActionList();
		al.add(up);
		al.add(down);
		al.add(eat);
		al.add(left);
		al.add(right);
		al.add(drink);
		
		// generated indexes are
		// create variable(s)
		Variable y = new Variable("yPos",10);
		y.addValue(12);
		y.addValue(8);
		y.addValue(3);
		y.addValue(13);
		y.addValue(18);
		
		
		Variable x = new Variable("xPos", 0);
		x.addValue(2);
		x.addValue(123);
		x.addValue(1);
		x.addValue(32);
		x.addValue(-6);
		x.addValue(-16);
		
		Variable z = new Variable("zPos", 0);
		z.addValue(2);
		z.addValue(7);
		z.addValue(1);
		z.addValue(777);
		z.addValue(11);
		z.addValue(14);
		z.addValue(114);
		z.addValue(0);  ////////////////////////////////? TODO
		z.addValue(-960);
		
		//VariableList val = new VariableList();
		
		
		
		//val.add(y);
		val.add(x);
		//val.add(z);

/*
		System.out.print("TOLIK: ");
		System.out.print(" "+y.getNumValues());
		System.out.print(" "+x.getNumValues());
		System.out.print(" "+z.getNumValues());
		System.out.println(" "+al.size());*/
		// Aaaaaaaaaddding 2 varia
		
		// create the mappings
		
		//PrintableMatrix m = new PrintableMatrix(val, al, log);
		QSAMatrix m = new QSAMatrix(null, al, log);
		
		/*
		System.out.println("x_ matrix inited!");
		m.m.checkDimensions();
		*/
		
		//m.addVariable(y);
		m.addVariable(x);
		//m.addVariable(z);
		
		/*
		System.out.println("x_ variable added");
		m.m.checkDimensions();
		*/
		
		String [][] par;
		
		//////// setting the last vector ou jeeeee
		
		
		
	/*
		par = getNamesVals(new String[]{m.ACTS,"yPos"}, new String[]{"down","12"});
		par = getNamesVals(new String[]{m.ACTS,"yPos"}, new String[]{"eating","12"});
		*/
		/*
		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"0","eating","12","7"});
		m.set(par, 18);
		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"123","down","8","11"});
		m.set(par, 19);
		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"123","down","8","-960"});
		m.set(par, 7);
*/

		par = getNamesVals(new String[]{"xPos",m.ACTS}, new String[]{"0","eating"});
		m.set(par, 18);
		par = getNamesVals(new String[]{"xPos",m.ACTS}, new String[]{"123","down"});
		m.set(par, 19);
		par = getNamesVals(new String[]{"xPos",m.ACTS}, new String[]{"123","down"});
		m.set(par, 7);
		
		par = getNamesVals(	new String[]{"xPos",m.ACTS}, 
							new String[]{"14","eating"});
		m.set(par, 1);
		/*
		System.out.println("x_ some values added");
		m.m.checkDimensions();
		*/
		//this.harm.root.qmatrix = m;
		
		return m;
	}


public static QSAMatrix setupTestB( MyLogger log, VariableList val, ActionList al, 
		int divider, int poc){
		
		PrimitiveAction up = new PrimitiveAction("up");
		PrimitiveAction down = new PrimitiveAction("down");
		PrimitiveAction eat = new PrimitiveAction("eating");
		
		//ActionList al = new ActionList();
		//al.add(up);
		al.add(down);
		al.add(eat);
		// generated indexes are
		// create variable(s)
		Variable y = new Variable("yPos",10);
		y.addValue(12);
		y.addValue(8);
		y.addValue(3);
		y.addValue(13);
		y.addValue(18);
		
		
		Variable x = new Variable("xPos", 0);
		x.addValue(2);
		x.addValue(123);
		x.addValue(1);
		x.addValue(32);
		x.addValue(-6);
		x.addValue(-16);
		
		Variable z = new Variable("zPos", 0);
		z.addValue(2);
		z.addValue(7);
		z.addValue(1);
		z.addValue(777);
		z.addValue(11);
		z.addValue(14);
		z.addValue(114);
		z.addValue(0);  ////////////////////////////////? TODO
		z.addValue(-960);
		
		//VariableList val = new VariableList();
		
		
		
		val.add(y);
		val.add(x);
		val.add(z);

/*
		System.out.print("TOLIK: ");
		System.out.print(" "+y.getNumValues());
		System.out.print(" "+x.getNumValues());
		System.out.print(" "+z.getNumValues());
		System.out.println(" "+al.size());
	*/	
		
		// create the mappings
//		PrintableMatrix m = this.harm.root.qmatrix;
		
		QSAMatrix m = new QSAMatrix(val, al, log);
		
		String [][] par;
		
		//////// setting the last vector ou jeeeee
		
		
		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"0","eating","12","7"});
		m.set(par, 18);
	/*
		par = getNamesVals(new String[]{m.ACTS,"yPos"}, new String[]{"down","12"});
		par = getNamesVals(new String[]{m.ACTS,"yPos"}, new String[]{"eating","12"});
		*/
		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"123","down","8","11"});
		m.set(par, 19);
		
		par = getNamesVals(new String[]{"xPos",m.ACTS,"yPos","zPos"}, new String[]{"123","down","8","-960"});
		m.set(par, 7);
		//this.harm.root.qmatrix = m;
		
		return m;
	}



protected static void pi(String[] indexes){
	System.out.println(" generated names areeeeeeeee: ");
	for(int i=0; i<indexes.length; i++){
		System.out.print(indexes[i]+" ");			
	}
	System.out.println(" ");
}

protected static void pi(int[] indexes){
	System.out.println(" generated indexes areeeeeeeee: ");
	for(int i=0; i<indexes.length; i++){
		System.out.print(indexes[i]+" ");			
	}
	System.out.println(" ");
}

protected static void printNames(String[][] x){
	
	System.out.println("---------------------");
	for(int i=0; i<x.length; i++){
		System.out.println(x[i][0]+" \t"+x[i][1]);
	}
}

	

}
