package bricks.util;
import bricks.environment.*;
import java.util.*;

public abstract class TrafficData extends Data implements Cloneable {

    public double dataSize;

    // for ProbeData and PacketData
    //protected Node previousNode = null;

    public abstract String getName();
    public abstract String toOriginalString();

    protected Vector listOfRoute;
    protected Enumeration nextNodes;
    protected Node currentNode;
    protected int indexOfCurrentNode = 0;

/************************* public method *************************/
    /** 
     * generates a clone of the TrafficData.
     */
    public synchronized Object clone() {
        try {
            TrafficData d = (TrafficData)super.clone();
	    d.dataSize = this.dataSize;
	    d.timeEventComes = this.timeEventComes;
            return d;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public void setListOfRoute(Vector v) {
	this.listOfRoute = v;
    }

    // overriden by RequestedData
    public void gotoNextNode(double currentTime) {
	Node node = (Node)getNextNode();
	node.schedule(currentTime, this);
    }

    // this method is for class RequestedData and ProbeData.
    public Vector getSubList(double currentTime){
	return new Vector();
    }

    public void updateProcessingTime(double currentTime, double throughput) {
	processingTime = dataSize / throughput;
    }

    // only TrafficData
    public void updateTimeEventComes(double currentTime, double interval) {
	timeEventComes = currentTime + interval;
    }

    //public void initRoute(double currentTime, Vector listOfRoute) {
    //this.listOfRoute = listOfRoute;
    //nextNodes = listOfRoute.elements();
    //currentNode = (Node)nextNodes.nextElement();
    //}

    public Node nextNode() {
	//System.out.print("nextNode : " +
	//(Node)listOfRoute.elementAt(indexOfCurrentNode + 1) + "\t");
	return (Node)listOfRoute.elementAt(indexOfCurrentNode + 1);
    }

    public Vector getListOfRoute() {
	return (Vector)listOfRoute.clone();
    }

    public int getIndexOfCurrentNode() {
	return indexOfCurrentNode;
    }

    protected String generateId() {
	return  getName() + serialNumber++;
    }

    /* for debug */
    public String showListOfRoute() {
	return listOfRoute.toString();
    }

    public double getDataSize() {
	return dataSize;
    }

/************************* protected method *************************/
    protected Node getNextNode() {
	if (nextNodes.hasMoreElements()) {
	    currentNode = (Node)nextNodes.nextElement();
	    indexOfCurrentNode = indexOfCurrentNode + 1;
	} else {
	    error(getName() + " - No more nextNode Element [" + listOfRoute + "]");
	}
	//System.out.println(this + ".getNextNode : " + currentNode + "[" +
	//(indexOfCurrentNode - 1) + "] " + listOfRoute);
	return currentNode;
    }	

    protected String vectorToString(Vector vec) {
	Enumeration e = vec.elements();
	String str = null;
	while (e.hasMoreElements()) {
	    str += e.nextElement() + ", ";
	}
	return str;
    }
}


