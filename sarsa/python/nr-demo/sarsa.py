# Create the NeuralModule which implements discrete RL algorithm - Q(lambda) - Q-learning with eligibility traces
#
# starts: 
#   -ROS-java node which receives description of wold state (together with a reinforcement) and publishes (executes) one of possible actions
#   -NeuralModule with modem that communicates with the ROS node (represents ROS node in the Nengoros)
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

net=nef.Network('Demo of NeuralModule which implements discrete RL')
net.add_to_nengo()  

#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 

finderA = rl_sarsa.qlambda("RL",2, 4)   # 2 state variables, 4 actions
many=net.add(finderA)

#Create a white noise input function with params: baseFreq, maxFreq [rad/s], RMS, seed
generator=FunctionInput('StateGenerator', [FourierFunction(.1, 10,1, 12),
    FourierFunction(.5, 11,1.6, 17),FourierFunction(.5, 11,1.6, 17)],Units.UNK) 
    
reward=FunctionInput('RewardGenerator', [FourierFunction(.1, 10,1, 12),
        FourierFunction(.5, 11,1.6, 17),FourierFunction(.5, 11,1.6, 17)],Units.UNK)
        
net.add(generator)
net.add(reward)
net.connect(generator,	finderA.newTerminationFor(QLambda.topicDataIn,[0,1,1]))
net.connect(reward,	finderA.newTerminationFor(QLambda.topicDataIn,[1,0,0]))


print 'Configuration complete.'
