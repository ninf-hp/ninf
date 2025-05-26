package bricks.util;
import bricks.environment.*;
import java.util.*;

public class RequestedData extends TrafficData {

    public static int numRequestedData = 0;
    protected static int serialNumber = 0;

    protected Node source;
    protected Node destination;
    protected String log;

    public double dataSizeForSend;
    public double dataSizeForReceive;
    public double numInstructions;

    public ClientNode client;
    public ServerNode server;

    public double startTime = Double.NEGATIVE_INFINITY;
    public int priority;

    // for fallback
    public double elapsedTime = Double.NEGATIVE_INFINITY;

    public double estimatedSendDuration;
    public double estimatedExecDuration;
    public double estimatedRecvDuration;
    //public double estimatedDuration;

    public double sendDuration;
    public double execDuration;
    public double recvDuration;
    //public double duration;

    /**
     * for deadline scheduling
     **/
    public Vector scheduledServers = null;
    public double deadlineFactor = Double.NEGATIVE_INFINITY;
    public double deadline = Double.NEGATIVE_INFINITY;
    public double expectedFinishTime;
    public int reschedule = 0;

    /*
      [ dataSizeForSend dataSizeForReceive numInstructions ]
      cn (schedule) (first out cn & in nq1) (nd in nq1) nq1 (nq1 top)
      (out nq1 & in sq) sq (sq top) (first out sq & in nq2) (nd in nq2) 
      nq2 (nq2 top) (out nq2 & in cn) cn
    */

    public RequestedData(
	ClientNode clientNode, double timeEventComes, 
	double dataSizeForSend,	double dataSizeForReceive, 
	double numInstructions
    ) {
	id = generateId();
	processingTime = 0.0;
	this.client = clientNode;
	this.source = clientNode;
	this.timeEventComes = timeEventComes;
	this.dataSizeForSend = dataSizeForSend;
	this.dataSizeForReceive = dataSizeForReceive;
	this.numInstructions = numInstructions;
	this.dataSize = dataSizeForSend;
    }

    public RequestedData(
	ClientNode clientNode, double timeEventComes, 
	double dataSizeForSend, double dataSizeForReceive, 
	double numInstructions, double deadlineFactor
    ) {
	id = generateId();
	processingTime = 0.0;
	this.client = clientNode;
	this.source = clientNode;
	this.timeEventComes = timeEventComes;
	this.dataSizeForSend = dataSizeForSend;
	this.dataSizeForReceive = dataSizeForReceive;
	this.numInstructions = numInstructions;
	this.dataSize = dataSizeForSend;
	this.deadlineFactor = deadlineFactor;
    }

    // for EPRequestedData
    public RequestedData(
	ClientNode clientNode, double timeEventComes, int numTask, 
	Sequence dataSizeForSendSq, Sequence dataSizeForReceiveSq,
	Sequence numInstructionsSq
    ) {
	this.id = generateId();
	processingTime = 0.0;
	this.client = clientNode;
	this.source = clientNode;
	this.timeEventComes = timeEventComes;
    }

    // for EPRequestedData
    public RequestedData(
	ClientNode clientNode, double timeEventComes, int numTask, 
	Sequence dataSizeForSendSq, Sequence dataSizeForReceiveSq,
	Sequence numInstructionsSq, double deadlineFactor
    ) {
	this.id = generateId();
	processingTime = 0.0;
	this.client = clientNode;
	this.source = clientNode;
	this.timeEventComes = timeEventComes;
	this.deadlineFactor = deadlineFactor;
    }

/************************* needed method *************************/
    /** 
     * generates a clone of the TrafficData.
     */
    public synchronized Object clone() {
	RequestedData d = (RequestedData)super.clone();
	d.source = this.client;
	d.dataSizeForSend = this.dataSizeForSend;
	d.dataSizeForReceive = this.dataSizeForReceive;
	d.numInstructions = this.numInstructions;
	return d;
    }

    protected double start = Double.NEGATIVE_INFINITY;

