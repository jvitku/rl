# Drag and drop template for the Nengo simulator which represents ROS node implementing particular logic operation.
#
# by Jaroslav Vitku
#
# for more information how to make such template, see: http://nengo.ca/docs/html/advanced/dragndrop.html or notes/add_node_to_gui.md
#

import nef
import math
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from org.hanns.rl.discrete.ros.sarsa import QLambda as QLambda


# node utils..
title='RL_SARSA'
label='RL_SARSA'
icon='rl_sarsa.png'

# parameters for initializing the node
params=[
('name','Select name for the RL SARSA',str),
('noStateVars','No. of state variables',int),
('sampleCount','Samplling: no. of values of state variables - all the same',int),
('noActions','No. of allowed acitons',int),
('logPeriod','Log info each n-th step',int),
('maxDelay','Max length of the action-state loop in steps',int),
('independent','Can be group pndependent? (pushed into namespace?) select true',bool)
]

# try to instantiate node with given parameters (e.g. check name..)
def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass


def make(net,name='NeuralModule which implements RL SARSA algorithm', 
independent=True, useQuick=True, prospLen=3, noStateVars=2, noActions=4, sampleCount=30,logPeriod=100, maxDelay=1, synchronous=True):

    # full name of the reosjava node to be started
    classname = "org.hanns.rl.discrete.ros.sarsa.config.QlambdaCoverageReward";
    
    #command to launch and configure the RL rosjava node
    command = [classname, '_'+QLambda.noInputsConf+ ':=' + str(noStateVars),
    '_'+QLambda.noOutputsConf+':='+str(noActions),
    '_'+QLambda.sampleCountConf+':='+str(sampleCount),
    '_'+QLambda.logPeriodConf+':='+str(logPeriod),
    '_'+QLambda.filterConf+':='+str(maxDelay)];
	
    # create a group with a given name
    g = NodeGroup(name, independent);    	
    g.addNode(command, "rl_sarsa", "java");     # start and configure the rosjava node
    module = NeuralModule(name+'_QLambda', g);  # create the neural module representing the node
    
    # create config IO
    module.createConfigEncoder(QLambda.topicAlpha,"float",QLambda.DEF_ALPHA); 	# alpha config input, def. value is DEF_ALPHA
    module.createConfigEncoder(QLambda.topicGamma,"float",QLambda.DEF_GAMMA);
    module.createConfigEncoder(QLambda.topicLambda,"float",QLambda.DEF_LAMBDA);
    module.createEncoder(QLambda.topicImportance,"float", 1);					# default value is 0
    
    # QLambdaCoverageReward classname => float[]{prosperity, coverage, reward/step}
    module.createDecoder(QLambda.topicProsperity, "float", prospLen);			
    
    # create data IO
    module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
    module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
    
    many=net.add(module)                    # add it into the network

