#!/bin/sh
# How to configure ER modeller installed locally in your computer.
#			Careful! ER modeller does not work on my system (Debian) until I installed
#			JRE 1.5.0_08  - build 08 is important just for Debian 
#			it works properly on previous version on Solaris 9 
#			I did not tested on another Linux distributions - your notices are welcome
# 		The rest of variables have relative value to ER_PATH, hence there is no reason to change 
# 		their values.

# The script bellow shows how I am runnig ER modeller on my Linux (Debian)

#How do I register Java binaries globally on Linux?
#You must place the correct path to the installed binaries in your /etc/bashrc configuration file.
#for example:
#PATH=/jdk1.6.0/bin:/jdk1.6.0/jre/bin:$PATH 

#If you need to set a path to JRE uncomment and update these lines:
# export ER_JRE_PATH=/opt/jre1.5.0_08
# $ER_JRE_PATH/bin/java -jar erm.jar "$@"

#otherwise...
java -jar erm.jar "$@"