    public void gotoNextNode(double currentTime) {

	Node node = currentNode;
	Node nextNode = (Node)getNextNode();
	SimulationDebug.println(Format.format(currentTime, 4) + " : ND : " + 
	this + " goto " + nextNode + "\n");
	//System.out.println("current: " + node + ", next: " + nextNode);

	// enqueue time
	//getLog(currentTime);
	getLog(currentTime, "e");
	getNodeLog(nextNode);

	if (start < 0) {
	    start = startTime;

	} else {
	    if (nextNode instanceof ServerNode) {
		sendDuration = currentTime - start;
		start = currentTime;

	    } else if (node instanceof ServerNode) {
		execDuration = currentTime - start;
		start = currentTime;

	    } else if (nextNode instanceof ClientNode) {
		recvDuration = currentTime - start;
		//duration = sendDuration + execDuration + recvDuration;
	    }
	}

	nextNode.schedule(currentTime, this);
    }

    // override
    // caller: Network
    public void updateTimeEventComes(double currentTime) {
	timeEventComes = currentTime + processingTime;
	// dequeue time
	//getLog(currentTime);
	getLog(currentTime, "d");
    }

    // override
    public void outLogString(double currentTime) {
	//client.printLog(log);
	//client.printLog(this.toLogString(3));
	client.printLog(this.toLogString(6));
	numRequestedData++;
    }

    // for EPClientNode
    public void outLogString(String str) {
	// time Request finished
	client.printLog(str);
    }

    public String toOriginalString() {
	String str = "RequestedData<" + id + "> " + 
	    Format.format(timeEventComes, 3) + ",  " +
	    Format.format(dataSizeForSend, 3) + ",  " + 
	    Format.format(dataSizeForReceive, 3) + ",  " + 
	    Format.format(numInstructions, 3) + "\n";
	str += "  from " + client + " to " + server + "\n";
	//str += "  Route :: " + vectorToString(listOfRoute);
	return str;
    }

    // debug
    public String showLog() {
	return log;
    }

    public String getName() {
	return "RequestedData";
    }

/************************* public method *************************/
    public String toLogString(int precision) {

	String str = this + " " + 
	    Format.format(dataSizeForSend, precision) + " " +
	    Format.format(dataSizeForReceive, precision) + " " + 
	    Format.format(numInstructions, precision) + " ";

	str =  str + client + " " + server + " " + 
	    Format.format(startTime, precision) + " " + 
	    Format.format(sendDuration, precision) + " " + 
	    Format.format(execDuration, precision) + " " + 
	    Format.format(recvDuration, precision) + " " + 
	    Format.format(sendDuration + execDuration + recvDuration, precision);
	//Format.format(duration, precision);

	if (deadlineFactor >= 0.) { /* deadline */
	    str += " deadline " + 
		Format.format(deadline, precision) + " " +
		Format.format(expectedFinishTime, precision) + " " + 
		Format.format(deadlineFactor, precision) + " " +
		priority + " ";
	    if (scheduledServers == null)
		str += "0";
	    else
		str += scheduledServers.size();
	}

	return str;
    }

    public void initRoute(
	double currentTime, Vector listOfRoute, ServerNode destination
    ) {
	this.destination = destination;
	initRoute(currentTime, listOfRoute);
    }

    public void initRoute(double currentTime, Vector listOfRoute) {
	this.listOfRoute = listOfRoute;

	// for Packets
	nextNodes = listOfRoute.elements();
	if (!nextNodes.hasMoreElements()) {
	    System.err.println("route is null at " + this);
	    System.exit(3);
	}
	currentNode = (Node)nextNodes.nextElement();
	indexOfCurrentNode = 0;
	log = outInitData() + currentNode.toString();
	// schedule time
	//getLog(currentTime);
	getLog(currentTime, "s");

	//System.out.println(this + ": initRoute()...");
	//System.out.println(this + ": " + vectorToString(listOfRoute));
    }

    // override
    public void updateDataSize() {
	SimulationDebug.println(
	    this + ": updateDataSize : indexOfCurrentNode = " +
	    indexOfCurrentNode
	);
	Node node = (Node)listOfRoute.elementAt(indexOfCurrentNode);
	if (node instanceof ClientNode) {
	    dataSize = dataSizeForSend;
	} else if (node instanceof ServerNode) {
	    dataSize = dataSizeForReceive;
	} else {
	    // if node instanceof NetworkNode
	    node = (Node)listOfRoute.elementAt(indexOfCurrentNode+1);
	    if (node instanceof ServerNode) { // NetworkNodeForSend
		dataSize = numInstructions;
	    }
	}
    }

