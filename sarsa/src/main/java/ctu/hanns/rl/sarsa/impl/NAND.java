package ctu.hanns.rl.sarsa.impl;

import org.ros.namespace.GraphName;

import ctu.hanns.rl.sarsa.MisoGate;

public class NAND extends MisoGate{

	@Override
	public boolean copute(boolean a, boolean b) { return !(a && b); }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("NAND"); }
}
