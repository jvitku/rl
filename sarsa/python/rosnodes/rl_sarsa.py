# Jython helper for launching a NeuralModule which implements discrete RL algorithm - Q(lambda) - Q-learning with eligibility traces
#
# starts: 
#   -ROS-java node which receives description of wold state (together with a reinforcement) and publishes (executes) one of possible actions
#   -NeuralModule with modem that communicates with the ROS node (represents ROS node in the Nengoros)
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule

from org.hanns.rl.discrete.ros.sarsa import HannsQLambdaVis as QLambda

# java classes
node =  "org.hanns.rl.discrete.ros.sarsa.HannsQLambdaVis"
nodep = "org.hanns.rl.discrete.ros.sarsa.HannsQLambdaVisProsperity"

# Synchronous NeuralModule implementing QLambda algorithm
def qlambda(name, noStateVars=2, noActions=4):
	g = NodeGroup("QLambda", True);
	g.addNode(node, "QLambda", "java");
	module = NeuralModule(name+'_QLambda', g, False)
	
	module.createEncoder(QLambda.topicAlpha,"float",1); 				# alpha config
	module.createEncoder(QLambda.topicGamma,"float",1);
	module.createEncoder(QLambda.topicLambda,"float",1);
	module.createEncoder(QLambda.topicImportance,"float",1);
	
	module.createEncoder(QLambda.topicProsperity,"float",1);
	
	module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
	return module

# Synchronous NeuralModule implementing QLambda algorithm
def qlambdaProsperity(name, noStateVars=2, noActions=4):
	g = NodeGroup("QLambda", True);
	g.addNode(nodep, "QLambda", "java");
	module = NeuralModule(name+'_QLambda', g, False)
	
	module.createEncoder(QLambda.topicAlpha,"float",1); 				# alpha config
	module.createEncoder(QLambda.topicGamma,"float",1);
	module.createEncoder(QLambda.topicLambda,"float",1);
	module.createEncoder(QLambda.topicImportance,"float",1);

	module.createDecoder(QLambda.topicProsperity,"float",3);
		
	module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
	
	return module


