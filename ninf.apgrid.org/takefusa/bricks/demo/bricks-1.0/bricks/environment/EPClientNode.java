package bricks.environment;
import bricks.util.*;
import java.io.*;
import java.util.*;

/** 
 * EPClientNode.java
 * <pre>
 * EPClientNode invoke EP tasks.
 * declaration: 
 * EPClient [name] [Scheduler name] Requests(\\<BR>
 *   [num of tasks] [data size for send] [data size for receive] \\
 *   [numInstruction] [interval of invoking])
 * </pre>
 */
public class EPClientNode extends ClientNode {

    /**
     * The number sequence of tasks in the job.
     */
    protected Sequence numTasks;

    public ServerNode scheduledServer;
    protected Hashtable tasks = new Hashtable();

    /** 
     * Constructs a EPClientNode with
     *     owner : a SimulationSet of your configuration,
     *     key : a key of the ClientNode,
     *     keyOfScheduler : a key of the scheduler that allocates 
     *                      a new task of the ClientNode,
     *     numTasks : number of tasks in the job.
     *     dataSizeForSend : dataSizeForSend of the ClientNode,
     *     dataSizeForReceive : dataSizeForReceive of the ClientNode,
     *     numInstructions : numInstructions of the ClientNode,
     *     throwingInterval : throwingInterval of the ClientNode.
     */
    public EPClientNode(
	SimulationSet owner, String key, String keyOfScheduler, 
	Sequence numTasks, 
	Sequence dataSizeForSend, Sequence dataSizeForReceive,
	Sequence numInstructions, Sequence throwingInterval
    ) {
	super(
	    owner, key, keyOfScheduler, numTasks, dataSizeForSend, 
	    dataSizeForReceive, numInstructions, throwingInterval
	);
	this.numTasks = numTasks;
    }

    public EPClientNode(
	SimulationSet owner, String key, String keyOfScheduler, 
	Sequence numTasks, 
	Sequence dataSizeForSend, Sequence dataSizeForReceive,
	Sequence numInstructions, Sequence throwingInterval,
	Sequence deadlineFactor
    ) {
	super(
	    owner, key, keyOfScheduler, numTasks, dataSizeForSend, 
	    dataSizeForReceive, numInstructions, throwingInterval
	);
	this.numTasks = numTasks;
    }

/************************* overriden method *************************/
    public String getName() {
	return "EPClientNode";
    }

    /** 
     * processEvent
     *     SEND_PACKET : sends the first packet of queue of the Packets.
     *     DISPATCH : dispatches the nextData.
     *     DIVIDE_PACKET : invokes data and decompose it into logical packets.
     */
    public void processEvent(double currentTime) {
	
	if (event == SEND_PACKET) {
	    //System.out.println(
	    SimulationDebug.println(
		"EPClientNode:" + Format.format(currentTime, 4) + 
		":" + this + ": SEND_PACKET"
	    );
	    packets.sendFirstPacket(currentTime);

	} else if (event == DISPATCH) {
	    //System.out.println(
	    SimulationDebug.println(
		"***** EPClientNode:" + Format.format(currentTime, 4) + 
		":" + this + ": DISPATCH " + nextData
	    );
	    //System.out.println("*****\n" + packets + "\n*****");
	    dispatchServers(currentTime, (EPRequestedData)nextData);
	    nextData = getNextData(currentTime);

	} else if (event == DIVIDE_PACKET) { 
	    //System.out.println(
	    SimulationDebug.println(
		"EPClientNode: " + Format.format(currentTime, 4) + 
		": " + this + ": DIVIDE_PACKET " + nextData
	    );
	    RequestedData rd = (RequestedData)scheduledData.firstElement();
	    scheduledData.removeElementAt(0);
	    SimulationDebug.println(
		"EPClientNode:" + Format.format(currentTime, 4) + ":" +
		this + ": DIVIDE_PACKET " + rd
	    );
	    packets.divideTrafficData(currentTime, rd);
	    packets.sendFirstPacket(currentTime);

	} else { /* RESCHEDULE */
	    //System.out.println(
	    SimulationDebug.println(
		"EPClientNode:" + Format.format(currentTime, 4) + 
		":" + this + ": RESCHEDULE "
	    );
	    EPRequestedData d = (EPRequestedData)rescheduledList.firstElement();
	    rescheduledList.removeElementAt(0);
	    dispatchServers(currentTime, d);
	}
    }

