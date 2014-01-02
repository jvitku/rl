package massim.gui.subwindows;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import massim.agent.mind.harm.HarmSystem;
import massim.gui.lowLevel.BorderedJPanel;
import massim.gui.lowLevel.KeyHandler;
import massim.shared.SharedData;

/**
 * part of GUI for manual control of agent, mainly:
 * -	some simulation information
 * -	buttons play, step, stop..
 * 
 * @author jardavitku
 *
 */
public class ManualControl implements MyPanelInterface, KeyListener, ActionListener{
	
	private final JPanel main;
	private final SharedData shared;
	private final HarmSystem harm;
	private final JLabel generalInfo;
	
	private final JTextArea displayArea;
    private final JTextField typingArea;
    static final String newline = System.getProperty("line.separator");
	
	private final JButton stop, step;
	private final JRadioButton playMode, stepMode, manualMode;
	private final ButtonGroup group;
	private final String cPlay = "playCommand", cStep = "stepCommand", cManual = "manualCommand";  
	private final String cButtonStep = "        step        ", cButtonStop = "stop";
	private final int WIDTH = 250;
	private final int HEIGHT = 600; 
	
	Container cc;
	
	public ManualControl(HarmSystem harm, SharedData shared){
		
		this.shared = shared;
		this.harm = harm;
		
		main = BorderedJPanel.create("Manual control and general info", WIDTH, HEIGHT);
		main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));
		
		// panel with step/stop buttons
		JPanel buttonPanel =  new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		stop = new JButton(cButtonStop);
		stop.addActionListener(this);
		step = new JButton(cButtonStep);
		step.addActionListener(this);
		buttonPanel.add(step);
		buttonPanel.add(stop);
		
		
		// panel with radio buttons: play/step/manual
		JPanel radioButtonPanel = new JPanel();
		radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel,BoxLayout.X_AXIS));
		stepMode = new JRadioButton("step");
		playMode = new JRadioButton("play");
		manualMode = new JRadioButton("manual");
		
		stepMode.addActionListener(this);
		playMode.addActionListener(this);
		manualMode.addActionListener(this);
		
		stepMode.setActionCommand(cStep);
		playMode.setActionCommand(cPlay);
		manualMode.setActionCommand(cManual);
		
		group = new ButtonGroup();
		group.add(stepMode);
		group.add(playMode);
		group.add(manualMode);
		
		radioButtonPanel.add(stepMode);
		radioButtonPanel.add(playMode);
		radioButtonPanel.add(manualMode);
		
		// add agent behavior controls into one panel 
		JPanel p =  BorderedJPanel.create("Agent behavior", WIDTH, 100);
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		p.add(buttonPanel);
		p.add(radioButtonPanel);
		
		
		// panel with keyboard listener and keyboard monitor
		JPanel textAreas = BorderedJPanel.create("Manual control", WIDTH, 90);
		textAreas.setLayout(new BoxLayout(textAreas,BoxLayout.Y_AXIS));
		textAreas.setMaximumSize(new Dimension(WIDTH, 90));
		typingArea = new JTextField("Press:{a,d,w,x,q,e,y,c} or: {k,l,p}",20);
        typingArea.addKeyListener(this);
        typingArea.requestFocusInWindow();
        typingArea.setEditable(false);
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        
        textAreas.add(typingArea);
        textAreas.add(displayArea);
        
		// panel with general info about the simulation (step etc..)
		JPanel info = BorderedJPanel.create("Simulation info", WIDTH, HEIGHT);
		info.add(Box.createRigidArea(new Dimension(WIDTH, 2)));
		generalInfo = new JLabel();
		generalInfo.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		generalInfo.setAlignmentX(SwingConstants.LEADING);	// works
		info.add(generalInfo); 
		
		// add it all together
		main.add(p, BorderLayout.CENTER);
		main.add(textAreas, BorderLayout.CENTER);
		main.add(info);
		main.add(Box.createVerticalGlue());
		
		info.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		info.setMinimumSize(new Dimension(WIDTH, 60));
		
		playMode.setSelected(true);
		// update data
		this.update();
	}

	
	
	/**
	 * called every time the GUI is updated
	 */
	public void update(){
		generalInfo.setText("Sim.step:\n"+shared.agentsSteps);
		
	}
	
	@Override
	public JPanel getMainPanel() { return this.main; }

	@Override
	public void sizeChangedTo(Dimension size) {
		System.err.println("ManualControl: dont use this method, it is done automatically..");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		if(e.getActionCommand().equals(cStep)){
			shared.setManualMode(false);
			shared.setWaitingConditionForGUI(true);
		}
		else if(e.getActionCommand().equals(cPlay)){
			shared.setManualMode(false);
			shared.setWaitingConditionForGUI(false);
		}
		else if(e.getActionCommand().equals(cManual)){
			KeyHandler.handleKeyChar('s',shared,harm.root.getActionNames());	// execute one skip
			shared.setManualMode(true);
			shared.setActionReady(false);	    
		}
		// step button pressed?
		else if(e.getActionCommand().equals(cButtonStep)){
			// go to the step mode, and allow one step
			stepMode.setSelected(true);
			shared.setManualMode(false);
			shared.setWaitingConditionForGUI(true);
			shared.step();
		}
		else if(e.getActionCommand().equals(cButtonStop)){
			shared.stopAndSave();
		}
		else{
			System.err.println("ManualControl: actionPerformed: actionCommand not recognized");
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) { /*ignore*/ }

	@Override
	public void keyReleased(KeyEvent arg0) { /*ignore*/ }

	@Override
	public void keyTyped(KeyEvent e) {
		String result = KeyHandler.handleKey(e,shared,harm.root.getActionNames());
        displayInfo(e, "KEY TYPED: ", result);
	}
	
	private void displayInfo(KeyEvent e, String keyStatus, String result){
    	
    	// key has been ignored by key handler?
    	if(result == null)
    		return;
    	
    	// go to the manual mode
    	shared.setManualMode(true);
    	manualMode.setSelected(true);
    	
    	int id = e.getID();
        String keyString;
        
        // only character keys
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key was:= '" + c + "' it is action:\""+result+"\"";
            displayArea.replaceRange(keyString, 0, displayArea.getText().length());
        }
    }


}
