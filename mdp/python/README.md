Jython Integratioin with Nengo
------------------------------

Files in these folders will be symlinked into the `nengo/simulator-ui/` subfolders for use in the NengoRos system.

Naming Convention
-------------------

In order to get unique names across projects, each script/graphic should be named according to the following convention:

	[project name]_[subproject name]_[optional subproject name]_[optional node name]



Purpose of Particular Folders are:
---------------------------------

1. **rosnodes** - These Jython scripts should contain pre-written methods for simpler use of **Java/C++/Python ROS nodes** in the Nengoros simulation (see examples in the `nr-demo` folder). Destination location:

		nengo/simulator-ui/python/rosnodes


2. **nef/templates** - Together with `../images/nengoIcons', the scripts in this folder initialize a **graphical representation** of ROS nodes (networks of ROS nodes), see [Nengo tutorial](http://nengo.ca/docs/html/advanced/dragndrop.html) for this project. Destination location:

		nengo/simulator-ui/python/nef/templates/



3. **nr-demo** - This folder contains demo scripts showing how to use ROS nodes contained in this project. Destination location:

		nengo/simulator-ui/nr-demo



4. **scripts** - This folder can contain project-related scripts which are not for demo purposes. Destination location:

		nengo/simulatir-ui/scripts


Script used for Linking
-----------------------

Script which symlinks these data is located in the project demonodes under:

	demonodes/linkdata


