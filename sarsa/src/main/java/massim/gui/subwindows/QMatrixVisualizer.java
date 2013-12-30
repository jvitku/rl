package massim.gui.subwindows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import massim.agent.mind.harm.HarmSystem;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.PhysilogicalAction;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.actions.SomeSpaceWithVariables;
import massim.agent.mind.harm.components.qmatrix.QSAMatrix;
import massim.agent.mind.harm.variables.VariableList;
import massim.gui.lowLevel.BorderedJPanel;
import massim.gui.lowLevel.ValSelector;
import massim.shared.SharedData;

public class QMatrixVisualizer implements MyPanelInterface, ActionListener {

	private final JPanel panel;
	private final JLabel theLabel;
	private final JPanel controls;
	private  JPanel selectors;
	
	private JComboBox firstSelectorY, secondSelectorX;
	
	private ArrayList<ValSelector> selectorList;
	private int[] selectedIndexes;	// indexes that are currently selected by the selectors
	private boolean updateSelectors;
	
	private JRadioButton utilitiMatrix;
	private final String UTIL = "Utility M.";
	private boolean printUtility;
	
	private JButton printToFile;	// print maximum utilities distribution to file
	private final String printMUD = "printMUD";
	
	private final String nameOfFrame = "Q(s,a) matrix - decision space";
	String test = "<html><table border=\"2\"> <tr><td> This is a cell </td> <td> This is a cell </td> " +
			"</tr><tr> <td> This is the new row </td> <td> I'm on the new row, too!</td></tr>" +
			"</table></html>";
	String test2 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	
	String test3 = "<html><table border=\"1\"><tr><th>Header 1</th><th>Header 2</th></tr><tr>" +
			"<td>row 1, cell 1</td><td>row 1, cell 2</td></tr><tr><td>row 2, cell 1</td>" +
			"<td>row 2, cell 2</td></tr></table></html>";
	
	private final HarmSystem harm;
	private final WorldLabelDescription descriptWindow;
	private final SharedData shared;
	private SomeSpaceWithVariables space;
	
	private SomeSpaceWithVariables prevSpace;
	
	private String[][] actualSelection;
	
	private final int CHEIGHT = 200; 
	
