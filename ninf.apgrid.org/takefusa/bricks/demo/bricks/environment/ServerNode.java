package bricks.environment;
import bricks.util.*;
import java.io.*;
import java.util.*;

/** 
 * ServerNode.java
 * <pre>
 * ServerNode represents the computational resources of the given global 
 * computing system, and is parameterized by e.g., performance, load, and
 * their variance over time.
 * The data injected by ServerNode are decomposed into logical packets.
 * </pre>
 */
public class ServerNode extends NodeWithQueue {

    /** 
     * The number of executed Instructions invoked by other users.
     */
    protected Sequence numInstructions;

    protected RingBuffer traceBuffer;
    protected Hashtable hashtableOfRoutes = new Hashtable();

    /** 
     * Packets decomposes transmitted data into logical packets and 
     * sends them into a NetworkNode.
     */
    protected Packets packets;

    /**
     * Constructs a ServerNode with
     *     owner : a SimulationSet of your configuration,
     *     key : a key of the ServerNode,
     *     queue : a queue which represents performance, congestion, and
     *             their variance of the ServerNode,
     *     numInstructions : numInstructions of the ServerNode,
     *     interarrivalTime : The interval that the ServerNode invokes 
     *                        extraneous jobs.
     */
    public ServerNode(
	SimulationSet owner, String key, Queue queue,
	Sequence numInstructions, Sequence interarrivalTime
    ){
	this.owner = owner;
	this.key = key;
	this.queue = queue;
	queue.owner = this;
	this.numInstructions = numInstructions;
	this.interarrivalTime = interarrivalTime;
	this.logWriter = owner.serverLogWriter;
	nextLink = new Vector();
	traceBuffer = new RingBuffer(512);
	init();
	packets = new Packets(this, (owner.logicalPacket).packetSize);
    }

    /**
     * Constructs a ServerNode with
     *     traceBufferSize : the size of the trace buffer.
     */
    public ServerNode(
	SimulationSet owner, String key, Queue queue,
	Sequence numInstructions, Sequence interarrivalTime,
	int traceBufferSize
    ){
	this.owner = owner;
	this.key = key;
	this.queue = queue;
	queue.owner = this;
	this.numInstructions = numInstructions;
	this.interarrivalTime = interarrivalTime;
	logWriter = owner.serverLogWriter;
	nextLink = new Vector();
	traceBuffer = new RingBuffer(traceBufferSize);
	init();
	packets = new Packets(this, (owner.logicalPacket).packetSize);
    }

/************************* needed method *************************/
    /** 
     * returns a type of the ServerNode.
     */
    public String getName() {
	return "ServerNode";
    }

    /** 
     * processEvent
     *     SEND : sends a processed job.
     *     QUEUE : processes queue event.
     *     OTHERSDATA : puts extraneous job into queue of the ServerNode.
     */
    public void processEvent(double currentTime) {
	if (event == SEND) {
	    SimulationDebug.println(Format.format(currentTime, 3) + " " +
				    this + " 's event = SEND");
	    SimulationDebug.println(
		"ServerNode:" + Format.format(currentTime, 4) + 
		":" + this + ": SEND_PACKET"
	    );
	    // send RequestedData packet
	    packets.sendFirstPacket(currentTime);

	} else if (event == QUEUE) {
	    SimulationDebug.println(Format.format(currentTime, 3) + " " + 
				    this + " 's event = QUEUE");
	    // change source & destination !!
	    Data top = queue.top();
	    // processEvent of servedJob
	    queue.processEventForPackets(currentTime);
	    if (top instanceof TrafficData) {
		top.returnRoute();
		SimulationDebug.println(currentTime + " : " + this +
					": QUEUE with Packet: send " + top);
		packets.divideTrafficData(currentTime, (TrafficData)top);
		packets.sendFirstPacket(currentTime);
	    }
	    outQueueLength(currentTime, queue.size());

	} else { // OthersData
	    // get OthersData from comingEventList
	    if (!queue.isFull()) {
		schedule(currentTime, nextData);
	    }
	    nextData = getNextData(currentTime);
	}
    }

    /**
     * updateNextEvent finds next event which occurs earliest.
     **/
    public void updateNextEvent(double currentTime) {
	event = OTHERSDATA;
	double earliestTime = nextData.timeEventComes;
	if (!packets.isEmpty()) {
	    TrafficData requestedData = packets.firstPacket();
	    if (requestedData.timeEventComes < earliestTime) {
		earliestTime = requestedData.timeEventComes;
		event = SEND;
	    }
	}
	if (!queue.isEmpty()) {
	    if (queue.getTimeQueueEventComes() < earliestTime) {
		earliestTime = queue.getTimeQueueEventComes();
		event = QUEUE;
	    }
	}
	nextEventTime = earliestTime;
    }