    public void updateNextEvent(double currentTime) {
	super.updateNextEvent(currentTime);
	SimulationDebug.println(
	    Format.format(currentTime, 3) + ": EPClientNode: updateNextEvent" +
 	    "event = " + event + ", nextEventTime = " + nextEventTime
	);
    }

    /** 
     * calls the data's outLogString when the RequestData 
     * returns to the ClientNode.
     */
    public void schedule(double currentTime, Data data) {

	if (!(data instanceof EPRequestedData)) {
	    System.err.println("EPClientNode.schedule(): This is not an instance of EPRequestedData");
	    System.exit(3);
	}

	EPRequestedData epdata = (EPRequestedData)data;
	EPRequestedData org = (EPRequestedData)tasks.get(epdata.id);
	scheduler.finishTask(epdata.epTask);

	if (epdata.outLogString(currentTime, org)) { /* finish one job */
	    org.outOrgLogString(currentTime);
	    tasks.remove(org);

	} else { /* org is not completed. */
	    //System.out.println("schedule next!! -----" + org + "-----");
	    if (org.hasMoreNotScheduledTasks()) {
		//System.out.println("schedule next!! ===" + org + " ===");
		dispatchServers(currentTime, org, epdata.epTask);
		updateNextEvent(currentTime);
	    }
	}
    }

    /**
     * for fallback
     **/
    public void fallback(double currentTime, RequestedData data) {
	if (data instanceof EPRequestedData) {
	    EPRequestedData epdata = (EPRequestedData)data;
	    scheduler.finishTask(epdata.epTask);
	    dispatchServersForFallback(currentTime, (EPRequestedData)data);

	} else {
	    BricksUtil.abort(this + "data is not EPRequestedData");
	}
    }

    // need to implement!!
    protected void dispatchServersForFallback(double currentTime, EPRequestedData epdata) {;}

/************************* protected method *************************/
    protected Data getNextData(double currentTime) {
	double timeEventComes =
	    currentTime + throwingInterval.nextDouble(currentTime);
	EPRequestedData d = new EPRequestedData(
	    this, timeEventComes, numTasks.nextInt(), dataSizeForSend,
	    dataSizeForReceive, numInstructions
	);
	return d;
    }

    // error
    protected void dispatchServers(double currentTime, RequestedData data) {
	System.err.println("EPClientNode.dispatchServers(): This is not an instance of EPRequestedData");
	System.exit(3);
    }

    // first
    protected void dispatchServers(
	double currentTime, EPRequestedData epdata
    ) {
	try {
	    scheduler.selectServers(currentTime, this, epdata);

	    epdata.startTime = currentTime;
	    epdata.first = false;

	    Vector scheduled = epdata.getScheduledTasks();
	    /* add epdata into tasks */
	    tasks.put(epdata.id, epdata);
	    
	    for (int i = scheduled.size() - 1; i >= 0; i--) {
		EPRequestedData d = (EPRequestedData)scheduled.elementAt(i);
		d.epTask.startTime = d.timeEventComes;
		ServerNode node = (ServerNode)d.getDestination();
		initRoute(d.timeEventComes, d, node);
		/* moved from processEvent */
		scheduledData.addElement(d);
	    }
	    SimulationDebug.println(
	        "EPClientNode: scheduledData.size = " + scheduledData.size()
	    );

	} catch (BricksNotScheduledException e) {
	    SimulationDebug.println(e);
	    epdata.outLogString(epdata + " scheduling failed");
	    /* add epdata into reschedulingList */
	    addIntoRescheduledList(epdata);
	}
    }

    // second or more
    protected void dispatchServers(
	double currentTime, EPRequestedData epdata, EPTask epTask
    ) {
	//System.out.println("second or more");
	try {
	    scheduler.selectServers(currentTime, this, epdata, epTask);
	    Vector scheduled = epdata.getScheduledTasks();

	    // some tasks scheduled
	    for (int i = scheduled.size() - 1; i >= 0; i--) {
		EPRequestedData d = (EPRequestedData)scheduled.elementAt(i);
		d.epTask.startTime = d.timeEventComes;
		ServerNode node = (ServerNode)d.getDestination();
		initRoute(d.timeEventComes, d, node);
		// moved from processEvent 
		scheduledData.addElement(d);
	    }

	} catch (BricksNotScheduledException e) {
	    SimulationDebug.println(e);
	    epdata.outLogString(epdata + " scheduling failed");
	    // add epdata into reschedulingList
	    addIntoRescheduledList(epdata);
	}
    }
}

