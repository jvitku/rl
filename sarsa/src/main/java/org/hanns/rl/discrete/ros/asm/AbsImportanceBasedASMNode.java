package org.hanns.rl.discrete.ros.asm;

import org.hanns.rl.discrete.actionSelectionMethod.epsilonGreedy.config.impl.ImportanceBasedConfig;

public abstract class AbsImportanceBasedASMNode extends AbstractASMNode{


	/**
	 * Amount of randomization in the ASM (how important the proper selection of action is?)
	 */
	public static final String importanceConf = "importance";
	public static final String topicImportance = conf+importanceConf;
	public static final double DEF_IMPORTANCE = ImportanceBasedConfig.DEF_IMPORTANCE;
	
	@Override
	protected void registerParameters(){
		super.registerParameters();
		
		paramList.addParam(importanceConf, ""+DEF_IMPORTANCE, "How important is selection of the optimal action?");
	}
}
