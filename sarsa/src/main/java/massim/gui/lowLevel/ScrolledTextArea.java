package massim.gui.lowLevel;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ScrolledTextArea {

	public JTextArea textArea;
	public JScrollPane scroll;
	
	protected final int prefferedXDim = 250;
	protected final int prefferedYDim = 125;
	
	protected final String nl = System.getProperty("line.separator");
	
	public ScrolledTextArea(){
	
		// create some text area
		textArea = new JTextArea();
	    textArea.setEditable(true);
	    
	    // place it to the JScrollPane
	    scroll = new JScrollPane(textArea);
	    scroll.setPreferredSize(new Dimension(prefferedXDim, prefferedYDim));
	    
	    // write something..
	    //textArea.append("ahoj!"+nl+" _"+nl+"ahoj!");
	}

	
	// return the resulting object to append to the window
	public JScrollPane getObject(){ return this.scroll; }
	
	public void write(String what){
		this.textArea.removeAll();
		this.textArea.append(what);
	}
	
	public void append(String what){
		this.textArea.append(this.nl+what);
	}
	
	protected void setEditable(boolean editable){
		this.textArea.setEditable(editable);
	}
	
	
}
