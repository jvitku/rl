HANNS project - Nodes for Reinforcement Learning
================================================


Author Jaroslav Vitku [vitkujar@fel.cvut.cz]


About
------

This is a part of Hybrid Artificial Neural Network Systems (HANNS) project (see: http://artificiallife.co.nf ). 

Each node can be connected into a potentially heterogeneous network of nodes communicating via the ROS, potentially Nengoros ( http://nengoros.wordpress.com ). 

This is ROS meta-package (currently without direct catkin support), a collection of ROS(java) nodes.

 
Purpose of this repository
-----------------------

This project implement algorithms based on Reinforcement Learning (RL).


Technical notes
---------------

This is a collection of as domain-independent nodes as possible: run the node, send data in and receive the result. 

These nodes depend on rosjava\_core ( https://github.com/rosjava/rosjava_core ), are build by means of Gradle but they are compatible with ROS ( http://wiki.ros.org ). 
All these nodes will be catkinized for better support by ROS in the future.


Installation
------------------

The best way how to install these nodes so far, is to use them as a part of the NengoRos project (see: https://github.com/jvitku/nengoros )

TODO
----------

- check Motivation = 0, is it really random ASM???


Changelog
------------------

- new project SMDP will hold RL algorithm and action selection method as separated nodes (some synchronization challenges)
- project SARSA renamed to MDP

