# This is python interface for the project demonodes/basic. 
# This helps using particular ROsJava nodes in Jython scripts for Nengo simulator.
#
# Import this in your scirpt in order to use it, for examples see ../nr-demo/ scripts.
#
#
# author Jaroslav Vitku

import nef
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
# Note that modules without outputs (Nengo-decoder) cannot use synchronous mode (DefaultNeuralModule).
# The simulator waits each simulation step for the output in the synchronous mode, this case:
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule

# java classes
minmaxint =   	"org.hanns.demonodes.pubsub.MinMaxInt"
mmf = 			"org.hanns.demonodes.pubsub.MinMaxFloat"

time = 			"org.hanns.demonodes.time.TimeAwareNode"


# Finds min and max values form vector of 4 input floats and publishes them as Integers
# Asynchronous communication with Nengo.
def async_minmaxint_node(name):
	g = NodeGroup("AsynMinMaxInt", True);        			# create independent (True) group called..
	g.addNode(minmaxint, "AsynMinMaxInt", "java");     		# start java node and name it finder
	module = NeuralModule(name+'_AsynMinMaxInt', g, False)   # construct the Neural Module
	# add encoder to the module (input)
	# It is output on modem which is connected to input (topic name) to the ROS node
	module.createEncoder("org/hanns/demonodes/pubsub/IN", "float", 4)  	# ..called TERMINATION of SimpleNode
	# Add decoder to the module (output):
	# It is input if modem which is connected to output (topic name) of the ROS node
	module.createDecoder("org/hanns/demonodes/pubsub/OUT", "int", 2)    # ..called ORIGIN of SimpleNode
	return module


# The same as above with synchronous communication
def sync_minmaxint_node(name):
	g = NodeGroup("SynMinMaxInt", True);        			
	g.addNode(minmaxint, "SynMinMaxInt", "java");     		
	module = NeuralModule(name+'_SynMinMaxInt', g)    	# here: Nengo waits each step for response from node
	module.createEncoder("org/hanns/demonodes/pubsub/IN", "float", 4)  			
	module.createDecoder("org/hanns/demonodes/pubsub/OUT", "int", 2)    			
	return module

def sync_minmaxfloat_node(name):
	g = NodeGroup("SynMinMaxFloat", True);        			
	g.addNode(mmf, "SynMinMaxFloat", "java");     		
	module = NeuralModule(name+'_SynMinMaxFloat', g)    	# here: Nengo waits each step for response from node
	module.createEncoder("org/hanns/demonodes/pubsub/IN", "float", 4)  			
	neuron.createDecoder("org/hanns/demonodes/pubsub/OUT", "float", 2)  # add FLOAT publisher
	return module

def async_minmaxfloat_node(name):
	g = NodeGroup("AsynMinMaxFloat", True);        			
	g.addNode(mmf, "AsynMinMaxFloat", "java");     		
	module = NeuralModule(name+'_AsynMinMaxFloat', g, False)    	
	module.createEncoder("org/hanns/demonodes/pubsub/IN", "float", 4)  			
	module.createDecoder("org/hanns/demonodes/pubsub/OUT", "float", 2)    		
	return module

def async_timeaware_node(name):
	g = NodeGroup("AsynTimeAwareNode", True);        			
	g.addNode(time, "AsynTimeAwareNode", "java");     		
	module = NeuralModule(name+'_AsynTimeAwareNode', g, False)    	
	module.createEncoder("org/hanns/demonodes/pubsub/IN", "float", 4)			
	module.createDecoder("org/hanns/demonodes/pubsub/OUT", "float", 2)
	return module
	
def sync_timeaware_node(name):
	g = NodeGroup("SynMinMaxFloat", True);        			
	g.addNode(time, "SynMinMaxFloat", "java");     		
	module = NeuralModule(name+'_SynMinMaxFloat', g)    	
	module.createEncoder("org/hanns/demonodes/pubsub/IN", "float", 4)  			
	module.createDecoder("org/hanns/demonodes/pubsub/OUT", "float", 2)    		
	return module


