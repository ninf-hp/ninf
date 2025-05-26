package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

public class LoadThroughputScheduler extends Scheduler implements SchedulingUnit {

    public LoadThroughputScheduler(String keyOfMetaPredictor) {
	this.keyOfMetaPredictor = keyOfMetaPredictor;
	schedulingOverhead = 0.0;
    }

    // for Deadline
    public LoadThroughputScheduler(
	String keyOfMetaPredictor, double optimisticFactor, double interval
    ) {
	this.keyOfMetaPredictor = keyOfMetaPredictor;
	schedulingOverhead = 0.0;
    }

/************************* needed method *************************/
    public String getName() {
	return "LoadThroughputScheduler";
    }

    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {
	// first time (for the comparison!!)
	if ((data.deadline < 0.) && (data.deadlineFactor > 0))
	    data.deadline = getDeadline(currentTime, data);

	double minElapsedTime = Double.POSITIVE_INFINITY;
	ServerNode selectedServer = null;

	Enumeration e = resourceDB.servers(data);
	while (e.hasMoreElements()) {
	    ServerNode serverNode = (ServerNode) e.nextElement();
	    if (!data.scheduled(serverNode)) { //for fallback
		double elapsedTime = getEstimate(
		    currentTime, clientNode, serverNode, data
		);
		if (minElapsedTime > elapsedTime) {
		    minElapsedTime = elapsedTime;
		    selectedServer = serverNode;
		}
	    }
	}

	// for fallback
	if (selectedServer == null) {
	    if (data.numSchedule() == 0)
		throw new BricksNotScheduledException(this.toString());

	    e = data.scheduledServers();
	    while (e.hasMoreElements()) {
		ServerNode serverNode = (ServerNode) e.nextElement();
		double elapsedTime = getEstimate(
		    currentTime, clientNode, serverNode, data
		);
		if (minElapsedTime > elapsedTime) {
		    minElapsedTime = elapsedTime;
		    selectedServer = serverNode;
		}
	    }
	}

	//System.out.println(
	SimulationDebug.println(
	    "LoadThroughputScheduler : select " + selectedServer
	);
	updateStatus(currentTime, clientNode, selectedServer, data);
	if (data.deadlineFactor > 0)
	    data.setElapsedTime(data.deadline - currentTime);
    }
}

