package ctu.hanns.logic.gates.impl;

import org.ros.namespace.GraphName;

import ctu.hanns.logic.gates.MisoGate;

public class NAND extends MisoGate{

	@Override
	public boolean copute(boolean a, boolean b) { return !(a && b); }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("NAND"); }
}
