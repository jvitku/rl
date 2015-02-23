# The same as the previous one, but here, the forgetting coverage and reward are combined into the prosperity.
#
# All values are printed into files.
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

class ProsperitySaver(nef.SimpleNode):
	def init(self):
		self.val = [0,0,0];
	def termination_data(self, values, dimensions=3):
		self.val = values;
	def tick(self):
		#f=file('data.txt','a+')
		f=file(self.name,'a+')
		f.write('%1.3f %s\n'%(self.t,' '.join(map(str, self.val))))
		f.close()

# this method is taken from (and modified to use custom class) the rl/sarsa/python/rosnodes/rl_sarsa.py
classMOO = "org.hanns.rl.discrete.ros.sarsa.config.QlambdaCoverageReward" 
#  Publishes: {composed prosperity, BinaryCoverageForgetting, BinaryRewardPerStep}

def qlambdaMOO(name, noStateVars=2, noActions=4, noValues=5, logPeriod=100, maxDelay=1):
	command = [classMOO, '_'+QLambda.noInputsConf+ ':=' + str(noStateVars), 
	'_'+QLambda.noOutputsConf+':='+str(noActions),
	'_'+QLambda.sampleCountConf+':='+str(noValues),
	'_'+QLambda.logPeriodConf+':='+str(logPeriod),
	'_'+QLambda.filterConf+':='+str(maxDelay)]
	g = NodeGroup("RL", True);
	g.addNode(command, "RL", "java");
	module = NeuralModule(name+'_QLambda', g, False)
	module.createEncoder(QLambda.topicAlpha,"float",1); 				# alpha config
	module.createEncoder(QLambda.topicGamma,"float",1);
	module.createEncoder(QLambda.topicLambda,"float",1);
	module.createEncoder(QLambda.topicImportance,"float",1);
	module.createDecoder(QLambda.topicProsperity,"float", 3);			# float[]{prosperity, coverage, reward/step}
	module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
	module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
	return module

# build configuration of the experiment with given RL parameters
def buildSimulation(alpha, gamma, lambdaa, importance,expName='test0'):
	net=nef.Network('HandWired parameters of RL node to bias')
	net.add_to_nengo()  

	#rl = rl_sarsa.qlambda("RL", noStateVars=2, noActions=4, noValues=20, logPeriod=2000)
	rl = rl_sarsa.qlambdaASM("RL", noStateVars=2, noActions=4, noValues=20, logPeriod=2000)
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

#f = open('data/tmp/ea_%d.txt'%expNo, 'w');
#sx = Saver('ea_%d_agents.txt'%expNo);		# saves best agent from actual generation during the evolution into a file


#t = 20	# 20/0.001= 20 000 steps ~ 10 000 RL steps 
t = 80
dt = 0.001
runs = 5
base = 'noea_importance'
#vals = [0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]

vals = [0.5, 0.6, 0.7, 0.8, 0.9, 1]
# run the experiment several times, plot average in the matlab
for j in range(len(vals)):
	print 'xxxxxxx testing the value: '+str(vals[j])
	for i in range(runs):
		name = base +'_' + str(vals[j])+ '_%d'%i;
		#name+"_"+str(vals[j])
		print '----------------- starting experiment named: '+name
		prosp = evalConfiguration(QLambda.DEF_ALPHA,QLambda.DEF_GAMMA,QLambda.DEF_LAMBDA,vals[j],t,dt,name)
		print '----------------- exp named: '+name+' done, the value is '+str(prosp[0])
		
	
#prosp = evalConfiguration(QLambda.DEF_ALPHA,QLambda.DEF_GAMMA,QLambda.DEF_LAMBDA,QLambda.DEF_IMPORTANCE,t,dt)#0.01)#







