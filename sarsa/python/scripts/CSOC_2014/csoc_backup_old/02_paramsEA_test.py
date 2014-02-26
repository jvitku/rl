# Architecture containing gridWorld simulator and RL nodes. 
# The RL node has configuration connected by weighted connections to bias. 
#
# In this script, the default values of parameters are used.
#
# The values of the paramters will be determined by the EA, based on the prosperity measure.
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

import rl_sarsa
import gridworld
# build configuration of the experiment with given RL parameters
def buildExperiment(alpha, gamma, lambdaa, importance):
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
	return net
	
# runs the simulation for given time, expects RL_QLambda node in given network, returns prosperity
def runExperiment(net,t,dt):
	
	print 'running the network for '+str(t)+' with step '+str(dt)
	net.reset()
	net.run(t,dt)
	
	rl = net.get("RL_QLambda")
	prosp = rl.getOrigin(QLambda.topicProsperity).getValues().getValues(); # read the prosperity
	return prosp;
	

def evalInd(ind):
	# TODO here get parameters from the ind
    net = buildExperiment(alpha,gamma, lambdaa, importance);
    prosperity = runExperiment(net,t,dt);
    ind.setFitness(prosperity);
    return prosperity

# experiment setup - constants
mr = 25;
ii = 0;

INdim = 2;
OUTdim = 1;
minw = -0.0;
maxw = 0.3;

# which setup to use?
config=4

if config == 1: # no 4 from the 1annea file
    useRecurrent = True
    pMut = 0.15
    pCross = 0.9
    popsize = 25;
    maxgen = 70;
    N=4;

numRuns=10;

for expNo in range(numRuns):

  print '----------------------- experiment number %d'%expNo
  # init EA
  ea = EA(INdim, OUTdim, N, maxgen,popsize,minw,maxw);
  ea.setProbabilities(pMut,pCross);
  ea.initPop();
  f = open('data/ea_%d.txt'%expNo, 'w');

  # evolution insert here
  while ea.wantsEval():
      print 'Gen: '+repr(ea.generation())+'/'+repr(maxgen)+' actual ind is ' +repr(ea.actualOne())+'/'+repr(popsize)+' best so far: '+repr(ea.getBestFitness());

      ind = ea.getInd();
      #ind.printMatrix();        

      error = evalInd(ind);
      ind.getFitness().setError(error);

      print 'Ind: '+repr(ea.actualOne())+' Error is: '+repr(error) +' fitness is: '+repr(ind.getFitness().get());

      print ea.getActualWeights();

      # evaluated the last individual in the generatio? write stats
      if (ea.actualOne() == (popsize-1)):
          print 'check: '+repr(ea.generation())
          fit = ea.getBestInd().getFitness().get();
          er = ea.getBestInd().getFitness().getError();
          print '%d %.5f %.5f\n' % (ea.generation(),fit,er)
          f.write('%d %.8f %.8f\n' % (ea.generation(),fit,er))
          f.flush()
          os.fsync(f.fileno()) # just write it to disk

      # poc++ and check end of ea
      ea.nextIndividual();

  f.close()

# load the best one found
ind = ea.getIndNo(ea.getBest());
net = buildExperiment(ind);
print 'best fitness is:'
print ind.getFitness().get();

print 'done\n\n\n'




