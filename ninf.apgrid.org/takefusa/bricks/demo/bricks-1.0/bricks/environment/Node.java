package bricks.environment;
import bricks.util.*;
import java.io.*;
import java.util.*;

public abstract class Node extends Obj {

    protected Vector nextLink;
    protected Data nextData;
    protected PrintWriter logWriter;
    protected abstract Data getNextData(double currentTime);
    public abstract void schedule(double currentTime, Data data);
    public abstract String toOriginalString(double currentTime);
    public abstract String getName();

/************************* public method *************************/

    /* caller:  SimulationSet.initLinks() */
    /* overriden by ClientNode and ServerNode */
    public void tracePath(Vector list) {

	/* Check return path */
	if (list.contains(this))
	    return;
	
	/* trace next node */
	list.addElement(this);
	Enumeration e = nextLink.elements();
	while (e.hasMoreElements()) {
	    Vector subList = (Vector)list.clone();
	    Node node = (Node)e.nextElement();
	    node.tracePath(subList);
	}
	return;
    }

    // overriden by PacketTransClientNode, PacketTransServerNode
    public void putInterarrivalTimeOfPackets(
	Sequence ng, Node toNode
    ) {;}

    public void initSession(double currentTime) {
	nextData = getNextData(currentTime);
	updateNextEvent(currentTime);
    }

    public boolean isFull() {
	return false;
    }

/************************* protected method *************************/
    protected void putLinkedNode(Node node) {
	nextLink.addElement(node);
    }

    protected void error(String message) throws RuntimeException {
	System.err.println(getName() + " Error:: " + message);
	throw new RuntimeException();
    }
}

