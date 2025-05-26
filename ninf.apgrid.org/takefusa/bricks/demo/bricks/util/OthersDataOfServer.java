package bricks.util;
import bricks.environment.*;
import java.util.*;

public class OthersDataOfServer extends Data {
    protected ServerNode serverNode;
    protected double numInstructions;
    
    public OthersDataOfServer(
	ServerNode serverNode,
	double timeEventComes,
	double numInstructions
    ) {
	id = generateId();
	processingTime = 0.0;
	this.serverNode = serverNode;
	this.timeEventComes = timeEventComes;
	this.numInstructions = numInstructions;
    }

/************************* needed method *************************/
    public void updateProcessingTime(double currentTime, double throughput) {
	processingTime = numInstructions / throughput;
    }

    public String toOriginalString() {
	String str = "OthersDataOfServer<" + id + "> " + serverNode + ",  " +
	    Format.format(timeEventComes, 3) + ",  " + 
	    Format.format(numInstructions, 3) + "\n";
	return str;
    }

    public String getName() {
	return "OthersDataOfServer";
    }

    public double getDataSize() {
	return numInstructions;
    }

/************************* protected method *************************/
    protected static String generateId() {
	return "ods" + serialNumber++;
    }
}
