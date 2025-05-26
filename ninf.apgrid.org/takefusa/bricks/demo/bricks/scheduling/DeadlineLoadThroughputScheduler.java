package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

public class DeadlineLoadThroughputScheduler extends LoadThroughputScheduler {

    /**
     * The optimisticFactor is in the range (0.0, 1.0].
     **/
    protected double optimisticFactor;
    protected double interval;

    public DeadlineLoadThroughputScheduler(
	String keyOfMetaPredictor, double optimisticFactor, double interval
    ) {
	super(keyOfMetaPredictor, optimisticFactor, interval);
	this.optimisticFactor = optimisticFactor;
	this.interval = interval;

	if ((optimisticFactor <= 0.0) || (optimisticFactor > 1.0)) {
	    System.err.println(
		"The optimisticFactor has to be in the range (0.0, 1.0]."
	    );
	    System.exit(3);
	}
    }

/************************* needed method *************************/
    public String getName() {
	return "DeadlineLoadThroughputScheduler";
    }

    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {

	// not defined deadlineFactor
	if (data.deadlineFactor < 0.) {
	    super.selectServers(currentTime, clientNode, data);
	    return;
	}

	// first time
	if (data.deadline < 0.)
	    data.deadline = getDeadline(currentTime, data);

	double idealElapsedTime = 
	    (data.deadline - currentTime) * optimisticFactor;

	ServerNode safeServer = null;
	ServerNode outServer = null;
	double safeDiff = Double.POSITIVE_INFINITY;
	double outDiff = Double.NEGATIVE_INFINITY;

	Enumeration e = resourceDB.servers(data);
	while (e.hasMoreElements()) {
	    ServerNode serverNode = (ServerNode) e.nextElement();
	    if (!data.scheduled(serverNode)) { // for fallback
		double elapsedTime = getEstimate(
		    currentTime, clientNode, serverNode, data
		);
		double diff = idealElapsedTime - elapsedTime;

		if (diff < 0.0) {
		    if (outDiff < diff) {
			outServer = serverNode;
			outDiff = diff;
		    }
		} else {
		    if (safeDiff > diff) {
			safeServer = serverNode;
			safeDiff = diff;
		    }
		}
	    }
	}

	// for fallback
	if ((safeServer == null) && (outServer == null)) {
	    e = data.scheduledServers();
	    while (e.hasMoreElements()) {
		ServerNode serverNode = (ServerNode) e.nextElement();
		double elapsedTime = getEstimate(
		    currentTime, clientNode, serverNode, data
		);
		double diff = idealElapsedTime - elapsedTime;

		if (diff < 0.0) {
		    if (outDiff < diff) {
			outServer = serverNode;
			outDiff = diff;
		    }
		} else {
		    if (safeDiff > diff) {
			safeServer = serverNode;
			safeDiff = diff;
		    }
		}
	    }
	}

	ServerNode selectedServer = null;
	if (safeServer != null) {
	    if (safeDiff > interval) { // reschedule
		updateStatus(data, interval);
		data.reschedule++;
		//System.out.println(
		SimulationDebug.println(
		    this + ": " + currentTime + " : Reschedule");
		throw new BricksNotScheduledException(this.toString());
	    } else {
		selectedServer = safeServer;
		data.expectedFinishTime = currentTime + 
		    idealElapsedTime - safeDiff;
		//System.out.println(
		SimulationDebug.println(
		    this + ": " + currentTime + " : Schedule completed(safe)");
	    }
	} else { // out
	    selectedServer = outServer;
	    data.expectedFinishTime = currentTime + idealElapsedTime - outDiff;
	    //System.out.println(
	    SimulationDebug.println(
		this + ":" + currentTime + " : Schedule completed(out)");
	}

	updateStatus(currentTime, clientNode, selectedServer, data);
	data.setElapsedTime(data.deadline - currentTime);
    }
}
