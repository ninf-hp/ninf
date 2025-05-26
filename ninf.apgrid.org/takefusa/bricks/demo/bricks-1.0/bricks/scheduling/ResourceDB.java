package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.io.*;
import java.util.*;

public abstract class ResourceDB {

    public String key;
    protected SimulationSet owner;

    public double aveServerPerformance;
    public double aveNetworkThroughput;

    // key & node info
    protected Hashtable serverState = new Hashtable();
    protected Hashtable networkState = new Hashtable();
    protected int numNetworkHistory = 128;
    protected int numServerHistory = 128;

    protected Hashtable staticServerInfoDB = new Hashtable();
    protected Hashtable staticNetworkInfoDB = new Hashtable();
    protected Hashtable schedulingDB = new Hashtable();

/************************* public method *************************/
    public void init(SimulationSet owner) {

	this.owner = owner;

	SimulationDebug.println("init ServerState in ResourceDB");
	initServerState();
	initStaticServerInfoDB();
	initSchedulingDB();

	SimulationDebug.println("init NetworkState in ResourceDB, size = " +
				owner.routeService.size());
	initNetworkState();
	initStaticNetworkInfoDB();
    }

    public void finish() {;}

    // need to modify!!!
    public Enumeration servers(RequestedData requestedData) {
	return serverState.keys();
    }

    public Enumeration servers() {
	return serverState.keys();
    }

    // need to modify!!!
    public int numServers(RequestedData requestedData) {
	return serverState.size();
    }

    public int numServers() {
	return serverState.size();
    }

    public void putServerInfo(ServerInfo serverInfo) {
	RingBuffer buffer = (RingBuffer)serverState.get(serverInfo.owner);
	buffer.put(serverInfo);
    }

    public void putNetworkInfo(NetworkInfo networkInfo) {
	NodePair pair = networkInfo.owner;
	SimulationDebug.println(
	   "Pair[" + pair.sourceNode + "-" + pair.destinationNode + "]"
	);
	RingBuffer buffer = (RingBuffer)networkState.get(pair);
	buffer.put(networkInfo);
    }

    public Vector getServerList() {
	Vector v = new Vector(serverState.size());
	Enumeration e = serverState.keys();
	while (e.hasMoreElements()) {
	    v.addElement(e.nextElement());
	}
	return v;
    }

    public StaticServerInfo getStaticServerInfo(ServerNode server) {
	return (StaticServerInfo)staticServerInfoDB.get(server);
    }

    public ServerInfo getServerInfo(ServerNode server) {
	RingBuffer buffer = (RingBuffer)serverState.get(server);
	ServerInfo info = (ServerInfo)buffer.get();
	if (info == null)
	    info = new ServerInfo(server, 1.0);
	return info;
    }

    public ServerInfo getServerInfoAt(ServerNode server, int index) {
	RingBuffer buffer = (RingBuffer)serverState.get(server);
	return (ServerInfo)buffer.get(index);
    }

    public NetworkInfo getNetworkInfo(NodePair pair) {
	RingBuffer buffer = (RingBuffer)networkState.get(pair);
	NetworkInfo info = (NetworkInfo)buffer.get();
	if (info == null) {
	    StaticNetworkInfo sinfo = 
		(StaticNetworkInfo)staticNetworkInfoDB.get(pair);
	    //System.out.println(pair + " - " + sinfo);
	    info = new NetworkInfo(pair, sinfo.maxThroughput / 2.0);
	}
	return info;
    }

    public NetworkInfo getNetworkInfo(Node sourceNode, Node destinationNode) {
	NodePair pair = new NodePair(sourceNode, destinationNode);
	return getNetworkInfo(pair);
    }

    public NetworkInfo getNetworkInfoAt(
	Node sourceNode, Node destinationNode, int index
    ) {
	NodePair pair = new NodePair(sourceNode, destinationNode);
	return getNetworkInfoAt(pair, index);
    }

    public NetworkInfo getNetworkInfoAt(NodePair pair, int index) {
	RingBuffer buffer = (RingBuffer)networkState.get(pair);
	return (NetworkInfo)buffer.get(index);
    }

