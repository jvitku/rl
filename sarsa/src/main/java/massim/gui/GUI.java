package massim.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import massim.agent.mind.Mind;
import massim.agent.mind.harm.HarmSystem;
import massim.agent.mind.intentions.ASimpleIntentions;
//import massim.agent.body.Body;
import massim.agent.body.physiological.PhysiologicalStateSpace;

import massim.gui.lowLevel.BorderedJPanel;
import massim.gui.lowLevel.SpringLayoutUtilities;
import massim.gui.subwindows.ManualControl;
import massim.gui.subwindows.WorldLabelDescription;
//import massim.gui.subwindows.harm.HierarchyPanel;
import massim.shared.SharedData;


public class GUI extends JFrame implements Runnable, ChangeListener{


	//private Body body;
	private Mind mind;

	private static final long serialVersionUID = -2374475574410144355L;
	// UI
	String action = "nope";
	JTextArea displayArea;
	JTextField typingArea;
	static final String newline = System.getProperty("line.separator");

	// sliders for physiological state space variables
	JSlider[] physiologicalSliders;

	// whether I have moved with the sliders (exception is also called)
	private boolean[] ignoreSlider;

	// reference to the agents physiological state space
	PhysiologicalStateSpace state;
	Timer GUIRefreshTimer;

	// containers from the left to right (in the main window)
	Container ca, cb, cc, cd;

	public WorldLabelDescription descriptWindow;
	//public HierarchyPanel hierarchy;

	JTextArea console;
	SharedData shared;
	private HarmSystem harm;

	// how often to refresh GUI
	public final long refreshDelay= 300;                   // 5 seconds delay   700
	public final long startDelay = 500;

	public GUIDecisionSpace guiII;
	public GUINeuralNet guiNN;
	public GUIPlanner guiPlan;

	public ManualControl manualPanel;

	public final ASimpleIntentions ints;


	private final int T = 0;
	private final int LEV = 5;


	private ArrayList<JSlider> intentionalSliders;
	private ArrayList<Boolean> ignoreIntSlider;
	private JPanel compInts;

	public GUI(SharedData shared, /*Body */ int b, Mind m) {
		super("Agent's mind and body");

		//this.body = b;
		this.mind = m;
		this.shared = shared;

		//this.state = this.body.space;
		this.harm = this.mind.harm;
		this.ints = m.ints;
	}


