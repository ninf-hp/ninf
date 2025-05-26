package bricks.environment;
import bricks.util.*;
import bricks.scheduling.*;
import java.io.*;
import java.util.*;

/** 
 * ClientNode.java
 * <pre>
 * ClientNode represents the machine, upon which global computing 
 * tasks are initiated by the user program.
 * The data injected by the ClientNode are decomposed into logical packets.
 * </pre>
 */
public class ClientNode extends Node implements Cloneable {

    /** 
     * The scheduler that allocates a new task of the client.
     */
    protected Scheduler scheduler;
    
    /**
     * The size of transmitted data from the client to a server.
     */
    protected Sequence dataSizeForSend;

    /**
     * The size of transmitted data from a server to the client.
     */
    protected Sequence dataSizeForReceive;

    /**
     * The number of executed instructions (operations) in the task.
     */
    protected Sequence numInstructions;

    /**
     * The interval that the ClientNode invokes tasks.
     */
    protected Sequence throwingInterval;

    /** 
     * Packets decomposes transmitted data into logical packets and 
     * sends them into a NetworkNode.
     */
    protected Packets packets;

    /**
     * This is the factor which decides deadlines of each RequestData.
     */
    protected Sequence deadlineFactor;

    protected Hashtable hashtableOfRoutes = new Hashtable();
    protected Vector scheduledData = new Vector();

    /** 
     * The variable for event flags.
     */
    protected int event;
    protected static final int DISPATCH      = 0;
    protected static final int SEND_DATA     = 1;
    protected static final int SEND_PACKET   = 2;
    protected static final int DIVIDE_PACKET = 3;
    protected static final int RESCHEDULE = 4;

    protected Vector rescheduledList = new Vector();

    protected int precision = 3;

    /**
     * Constructs a ClientNode for Obj.java's main().
     */
    public ClientNode(String key) {
	this.key = key;
	init();
    }

    /** 
     * Constructs a ClientNode with
     *     owner : a SimulationSet of your configuration,
     *     key : a key of the ClientNode,
     *     keyOfScheduler : a key of the scheduler that allocates 
     *                      a new task of the ClientNode,
     *     dataSizeForSend : dataSizeForSend of the ClientNode,
     *     dataSizeForReceive : dataSizeForReceive of the ClientNode,
     *     numInstructions : numInstructions of the ClientNode,
     *     throwingInterval : throwingInterval of the ClientNode.
     */
    public ClientNode(
	SimulationSet owner, String key, String keyOfScheduler,
	Sequence dataSizeForSend, Sequence dataSizeForReceive,
	Sequence numInstructions, Sequence throwingInterval
    ) {
	this.owner = owner;
	this.key = key;
	scheduler = owner.getScheduler(keyOfScheduler);
	logWriter = owner.requestedDataLogWriter;
	this.dataSizeForSend = dataSizeForSend;
	this.dataSizeForReceive = dataSizeForReceive;
	this.numInstructions = numInstructions;
	this.throwingInterval = throwingInterval;

	nextLink = new Vector();
	init();
	packets = new Packets(this, (owner.logicalPacket).packetSize);
    }

    /** 
     * Constructor for deadline scheduling.
     **/
    public ClientNode(
	Sequence deadlineFactor,
	SimulationSet owner, String key, String keyOfScheduler,
	Sequence dataSizeForSend, Sequence dataSizeForReceive,
	Sequence numInstructions, Sequence throwingInterval
    ) {
	this.owner = owner;
	this.key = key;
	scheduler = owner.getScheduler(keyOfScheduler);
	logWriter = owner.requestedDataLogWriter;
	this.dataSizeForSend = dataSizeForSend;
	this.dataSizeForReceive = dataSizeForReceive;
	this.numInstructions = numInstructions;
	this.throwingInterval = throwingInterval;
	this.deadlineFactor = deadlineFactor;

	nextLink = new Vector();
	init();
	packets = new Packets(this, (owner.logicalPacket).packetSize);
    }

    /**
     * Constructor for EPClientNode.
     **/
    public ClientNode(
	SimulationSet owner, String key, String keyOfScheduler, 
	Sequence numTasks, 
	Sequence dataSizeForSend, Sequence dataSizeForReceive,
	Sequence numInstructions, Sequence throwingInterval
    ) {
	this.owner = owner;
	this.key = key;
	scheduler = owner.getScheduler(keyOfScheduler);
	logWriter = owner.requestedDataLogWriter;
	this.dataSizeForSend = dataSizeForSend;
	this.dataSizeForReceive = dataSizeForReceive;
	this.numInstructions = numInstructions;
	this.throwingInterval = throwingInterval;

	nextLink = new Vector();
	init();
	packets = new Packets(this, (owner.logicalPacket).packetSize);
    }

