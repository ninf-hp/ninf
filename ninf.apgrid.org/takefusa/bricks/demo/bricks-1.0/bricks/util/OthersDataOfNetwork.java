package bricks.util;
import bricks.environment.*;
import java.util.*;

public class OthersDataOfNetwork extends Data {
    protected NetworkNode networkNode;
    protected double dataSize;

    public OthersDataOfNetwork(
	NetworkNode networkNode, double timeEventComes, double dataSize
    ) {
	id = generateId();
	processingTime = 0.0;
	this.networkNode = networkNode;
	this.timeEventComes = timeEventComes;
	this.dataSize = dataSize;
    }

/************************* needed method *************************/
    public String toOriginalString() {
	String str = "thersDataOfNetwork<" + id + "> " + networkNode
	    + ",  " + Format.format(timeEventComes, 3) + ", " + 
	    Format.format(dataSize, 3) + "\n";
	return str;
    }

    public void updateProcessingTime(double currentTime, double throughput) {
	processingTime = dataSize / throughput;
    }

    public String getName() {
	return "OthersDataOfNetwork";
    }

    public double getDataSize() {
	return dataSize;
    }

/************************* protected method *************************/
    protected static String generateId() {
	return "odn" + serialNumber++;
    }
}
