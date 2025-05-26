#
# Makefile
# Bricks : A Performance Evaluation System for 
#          Grid Computing Scheduling Algorithms
#

#setenv JAVA_VERSION 1.2

#JAVAC = javac -d ~/java
JAVAC = javac 
JAVA = java
#JAVAC = /usr/local/IBMJava2-13/bin/javac -d ~/java
#JAVA = /usr/local/IBMJava2-13/bin/java
#JAVA = java -prof

all:	util environment scheduling tools
	$(JAVAC) bricks/Simulate.java

env:	environment

sche:	scheduling

main:	
	$(JAVAC) bricks/Simulate.java

util:
	$(JAVAC) bricks/util/*.java

environment:
	$(JAVAC) bricks/environment/*.java

scheduling:
	$(JAVAC) bricks/scheduling/*.java

tools:
	$(JAVAC) bricks/tools/*.java

rsim:	RemoteSimulate.java
	$(JAVAC) bricks/RemoteSimulate.java
	
nws:
	$(JAVAC) bricks/plugin/nws/*.java

debug:	
	$(JAVAC) bricks/util/SimulationDebug.java

ver:	
	$(JAVA) -version

#run:
#	java Simulate 

clean:
	\rm 	bricks/*~ \
		bricks/environment/*~ \
		classbricks/scheduling/*~ \
		bricks/util/*~ \
		bricks/tools/*~ \
		bricks/plugin/*/*~

