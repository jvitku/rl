package massim.gui.subwindows;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

public interface MyButtonedPanelInterface {

		
		
		// returns the main panel to be added in the windos
		public JPanel getMainPanel();
		
		public void sizeChangedTo(Dimension size);

		public JRadioButton getRadioButton();

	
}
