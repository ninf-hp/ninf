package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;
import java.io.*;

public abstract class NetworkMonitor extends Obj implements SchedulingUnit {

    protected PrintWriter logWriter;

    protected Sequence interprobingTime;
    protected Sequence probeDataSize;
    protected String keyOfResourceDB;
    protected ResourceDB resourceDB;

    // nodes
    protected Vector networkList = new Vector();

/************************* needed method *************************/
    public String toInitString() {
	return getName() + " : " + key + "\n";
    }

    public void init(SimulationSet owner) {
	this.owner = owner;
	this.logWriter = owner.networkMonitorLogWriter;
	this.resourceDB = owner.getResourceDB(keyOfResourceDB);

	Enumeration e = owner.routes();
	while (e.hasMoreElements()) {
	    networkList.addElement((NodePair)e.nextElement());
	}
	//System.out.println("networkList: " + networkList);
    }

    /**************************************************/

    public void initSession(double currentTime) {
	updateNextEvent(currentTime);
    }

    public void printLog(String log) {
	if (logWriter != null)
	    logWriter.println(log);
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
    public void collectNetworkInfo(NodePair pair, NetworkInfo networkInfo) {
	resourceDB.putNetworkInfo(networkInfo);
	printLog(this + " " + networkInfo);
	//SimulationDebug.println(
	//"### Monitor ### " + this + " " + networkInfo
	//);
    }

/************************* protected method *************************/

    protected void probe(double currentTime) {

	//SimulationDebug.println(owner.routeService);
	Enumeration e = networkList.elements();
	while (e.hasMoreElements()) {
	    NodePair pair = (NodePair)e.nextElement();
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
}