	@Override
	public void run() {

		this.descriptWindow = new WorldLabelDescription(this.shared);

		guiII = new GUIDecisionSpace(this.descriptWindow, this.harm, this.shared);

		/*if(this.mind.neuralSystem.isUsed())
			guiNN = new GUINeuralNet(this.shared, this.body, this.mind);*/

		//this.guiPlan = new GUIPlanner(this.shared, this.body, this.mind);

		// set the period to refresh GUI
		GUIRefreshTimer = new Timer();
		GUIRefreshTimer.schedule(new TimerTask(){
			public void run(){
				refreshDisplay();
			}
		}, startDelay,refreshDelay);

		//Set up the content pane.
		this.addComponentsToPane();

		//Display the window, set close operation..
		this.pack();
		this.setVisible(true);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * this should refresh data based on the state of inner variables
	 */
	private void refreshDisplay(){

		// for all physiological sliders, if the agent changed the value, change and ignore event
		for(int i=0; i<state.size(); i++){
			// if some of the sliders has not been initialized so far, return 
			if(physiologicalSliders[i] == null)
				return;

			if(physiologicalSliders[i].getValue() != state.getVal(i)){
				this.ignoreSlider[i] = true;
				double tmp = Math.round(state.getVal(i)); 
				physiologicalSliders[i].setValue((int)tmp);
				this.ignoreSlider[i] = true;
			}    		
		}
		// some intention added?
		if(this.ints.isUsed()){

			// check for new intention
			if(this.intentionalSliders.size() != this.ints.size()){
				this.mypl("Some intention added! ");
				this.tryToAddNewSliders();
			}

			// refresh slider values
			for(int i=0; i<this.intentionalSliders.size(); i++){

				int tmp = (int)Math.round(this.ints.getVal(i));

				// value not correct
				if(this.intentionalSliders.get(i).getValue() != tmp){
					this.ignoreIntSlider.set(i, true);
					intentionalSliders.get(i).setValue(tmp);
					this.ignoreIntSlider.set(i, true);
				}   
			}
		}


		if(harm.root != null && this.descriptWindow != null){	
			//this.descriptWindow.writeSpaceStats();
			this.guiII.updateData();
		}

		/*if(this.mind.neuralSystem.isUsed())
    		this.guiNN.updateData();
		 */

		//this.hierarchy.update();

		this.manualPanel.update();

		// for all intentional sliders, if the agent changed the value, change and ignore event

	}



	private void mypl(String what){
		if(LEV<T)
			System.out.println("GUI: "+what);
	}

	/**
	 * physiological & intentional state spaces, manual control etc.. 
	 */
	private void addColumnA(){

		this.initPhysiologicalSliders();

		// create physiological JPanel and add all sliders in it
		JPanel comp = BorderedJPanel.create("Physiological state space",
				60,90*physiologicalSliders.length);	// x, and y dimensions

		for(int i=0; i<physiologicalSliders.length; i++)
			comp.add(physiologicalSliders[i]);

		ca.setLayout(new BoxLayout(ca,BoxLayout.PAGE_AXIS));
		ca.add(comp);

		// height of the physiological sliders
		ca.add(Box.createRigidArea(new Dimension(200, 20)));

		ca.setMaximumSize(new Dimension(35,30000));

		///////////// intentions
		if(this.ints.isUsed()){

			// create physiological JPanel and add all sliders in it
			compInts = BorderedJPanel.create("Intentional state space",
					60,90/*intentionalSliders.size()*/);	// x, and y dimensions

			// add rigid area (empty intentions)
			compInts.add(Box.createRigidArea(new Dimension(200, 2)));

			// add frame to GUI
			ca.add(compInts);

			// add all known sliders to the list and GUI
			this.initIntentionalSliders();
		}
	}

	private void initIntentionalSliders(){

		this.intentionalSliders = new ArrayList<JSlider>();
		this.ignoreIntSlider = new ArrayList<Boolean>();
		this.tryToAddNewSliders();

	}

	private void tryToAddNewSliders(){
		this.mypl("Last knwon size of intensions is: "+this.intentionalSliders.size()+
				" and actual is: "+this.ints.size());

		int start = this.intentionalSliders.size();/*
    	if(start>0)
    		start = start-1;*/

		for(int i=start; i<ints.size(); i++){

			JSlider tmp = new JSlider(JSlider.HORIZONTAL, 
					ints.getMinVal(), ints.getMaxVal(), (int)ints.getVal(i));

			// create new slider for it	
			tmp.setBorder(BorderFactory.createTitledBorder(ints.getName(i)));

			tmp.setMajorTickSpacing(1/*(int)state.getCenter(i)*/); //?
			tmp.setMinorTickSpacing(1);
			tmp.setPaintTicks(true);
			tmp.setPaintLabels(true);
			tmp.setVisible(true);
			tmp.addChangeListener(this);

			// add to list
			this.intentionalSliders.add(tmp);
			this.ignoreIntSlider.add(new Boolean(false));

			compInts.add(tmp);
		}
	}

	/**
	 * interactive monitor of agents mind 
	 */
	private void addColumnB(){
		/*

    	// just draw the picture for now
        Panel scheme = new ShowImage("../resources/human.png",400,400);
        scheme.setPreferredSize(new Dimension(400,400));

        cb.setLayout(new BoxLayout(cb,BoxLayout.PAGE_AXIS));
        cb.add(scheme);
		 */

		//hierarchy = new HierarchyPanel(harm, this.descriptWindow, this.shared, this.guiPlan);
		cb.setLayout(new BoxLayout(cb,BoxLayout.PAGE_AXIS));
		//cb.add(hierarchy.getMainPanel());
	}

	/**
	 * add clear and safe buttons
	 */
	private void addColumnD(){
		cd.setLayout(new BoxLayout(cd,BoxLayout.PAGE_AXIS));
		manualPanel = new ManualControl(harm, shared);
		cd.add(manualPanel.getMainPanel());

	}



	private void addComponentsToPane() {

		// initialize the columns
		ca = new Container();
		cb = new Container();
		//cc = new Container();
		cd = new Container();

		// add data to the columns
		addColumnA();
		addColumnB();
		//addColumnC();
		addColumnD();

		//Set up the content pane.
		Container contentPane = getContentPane();
		contentPane.setLayout(new SpringLayout());

		// Containers marked from left to right
		contentPane.add(ca);
		contentPane.add(cb);
		//contentPane.add(cc);
		contentPane.add(cd);

		// set the layout and we're done
		SpringLayoutUtilities.makeCompactGrid(contentPane, 1, contentPane.getComponentCount(), 6,6,6,6);
		pack();
	}

	/**
	 * initialize the physiological sliders and their listeners
	 */
	private void initPhysiologicalSliders(){

		this.physiologicalSliders = new JSlider[state.size()];
		this.ignoreSlider = new boolean[state.size()];

		// for all physiological state space variables:
		for(int i=0; i<state.size(); i++){

			// create new slider for it	
			physiologicalSliders[i] = new JSlider(JSlider.HORIZONTAL, 
					(int)state.getMinVal(i), (int)state.getMaxVal(i), (int)state.getVal(i));
			physiologicalSliders[i].setBorder(BorderFactory.createTitledBorder(state.getName(i)));

			physiologicalSliders[i].setMajorTickSpacing((int)state.getCenter(i));
			physiologicalSliders[i].setMinorTickSpacing(1);
			physiologicalSliders[i].setPaintTicks(true);
			physiologicalSliders[i].setPaintLabels(true);
			physiologicalSliders[i].setVisible(true);

			physiologicalSliders[i].addChangeListener(this);

			ignoreSlider[i] = false;
		}
	}



	@Override
	public void stateChanged(ChangeEvent event) {

		// check all physiological sliders
		for(int i=0; i<this.physiologicalSliders.length; i++){
			// if we have found the source, handle it and return (just one source supposed here)
			if(event.getSource().equals(physiologicalSliders[i])){

				if(this.ignoreSlider[i]){
					this.ignoreSlider[i] = false;
					return;
				}
				System.out.println("setting! to this: "+this.physiologicalSliders[i].getValue());
				state.setVal(i, this.physiologicalSliders[i].getValue());
				this.ignoreSlider[i] = true;
				return;
			}
		}
		// check all intentionalSLiders
		for(int i=0; i<this.intentionalSliders.size(); i++){
			if(event.getSource().equals(this.intentionalSliders.get(i))){
				if(this.ignoreIntSlider.get(i)){
					this.ignoreIntSlider.set(i, false);
					return;
				}
				this.mypl("setting intention! to this: "+this.intentionalSliders.get(i).getValue());

				this.ints.setVal(i, this.intentionalSliders.get(i).getValue());
				this.ignoreIntSlider.set(i, true);
				return;
			}

		}

	}  

}