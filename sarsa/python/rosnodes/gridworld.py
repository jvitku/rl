# Jython helper for launching a NeuralModule which implements discrete simulator with 2D reward map
# The agent is allowed to move in the map by means of four actions: {<,>,^,v}
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule

from org.hanns.rl.discrete.ros.sarsa import HannsQLambdaVisProsperity as QLambda
from org.hanns.rl.discrete.ros.testnodes import BenchmarkGridWorldNode as World

nodep =  "org.hanns.rl.discrete.ros.testnodes.BenchmarkGridWorldNode"

# Synchronous Discrete Simulator with 2D map
def benchmark(name):
	
	command = [nodep]	# no parameters
	noActions = 4;		# hardcoded
	noStateVars = 2;
	
	g = NodeGroup("GridWorld", True);
	g.addNode(command, "GridWorld", "java");
	module = NeuralModule(name+'_GridWorld', g, False)
	
	module.createEncoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createDecoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
	
	return module

