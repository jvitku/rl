package massim.gui.lowLevel;

import java.awt.event.KeyEvent;

import massim.shared.SharedData;

public class KeyHandler {

	
	
    // to override
    public static String handleKey(KeyEvent e, SharedData b, String[] names){ 

		//super.keyTyped(e);
		char c = e.getKeyChar();
		
		return KeyHandler.handleKeyChar(c, b, names);
    }
    
    public static String handleKeyChar(char c, SharedData b, String[] names){
    	String action;
	
		if(c=='s'){
			action = "skip";
			if(isInNames(action,names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='w'){
			action = "up";
			if(isInNames(action,names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='x'){
			action = "down"; //south taky jde
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='a' || c=='A'){
			action = "left";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='d'){
			action = "right";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='q'){
			action = "northwest";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='e'){
			action = "northeast";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='c'){
			action = "southeast";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='y'){
			action = "southwest";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='k'){
			action = "drink";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='l'){
			action = "eat";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		else if(c=='p'){
			action = "press";
			if(isInNames(action, names)){
				setAction(action,b);
				return action;
			}
			return null;
		}
		return null;
    }
    
	
	private static void setAction(String action, SharedData b){
		b.setAction(action);
		//System.out.println("Action received, ---------------------it is: "+action);
		
	}
	
	private static boolean isInNames(String what, String[] names){
		for(int i=0; i<names.length; i++)
			if(what.equalsIgnoreCase(names[i]))
				return true;
		return false;
	}
	
}
