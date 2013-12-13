Implementation of the SARSA Algorithm - ROS Node
====================================================

Author Jaroslav Vitku [vitkujar@fel.cvut.cz]

About
------

Project which should implement simple Step-Action-Reward-Step-Action (**SARSA**) type of **Reinforcement Learning** (RL) algorithm. 

This ROS node will be used mainly in Hybrid Artificial Neural Network Systems, used in the ROS network or the Nengoros simulator ( http://nengoros.wordpress.com ). 


Installation
------------------

The best way how to use these nodes is by means of the NengoRos project (see: https://github.com/jvitku/nengoros )

Currently, the installation employs the `linkdata` shell script, located under `nengoros/demonodes`. 

For more information how to use this script, see [demonodes/README.md](https://github.com/jvitku/demonodes/blob/nengoros-master-v0.0.2/README.md) or run from the folder `nengoros/demonodes` this:

	./linkdata -h


Usage Information 
----------------

Node has two types of inputs and one type of output:

* Reinforcement: one input whose value determines amount of current reinforcement
* Data: this usually multidimensional input determines the input to the RL module
* Output: this (potentially multidimensional) output determines an action selected by the SARSA algorithm

Before usage, the number of input/output connections has to be specified. This determines necessary properties of algorithm data types (matrix dimensions etc.).
Then, the class instance is obtained and starts communication across the ROS network.
From this point, the ROS node can be use as a SARSA module.





