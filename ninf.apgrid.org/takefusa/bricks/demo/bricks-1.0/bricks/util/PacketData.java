package bricks.util;
import bricks.environment.*;
import java.util.*;

public class PacketData extends TrafficData {

    protected static int serialNumber = 1;

    public PacketData(double dataSize, Vector listOfRoute) {
	this.processingTime = 0.0;
	this.dataSize = dataSize;
	id = generateId();
	this.listOfRoute = listOfRoute;
	indexOfCurrentNode = 0;
	nextNodes = listOfRoute.elements();
	currentNode = (Node)nextNodes.nextElement();
    }

/************************* needed method *************************/
    public String getName() {
	return "pad";
    }

/************************* overriden method *************************/
    public String toOriginalString() {
	String str = "PacketData<" + id + "> " + 
	    Format.format(timeEventComes, 3) + ",  " +
	    Format.format(dataSize, 3) + "\n";
	return str;
    }
}
