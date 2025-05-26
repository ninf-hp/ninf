package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

/**
 * EPSelfScheduler is a self scheduler for EP programs.<BR>
 * Usage:<BR>
 * EPSelfScheduler(String keyOfMetaPredictor)
 **/
public class EPSelfScheduler extends RoundRobinScheduler {

    public EPSelfScheduler(String keyOfMetaPredictor) {
	super(keyOfMetaPredictor);
    }

/************************* needed method *************************/
    public String getName() {
	return "EPSelfScheduler";
    }

    // first
    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {

	SimulationDebug.println("EPSelfScheduler.selectServers");
	if (!(data instanceof EPRequestedData)) {
	    super.selectServers(currentTime, clientNode, data);
	    return;
	}

	EPRequestedData epdata = (EPRequestedData)data;

	if (epdata.first) { /* first time */
	    //System.out.println("first time: " + epdata);
	    if (!allocateTasksToAllServers(currentTime, epdata))
		throw new BricksNotScheduledException(this.toString());
	} else { /* not first time */
	    System.err.println("Client calls this method at first time only");
	    System.exit(3);
	}
    }

    // second or more
    public void selectServers (
	double currentTime, ClientNode clientNode, 
	EPRequestedData epdata, EPTask eptask
    ) throws BricksNotScheduledException {
	servers = resourceDB.getServerList();

	if (!servers.contains(eptask.server)) { /* schedule failed */
	    SimulationDebug.println("Schedule failed!!");
	    throw new BricksNotScheduledException(this.toString());
	}

	/* schedule successed */
	EPTask task = epdata.getNextTask();
	if (task == null) {
	    System.err.println(this + ": All tasks scheduled");
	    System.exit(3);
	}
	SimulationDebug.println("Schedule successed!!");
	task.allocateServer(eptask.server);
	updateStatus(currentTime, epdata);
    }

    protected boolean allocateTasksToAllServers(
	double currentTime, EPRequestedData epdata
    ) {
	servers = resourceDB.getServerList();
	if (servers.size() == 0)
	    return false;

	Enumeration e = servers.elements();
	while (e.hasMoreElements()) {
	    ServerNode server = (ServerNode)e.nextElement();
	    EPTask task = epdata.getNextTask();
	    task.allocateServer(server);
	}			

	return true;
    }
}
