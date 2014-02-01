# Create the NeuralModule which implements discrete RL algorithm - Q(lambda) - Q-learning with eligibility traces
#
# starts: 
#   -Neural module with the RL algorithm
#	-Three config parameter inputs with default value of parameters: alpha, gamma, lamda
#   -Two signal generators: reward generator generates only 1st dim, state generator generates two state vars
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from org.hanns.rl.discrete.ros.sarsa import QLambda
import rl_sarsa
import gridworld

net=nef.Network('Demo of SARSA RL module interacting with the simulator of discrete 2D world with one reward')
net.add_to_nengo()  

#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 

rl = rl_sarsa.qlambdaConfigured("RL",net, 2 ,4, 10)   # 2 state variables, 5 actions, xsize=20

world = gridworld.example("map");
net.add(world)

# data
net.connect(world.getOrigin(QLambda.topicDataIn), rl.newTerminationFor(QLambda.topicDataIn))
net.connect(rl.getOrigin(QLambda.topicDataOut), world.getTermination(QLambda.topicDataOut))

net.view()
net.run(1,0.001)
print 'Configuration complete.'

