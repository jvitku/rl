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
independent=True, useQuick=True):

    prospLen = 3;
    command = [classname];

    # create group with a name
    g = NodeGroup(name, independent);    	# create independent group called..
    g.addNode(command, "rl_sarsa", "java"); # start java node
    module = NeuralModule(name+'_QLambda', g);
    
    # create config IO
    module.createConfigEncoder(QLambda.topicAlpha,"float",QLambda.DEF_ALPHA); 	# alpha config input, def. value is DEF_ALPHA
    module.createConfigEncoder(QLambda.topicGamma,"float",QLambda.DEF_GAMMA);
    module.createConfigEncoder(QLambda.topicLambda,"float",QLambda.DEF_LAMBDA);
    module.createEncoder(QLambda.topicImportance,"float",1);					# default value is 0
    
    # QLambdaCoverageReward classname => float[]{prosperity, coverage, reward/step}
    module.createDecoder(QLambda.topicProsperity, "float", prospLen);			
    
    # create data IO
    module.createDecoder(QLambda.topicDataOut, "float", noActions)  	# decode actions
    module.createEncoder(QLambda.topicDataIn, "float", noStateVars+1) 	# encode states (first is reward)
    
    many=net.add(module)                    # add it into the network

