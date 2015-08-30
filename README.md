# spot-auto-scaling
An auto-scaling tool for web applications using spot instances

1. Overview

This tool aims to help web application providers reliably and cost-efficiently scale their application using spot instances. It provides an implementation for Amazon EC2 and a simluation verstion for tesing and concept validation. For the details of the algorithm, please refer to the paper

Chenaho Qu, Rodrigo N. Calheiros, and Rajkumar Buyya, Reliable and Cost-efficeint Auto-Scaling of Web Applications Using Heterogeneous Spot Instances.

2. Prerequiste
The auto-scaling system currently uses software-based load balancing solution. User need first to have a machine running the load balancer software. The machine running the auto-scaling tool should be able to ssh to it. In the load blanacer machine, it should have a script to dynamically make changes to the load balancer configuration. The script implemenation should be able to accept parameters as xxx/user_script.sh machine-1-address machine-1-weight machine-2-address machine-2-weight .....

If user employ other load balancer solutions or have a different interface to make configuration changes, he needs to extend the current code and integrate his own load balancer solution by implenmenting the ILoadBalancer interface and the ILoadBalancerLoader interface.

The tool also allow the user to configure newly started VMs before link them to the load balancer by implemnting the OnlineTask interface. The default solution in the tool is to configure the instance through ssh. We allow user to specify the path to the configuration script and the parameters in the configuration file. This require the user have the configuration file stored in the vm image and the machine running the auto-scaling too should be able to ssh the newly started machine. For other configuration ways, user needs to implement their own IOnlineTaskLoader and IOnlineTask.

3. Usage

Check out the code and use maven to compile:

first go to auto-scaling-core and run
maven clean package install

Then go to either auto-scaling-aws or auto-scaling-cloudsim to run the aws version or the simulation version
maven clean package install

Before starting the tool, user first needs to configure the application using configuration files. We provide the example configuration file in the conf folder for both versions.

Users can configure the log configuration by editing the log4j.xml file.

For other configurations, users need to replace all the xxx with their own settings.

To run the simulation version

java -Dlog4j.configurationFile=xxx/log4j2.xml -jar spot-auto-scaling-cloudsim-0.0.1-SNAPSHOT-jar-with-dependencies.jar $path_to_configuration_folder

To run the aws version

java -Dlog4j.configurationFile=xxx/log4j2.xml -jar spot-auto-scaling-cloudsim-0.0.1-SNAPSHOT-jar-with-dependencies.jar $path_to_configuration_folder

4. Reminder
User can change the list of spot vm types considered and their profile against the scaling application in the configuration file. If the user wants to use VM types that only support paravirtual image, don't forget to make images for both paravirtual and hvm hypervisors.
