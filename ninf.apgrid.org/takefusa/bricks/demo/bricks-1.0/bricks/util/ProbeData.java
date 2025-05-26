package bricks.util;
import bricks.environment.*;
import bricks.scheduling.*;
import java.util.*;

public class ProbeData extends TrafficData {

    protected static int serialNumber = 1;
    protected NodePair probedNetwork;

    public NetworkMonitor masterNetworkMonitor;
    public double startTime;
    public double totalDataSize;

    public ProbeData(
	NodePair probedNetwork, Vector route,
	NetworkMonitor masterNetworkMonitor, 
	double dataSize, double currentTime
    ) {
	id = generateId();
	processingTime = 0.0;
	this.probedNetwork = probedNetwork;
	this.masterNetworkMonitor = masterNetworkMonitor;
	this.dataSize = dataSize;
	totalDataSize = dataSize;
	startTime = currentTime;
	indexOfCurrentNode = 0;

	initListOfRoute(route);
	nextNodes = listOfRoute.elements();
	currentNode = (Node)nextNodes.nextElement();
	SimulationDebug.println(
	    "ProbeData: probedNetwork = " + probedNetwork + "(" + 
	    listOfRoute + ")"
	);
    }

/************************* needed method *************************/
    public String toOriginalString() {
	String str = "ProbeData<" + id + "> " + probedNetwork
	    + ",  " + Format.format(timeEventComes, 3) + ", " + 
	    Format.format(dataSize, 3) + "\n";
	return str;
    }

    public Vector getSubList(double currentTime) {
	//System.out.println(vectorToString(listOfRoute));
	return (Vector)listOfRoute.clone();
    }

    public String getName() {
	return "prd";
    }

    public void killed(double currentTime) {
	SimulationDebug.println(
	    "ProbeData(" + this + "): " + Format.format(currentTime, 3) +
	    " : killed"
	);
	probedNetwork.finishProbing(currentTime, this);
    }

/************************* protected method *************************/
    /** 
     * initListOfRoute changes the route 
     *	from [[source] [network 0] ... [network n] [destination]]
     *	to   [[network 0] [network 0] ... [network n] TerminalNode]
     **/
    protected void initListOfRoute(Vector route) {
	listOfRoute = route;
	listOfRoute.setElementAt(listOfRoute.elementAt(1), 0);
	listOfRoute.setElementAt(new TerminalNode(), listOfRoute.size()-1);
	// for Packets
	nextNodes = listOfRoute.elements();
    }
}
