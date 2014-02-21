# Create the NeuralModule which implements discrete RL algorithm - Q(lambda) - Q-learning with eligibility traces
#
# starts: 
#   -Neural module with the RL algorithm
#	-Four config parameter inputs with default value of parameters: alpha, gamma, lamda, importance
#   -Two signal generators: reward generator generates only 1st dim, state generator generates two state vars
#
# Test:
# 	-let the agent lear 
#	-move with the parameter IPORTANCE towards 1 - observe that the agent sticks to one of reward sources and exploits it
# 	-move the parameter IMPORTANCE lower - observe how the agent continues with exploration
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

net=nef.Network('Demo of NeuralModule which implements discrete Q-learning with eligibility trace')
net.add_to_nengo()  

#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 

finderA = rl_sarsa.qlambdaASMConfigured("RL", net, 2, 4)   # 2 state variables, 4 actions

#Create a white noise input function with params: baseFreq, maxFreq [rad/s], RMS, seed
# first dimension is reward, do not generate signal (ignored in the connection matrix)
generator=FunctionInput('StateGenerator', [FourierFunction(0,0,0,12),
    FourierFunction(.5, 11,1.6, 17),FourierFunction(.2, 21,1.1, 11)],Units.UNK) 

# first dimension is reward, do not generate states (these are ignored in the conneciton matrix)
reward=FunctionInput('RewardGenerator', [FourierFunction(.1, 10,1, 12),
        FourierFunction(0,0,0, 17),FourierFunction(0,0,0, 17),],Units.UNK)

net.add(generator)
net.add(reward)

tx=[[0 for j in range(3)] for i in range(3)]
tx[1][1] = 1;
tx[2][2] = 1;	# identity transform without first dimension (do not connect reward directly!)


tr=[[0 for j in range(3)] for i in range(3)]
tr[0][0] = 1;	# identity transform where only the dimension is connected


# data
#net.connect(generator,	finderA.newTerminationFor(QLambda.topicDataIn,[0,1,1]))
#net.connect(reward,		finderA.newTerminationFor(QLambda.topicDataIn,[1,0,0]))
net.connect(generator,	finderA.newTerminationFor(QLambda.topicDataIn,tx))
net.connect(reward,		finderA.newTerminationFor(QLambda.topicDataIn,tr))

print 'Done'
print 'Demo: two generators: one for states and one for reward.'
print 'Note that learning will not converge here.'