    // override
    public void tracePath(Vector list) {

	/* Check return path */
	if (list.contains(this))
	    return;
	
	list.addElement(this);
	if (list.size() == 1) { /* Start */
	    /* trace next node */
	    Enumeration e = nextLink.elements();
	    while (e.hasMoreElements()) {
		Vector subList = (Vector)list.clone();
		Node node = (Node)e.nextElement();
		node.tracePath(subList);
	    }
	} else { /* End */
	    Node node = (Node)list.firstElement();
	    if (node instanceof ClientNode) {
		((ClientNode)node).addRoute(this, list);

	    } else if (node instanceof ServerNode) {
		((ServerNode)node).addRoute(this, list);

	    } else {
		System.err.println("configuration file");
		System.exit(3);
	    }
	}
	return;
    }

    public void addRoute(Node node, Vector list) {
	hashtableOfRoutes.put(node, list);
    }

    /* caller: RouteService */
    public Hashtable getRoutes() {
	return hashtableOfRoutes;
    }

    // override
    protected Data getNextData(double currentTime) {
	double timeEventComes = 
	    currentTime + interarrivalTime.nextDouble(currentTime);
	Data odos = new OthersDataOfServer(
	    this, timeEventComes, numInstructions.nextDouble(currentTime)
	);
	return odos;
    }

    // override
    protected void printLog(double currentTime, String log) {
	if (logWriter != null)
	    logWriter.println(
		this + " " + Format.format(currentTime, 3) + " " + log
	    );
	trace(currentTime, queue.isEmpty(), queue.size());
    }

    public String toOriginalString(double currentTime) {
	String str = "  [" + getName() + " " + key + "] : ";
	//str += "IdleTime = " + currentIdleTime(currentTime) + "\n";
	str += "  nextData : " + nextData.toOriginalString();
	str += "  servedData : " + queue.top() + "\n";
	str += queue.toOriginalString();
	return str;
    }

    public String toInitString() {
	String str = "  [" + getName() + " " + key + "] : ";
	str += queue.getName() + ", " + 
	    numInstructions.mean + ", " + interarrivalTime.mean + "\n";
	str += queue.toOriginalString();
	return str;
    }

/************************* public method *************************/
    /**
     * returns StaticServerInfo at First
     */
    public StaticServerInfo getStaticServerInfo() {
	return new StaticServerInfo(this, queue.maxThroughput);
    }

    /** 
     * returns ServerInfo that denotes current status of the ServerNode.
     */
    public ServerInfo getServerInfo(double currentTime, double trackingTime) {

	int index = 0;
	double busyTime = 0;
	double load = 0;
	double previousTime = currentTime;

	//System.out.println("traceBuffer.size = " + traceBuffer.size());
	for (int i = 0 ; i < traceBuffer.size() ; i++) {
	    ServerTrace serverTrace = (ServerTrace)traceBuffer.get(i);
	    if (serverTrace == null) {
		break;
	    }
	    if (!serverTrace.idle) {
		busyTime = busyTime + previousTime - serverTrace.time;
	    }
	    load = load + (double)serverTrace.queueLength * 
		(previousTime - serverTrace.time);
	    previousTime = serverTrace.time;

	    if (currentTime - previousTime >= trackingTime) {
		break;
	    }
	}

	if (currentTime == previousTime) {
	    load = 0.0;
	    busyTime = 0.0;
	} else {
	    load = load / (currentTime - previousTime);
	    busyTime = busyTime / (currentTime - previousTime);
	}

	if (queue.isInterpolationCpu()) {
	    load = queue.getLoad(currentTime, trackingTime) + load;
	    busyTime = queue.getCpuUtilization(currentTime, trackingTime) + 
		busyTime;
	    if (busyTime > 1.0) {
		busyTime = 1.0;
	    }
	}

	return new ServerInfo(this, currentTime, trackingTime, load, busyTime);
    }

/************************* protected method *************************/
    protected void trace(double currentTime, boolean idle, int length){
	ServerTrace serverTrace = new ServerTrace(currentTime, idle, length);
	traceBuffer.put(serverTrace);
    }

/*************** overriden method defined by NodeWithQueue ***************/
    public void putInterarrivalTimeOfPackets(Sequence ng, Node toNode) {
	packets.putInterarrivalTimeOfPackets(ng, toNode);
    }
}
