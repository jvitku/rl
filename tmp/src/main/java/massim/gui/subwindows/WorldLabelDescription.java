package massim.gui.subwindows;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import massim.agent.mind.harm.actions.SomeSpaceWithVariables;
import massim.shared.SharedData;


public class WorldLabelDescription {
	
	private JLabel theLabel;
	
	// this to be added to the window
	public JPanel panel;
    
	private final String initialText = " ";
	private final String nameOfFrame = "Actual (part of) world properties";
	
	private final int xSize = 330;
	private final int ySize = 500;
	
	private final int small = 25;
	
	private final String fs = "<font size=-2>";
	private final String fse = "</font>";
	private final String div = "<div align=\"right\">";
	private final String dive = "</div>";
	
	private final String fr =  "<font color=red>";
	private final String fb =  "<font color=yellow>";
	private final String fe = "</font>";
	
	private SomeSpaceWithVariables space;
	private SharedData shared;
	
	public WorldLabelDescription(SharedData shared){
		
		this.shared = shared;
		
		theLabel = new JLabel();
        theLabel.setVerticalAlignment(SwingConstants.CENTER);
        theLabel.setHorizontalAlignment(SwingConstants.CENTER);
        theLabel.setText(initialText);
        theLabel.setAutoscrolls(true);

        // init the frame with name
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(nameOfFrame),
                        BorderFactory.createEmptyBorder(10,10,10,10)));
        
        panel.setPreferredSize(new Dimension(xSize+small, ySize+small));
        
        // add scrolling something
        JScrollPane scrollPane = new JScrollPane(theLabel,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scrollPane.getViewport().setBackground(Color.lightGray);
        
        // add all to the panel
        panel.add(scrollPane);
	}
	
	public Dimension getPrefferedSize(){ return this.panel.getPreferredSize(); }
	
	/**
	 * @return - the component to be added to the main window
	 */
	public JPanel getMainPanel(){ return this.panel; }
	public SomeSpaceWithVariables getDecisionSpace(){ return this.space; }
	
	
	public void writeSpaceStats(){
		
		this.space = this.shared.getActualSpaceSelected();
		
		if(this.space == null){
			theLabel.setText("<html>\nNothing selected..<br><br>" +
					"click on the desired decision space<br> " +
					"or set the planning window depth");
			return;
		}
		
		if(this.space.getVariables() == null){
			theLabel.setText("<html>\nSome decision space selected //TODO :-)<br><br>");
			return;
		}
			
		
		String out = "<html>\n";
		out = out + "<b>--------Search space:"+div+" dimension: <b>"+space.getSpaceDimension()+
			"</b>"+dive+ div+"size: <b>"
			+space.getSpaceSize()+dive+"</b></b>";
		
		out = out + fs+"Values are: "+fb+"previous"+fe+" " +
        fr+"actual"+fe+fse+"<br>\n";
		out = out + "<br><b>--------Number of variables: "+space.getVariables().size()+"</b><br>";
		
		// for each variable print out their values
		for(int i=0; i<space.getVariables().size(); i++){
			out = out + fs+space.getVariables().get(i).getName();
			out = out+div+fs;
			// for all values, write out it
			for(int j=0; j<space.getVariables().get(i).getNumValues(); j++){
				if(space.getVariables().get(i).isActual(j)){
					out = out + fr+ space.getVariables().get(i).vals.get(j).getStringVal() +fe+" ";
				}
				else if(space.getVariables().get(i).isPrevious(j)){
					out = out + fb+ space.getVariables().get(i).vals.get(j).getStringVal() +fe+" ";
				}
				else
					out = out + space.getVariables().get(i).vals.get(j).getStringVal()+" ";
			}
			out = out + "    "+dive;
		}
		
		out = out+ "<br>"+"<b>--------Number of actions: "+space.getActions().size()+"</b><br>"+fs;

		// print out all actions
		for(int i=0; i<space.getActions().size(); i++){
			if(space.getLastExecuted() == i)
				out = out + fr+ space.getActions().get(i).getName() + fe+ "<br>";
			else
				out = out + space.getActions().get(i).getName() + "<br>";
		}
		out = out + fse;
		
		out = out+ fse+"<br><b>--------Number of constants: "+space.getConstants().size()+"</b><br>"+fs;

		// for each variable print out their values
		for(int i=0; i<space.getConstants().size(); i++){
			out = out +fs+ space.getConstants().get(i).getName();
			
			// for all values, write out it
			out = out+div+fs+space.getConstants().get(i).getValuesToString() + "    "+fse+dive;
		}

		
		theLabel.setText(out);
		
	}

}