    /**
     * Constructor for EPClientNode & deadline scheduling
     **/
    public ClientNode(
	Sequence deadlineFactor,
	SimulationSet owner, String key, String keyOfScheduler, 
	Sequence numTasks, 
	Sequence dataSizeForSend, Sequence dataSizeForReceive,
	Sequence numInstructions, Sequence throwingInterval
    ) {
	this.owner = owner;
	this.key = key;
	scheduler = owner.getScheduler(keyOfScheduler);
	logWriter = owner.requestedDataLogWriter;
	this.dataSizeForSend = dataSizeForSend;
	this.dataSizeForReceive = dataSizeForReceive;
	this.numInstructions = numInstructions;
	this.throwingInterval = throwingInterval;
	this.deadlineFactor = deadlineFactor;

	nextLink = new Vector();
	init();
	packets = new Packets(this, (owner.logicalPacket).packetSize);
    }

/************************* needed method *************************/
    /** 
     * generates a clone of the ClientNode.
     */
    public synchronized Object clone() {
        try {
            ClientNode c = (ClientNode)super.clone();
            return c;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /** 
     * returns a type of the ClientNode.
     */
    public String getName() {
	return "ClientNode";
    }

    /* override */
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

    /** 
     * updates the event.
     */
    public void updateNextEvent(double currentTime) {
	event = DISPATCH;
	double earliestTime = nextData.timeEventComes;
	if (!packets.isEmpty()) {
	    TrafficData td = packets.firstPacket();
	    if (td.timeEventComes < earliestTime) {
		earliestTime = td.timeEventComes;
		event = SEND_PACKET;
	    }
	} 
	if (!scheduledData.isEmpty()) {
	    RequestedData td = (RequestedData)scheduledData.firstElement();
	    if (td.timeEventComes < earliestTime) {
		earliestTime = td.timeEventComes;
		event = DIVIDE_PACKET;
	    }
	}
	if (!rescheduledList.isEmpty()) {
	    RequestedData d = (RequestedData)rescheduledList.firstElement();
	    if (d.timeEventComes < earliestTime) {
		earliestTime = d.timeEventComes;
		event = RESCHEDULE;
	    }
	}
	nextEventTime = earliestTime;
    }

    /** 
     * processEvent
     *     SEND_PACKET : sends the first packet of queue of the Packets.
     *     DISPATCH : dispatches the nextData.
     *     DIVIDE_PACKET : invokes data and decompose it into logical packets.
     *     RESCHEDUET : dispatch data whose schedule failed.
     */
    public void processEvent(double currentTime) {
	if (event == SEND_PACKET) {
	    SimulationDebug.println(
		"ClientNode:" + Format.format(currentTime, precision) + 
		":" + this + ": SEND_PACKET"
	    );
	    packets.sendFirstPacket(currentTime);

	} else if (event == DISPATCH) {
	    //System.out.println(
	    SimulationDebug.println(
		"ClientNode:" + Format.format(currentTime, precision) + 
		":" + this + ": DISPATCH [" + nextData + "]"
	    );
	    dispatchServers(currentTime, (RequestedData)nextData);
	    nextData = getNextData(currentTime);

	} else if (event == DIVIDE_PACKET) {
	    RequestedData rd = (RequestedData)scheduledData.firstElement();
	    scheduledData.removeElementAt(0);
	    //System.out.println(
	    SimulationDebug.println(
		"ClientNode:" + Format.format(currentTime, precision) + ":" +
		this + ": DIVIDE_PACKET [" + rd + "]"
	    );
	    packets.divideTrafficData(currentTime, rd);
	    //System.out.println("sendFirstPacket...");
	    packets.sendFirstPacket(currentTime);

	} else { /* RESCHEDULE */
	    RequestedData d = (RequestedData)rescheduledList.firstElement();
	    //System.out.println(
	    SimulationDebug.println(
		"ClientNode:" + Format.format(currentTime, precision) + 
		":" + this + ": RESCHEDULE [" + d + "]"
	    );
	    rescheduledList.removeElementAt(0);
	    dispatchServers(currentTime, d);
	}
    }

    public String toOriginalString(double currentTime) {
	String str = "  [" + getName() + " " + key + "]\n";
	str += "  nextData : " + nextData.toOriginalString();
	return str;
    }

    public String toInitString() {
	String str = "  [" + getName() + " " + key + "] : ";
	str += scheduler.getName() + ", " + dataSizeForSend.mean + ", " +
	    dataSizeForReceive.mean + ", " + numInstructions.mean + 
	    ", " + throwingInterval.mean + "\n";
	return str;
    }

    /** 
     * prints simulation log.
     */
    public void printLog(String log) {
	logWriter.println(log);
    }

    /** 
     * calls the data's outLogString when the RequestData 
     * returns to the ClientNode.
     */
    public void schedule(double currentTime, Data data) {

	if (data instanceof RequestedData)
	    scheduler.finishTask((RequestedData)data);

	// output simulation results
	data.outLogString(currentTime);
    }

    public void putInterarrivalTimeOfPackets(Sequence ng, Node toNode) {
	packets.putInterarrivalTimeOfPackets(ng, toNode);
    }

/************************* protected method *************************/
    protected Data getNextData(double currentTime) {
	double timeEventComes =
	    currentTime + throwingInterval.nextDouble(currentTime);
	Data nd = null;
	//System.out.println("deadlineFactor " + deadlineFactor);
	if (deadlineFactor != null) { /* deadline scheduling */
	    SimulationDebug.println(this + ": has deadlineFactor!");
	    nd = new RequestedData(
	                 this, timeEventComes, 
			 dataSizeForSend.nextDouble(), 
			 dataSizeForReceive.nextDouble(),
			 numInstructions.nextDouble(),
			 deadlineFactor.nextDouble()
		     );
	} else {  /* deadline scheduling */
	    SimulationDebug.println(this + ": deadlineFactor == null!");
	    nd = new RequestedData(
	                 this, timeEventComes, 
			 dataSizeForSend.nextDouble(), 
			 dataSizeForReceive.nextDouble(),
			 numInstructions.nextDouble()
		     );
	}
	return nd;
    }

    protected String toStringOfNodeList(Vector list) {
	String str = "";
	Enumeration e = list.elements();
	while (e.hasMoreElements()) {
	    Node node = (Node)e.nextElement();
	    str += node + ", ";
	}
	return str;
    }

    /**
     * for fallback
     **/
    public void fallback(double currentTime, RequestedData data) {
	//System.out.println("===== FALLBACK!! =====");
	scheduler.finishTask(data);
	data.flash();
	dispatchServers(currentTime, data);
	updateNextEvent(currentTime);
    }

    protected void dispatchServers(double currentTime, RequestedData data) {
	//System.out.println(
	SimulationDebug.println(
	    Format.format(currentTime, precision) + " : " + this + " : " +
	    scheduler + " [" + data + "]");
	if (data.startTime < 0.0) {
	    data.startTime = currentTime;
	}
	
	try {
	    scheduler.selectServers(currentTime, this, data);
	    /* Scheduled! */
	    //System.out.println(
	    SimulationDebug.println(
		Format.format(currentTime, precision) + " : " + this + 
		" [" + data + "] dispatched into " + data.getDestination());
	    initRoute(currentTime, data, (ServerNode)data.getDestination());
	    //scheduledData.addElement(nextData);
	    scheduledData.addElement(data);
	    //System.out.println("--- " + scheduledData + " ---");

	} catch (BricksNotScheduledException e) {
	    /* Reschedule! */
	    addIntoRescheduledList(data);
	    SimulationDebug.println(e);
	    //System.out.println(e);
	    //System.out.println(
	    SimulationDebug.println(
		Format.format(currentTime, precision) + ": RESCHEDULE " + 
		this + " [" + data + "] " + 
		Format.format(data.timeEventComes, precision) + "\n"
	    );
	}
    }

    protected void initRoute(
	double currentTime, RequestedData data, ServerNode sn
    ) {
	Vector route = (Vector)owner.getRoute(this, sn);
	Vector stoc = (Vector)owner.getRoute(sn, this);
	//SimulationDebug.println(data + " 's ctos = {" + route + "}");
	//SimulationDebug.println(data + " 's stoc = {" + stoc + "}");
	stoc.removeElement(sn);
	Enumeration e = stoc.elements();
	while (e.hasMoreElements()) {
	    route.addElement(e.nextElement());
	}
	SimulationDebug.println(data + " 's route = {" + route + "}");
	data.initRoute(currentTime, route, sn);
    }

    protected void addIntoRescheduledList(RequestedData data) {

	if (rescheduledList.size() == 0) {
	    rescheduledList.addElement(data);

	} else {
	    int cnt = 0;
	    Enumeration e = rescheduledList.elements();
	    while (e.hasMoreElements()) {

		RequestedData d = (RequestedData)e.nextElement();
		if (d.timeEventComes > data.timeEventComes) {
		    int index = rescheduledList.indexOf(d);
		    rescheduledList.insertElementAt(data, index);
		    break;
		}
		cnt++;
	    }
	    if (cnt == rescheduledList.size()) {
		rescheduledList.addElement(data);
	    }
	}
    }
}


