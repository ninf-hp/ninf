package bricks.scheduling;
import bricks.environment.*;
import bricks.util.*;
import java.util.*;

public abstract class Scheduler implements SchedulingUnit {
    public String key;
    protected double schedulingOverhead;
    protected SimulationSet owner;
    protected String keyOfMetaPredictor;
    protected MetaPredictor metaPredictor;
    protected ResourceDB resourceDB;

    public abstract void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException;

/************************* public method *************************/
    // caller: EPClientNode
    public void selectServers(
	double currentTime, ClientNode clientNode, 
	EPRequestedData epdata, EPTask eptask
    ) throws BricksNotScheduledException {;}

    public String toString() {
	return getName() + "_" + key;
    }

    public void init(SimulationSet owner) {
	this.owner = owner;
	metaPredictor = owner.getMetaPredictor(keyOfMetaPredictor);
	resourceDB = metaPredictor.getResourceDB();
    }

    public void finishTask(RequestedData data) {
	resourceDB.removeSchedulingInfo(data);
    }

    public void finishTask(EPTask eptask) {
	resourceDB.removeSchedulingInfo(eptask);
    }

/************************* protected method *************************/

    protected double getEstimate(
	double currentTime, ClientNode clientNode, 
	ServerNode serverNode, RequestedData data
    ) {
	// compute Comptation elapsedTime
	double comp = getComputationEstimate(
	    currentTime, serverNode, data.numInstructions
	);
	    
	// compute Communication elapsedTime: send
	double send = getCommunicationEstimate(
	    currentTime, clientNode, serverNode, data.dataSizeForSend
	);

	// compute Communication elapsedTime: receive
	double recv = getCommunicationEstimate(
	    currentTime, serverNode, clientNode, data.dataSizeForReceive
	);

	//System.out.println("elapsedTime: " + 
	SimulationDebug.println("elapsedTime: " + 
				Format.format(comp+send+recv, 3) + " = " +
				Format.format(send, 3) + " + " +
				Format.format(recv, 3) + " + " +
				Format.format(comp, 3));

	return comp + send + recv;
    }

    protected double getComputationEstimate(
	double currentTime, ServerNode serverNode, double numInstructions
    ) {
	ServerInfo serverInfo = metaPredictor.getServerInfo(
	    currentTime, serverNode
	);

	StaticServerInfo info = resourceDB.getStaticServerInfo(serverNode);
	double performance = info.performance;

	double estimate;
	if (serverInfo.availableCpu < 0.0) {
	    estimate = numInstructions / 
		(performance / (serverInfo.loadAverage + 1.0));
	    
	} else {  // NWS
	    estimate = numInstructions / 
		(performance * serverInfo.availableCpu);
	}
	SimulationDebug.println(serverInfo.toString());
	SimulationDebug.println("bandwidth = " + performance);
	SimulationDebug.println(getName() + ": " + serverNode + 
				" : server estimate = " + estimate);
	return estimate;
    }

    protected double getCommunicationEstimate(
	double currentTime, Node sourceNode, Node destinationNode, 
	double dataSize
    ) {
	NetworkInfo networkInfo = metaPredictor.getNetworkInfo(
	    currentTime, sourceNode, destinationNode
	);
	double estimate = dataSize / networkInfo.throughput;
	SimulationDebug.println(
	    getName() + ": " + sourceNode + "-" + destinationNode + 
	    " : network estimate = " + estimate
	);
	return estimate;
    }

    protected void updateStatus(
	double currentTime, ClientNode client, ServerNode server,
	RequestedData data
    ) {
	SimulationDebug.println("updateState for single task job");

	/* add scheduling overhead */
	//data.timeEventComes = data.timeEventComes + schedulingOverhead;
	data.timeEventComes = currentTime + schedulingOverhead;

	StaticServerInfo info = resourceDB.getStaticServerInfo(server);
	data.putDestination(server, info.priority);

	storeSchedulingInfo(currentTime, client, server, data);
    }
	
    // for not-scheduled job
    protected  void updateStatus(RequestedData data, double interval) {
	SimulationDebug.println("updateState for rescheduling");
	// add scheduling overhead 
	data.timeEventComes = 
	    data.timeEventComes + schedulingOverhead + interval;
    }

    protected void storeSchedulingInfo(
	double currentTime, ClientNode client, ServerNode server, 
	RequestedData data
    ) {
	data.putEstimate(
	    getCommunicationEstimate(
	        currentTime, client, server, data.dataSizeForSend
	    ), 
	    getCommunicationEstimate(
	        currentTime, server, client, data.dataSizeForReceive
	    ), 
	    getComputationEstimate(
	        currentTime, server, data.numInstructions
	    )
	);
	metaPredictor.addServerLoad(currentTime, server, data);
	metaPredictor.addNetworkLoad(currentTime, client, server, data);
	resourceDB.putSchedulingInfo(data);
    }

    //for deadline scheduling
    protected double getDeadline(double currentTime, RequestedData data) {

	double deadline = currentTime + 
	    (data.numInstructions / resourceDB.aveServerPerformance +
	     (data.dataSizeForSend + data.dataSizeForReceive) /
	     resourceDB.aveNetworkThroughput) * data.deadlineFactor;

	//System.out.println(
	SimulationDebug.println(
	    data + "'s deadline is " + data.deadline + " = " + 
	    currentTime + " + (" + 
	    data.numInstructions + "/" + resourceDB.aveServerPerformance
	    + "+(" + data.dataSizeForSend + "+" + data.dataSizeForReceive +
	    ")/" + resourceDB.aveNetworkThroughput+ ")) * " +
	    data.deadlineFactor
	);
	return deadline;
    }

/************************* for EP scheduling *************************/
    protected void updateStatus(
	double currentTime, EPRequestedData epdata
    ) {
	SimulationDebug.println("updateState for EP exe");
	//epdata.timeEventComes = epdata.timeEventComes + schedulingOverhead;
	epdata.timeEventComes = currentTime + schedulingOverhead;

	Enumeration e = epdata.epTasks();
	while (e.hasMoreElements()) {
	    EPTask task = (EPTask)e.nextElement();
	    if (task.status.equals("SCHEDULED"))
		updateTaskStatus(currentTime, task);
	}
    }

    protected void updateTaskStatus(double currentTime, EPTask eptask) {
	StaticServerInfo info = 
	    resourceDB.getStaticServerInfo((ServerNode)eptask.server);
	eptask.update(info.priority);
	storeSchedulingInfo(currentTime, eptask);
    }

    protected void storeSchedulingInfo(double currentTime, EPTask eptask) {
	eptask.putEstimate(
	    getCommunicationEstimate(
	        currentTime, eptask.client, eptask.server, eptask.dataSizeForSend), 
	    getCommunicationEstimate(
	        currentTime, eptask.server, eptask.client, eptask.dataSizeForReceive), 
	    getComputationEstimate(
	        currentTime, eptask.server, eptask.numInstructions)
	);
	metaPredictor.addServerLoad(currentTime, eptask);
	metaPredictor.addNetworkLoad(currentTime, eptask);
	resourceDB.putSchedulingInfo(eptask);
    }

    // for deadline scheduling
    protected double getDeadline(double currentTime, EPTask eptask) {

	double deadline = currentTime + 
	    (eptask.numInstructions / resourceDB.aveServerPerformance +
	     (eptask.dataSizeForSend + eptask.dataSizeForReceive) /
	     resourceDB.aveNetworkThroughput) * eptask.deadlineFactor;
	return deadline;
    }
}

