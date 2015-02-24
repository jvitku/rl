# Create the NeuralModule which implements discrete RL algorithm - Q(lambda) - Q-learning with eligibility traces
#
# Start the benchmark B
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

net=nef.Network('Demo of SARSA RL module interacting with the simulator of discrete 2D world with obstacles and rewards')
net.add_to_nengo()  

rl = rl_sarsa.qlambdaASMConfigured("RL",net, noStateVars=2, noActions=4, noValues=30)   # 2 state variables, 4 actions, xsize=30

# TODO this seems not to work now..
world = gridworld.benchmarkA("map_30x30","BenchmarkGridWorldNodeB");
net.add(world)

# data
net.connect(world.getOrigin(QLambda.topicDataIn), rl.newTerminationFor(QLambda.topicDataIn))
net.connect(rl.getOrigin(QLambda.topicDataOut), world.getTermination(QLambda.topicDataOut))

print 'Configuration complete.'
