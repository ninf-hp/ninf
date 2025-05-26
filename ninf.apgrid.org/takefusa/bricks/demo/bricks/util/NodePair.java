package bricks.util;
import bricks.environment.*;
import bricks.scheduling.*;
import java.util.*;

public class NodePair implements Cloneable {

    public Node sourceNode;
    public Node destinationNode;
    protected Vector route;
    //protected int no;

    public NodePair(Node sourceNode, Node destinationNode) {
	this.sourceNode = sourceNode;
	this.destinationNode = destinationNode;
    }

    /******************** needed method ********************/

    public int hashCode() {
	return sourceNode.hashCode() + destinationNode.hashCode();
    }

    public boolean equals(Object o) {
	if (!(o instanceof NodePair))
	    return false;

	//SimulationDebug.println("this.sourceNode = " + sourceNode + 
	//", this.destinationNode = " + destinationNode);
	//SimulationDebug.println("o.sourceNode = " + ((NodePair)o).sourceNode +
	//", o.destinationNode = " + ((NodePair)o).destinationNode);

	//if (sourceNode.equals(((NodePair)o).sourceNode) &&
	//destinationNode.equals(((NodePair)o).destinationNode))
	if ((sourceNode.key).compareTo((((NodePair)o).sourceNode).key) == 0 &&
	    (destinationNode.key).compareTo((((NodePair)o).destinationNode).key) == 0)
	    return true;
	else
	    return false;
    }

    public String toString() {
	return "[" + sourceNode + " - " + destinationNode + "]";
    }

    /******************** public method for monitoring ********************/

    public synchronized Object clone() {
        try {
	    NodePair p = (NodePair)super.clone();
	    //p.no = no;
	    if (route != null)
		p.route = (Vector)route.clone();
	    return p;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    //public int getNo() {
    //return no;
    //}

    public void probe(
	NetworkMonitor networkMonitor, Vector route,
	double currentTime, double dataSize
    ) {
	this.route = route;
	//this.no = no;
	SimulationDebug.println(networkMonitor + ": [" + route + "] " +
				" currentTime = " + currentTime +
				", dataSize" + dataSize);
	ProbeData pd = new ProbeData(
	    this, route, networkMonitor, dataSize, currentTime
	);
	NetworkNode firstNetworkNode = (NetworkNode)route.firstElement();
	firstNetworkNode.probe(currentTime, pd);
    }

    public void finishProbing(double currentTime, ProbeData probeData) {
	SimulationDebug.println(
	    this + ".finishProbing: " + Format.format(currentTime, 3) + 
	    " : probeData = " + probeData
	);
	double latency = currentTime - probeData.startTime;
	double currentThroughput = probeData.totalDataSize / latency;

	NetworkMonitor networkMonitor = probeData.masterNetworkMonitor;
	networkMonitor.collectNetworkInfo(
	    this,  
	    new NetworkInfo(this, currentTime, currentThroughput, latency)
	);
    }


    /************************* test *************************/

    public static void main(String[] argv) {

	ClientNode c1 = new ClientNode("c1");
	System.out.println("new ClientNode c1 : " + c1);
	ClientNode c2 = new ClientNode("c2");
	System.out.println("new ClientNode c2 : " + c2);

	ClientNode c3 = new ClientNode("c3");
	System.out.println("new ClientNode c3 : " + c3);
	ClientNode c4 = new ClientNode("c4");
	System.out.println("new ClientNode c4 : " + c4);

	NodePair p1 = new NodePair(c1, c2);
	NodePair p2 = new NodePair(c3, c4);

	NodePair p3 = (NodePair)p1.clone();

	System.out.println("comparison: p1(" + p1 + ") & p2(" + p2 + ")");
	if (p1.equals(p2))
	    System.out.println("p1 == p2");
	else
	    System.out.println("p1 != p2");

	System.out.println("comparison: p1(" + p1 + ") & p3(" + p3 + ")");
	if (p1.equals(p3))
	    System.out.println("p1 == p3");
	else
	    System.out.println("p1 != p3");
    }
}
