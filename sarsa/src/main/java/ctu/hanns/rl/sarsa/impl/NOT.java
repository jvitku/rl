package ctu.hanns.rl.sarsa.impl;

import org.ros.namespace.GraphName;

import ctu.hanns.rl.sarsa.SisoGate;

public class NOT extends SisoGate{
	
	@Override
	public boolean copute(boolean a) { return !a; }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("OR"); }

}
