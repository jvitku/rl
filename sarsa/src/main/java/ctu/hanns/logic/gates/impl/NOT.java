package ctu.hanns.logic.gates.impl;

import org.ros.namespace.GraphName;

import ctu.hanns.logic.gates.SisoGate;

public class NOT extends SisoGate{
	
	@Override
	public boolean copute(boolean a) { return !a; }
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("OR"); }

}
