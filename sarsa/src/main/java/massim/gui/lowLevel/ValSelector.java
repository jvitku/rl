package massim.gui.lowLevel;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * GUI value selector for selecting the values for dimensions of Q(s,a) matrix that are not displayed
 * 
 * @author jardavitku
 */
public class ValSelector {
	
	public final JComboBox box;
	public final JLabel label;
	public final String varName;
	
	public ValSelector(String varName, String[] values, ActionListener l){
		
		this.varName = varName;
		
		box= new JComboBox(values);
		box.setEditable(false);
		box.setSelectedIndex(0);
		box.setMaximumRowCount(20);
		box.addActionListener(l);
		
		label = new JLabel(varName+":");
	}
	
	public void removeActionListener(ActionListener l){ box.removeActionListener(l); }

}
