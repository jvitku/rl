package massim.agent.mind.harm.components.predictors;

import massim.framework.util.xml.test.Loadable;


/**
 * settings of basic coefficients for Stochastic Return Predictor
 * 
 * this class is meant to be instantiated from the configuration XML file
 * 
 * @author jardavitku
 *
 */
public class HarmSystemSettings implements Loadable{
	
	// stochastic return predictor settings
	public double alpha;
	public double gamma;
	public double minEpsilon;
	// eligibility settings
	public int eligibilityLength;
	public double lambda;
	
	
	// HARM system settings 
	public int actionWinInitLength;		// initial length: how long can be abstract action
	public double actionWinLengthAdd; 	// by this number the window is extended every step
	
	// each action (abstract/primitive) and value of variable has some step in which have been seen 
	// each step the num is decaying, if the num is > than trigger, accept value, else discard
	public double stepDecay;	
	public double trigger;
	
	public int minWindowLength;		// hierarchy learning triggered by reinforcements, min learning w.l.
	public double actionTrigger;		// action with relative count under this trigger wont be used
	public double variableTrigger;
	
	// intentional state space setup
	public boolean useInt;						// whether to use (that means generate) intentions at all
	public double intCoefficient;				// how much will intention decay with one step
	public int intReinforcementBaseStrength;	// how much will be received to the Q(s,a) matrix
	public double intA; 						// declination of variable value->motivation fcn.: m=ax
	public boolean reportIntMSD;				// msd for intentions
	public int reportSeparately;					// number of intentions to be printed separately
												// if the intention is not inited, it will print -1
	public String dataPath;						
	public String experimentname;		
	
	public double flatSelectorRandomization;	// how much to randomize 
	
	public int pauseAt;							// pause agent's learning at simulation step no..
	
	public boolean batchPlanner;				// whether to execute tasks in batch mode or sequentially  
	
	@Override
	public void init() {
		
		if(lambda>1 || lambda<0)
			System.err.println("PredictorSettings: XML config error: lambda has to be in<0;1> is: "+lambda);
		if(gamma>1 || gamma<0)
			System.err.println("PredictorSettings: XML config error: gamma has to be in<0;1> is: "+gamma);
		if(alpha>1 || alpha<0)
			System.err.println("PredictorSettings: XML config error: alpha has to be in<0;1> is: "+alpha);
		if(minEpsilon>1 || minEpsilon<0)
			System.err.println("PredictorSettings: XML config error: minEpsilon has to be in<0;1> is: "
					+minEpsilon);
		if(eligibilityLength<0)
			System.err.println("PredictorSettings: XML config error: eligibilityLength has to be " +
					"bigger than 0, and is: "+eligibilityLength);
		
	}
	

}
