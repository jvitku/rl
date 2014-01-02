package massim.gui.lowLevel;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class BorderedJPanel{
	
	/**
	 * this just creates border... 
	 * @return Container (JPanel) with border
	 */
	public static JPanel create(String label, int width, int height){

	    //Border loweredbevel = BorderFactory.createLoweredBevelBorder();
	    
	    Border other = BorderFactory.createTitledBorder(label);
	    TitledBorder titled = BorderFactory.createTitledBorder( other );  // , "other parameter name");
	    
	    titled.setTitleJustification(TitledBorder.DEFAULT_JUSTIFICATION);
	    titled.setTitlePosition(TitledBorder.ABOVE_TOP);
	    
	    JPanel comp = new JPanel();
	    comp.setPreferredSize(new Dimension(width,height));
	    
	    comp.setBorder(titled);
	    comp.setLayout(new BoxLayout(comp,BoxLayout.PAGE_AXIS));
	    
		return comp;
	}
	
	public static JPanel create(JPanel comp, String label, int width, int height){

	    //Border loweredbevel = BorderFactory.createLoweredBevelBorder();
	    
	    Border other = BorderFactory.createTitledBorder(label);
	    TitledBorder titled = BorderFactory.createTitledBorder( other );  // , "other parameter name");
	    
	    titled.setTitleJustification(TitledBorder.DEFAULT_JUSTIFICATION);
	    titled.setTitlePosition(TitledBorder.ABOVE_TOP);
	    
	    comp.setPreferredSize(new Dimension(width,height));
	    
	    comp.setBorder(titled);
	    comp.setLayout(new BoxLayout(comp,BoxLayout.PAGE_AXIS));
	    
		return comp;
	}
	
}
