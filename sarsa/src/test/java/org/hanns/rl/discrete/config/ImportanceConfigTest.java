package org.hanns.rl.discrete.config;

import static org.junit.Assert.*;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;
import org.junit.Test;

public class ImportanceConfigTest {

	@Test
	public void importanceVsEpsilon(){

		ImportanceBasedConfig ibc = new ImportanceBasedConfig();

		assertTrue(ibc.getMinEpsilon()==ImportanceBasedConfig.DEF_MINEPSILON);
		assertTrue(ibc.getImportance()==ImportanceBasedConfig.DEF_IMPORTANCE);
		
		//importance 0 -> epsilon is 1
		ibc.setImportance(0);

		assertTrue(ibc.getEpsilon()==1);
		// importance 1 => epsilon = minEpsion
		ibc.setImportance(1);
		assertTrue(ibc.getEpsilon()==ibc.getMinEpsilon());
	}
	
	/**
	 * Solves the bug: if importance>1 (should not happen?), the epsilon <0 
	 */
	@Test
	public void importanceBug(){
		ImportanceBasedConfig ibc = new ImportanceBasedConfig();


		double sampling = 0.1;
		double boundary = 100;
		
		double pos = -boundary;
		
		while(pos<boundary){
			
			ibc.setImportance((float)pos);


			assertTrue(ibc.getImportance()<=1 && ibc.getImportance()>=0);
			assertTrue(ibc.getEpsilon()>=ibc.getMinEpsilon() && ibc.getEpsilon()<=1);
			
			pos+=sampling;
		}
	}

}

