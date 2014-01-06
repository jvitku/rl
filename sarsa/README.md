Implementation of the SARSA Algorithm - ROS Node
====================================================

Author Jaroslav Vitku [vitkujar@fel.cvut.cz]

About
------

Project which should implement simple Step-Action-Reward-Step-Action (**SARSA**) type of **Reinforcement Learning** (RL) algorithm. 

This ROS node will be used mainly in Hybrid Artificial Neural Network Systems, used in the ROS network or the [Nengoros simulator](http://nengoros.wordpress.com). 


Requirements
------------------

Rosjava [core](https://github.com/jvitku/nengoros).

Installation
------------------

Installation can be obtained by running the following command

	./gradlew installApp

In case of any problems, the best way how to use these nodes is by means of the NengoRos project (see: [https://github.com/jvitku/nengoros](https://github.com/jvitku/nengoros) )

Usage Information 
----------------

Node has two types of inputs and one type of output:

* Reinforcement: one input whose value determines amount of current reinforcement
* Data: this usually multidimensional input determines the input to the RL module
* Output: this (potentially multidimensional) output determines an action selected by the SARSA algorithm

Before usage, the number of input/output connections has to be specified. This determines necessary properties of algorithm data types (matrix dimensions etc.).
Then, the class instance is obtained and starts communication across the ROS network.
From this point, the ROS node can be use as a SARSA module.

Running the Demo
---------------------

The algorithm can be ran either as a NeuralModule in the Nengoros simulator, or as a standalone ROS node. 

## Standalone ROS Node

The start scripts are prepared in the `rl/sarsa` folder which simplify launching the nodes. There are three following nodes:

* **QLambda node** (`org.hanns.rl.discrete.ros.sarsa.QLambda`) which implements the Q(lambda) discrete RL algorithm with eligibility traces
* **GridWorldNode** (`org.hanns.rl.discrete.ros.testnodes.GridWorldNode`) which implements simple simulator with a grid world and a source of reinforcement
* **QLambdaConfigurator** (`org.hanns.rl.discrete.ros.testnodes.QLambdaConfigurator`) which serves as an example of online configuration of ROS RL node

The following should be started from the `rl/sarsa` folder for successful running the demo:

	jroscore
	./conf 		# RL algorithm configuration
	./map		# simple grid world simulator
	./qlambda	# Q(lambda) discrete RL algorithm

## Nengoros Integration

The standard jython scripts are included in the `rl/sarsa/python` folder, so [linking the data](http://nengoros.wordpress.com/tutorials/integrating-new-project-with-the-nengoros/) into the simulator and following one of tutorials [how to run ROS nodes](http://nengoros.wordpress.com/tutorials/demo-2-publisher-subscriber/) in the Nengoros is sufficient. To link data, run the following command from the folder `nengoros/demonodes`:

	./linkdata -cf ../rl/sarsa
	
For example how to use the QLambda node, start the Nengoros simulator and write the following command into the console:

	run nr-demo/sarsa/demo.py


TODO and Design Details
-----------------------

Node configuration is defined on two main levels:

* **Final configuration** is defined by means of ROS parameters received (ideally) during the ROS node startup. The configuration parameter may change during the simulation, but this will most likely result in the resetting the algorithm data.

* **Online Configuration** will be determined online by means of config inputs. These inputs are basically the same as data inputs and their values can be changed during the simulation.

## Discrete SARSA
* **Number of (domain independent) actions** is received as the final configuration (in the configuration) received as ROS node parameter during the start


* **Number of world states** is another question, this could be done in one of the following manners:

	* States are defined by array of positive integers on the interval `<0;x>`
 	* ROS communication by means of String messages (of known set) defining possible states (not very universal)
 	* Automatic identification of states (e.g. integers on the interval `<?,?>`)

	Or several possibilities from above which cane be turned on/off by the static configuration.

### Configuration

#### Inputs Outputs

* Actions:
	
	* The algorithm select 1ofN actions, so **single integer defining** how many actions to make. For compatibility with the Nengo, these will be represented exactly as 1ofN. Data is vector of N numbers, where only one value is set to 1. 
	
	* The configuration is **final** for now
	* Therefore this configuration **determines number of outputs** (Nengoros)

* States

	* State can be potentially multidimensional, while supposing that the received values are on **range of <0,1>**, the sampling can be defined by two parameters as:
	
		* Sampling resolution (how many samples between <0,1>)
		* Size of input data (length of received vector of values on the interval <0,1>)
		
	* The configuration is **final** for now
	* This configuration (also) **determines number of inputs** (Nengoros)
	
	The sampling resolution can be set the same for all inputs or for each one separately (different size of Q-learning matrix)

#### Algorithm parameters

These **online** configurations determine the parameters of the algorithm, namely:

* Learning algorithm setup:
	* **Alpha** - Learning rate from <0,1>
	* **Gamma** - Forgetting factor from <0,1>
		
* Exploration vs. exploitation setup:
	
	* **Epsilon** - Epsilon-greedy action selection algorithm. Parameter from <0,1>. With the probability of `Epsilon`, the algorithm selects action randomly with uniform distribution. With the probability of `1-Epsilon`, the greedy selection strategy is used.
		
* Eligibility traces setup:
	
	* **Lambda** - Eligibility traces parameter from <0,1>. If Lambda=0, the algorithm is one step TD, if Lambda=1, the algorithm becomes Monte-Carlo Method.
	* **Eligibitily Length** - final length of the eligibility trace (for purposes of implementation).

	
	
### Running the Algorithm

After launching the node:

* the algorithm is setup (if no configuration parameters foud, the defualt ones are loaded) 
* and the node waits for data.
* if the data sample is received, this is considered as a simulation step, which means:
	* one algorithm step is made and output values are updated

## TODO

* Implement Q-matrix which is dynamically allocated
* Add also the NOOP action everywhere (index is -1)


