# Jython helper for launching a NeuralModule which implements discrete simulator with 2D reward map
# The agent is allowed to move in the map by means of four actions: {<,>,^,v}
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule

from org.hanns.rl.discrete.ros.sarsa import QLambda as QLambda
from org.hanns.rl.discrete.ros.testnodes import BenchmarkGridWorldNode as World

nodep =  "org.hanns.rl.discrete.ros.testnodes.BenchmarkGridWorldNode"

# Synchronous Discrete Simulator with 2D map
def example(name, logPeriod=200):
	
	command = [nodep, '_'+World.logPeriodConf+':='+str(logPeriod)]
	
	noActions = 4;		# hardcoded
	noStateVars = 2;
	
	g = NodeGroup("GridWorld", True);
	g.addNode(command, "GridWorld", "java");
	module = NeuralModule(name+'_GridWorld', g, True)
	
	module.createEncoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createDecoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
	
	return module


package =  "org.hanns.rl.discrete.ros.testnodes.benchmark."
benchA =  "BenchmarkGridWorldNodeA"
benchB =  "BenchmarkGridWorldNodeB"	
# Synchronous Discrete Simulator with 2D map
# mapName specifies the name of the GridWorld node under package and it will load it 
def benchmarkA(name, mapName = benchA, logPeriod=200,synchronous=True): 

	command = [package+mapName, '_'+World.logPeriodConf+':='+str(logPeriod)]

	noActions = 4;		# hardcoded
	noStateVars = 2;

	g = NodeGroup(mapName, True);
	g.addNode(command, name, "java");
	module = NeuralModule(name+'_GridWorld', g, synchronous)
	module.createEncoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createDecoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
	return module

bench =  "org.hanns.rl.discrete.ros.testnodes.benchmark.twoReward.BenchmarkGridWorldNodeD"
def benchmarkTwoR(name, mapName = "benchmark", logPeriod=200,synchronous=True): 

	command = [bench, '_'+World.logPeriodConf+':='+str(logPeriod)]

	noActions = 4;		# hardcoded
	noStateVars = 2;

	g = NodeGroup(mapName, True);
	g.addNode(command, name, "java");
	module = NeuralModule(name+'_GridWorld', g, synchronous)
	module.createEncoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createDecoder(QLambda.topicDataIn, "float", noStateVars+2) 	# encode states (first is reward)
	return module
