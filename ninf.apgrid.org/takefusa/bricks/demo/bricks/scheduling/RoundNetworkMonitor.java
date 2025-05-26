package bricks.scheduling;
import bricks.util.*;
import java.util.*;
import java.io.*;

/** 
 * RoundNetworkMonitor(String keyOfResourceDB, Sequece interprobingTime,
 *                     Sequence probeDataSize)
 **/
public class RoundNetworkMonitor extends NetworkMonitor implements SchedulingUnit {

    Vector tokens = new Vector(10);
    Vector currentProbingNetworks = new Vector(10);

    public RoundNetworkMonitor(
	String keyOfResourceDB,	Sequence interprobingTime,
	Sequence probeDataSize
    ) {
	this.keyOfResourceDB = keyOfResourceDB;
	this.interprobingTime = interprobingTime;
	this.probeDataSize = probeDataSize;
    }

/************************* needed method *************************/
    // override
    public String getName() {
	return "RoundNetworkMonitor";
    }

    public void init(SimulationSet owner) {
	super.init(owner);
	//System.out.println("networkList: " + networkList);
	tokens.addElement(new Integer(0));
    }

    public void updateNextEvent(double currentTime) {
	//event = PROBE;
	nextEventTime =
	    nextEventTime + interprobingTime.nextDouble(currentTime);
    }
    
    public void processEvent(double currentTime) {
	probe(currentTime);
    }

/************************* public method *************************/
    // override
    public void collectNetworkInfo(NodePair pair, NetworkInfo networkInfo) {
	super.collectNetworkInfo(pair, networkInfo);

	int indexOfTokens = currentProbingNetworks.indexOf(pair);
	int token = ((Integer)tokens.elementAt(indexOfTokens)).intValue();
	token++;

	if (token == networkList.size()) { /* finish probe */
	    currentProbingNetworks.removeElementAt(indexOfTokens);
	    tokens.removeElementAt(indexOfTokens);

	} else { /* next probe */
	    tokens.setElementAt(new Integer(token), indexOfTokens);
	    NodePair next = (NodePair)networkList.elementAt(token);
	    currentProbingNetworks.setElementAt(next, indexOfTokens);
	    probe(next, networkInfo.probeTime);
	}
    }


/************************* protected method *************************/
    protected void probe(double currentTime) {

	tokens.addElement(new Integer(0));
	NodePair next = (NodePair)networkList.firstElement();
	currentProbingNetworks.addElement(next);
	probe(next, currentTime);
    }

    protected void probe(NodePair pair, double currentTime) {

	/* have to make clone of NodePair! */
	NodePair newPair = (NodePair)pair.clone();
	Vector route = owner.getRoute(pair);

	if (route == null)
	    System.out.println("Error:NetworkMonitor: route is null!");

	SimulationDebug.println("probe network: " + pair);
	newPair.probe(
	    this, route, currentTime, probeDataSize.nextDouble(currentTime)
	);
    }
}
