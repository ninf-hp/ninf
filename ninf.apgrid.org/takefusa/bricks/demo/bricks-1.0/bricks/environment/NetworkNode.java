package bricks.environment;
import bricks.util.*;
import java.io.*;
import java.util.*;

/** 
 * NetworkNode.java
 * <pre>
 * NetworkNode represents the (wide-area) network interconnecting 
 * a client and a server, and is parameterized by e.g., bandwidth, 
 * congestion, and their variance over time.
 * </pre>
 */
public class NetworkNode extends NodeWithQueue {

    /** 
     * The size of extraneous arribal data invoked from other nodes. 
     */
    protected Sequence dataSize;

    protected Packets probePackets;

    protected static final int PROBE = 3;

    /**
     * Constructs a NetworkNode with
     *     owner : a SimulationSet of your configuration,
     *     key : a key of the NetworkNode,
     *     queue : a queue which represents bandwidth, congestion, and
     *             their variance of the NetworkNode,
     *     dataSize : dataSize of the NetworkNode,
     *     interarrivalTime : The interval that the NetworkNode invokes 
     *                        extraneous data.
     *     logWriter : PrintWriter for a NetworkNodes log file.
     */
    public NetworkNode(
	SimulationSet owner, String key, Queue queue,
	Sequence dataSize, Sequence interarrivalTime,
	PrintWriter logWriter
    ) {
	this.owner = owner;
	this.key = key;
	this.queue = queue;
	queue.owner = this;
	this.dataSize = dataSize;
	this.interarrivalTime = interarrivalTime;
	this.logWriter = logWriter;
	nextLink = new Vector();
	probePackets = new Packets(this, (owner.logicalPacket).packetSize);
	init();
   }

    /**
     * Constructs a NetworkNode with
     *     owner : a SimulationSet of your configuration,
     *     key : a key of the NetworkNode,
     *     queue : a queue which represents bandwidth, congestion, and
     *             their variance of the NetworkNode,
     *     dataSize : dataSize of the NetworkNode,
     *     interarrivalTime : interarrivalTime of the NetworkNode,
     */
    public NetworkNode(
	SimulationSet owner, String key, Queue queue,
	Sequence dataSize, Sequence interarrivalTime
    ) {
	this.owner = owner;
	this.key = key;
	this.queue = queue;
	queue.owner = this;
	this.dataSize = dataSize;
	this.interarrivalTime = interarrivalTime;
	nextLink = new Vector();
	probePackets = new Packets(this, (owner.logicalPacket).packetSize);
	init();
   }

/************************* needed method *************************/
    /** 
     * returns a type of the NetworkNode.
     */
    public String getName() {
	return "NetworkNode";
    }

    /** 
     * initializes the NetworkNode, 
     * if it uses InterpolationInterarrivalTime.
     */
    public void init() {
	super.init();
	if (interarrivalTime instanceof InterpolationInterarrivalTime) {
	    ((InterpolationInterarrivalTime)interarrivalTime).init(
		queue.maxThroughput, dataSize.mean
	    );
	}
    }

    public String toOriginalString(double currnetTime) {
	String str = "  [" + getName() + " " + key + "]\n";
	str += "  nextData : " + nextData.toOriginalString();
	str += "  servedData : " + queue.top() + "\n";
	str += queue.toOriginalString();
	return str;
    }

    public String toInitString() {
	String str = "  [" + getName() + " " + key + "] : ";
	str += queue.getName() + ", " + dataSize.mean + ", " + 
	    interarrivalTime.mean + "\n";
	str += queue.toOriginalString();
	return str;
    }

    /** 
     * updates the event.
     */
    public void updateNextEvent(double currentTime) {
	event = OTHERSDATA;
	double earliestTime = nextData.timeEventComes;
	if (!queue.isEmpty()) {
	    if (queue.getTimeQueueEventComes() < earliestTime) {
		earliestTime = queue.getTimeQueueEventComes();
		event = QUEUE;
	    }
	}
	if (!probePackets.isEmpty()) {
	    TrafficData td = probePackets.firstPacket();
	    if (td.timeEventComes < earliestTime) {
		earliestTime = td.timeEventComes;
		event = PROBE;
		SimulationDebug.println(
		    "NetworkNode(" + this + "): nextEvent = PROBE (" +
		    Format.format(earliestTime, 3)
		);
	    }
	}
	nextEventTime = earliestTime;
    }

    /** 
     * processEvent
     *     QUEUE : processes queue event.
     *     OTHERSDATA : puts extraneous data into queue of the NetworkNode.
     *     PROBE : puts a packet to probe the NetworkNode.
     */
    public void processEvent(double currentTime) {
	if (event == QUEUE) {
	    //System.out.println(currentTime + " : " + this + ": QUEUE");
	    queue.processEvent(currentTime);
	    outQueueLength(currentTime, queue.size());
	} else if (event == OTHERSDATA) { 
	    if (!queue.isFull()) {
		schedule(currentTime, nextData);
	    }
	    nextData = getNextData(currentTime);
	} else { // PROBE
	    //SimulationDebug.println(
	    //"##NetworkNode: " + Format.format(currentTime, 3) + " : PROBE"
	    //);
	    probePackets.sendFirstPacket(currentTime);
	    updateNextEvent(currentTime);
	}
    }

    protected Data getNextData(double currentTime) {
	double timeEventComes = 
	    currentTime + interarrivalTime.nextDouble(currentTime);
	Data odon = new OthersDataOfNetwork(
	    this, timeEventComes, dataSize.nextDouble(currentTime)
	);
	return odon;
    }

    /** 
     * handles to probe.
     * caller: NodePair
     */
    public void probe(double currentTime, ProbeData pd) {
	SimulationDebug.println("NetworkNode(" + currentTime + "): probe - divideTrafficData");
	probePackets.divideTrafficData(currentTime, pd);

	SimulationDebug.println("NetworkNode(" + currentTime + "): probe - sendFirstPacket");
	probePackets.sendFirstPacket(currentTime);
    }

    public void putInterarrivalTimeOfPackets(Sequence ng, Node toNode) {
	//System.out.println("### ng = " + ng + ", toNode = " + toNode);
	//System.out.println("### probePackets = " + probePackets);
	probePackets.putInterarrivalTimeOfPackets(ng, toNode);
    }
}