    public void putSchedulingInfo(RequestedData data) {
	Vector v = (Vector)schedulingDB.get(data.server);
	v.addElement(data);
    }

    public void putSchedulingInfo(EPTask eptask) {
	Vector v = (Vector)schedulingDB.get(eptask.server);
	v.addElement(eptask);
    }

    public Vector getSchedulingInfo(ServerNode server) {
	return (Vector)schedulingDB.get(server);
    }

    public void removeSchedulingInfo(RequestedData data) {
	Vector v = (Vector)schedulingDB.get(data.server);
	v.removeElement(data);
    }

    public void removeSchedulingInfo(EPTask eptask) {
	Vector v = (Vector)schedulingDB.get(eptask.server);
	v.removeElement(eptask);
    }


/************************* protected method *************************/
    protected void initServerState() {
	Enumeration e = owner.servers();
	while (e.hasMoreElements()) {
	    ServerNode server = (ServerNode)e.nextElement();
	    RingBuffer buffer = new RingBuffer(numServerHistory);
	    serverState.put(server, buffer);
	}
    }

    protected void initStaticServerInfoDB() {
	Enumeration e = owner.servers();
	aveServerPerformance = 0.0;
	while (e.hasMoreElements()) {
	    ServerNode server = (ServerNode)e.nextElement();
	    StaticServerInfo info = server.getStaticServerInfo();
	    staticServerInfoDB.put(server, info);
	    aveServerPerformance = aveServerPerformance + info.performance;
	}
	aveServerPerformance = aveServerPerformance / owner.numServers();
	//System.out.println(this + ": aveServerPerformance = " + aveServerPerformance);
    }

    protected void initNetworkState() {

	Enumeration e = owner.routes();
	//SimulationDebug.println("- init NetworkState in ResourceDB");
	while (e.hasMoreElements()) {
	    NodePair pair = (NodePair)e.nextElement();
	    //SimulationDebug.println("init pair: " + pair + " at ResourceDB");
	    RingBuffer buffer = new RingBuffer(numNetworkHistory);
	    networkState.put(pair, buffer);
	}
    }

    protected void initStaticNetworkInfoDB() {

	Enumeration e = owner.routes();
	aveNetworkThroughput = 0.0;
	int numRoutes = 0;
	while (e.hasMoreElements()) {

	    NodePair pair = (NodePair)e.nextElement();
	    if (((pair.sourceNode instanceof ClientNode) && 
		 (pair.destinationNode instanceof ServerNode)) ||
		((pair.sourceNode instanceof ServerNode) && 
		 (pair.destinationNode instanceof ClientNode))) {

		double throughput = 
		    getMinThroughput(pair.sourceNode, pair.destinationNode);
		staticNetworkInfoDB.put(
		    pair, new StaticNetworkInfo(pair, throughput)
		);
		aveNetworkThroughput = aveNetworkThroughput + throughput;
		numRoutes++;
	    }
	}
	aveNetworkThroughput = aveNetworkThroughput / numRoutes;
	SimulationDebug.println("aveNetworkThroughput = " + aveNetworkThroughput);
    }

    protected double getMinThroughput(Node sourceNode, Node destinationNode) {
	Vector route = owner.getRoute(sourceNode, destinationNode);
	double throughput = 0.0;
	for (int i = route.size() - 2; i > 0 ; i--) {
	    Node node = (Node)route.elementAt(i);
	    if (node instanceof NetworkNode) {
		NetworkNode networkNode = (NetworkNode)node;
		if (throughput < networkNode.queue.maxThroughput) {
		    throughput = networkNode.queue.maxThroughput;
		}
	    }
	}
	//System.out.println("minThroughput = " + throughput);
	return throughput;
    }

    protected void initSchedulingDB() {
	Enumeration e = owner.servers();
	while (e.hasMoreElements()) {
	    ServerNode server = (ServerNode)e.nextElement();
	    Vector tmp = new Vector(100);
	    schedulingDB.put(server, tmp);
	}
    }
}
