                  How to build the ER Modeller sources

    TOOLS NEEDED

ER Modeller uses Ant and a Java Development Kit. The tools can be fetched from:

  - Java Development Kit, for example J2SE SDK 1.5 from
      http://java.sun.com/j2se/downloads/index.html

  - Ant, for example Ant 1.6.5 from
      http://archive.apache.org/dist/ant/binaries/

    TOOL INSTALLATION ON WINDOWS

This section explains how to install the tool-chain on windows.
  - Install the Java Development Kit 
  - Unpack the ant binary distribution somewhere

Assuming that we now have installed the above in the following directories

   C:\Program Files\Java\jdk1.5.0_06
   C:\apache-ant-1.6.5

To prepare for compiling you need to open a command window and do:

  set PATH=C:\Program Files\Java\jdk1.5.0_06\bin;c:\apache-ant-1.6.5\bin;%PATH%
  set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_06


    BUILDING

Once the tools are set up building ER Modeller can be done by running the
following command in this directory(where this file is located):

    ant all

This will build everything. Other targets which can be used are:
  build    - Main ER Modeller application
  compile - compiling sources
  languages - converting native files into unicode ascii files
  run - running erm.jar

