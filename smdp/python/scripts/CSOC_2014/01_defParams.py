# Architecture containing gridWorld simulator and RL nodes. 
#
# The RL node has values on configuration inputs defined by weighted connections to bias (1). 
#
# In this script, the default values of parameters are used.
#
# The values of the paramters can be optimized by an external elgorithm based on the prosperity measure.
# 
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from org.hanns.rl.discrete.ros.sarsa import QLambda as QLambda
import ca.nengo.model.impl.RealOutputImpl

import rl_sarsa
import gridworld

# log data into the file
class ProsperitySaver(nef.SimpleNode):
	def init(self):
		self.val = [0];
	def termination_data(self,values):
		self.val = values;
	def tick(self):
		#f=file('data.txt','a+')
		f=file(self.name,'a+')
		f.write('%1.3f %s\n'%(self.t,' '.join(map(str, self.val))))
		f.close()

# build configuration of the experiment with given RL parameters
def buildSimulation(alpha, gamma, lambdaa, importance,expName='test0'):
	net=nef.Network('HandWired parameters of RL node to bias')
	net.add_to_nengo()  

	rl = rl_sarsa.qlambda("RL", noStateVars=2, noActions=4, noValues=20, logPeriod=2000)
	world = gridworld.benchmarkA("map_20x20","BenchmarkGridWorldNodeC",10000);
	net.add(rl)									    # place them into the network
	net.add(world)

	# connect them together
	net.connect(world.getOrigin(QLambda.topicDataIn), rl.newTerminationFor(QLambda.topicDataIn))
	net.connect(rl.getOrigin(QLambda.topicDataOut), world.getTermination(QLambda.topicDataOut))

	# define the parameter sources (controllable from the simulation window)
	net.make_input('alpha',[alpha])
	net.make_input('gamma',[gamma])
	net.make_input('lambda',[lambdaa])
	net.make_input('importance',[importance])

	# connect signal sources to the RL node
	net.connect('alpha', rl.getTermination(QLambda.topicAlpha))
	net.connect('gamma', rl.getTermination(QLambda.topicGamma))
	net.connect('lambda', rl.getTermination(QLambda.topicLambda))
	net.connect('importance', rl.getTermination(QLambda.topicImportance))
	
	saver = net.add(ProsperitySaver('data_'+expName+'.txt'))
	net.connect(rl.getOrigin(QLambda.topicProsperity),saver.getTermination("data"));
	return net
	
# build configuration and run the eperiment for given amount of time, return the prosperity
def evalConfiguration(alpha,gamma, lambdaa, importance,t,dt,name):
	net = buildSimulation(alpha, gamma, lambdaa, importance,name)
	
	rl = net.get("RL_QLambda")
	net.reset()
	net.run(t,dt)
	prosp = rl.getOrigin(QLambda.topicProsperity).getValues().getValues(); # read the prosperity
	return prosp;

#t = 20	# 20/0.001= 20 000 steps ~ 10 000 RL steps 
t = 20
dt = 0.001
runs = 1
base = 'noea'
# run the experiment several times, plot average in the matlab
for i in range(runs):
	name = base + '_%d'%i;
	print '----------------- starting experiment named: '+name
	prosp = evalConfiguration(QLambda.DEF_ALPHA,QLambda.DEF_GAMMA,QLambda.DEF_LAMBDA,QLambda.DEF_IMPORTANCE,t,dt,name)
	print '----------------- exp named: '+name+' done, the value is '+str(prosp[0])
	
net = buildSimulation(QLambda.DEF_ALPHA,QLambda.DEF_GAMMA,QLambda.DEF_LAMBDA,QLambda.DEF_IMPORTANCE,expName='01_defaultParams'):




