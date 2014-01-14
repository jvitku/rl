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

from org.hanns.rl.discrete.ros.sarsa import QLambda as QLambda

# java classes
node =  "org.hanns.rl.discrete.ros.sarsa.QLambda"
nodep = "org.hanns.rl.discrete.ros.sarsa.QLambda"
nodedelay = "org.hanns.rl.discrete.ros.sarsa.QLambda"


# Synchronous NeuralModule implementing QLambda algorithm
# noStateVars = how many state variables is used
# noActions = how much actions the agent has available
# noValues = how much values has each state variable - this should correspond to the map dimensions
def qlambda(name, noStateVars=2, noActions=4, noValues=5, logPeriod=100):
	g = NodeGroup("QLambda", True);
	command = [nodep, '_'+QLambda.noInputsConf+ ':=' + str(noStateVars), 
	'_'+QLambda.noOutputsConf+':='+str(noActions),
	'_'+QLambda.sampleCountConf+':='+str(noValues),
	'_'+QLambda.logPeriodConf+':='+str(logPeriod)]
		
	g.addNode(command, "QLambda", "java");
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
# noStateVars = how many state variables is used
# noActions = how much actions the agent has available
# noValues = how much values has each state variable - this should correspond to the map dimensions
def qlambdaProsperity(name, noStateVars=2, noActions=4, noValues=5, logPeriod=100):
	
	command = [nodep, '_'+QLambda.noInputsConf+ ':=' + str(noStateVars), 
	'_'+QLambda.noOutputsConf+':='+str(noActions),
	'_'+QLambda.sampleCountConf+':='+str(noValues),
	'_'+QLambda.logPeriodConf+':='+str(logPeriod)]
		
	g = NodeGroup("QLambda", True);
	g.addNode(command, "QLambda", "java");
	module = NeuralModule(name+'_QLambda', g, False)
	
	module.createEncoder(QLambda.topicAlpha,"float",1); 				# alpha config
	module.createEncoder(QLambda.topicGamma,"float",1);
	module.createEncoder(QLambda.topicLambda,"float",1);
	module.createEncoder(QLambda.topicImportance,"float",1);

	module.createDecoder(QLambda.topicProsperity,"float",3);			# float[]{prosperity, coverage, reward/step}
		
	module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
	
	return module

# Build the RL module, add it into the network and connect
# two value generators defining the default values of the parameters
# so the script does not have to define it manually
def qlambdaConfigured(name, net, noStateVars=2, noActions=4, noValues=5, logPeriod=100):
	
	# build the node
	mod = qlambdaProsperity(name, noStateVars,noActions,noValues, logPeriod)
	net.add(mod)
	
	# define the configuration
	net.make_input('alpha',[QLambda.DEF_ALPHA])
	net.make_input('gamma',[QLambda.DEF_GAMMA])
	net.make_input('lambda',[QLambda.DEF_LAMBDA])
	net.make_input('importance',[QLambda.DEF_IMPORTANCE])
	
	# wire it
	net.connect('alpha', mod.getTermination(QLambda.topicAlpha))
	net.connect('gamma', mod.getTermination(QLambda.topicGamma))
	net.connect('lambda', mod.getTermination(QLambda.topicLambda))
	net.connect('importance', mod.getTermination(QLambda.topicImportance))
	
	return mod

#################################################################################
	

# TODO this one will be used everywhoere with different maxDelay (1/0)
def qlambdaDelay(name, noStateVars=2, noActions=4, noValues=5, logPeriod=100, maxDelay=1):

	command = [nodedelay, '_'+QLambda.noInputsConf+ ':=' + str(noStateVars), 
	'_'+QLambda.noOutputsConf+':='+str(noActions),
	'_'+QLambda.sampleCountConf+':='+str(noValues),
	'_'+QLambda.logPeriodConf+':='+str(logPeriod),
	'_'+QLambda.filterConf+':='+str(maxDelay)]

	g = NodeGroup("QLambda", True);
	g.addNode(command, "QLambda", "java");
	module = NeuralModule(name+'_QLambda', g, False)

	module.createEncoder(QLambda.topicAlpha,"float",1); 				# alpha config
	module.createEncoder(QLambda.topicGamma,"float",1);
	module.createEncoder(QLambda.topicLambda,"float",1);
	module.createEncoder(QLambda.topicImportance,"float",1);

	module.createDecoder(QLambda.topicProsperity,"float",3);			# float[]{prosperity, coverage, reward/step}

	module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)

	return module

def qlambdaConfiguredDelay(name, net, noStateVars=2, noActions=4, noValues=5, logPeriod=100, maxDelay=1):

	# build the node
	mod = qlambdaDelay(name, noStateVars,noActions, noValues, logPeriod, maxDelay)
	net.add(mod)

	# define the configuration
	net.make_input('alpha',[QLambda.DEF_ALPHA])
	net.make_input('gamma',[QLambda.DEF_GAMMA])
	net.make_input('lambda',[QLambda.DEF_LAMBDA])
	net.make_input('importance',[QLambda.DEF_IMPORTANCE])

	# wire it
	net.connect('alpha', mod.getTermination(QLambda.topicAlpha))
	net.connect('gamma', mod.getTermination(QLambda.topicGamma))
	net.connect('lambda', mod.getTermination(QLambda.topicLambda))
	net.connect('importance', mod.getTermination(QLambda.topicImportance))

	return mod
