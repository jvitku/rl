# Create the NeuralModule which receives 4 float values, finds min and max, converts them to int and passes to the output.
#
# starts: 
#   -ROS-java node (class extending the org.ros.Node) which does exactly the thing described above 
#   -NeuralModule with modem that communicates with the ROS node 
#
# For mode information see the  the script nr-demo/basic/basic_nodes.py
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils

import basic_nodes

# creates nef network and adds it to nengo (this must be first in the script) 
net=nef.Network('Demo of simple Neural modules which find min and max in received data and publish results')
net.add_to_nengo()  # here: delete old (toplevel) network and replace it with the newly CREATED one

################# setup the ROS utils (optional) 
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 


finderA = basic_nodes.async_minmaxint_node("MinMax Asynchronous Integer Finder") 	# build the neural module
many=net.add(finderA)                        							# add it into the network

finderB = basic_nodes.async_minmaxfloat_node("MinMax Synchronous Float Finder") 		# finds min and max and publishes floats
something=net.add(finderB)                        							# works in synchornous mode (Nengo waits for message each step)

#Create a white noise input function with params: baseFreq, maxFreq [rad/s], RMS, seed
generator=FunctionInput('Randomized input', [FourierFunction(.1, 10,1, 12),
    FourierFunction(.4, 20,1.5, 11),
    FourierFunction(.1, 10,0.9, 10),
    FourierFunction(.5, 11,1.6, 17)],Units.UNK) 

net.add(generator) 													# Add to the network and connect to neuron
net.connect(generator,	finderA.getTermination('org/hanns/demonodes/pubsub/IN'))# connect generator to the termnation on our neural module
net.connect(generator,	finderB.getTermination('org/hanns/demonodes/pubsub/IN')) 		# connect generator to the termnation on our neural module

# create ANN with 100 neurons which approximates it's input 
A=net.make('A',neurons=200,dimensions=2,radius=8)    				# identity transformation implemented by population of neurons
net.connect(finderA.getOrigin('org/hanns/demonodes/pubsub/OUT'),A)	# connect the origin on our module to the network

B=net.make('B',neurons=200,dimensions=2,radius=8)    				
net.connect(finderB.getOrigin('org/hanns/demonodes/pubsub/OUT'),	B)						


print 'Configuration complete.'
