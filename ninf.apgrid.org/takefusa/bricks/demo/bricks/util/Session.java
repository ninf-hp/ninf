package bricks.util;
import java.io.*;
import java.util.*;

/** Class Session */
public class Session {
    //Node currentNode;
    Obj currentObj;
    double currentTime = 0.0;
    SimulationSet simulationSet;

    Vector objList;

    public Session(SimulationSet simulationSet) {
	this.simulationSet = simulationSet;
	initSession();
	//debug();
    }

/************************* public method *************************/
    public void nextTimeStep() {
	double earliestTime = Double.POSITIVE_INFINITY;
	Enumeration e = simulationSet.objs();
	while (e.hasMoreElements()) {
	    Obj obj = (Obj)e.nextElement();
	    if (obj.nextEventTime < earliestTime) {
		earliestTime = obj.nextEventTime;
		currentObj = obj;
	    }
	}
	currentTime = earliestTime;
    }

    public boolean finish (double timeFinishedSimulation) {
	if ( currentTime >= timeFinishedSimulation ) {
	    simulationSet.finish();
	    return true;
	} else {
	    return false;
	}
    }

    public boolean finish (int numRequestedData) {
	if ( RequestedData.numRequestedData >= numRequestedData ) {
	    simulationSet.finish();
	    return true;
	} else {
	    return false;
	}
    }

    public void step() {
	//System.out.println(
	//SimulationDebug.println(
	//Format.format(currentTime, 3) + ": " + currentObj);
	currentObj.processEvent(currentTime);
	currentObj.updateNextEvent(currentTime);
	//debug();
    }

    public double getCurrentTime() {
	return currentTime;
    }

    String toOrignalString() {
	String str = 
	    "\n==================================================\n\n";
	str += "currentObj = " + currentObj + 
	    ", currentTime = " + currentTime + "\n";
	str += simulationSet.toOriginalString(currentTime);
	return str;
    }

/************************* private common method *************************/
    private void debug() {
	System.out.println(toOrignalString());
    }

/************************* private method *************************/
    /*
    private void initSession() {
	boolean flag = true;

	Enumeration e = simulationSet.objs();
	while (e.hasMoreElements()) {
	    Obj obj = (Obj) e.nextElement();
	    obj.initSession(currentTime);
	    if (flag) {
		nodeList.init(node);
	    } else {
		nodeList.put(node);
	    }
	    System.out.println("nodeList size = " + nodeList.size());
	}
    }
    */

    private void initSession() {
	Enumeration e = simulationSet.objs();
	double earliestTime = Double.POSITIVE_INFINITY;

	while (e.hasMoreElements()) {
	    Obj obj = (Obj)e.nextElement();
	    obj.initSession(currentTime);
	    if (obj.nextEventTime < earliestTime) {
		earliestTime = obj.nextEventTime;
		currentObj = obj;
	    }
	}
	currentTime = earliestTime;
    }
}