	public QMatrixVisualizer(WorldLabelDescription descriptWindow, HarmSystem harm, SharedData shared){
		this.shared = shared;
		this.harm = harm;
		this.descriptWindow = descriptWindow;
		this.space = this.shared.getActualSpaceSelected();
		this.prevSpace = space;
		
		this.updateSelectors = true;
		
		// init the frame with name, controls and hmtl label
        panel = new JPanel();
        initPanel();
        
		// init the JLabel to write out the html table
    	theLabel = new JLabel(); 
        JScrollPane scrollPane = new JScrollPane(theLabel,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setBackground(Color.lightGray);
        scrollPane.setBackground(Color.darkGray); // not working...
        theLabel.setBackground(Color.white);
        theLabel.setOpaque(true);
            
        // init controls panel
        controls = new JPanel();
        initControls();
        
        theLabel.setText(test2);
        
        this.printUtility = false;//arbitrary
	}
	
	
	private void initPanel(){
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(nameOfFrame),
                        BorderFactory.createEmptyBorder(10,10,10,10)));
        panel.setPreferredSize(this.descriptWindow.getPrefferedSize());
	}
	
	private void initControls(){
		
		Dimension commandDim = new Dimension(this.descriptWindow.getPrefferedSize().width,CHEIGHT);
		
		controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        controls.setPreferredSize(commandDim);
        
        JPanel util = new JPanel();
        util.setLayout(new BoxLayout(util, BoxLayout.X_AXIS));
        this.utilitiMatrix = new JRadioButton(this.UTIL);
        this.utilitiMatrix.addActionListener(this);
        util.add(this.utilitiMatrix);
        util.add(new JLabel(this.UTIL));
        
        // print maximum utilities distributions to file
        this.printToFile = new JButton(this.printMUD);
        this.printToFile.setVisible(true);
        this.printToFile.setEnabled(true);
        this.printToFile.addActionListener(this);
        //util.add(this.printToFile);
        
        // panel where to place controls
        JPanel p = BorderedJPanel.create("Select two dimensions to display", commandDim.width, 100);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(this.printToFile);
        p.setPreferredSize(new Dimension(this.descriptWindow.getPrefferedSize().width,CHEIGHT));
        p.setMaximumSize(new Dimension(100000, 100));
        
        
        // initialization of selectors
        JPanel pp = new JPanel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));
        this.firstSelectorY = new JComboBox(new String[]{"-"});
        this.firstSelectorY.addActionListener(this);
        pp.add(new JLabel("Y-axis (dim1)"));
        pp.add(this.firstSelectorY);
        
        
        JPanel ppp = new JPanel();
        ppp.setLayout(new BoxLayout(ppp, BoxLayout.Y_AXIS));
        this.secondSelectorX = new JComboBox(new String[]{"-"});
        this.secondSelectorX.addActionListener(this);
        ppp.add(new JLabel("X-axis (dim2)"));
        ppp.add(this.secondSelectorX);
        
        this.deleteValueSelectors();
        
        // panel with general info about the simulation (step etc..)
		JPanel info = BorderedJPanel.create("Simulation info", commandDim.width, 2000);
		//info.add(Box.createRigidArea(new Dimension(commandDim.width, 2))); // do not use this..
		info.setMaximumSize(new Dimension(100000, 1000000)); // joke was here, maximum size.. 
		info.add(Box.createVerticalGlue()); // WORKS!!!
		info.add(theLabel); 

		info.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        p.add(pp);
        p.add(ppp);
        
        JPanel box = new JPanel();
        box.setMaximumSize(new Dimension(100000, 1000000)); // joke was here, maximum size..
        box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
        //box.setMinimumSize(new Dimension(160,100));
        
        selectors = BorderedJPanel.create("Other dim vals.", (int)(commandDim.width/2), 2000);
        selectors.setLayout(new BoxLayout(selectors, BoxLayout.Y_AXIS));
        selectors.add(Box.createVerticalGlue()); // WORKS!!!
        selectors.setMaximumSize(new Dimension(70,100000));
        selectors.setMinimumSize(new Dimension(60,100));
        box.add(selectors);
        box.add(info);
        
        controls.add(p);
        
        //theLabel.setPreferredSize(this.panel.getPreferredSize());
        theLabel.setAlignmentX(SwingConstants.CENTER);	// text and the label as the list would be
        //theLabel.setAlignmentY(SwingConstants.CENTER);
        controls.add(this.utilitiMatrix);
        panel.add(controls);
        //panel.add(info);
        panel.add(box);
        
	}
	
	
	@Override
	public JPanel getMainPanel() { return this.panel; }

	@Override
	public void sizeChangedTo(Dimension size) {
		System.err.println("QMatrixVisualizer: sizeChangedTo(): dont call this please:-)");
	}
	
	private int divider = 0, poc = 0, p = 0;

	
	private ActionList al;
	private VariableList val;
	private boolean inited = false;
	
	
	private synchronized void repaintAll(){
		System.out.println("SHOULD repaiiiiiiiiiiiiiint");
		this.space = this.shared.getActualSpaceSelected();
		this.prevSpace = this.space;
		this.updateSelectors();
	}
	
	public synchronized void writeSpaceStats(){
		
		if(!inited){
    		
    		this.al = new ActionList();
    		this.val = new VariableList();
    		this.inited = true;
    	}
		
		this.space = this.shared.getActualSpaceSelected();
		/*
		if(!this.space.equals(this.prevSpace))
			this.repaintAll();
		*/
		if(this.space == null || this.space.getQMatrix()== null){
			theLabel.setText("Decision space not selected or initialized");
			this.secondSelectorX.removeAllItems();
	        this.firstSelectorY.removeAllItems();
	        this.updateSelectors = true;			// selectors etc deleted, update it
			return;
		}
		
		if(!shared.shouldWaitForGUI()){
			divider++;
	    	poc++;
		}
    	/*
    	if(QMatrixTest.testSizesA(this.harm.root.qmatrix, val, al, divider, poc))
    		space.getQMatrix().indicateMatrixChange();
    	*/
		/*
		if(QMatrixTest.testSizesC(this.harm.root.qmatrix, val, al, divider, poc))
    		space.getQMatrix().indicateMatrixChange();
		*/
		if(space.getQMatrix().matrixChanged() || this.updateSelectors || 
				this.shared.shouldReloadMatrix()){
			this.shared.discardReloadRequest();
			this.updateSelectors();
		}

		// repaint the matrix each round
		this.repaintMatrix();
	}
	
	
	private synchronized void repaintMatrix(){
		String secondSelected = (String)this.secondSelectorX.getSelectedItem();
    	String firstSelected = (String)this.firstSelectorY.getSelectedItem();
    	
    	String tmp = this.space.getQMatrix().printSorted(
				firstSelected, secondSelected, this.selectedIndexes);
    	if(tmp==null){
    		this.updateSelectors();
    		return;
    	}
    	
    	theLabel.setText(tmp);
	}
	
	private synchronized String[] deleteZeroDim(String[] names){
		if(names.length <= 1)
			return names;
		String [] out = new String[names.length-1];
		for(int i=1; i<names.length; i++)
			out[i-1] = names[i];
		
		return out;
	}
	

	private synchronized void updateSelectors(){

		QSAMatrix m = space.getQMatrix();
		String names[] = m.varNamesToArray();
		
		if(this.printUtility)
			names = this.deleteZeroDim(names);
        
		// update dimension selector
        this.secondSelectorX.removeAllItems();
        this.firstSelectorY.removeAllItems();
        
        for(int i=0; i<names.length; i++){
        	this.secondSelectorX.addItem(names[i]);
        	this.firstSelectorY.addItem(names[i]);
        }
        
        this.firstSelectorY.setSelectedIndex(0);
        //System.out.println(Names len us:)
        if(names.length>1)
        	this.secondSelectorX.setSelectedIndex(1);
        
        // update value selectors
        this.updateValueSelectors();
        
        this.updateSelectors = false;
        m.discardMatrixChange();
	}
	
	private synchronized void updateValueSelectors(){
		QSAMatrix m = space.getQMatrix();
		String names[] = m.varNamesToArray();
		
		if(this.printUtility)
			names = this.deleteZeroDim(names);

        String sel1 = (String)this.firstSelectorY.getSelectedItem();
        String sel2 = (String)this.secondSelectorX.getSelectedItem();
        
        ValSelector tmp;
        this.deleteValueSelectors();
        
        this.selectedIndexes = new int[names.length];
        
    	for(int i=0; i<names.length; i++){
    		
    		// actually selected index 
    		this.selectedIndexes[i] = 0;
    		
    		// ignore dimensions already selected
    		if(names[i].equalsIgnoreCase(sel1) || names[i].equalsIgnoreCase(sel2)){
    			continue;
    		}
    		
    		this.selectedIndexes[i] = m.getSortedValueInds(names[i])[0]; 
    		tmp = new ValSelector(names[i], m.getSortedValues(names[i]), this);
    		
    		selectors.add(tmp.label);
    		selectors.add(tmp.box);
    		
    		this.selectorList.add(tmp);
    	}
    	selectors.revalidate();
    	selectors.repaint();
	}
	
	private synchronized void deleteValueSelectors(){
		// for all boxes, remove listeners and remove them from GUI 
		if(this.selectorList != null && this.selectorList.size()>0){
			for(int i=0; i<this.selectorList.size(); i++){
				this.selectorList.get(i).removeActionListener(this);
				selectors.remove(this.selectorList.get(i).label);
				selectors.remove(this.selectorList.get(i).box);
			}
		}
		this.selectorList = new ArrayList<ValSelector>();
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		
		// radio button has changed, repaint matrix to the other one
		if(e.getSource() instanceof JRadioButton){
			JRadioButton r = (JRadioButton)e.getSource();
			this.printUtility = r.isSelected();
			
			this.updateSelectors();
			this.repaintMatrix();
			
			return;
		}
		
		// print MUD to file?
		if(e.getSource() instanceof JButton){
			// get the matrix
			QSAMatrix m = space.getQMatrix();
			if(m==null)
				return;
			// get the dimensions selected
			String secondSelected = (String)this.secondSelectorX.getSelectedItem();
	    	String firstSelected = (String)this.firstSelectorY.getSelectedItem();
	    	
	    	// print data to file
			m.printMaxUtilityToFile(firstSelected, secondSelected, this.selectedIndexes, space);
			return;
		}
		
		JComboBox cb = (JComboBox)e.getSource();
		
		String secondSelected = (String)this.secondSelectorX.getSelectedItem();
    	String firstSelected = (String)this.firstSelectorY.getSelectedItem();
    	
		
        if(this.space == null){
        	theLabel.setText("Decision space not selected or initialized ");
        	this.secondSelectorX.removeAllItems();
            this.firstSelectorY.removeAllItems();
            this.deleteValueSelectors();
            this.updateSelectors = true;
        	return;
        }
        
        QSAMatrix m = space.getQMatrix();
        
        if(m == null){
        	theLabel.setText("Q(s,a) matrix not initialized ");
        	this.secondSelectorX.removeAllItems();
            this.firstSelectorY.removeAllItems();
            this.updateSelectors = true;
            this.deleteValueSelectors();
            return;
        }
        
        //System.out.println("QM is mine!!!!!!!! "+m.getNumActions());
        
        if(this.firstSelectorY.getSelectedItem() == null ||
        		this.secondSelectorX.getSelectedItem() == null){
        	///this.updateSelectors = true; // obviously fulfilled many times.. 
        	return;
        }
        
        // change the X-axis variable?
        if(cb.equals(this.secondSelectorX)){
        	this.updateValueSelectors();
        	this.updateActualSelection(m);
        	String tmp = m.printSorted(firstSelected, secondSelected, this.selectedIndexes);
        	if(tmp==null){
        		this.updateSelectors();
        		return;
        	}else{
        		theLabel.setText(tmp);
        	}
        }
        // change the Y-axis variable?
        else if(cb.equals(this.firstSelectorY)){
        	this.updateValueSelectors();
        	this.updateActualSelection(m);
        	String tmp = m.printSorted(firstSelected, secondSelected, this.selectedIndexes);
        	if(tmp==null){
        		this.updateSelectors();
        		return;
        	}else{
        		theLabel.setText(tmp);
        	}
        }
        // list of value selectors
        else{
        	this.updateActualSelection(m);
        	
        	String tmp = m.printSorted(firstSelected, secondSelected, this.selectedIndexes);
        	if(tmp==null){
        		this.updateSelectors();
        		return;
        	}else{
        		theLabel.setText(tmp);
        	}
        }
	}
	
	private synchronized void updateActualSelection(QSAMatrix m){
		
		this.actualSelection =  new String[m.getDimension()][2];
		
    	for(int i=0; i<this.selectorList.size(); i++){
    		actualSelection[i][0] = this.selectorList.get(i).varName;
    		actualSelection[i][1] = (String)this.selectorList.get(i).box.getSelectedItem();
    	}
    	
    	String firstSelected = (String)this.firstSelectorY.getSelectedItem();
    	String secondSelected = (String)this.secondSelectorX.getSelectedItem();
    	
    	actualSelection[this.selectorList.size()  ][0] = firstSelected;
    	actualSelection[this.selectorList.size()  ][1] = m.valueIndexToName(firstSelected, 0);
    	
    	if(!firstSelected.equalsIgnoreCase(secondSelected)){
    		actualSelection[this.selectorList.size()+1][0] = secondSelected;
    		actualSelection[this.selectorList.size()+1][1] = m.valueIndexToName(secondSelected, 0);
    	}
    	selectedIndexes = m.getIndexes(this.actualSelection);
    	
    	// if we will want to print utility matrix, just call printSorted with shorter vector of indexes!
    	if(this.printUtility && selectedIndexes.length>1)
    		selectedIndexes = Arrays.copyOfRange(selectedIndexes, 1, selectedIndexes.length);	
	}
    
	protected synchronized void pi(String[] indexes){
		System.out.println(" generated names areeeeeeeee: ");
		for(int i=0; i<indexes.length; i++){
			System.out.print(indexes[i]+" ");			
		}
		System.out.println(" ");
	}
	
	protected synchronized void pi(int[] indexes){
		System.out.println(" generated indexes areeeeeeeee: ");
		for(int i=0; i<indexes.length; i++){
			System.out.print(indexes[i]+" ");			
		}
		System.out.println(" ");
	}
	
	protected synchronized void printNames(String[][] x){
		
		System.out.println("---------------------");
		for(int i=0; i<x.length; i++){
			System.out.println(x[i][0]+" \t"+x[i][1]);
		}
	}
}


