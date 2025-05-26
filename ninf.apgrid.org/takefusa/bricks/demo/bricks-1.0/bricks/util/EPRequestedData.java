package bricks.util;
import bricks.environment.*;
import java.util.*;

public class EPRequestedData extends RequestedData {

    public boolean first = true;
    public int totalTasks = 0;
    public EPTask epTask;

    protected Vector epTasks = new Vector();

    protected int currentTask = 0;

    public EPRequestedData(
	ClientNode clientNode, double timeEventComes, int numTasks, 
	Sequence dataSizeForSendSq, Sequence dataSizeForReceiveSq,
	Sequence numInstructionsSq
    ) {
	super(
	    clientNode, timeEventComes, numTasks, 
	    dataSizeForSendSq, dataSizeForReceiveSq, numInstructionsSq
	);
	for (int i = 0; i < numTasks; i++) {
	    EPTask epTask = new EPTask(
		this, i,
		dataSizeForSendSq.nextDouble(), 
		dataSizeForReceiveSq.nextDouble(),
		numInstructionsSq.nextDouble()
	    );
	    epTasks.addElement(epTask);
	}
    }

/************************* needed method *************************/
    public void outLogString(double currentTime) {
	System.err.println("This method has never been called!");
	System.exit(3);
    }

    /**
     * outLogString returns boolean if all tasks are executed.
     **/
    public boolean outLogString(
	double currentTime, EPRequestedData original
    ) {
	// Request finished
	//client.printLog(log);
	//client.printLog(this.toLogString(3));
	client.printLog(this.toLogString(6));
	EPTask task = original.getEPTask(this.epTask.taskId);
	task.status = "FINISHED";
	boolean b = original.epFinished();
	if (b) {
	    numRequestedData++;
	}
	return b;
    }

    public String toLogString(int precision) {

	String str = "EP " + id + " " +
	    epTask.taskId + " / " + epTasks.size() + " " +
	    Format.format(dataSizeForSend, precision) + " " +
	    Format.format(dataSizeForReceive, precision) + " " + 
	    Format.format(numInstructions, precision) + " ";

	str +=  client + " " + epTask.server + " " + 
	    Format.format(epTask.startTime, precision) + " " + 
	    Format.format(epTask.sendDuration, precision) + " " + 
	    Format.format(epTask.execDuration, precision) + " " + 
	    Format.format(epTask.recvDuration, precision) + " " + 
	    Format.format(epTask.sendDuration + epTask.execDuration + epTask.recvDuration, precision);

	// Need to modify!!
	if (deadlineFactor >= 0.) { /* deadline */
	    str += " deadline[ " + 
		Format.format(deadline, precision) + " " +
		Format.format(expectedFinishTime, precision) + " " + 
		Format.format(deadlineFactor, precision) + " " +
		priority + " " + epTask.reschedule + " ]";
	}

	return str;
    }

    public void outOrgLogString(double currentTime) {
	outOrgLogString(currentTime, 6);
    }

    public void outOrgLogString(double currentTime, int precision) {
	client.printLog("===== EP " + this + " " + client + 
			" #task: " + epTasks.size() + 
			" start: " + Format.format(startTime, precision) + 
			" finish: " + Format.format(currentTime, precision) +
			" total: " + Format.format(currentTime-startTime, precision)
			+ " =====");
    }

    public String getName() {
	return "EPRequestedData";
    }

    public void gotoNextNode(double currentTime) {

	Node node = currentNode;
	Node nextNode = (Node)getNextNode();
	SimulationDebug.println(Format.format(currentTime, 4) + " : ND : " + 
	this + " goto " + nextNode + "\n");

	// enqueue time
	//getLog(currentTime);
	getLog(currentTime, "e");
	getNodeLog(nextNode);

	if (start < 0) {
	    start = epTask.startTime;

	} else {
	    if (nextNode instanceof ServerNode) {
		epTask.sendDuration = currentTime - start;
		start = currentTime;

	    } else if (node instanceof ServerNode) {
		epTask.execDuration = currentTime - start;
		start = currentTime;

	    } else if (nextNode instanceof ClientNode) {
		epTask.recvDuration = currentTime - start;
		//epTask.duration = epTask.sendDuration + epTask.execDuration 
		//+ epTask.recvDuration;
	    }
	}

	nextNode.schedule(currentTime, this);
    }

/************************* public method *************************/
    public String toOriginalString() {
	return getName() + "(" + id + ": " + 
	    epTask.taskId + " / " + epTasks.size() + ")[" + 
	    Format.format(dataSizeForSend, 3) + " " +
	    Format.format(dataSizeForReceive, 3) + " " + 
	    Format.format(numInstructions, 3) +
	    " ]\n timeEventComes = " + Format.format(timeEventComes, 3) +
	    ", from " + source + " to " + destination;
    }

    /**
     * getScheduledTasks generates clone of EPRequestedData for each 
     * SCHEDULED EPTasks and Vectors of the clones and returns the Vectors.
     **/
    public Vector getScheduledTasks() {

	//System.out.println(this);
	Vector scheduled = new Vector(epTasks.size());
	for (int i = currentTask - 1; i >= 0; i--) {
	    EPTask epTask = getEPTask(i);
	    if (!epTask.status.equals("SCHEDULED"))
		break;
	    //System.out.println("================================");
	    epTask.status = "DISPATCHED";
	    EPRequestedData epData = this.clone(epTask);
	    scheduled.addElement(epData);
	}
	return scheduled;
    }

    public EPTask getNextTask() {
	if (currentTask == epTasks.size())
	    return null;
	else 
	    return (EPTask)epTasks.elementAt(currentTask++);
    }

    public int numEPTasks() {
	return epTasks.size();
    }

    public EPTask getEPTask(int id) {
	return (EPTask)epTasks.elementAt(id);
    }

    public Enumeration epTasks() {
	return epTasks.elements();
    }

    public boolean hasMoreNotScheduledTasks() {
	if (currentTask == epTasks.size())
	    return false;
	else 
	    return true;
    }

    /** 
     * caller: scheduler
     **/
    public void putEstimate(double send, double recv, double exec) {
	epTask.estimatedSendDuration = send;
	epTask.estimatedExecDuration = exec;
	epTask.estimatedRecvDuration = recv;
	//epTask.estimatedDuration = send + recv + exec;
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
	epTask.flash();
    }

/************************* protected method *************************/
    protected boolean epFinished() {
	boolean b = true;
	Enumeration e = epTasks.elements();
	while (e.hasMoreElements()) {
	    EPTask task = (EPTask)e.nextElement();
	    if (!task.status.equals("FINISHED")) {
		b = false;
		break;
	    }
	}
	return b;
    }

    protected EPRequestedData clone(EPTask task) {

	EPRequestedData data = (EPRequestedData)this.clone();
	data.epTask = task;

	data.dataSizeForSend = task.dataSizeForSend;
	data.dataSizeForReceive = task.dataSizeForReceive;
	data.numInstructions = task.numInstructions;

	data.destination = task.server;

	data.dataSize = task.dataSizeForSend;
	data.timeEventComes = this.timeEventComes;

	return data;
    }

    protected String outInitData() {
	String str = "[ EP " + id + " " +
	    epTask.taskId + " / " + epTasks.size() + " " +
	    Format.format(dataSizeForSend, 3) + " " +
	    Format.format(dataSizeForReceive, 3) + " " + 
	    Format.format(numInstructions, 3) + " ] ";
	return str;
    }
}