    public Vector getSubList(double currentTime) {
	// getLog of time (finished scheduling / enqueue firstpacket)
	getLog(currentTime, "t");

	SimulationDebug.println(
	    "listOfRoute = {" + listOfRoute + "} at RequestedData.getSubList()"
	);
	Vector list = new Vector();
	Enumeration e = null;
	try {
	    e = listOfRoute.elements();
	} catch (NullPointerException exc) {
	    exc.printStackTrace();
	    BricksUtil.abort(Format.format(currentTime, 3) + ": " +
			     toOriginalString());
	}
	boolean start = false;
	while (e.hasMoreElements()) {
	    Node node = (Node)e.nextElement();
	    if (!start) {
		if (node == currentNode) {
		    list.addElement(node);
		    start = true;
		}
		continue;
	    }
	    if (node == destination) {
		//list.addElement(list.lastElement());
		list.addElement(new TerminalNode());
		break;
	    } else {
		list.addElement(node);
	    }
	}
	//System.out.println(vectorToString(list));
	return list;
    }

    // override
    public void returnRoute() {
	Node tmp = source;
	source = destination;
	destination = tmp;
    }

    /** 
     * caller: scheduler
     **/
    public void putDestination(ServerNode serverNode, int priority) {
	this.destination = serverNode;
	this.server = serverNode;
	this.priority = priority;
    }

    /** 
     * caller: scheduler
     **/
    public void putEstimate(double send, double recv, double exec) {
	estimatedSendDuration = send;
	estimatedExecDuration = exec;
	estimatedRecvDuration = recv;
	//estimatedDuration = send + recv + exec;
    }

    /** 
     * caller: deadline scheduler
     **/
    public void setElapsedTime(double time) {
	elapsedTime = time;
    }

    /** 
     * caller: client
     **/
    public Node getDestination() {
	return (Node)destination;
    }

    /** 
     * caller: ResourceDB.removeSchedulingInfo()
     **/
    public boolean equals(Object object) {
	if (object instanceof RequestedData) {
	    RequestedData data2 = (RequestedData)object;
	    if (this.serialNumber == data2.serialNumber)
		return true;
	}
	return false;
    }

    /** 
     * for fallback
     **/
    public void fallback(double currentTime) {
	client.fallback(currentTime, this);
    }
	    
    // for fallback
    public void flash() {
	if (scheduledServers == null)
	    scheduledServers = new Vector();
	scheduledServers.addElement(server);
	source = client;
	destination = null;
	log = "";
	server = null;
	dataSize = dataSizeForSend;
	indexOfCurrentNode = 0;
    }

    public int numSchedule() {
	if (scheduledServers != null)
	    return scheduledServers.size();
	else 
	    return 0;
    }

    public boolean scheduled(ServerNode server) {
	if (scheduledServers != null) {
	    for (int i = numSchedule() - 1; i >= 0; i--) {
		if (server.equals(scheduledServers.elementAt(i)))
		    return true;
	    }
	}
	return false;
    }

    public Enumeration scheduledServers() {
	return scheduledServers.elements();
    }

/************************* protected method *************************/
    protected String vectorToString(Vector vec) {
	Enumeration e = vec.elements();
	String str = null;
	while (e.hasMoreElements()) {
	    str += e.nextElement() + ", ";
	}
	return str;
    }

    protected String outInitData() {
	String str = "[ " + Format.format(dataSizeForSend, 3) + " " +
	    Format.format(dataSizeForReceive, 3) + " " + 
	    Format.format(numInstructions, 3) + " ] ";
	return str;
    }

    protected void getLog(double currentTime) {
	Double doubleObj = new Double(currentTime);
	log = log + " " + Format.format(doubleObj.doubleValue(), 3);
    }

    protected void getLog(double currentTime, String str) {
	Double doubleObj = new Double(currentTime);
	log = log + " " + str + Format.format(doubleObj.doubleValue(), 3);
    }

    protected void getNodeLog(Node node) {
	log = log + " " + node;
    }
}


