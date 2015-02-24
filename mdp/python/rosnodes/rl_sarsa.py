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
classs = "org.hanns.rl.discrete.ros.sarsa.QLambda"
#  Publishes prosperity as follows: {composed prosperity, BinaryCoverage, BinaryRewardPerStep}
classMOO = "org.hanns.rl.discrete.ros.sarsa.config.QlambdaCoverageReward" 
	

def qlambdaASM(name, noStateVars=2, noActions=4, noValues=5, logPeriod=100, maxDelay=1,
classname="org.hanns.rl.discrete.ros.sarsa.config.QlambdaCoverageReward", prospLen=3,synchronous=True):
	"""Method that returns synchronous NeuralModule implementing the QLambda algorithm with 
	the ASM inbuilt. Configuration parameters of the node can be found in 
	the javadoc. Number of data inputs (size of the vector) to the module 
	is determined as 1+*noStateVars*, where the first element is the reward value.
	
	The parameter *maxDelay* describes the number of time-steps in the closed-loop learning, 
	that is: how many time steps the module should wait before change of the state
	to evalueate that the action had no-effect (state of the world has not changed).
	
	Note: if the configEncoders (config INs) are not connected, the default values
	are sent to the ROS node instead of zeros. So non-configured QLambda module
	will learn with use of predefined parameters. 
	
	:param string mame: name of the neural module (TODO) to be created
	:param integer noStateVars: number of state variables to be taken into account 
	:param integer noActions: number of actions the node can produce (encoding 1ofN is used)
	:param integer noValues: the number of values that is expected for each state variable (interval [0,1] is sampled)
	:param integer logPeriod: how often to print out the data
	:param integer maxDelay: max delay in the closed-loop learning
	:param string classname: full className of the ROS node to be launched
	:param integer prospLen: size of the vector expected from the nodes prosperity publisher
	:returns: NeuralModule that should be added into the network, the node represents the QLambda ROS node 
	"""
	# this command is used to launch the ROSjava node
	command = [classname, '_'+QLambda.noInputsConf+ ':=' + str(noStateVars), 
	'_'+QLambda.noOutputsConf+':='+str(noActions),
	'_'+QLambda.sampleCountConf+':='+str(noValues),
	'_'+QLambda.logPeriodConf+':='+str(logPeriod),
	'_'+QLambda.filterConf+':='+str(maxDelay)]

	# represent the ROS node by means of Neural Module
	g = NodeGroup("RL", True);
	g.addNode(command, "RL", "java");
	module = NeuralModule(name+'_QLambda', g, synchronous)

	# create config IO
	module.createConfigEncoder(QLambda.topicAlpha,"float",QLambda.DEF_ALPHA); 	# alpha config input, def. value is DEF_ALPHA
	module.createConfigEncoder(QLambda.topicGamma,"float",QLambda.DEF_GAMMA);
	module.createConfigEncoder(QLambda.topicLambda,"float",QLambda.DEF_LAMBDA);
	module.createEncoder(QLambda.topicImportance,"float",1);					# default value is 0

	# QLambdaCoverageReward classname => float[]{prosperity, coverage, reward/step}
	module.createDecoder(QLambda.topicProsperity, "float", prospLen);			

	# create data IO
	module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)

	return module


def qlambdaASMConfigured(name, net, noStateVars=2, noActions=4, noValues=5, logPeriod=100, maxDelay=1,
classname="org.hanns.rl.discrete.ros.sarsa.config.QlambdaCoverageReward", prospLen=3, synchronous=True):
	"""
	Similarly to the :func:`rl_sarsa:qlambdaASM`, this method builds the QLambda NeuralModule. 
	But this method takes the nef.Network, creates NeuralModule, adds it into the network, 
	adds signal inputs to each of the config inputs.
	
	:param string mame: name of the neural module (TODO) to be created
	:param net: nef.Network that the NeuralModule and its signal inputs will be added into
	:param integer noStateVars: number of state variables to be taken into account 
	:param integer noActions: number of actions the node can produce (encoding 1ofN is used)
	:param integer noValues: the number of values that is expected for each state variable (interval [0,1] is sampled)
	:param integer logPeriod: how often to print out the data
	:param integer maxDelay: max delay in the closed-loop learning
	:param string classname: full className of the ROS node to be launched
	:param integer prospLen: size of the vector expected from the nodes prosperity publisher
	:returns: NeuralModule that is added into the net, the node represents the QLambda ROS node
	"""
	# build the node
	mod = qlambdaASM(name, noStateVars, noActions, noValues, logPeriod, maxDelay, classname, prospLen, synchronous)
	net.add(mod)

	# define the configuration inputs
	net.make_input('alpha',[QLambda.DEF_ALPHA])
	net.make_input('gamma',[QLambda.DEF_GAMMA])
	net.make_input('lambda',[QLambda.DEF_LAMBDA])
	net.make_input('importance',[QLambda.DEF_IMPORTANCE])

	# wire them to the module
	net.connect('alpha', mod.getTermination(QLambda.topicAlpha))
	net.connect('gamma', mod.getTermination(QLambda.topicGamma))
	net.connect('lambda', mod.getTermination(QLambda.topicLambda))
	net.connect('importance', mod.getTermination(QLambda.topicImportance))

	return mod

# TMP: doc notes:
# values: 	For example, with ``dim_pre=2`` and ``dim_post=3``, 
# ref to functions:  	Method that returns QLambda node with ASM inbuilt. Helper function used by :func:`nef.Network.connect()` to create
# ref to params: 	*index_pre* and *index_post* are used to determine which values are 
# params:
#	:param integer dim_pre: first dimension of transform matrix
#	:type index_pre: list of integers or a single integer
#	:returns: a two-dimensional transform matrix performing the requested routing        
# to the topic QLambda.topicDataIn publishes noStateVars+1, where the first value in the vector is reward