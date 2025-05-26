package bricks.util;
import bricks.environment.*;
import java.util.*;

/** Class Data
 **   static  : clientNode, networkNode, serverNode,
 **		dataSize.., numInstructions
 **   dynamic : timeEventComes, queueintTime, logTime[] */

public abstract class Data {

    public double timeEventComes;
    protected double processingTime;
    public static long serialNumber = 0L;
    public String id;

    public abstract void updateProcessingTime(
	double currentTime, double throughput
    );
    public abstract String getName();
    public abstract double getDataSize();
    public abstract String toOriginalString();
    public String toString() {
	return id;
    }

/************************* method needed to override *************************/
    public void gotoNextNode(double currentTime) {;}
    public void outLogString(double currentTime) {;}
    public void updateDataSize() {;}
    public void returnRoute() {;}

/************************* default method *************************/
    public void updateTimeEventComes(double currentTime) {
	timeEventComes = currentTime + processingTime;
    }

    protected void error (String message) throws RuntimeException {
	System.err.println("Data Error:: " + message);
	throw new RuntimeException();
    }
}
