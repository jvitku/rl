package massim.gui;


import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import massim.agent.mind.harm.HarmSystem;
import massim.gui.subwindows.QMatrixVisualizer;
import massim.gui.subwindows.WorldLabelDescription;
import massim.shared.SharedData;

public class GUIDecisionSpace {
	
	
	private final JFrame frame;
	private final Container cc;

	private final HarmSystem harm;
	private final WorldLabelDescription descriptWindow;
	private final QMatrixVisualizer qv;
	private final SharedData shared;
	
	public GUIDecisionSpace(WorldLabelDescription descriptWindow, HarmSystem harm, SharedData shared){

		this.shared = shared;
		this.harm = harm;
		this.descriptWindow = descriptWindow;
		this.qv = new QMatrixVisualizer(descriptWindow, harm, shared);
		
        // initialize the main container
		cc = new Container();
		cc.setLayout(new BoxLayout(cc,BoxLayout.X_AXIS));
		
        cc.add(descriptWindow.getMainPanel());
        cc.add(qv.getMainPanel());
        
        // add to the window
        frame = new JFrame("Selected Decision Space");
        
        frame.setPreferredSize(new Dimension(1000,500));
        frame.add(cc);
		frame.setVisible(true);
		frame.pack();
		
	}
	
	public void updateData(){
		this.descriptWindow.writeSpaceStats();
		this.qv.writeSpaceStats();
	}

}

