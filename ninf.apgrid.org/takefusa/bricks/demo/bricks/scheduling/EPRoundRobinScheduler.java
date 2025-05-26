package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

/**
 * EPRoundRobinScheduler is a round robin scheduler for EP programs.<BR>
 * Usage:<BR>
 * EPRoundRobinScheduler(String keyOfMetaPredictor(, int index))
 **/
public class EPRoundRobinScheduler extends RoundRobinScheduler {

    public EPRoundRobinScheduler(String keyOfMetaPredictor, int index) {
	super(keyOfMetaPredictor, index);
    }

    public EPRoundRobinScheduler(String keyOfMetaPredictor) {
	super(keyOfMetaPredictor);
    }

/************************* needed method *************************/
    public String getName() {
	return "EPRoundRobinScheduler";
    }

    // first
    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {
	SimulationDebug.println("EPRoundRobinScheduler.selectServers");
	if (!(data instanceof EPRequestedData)) {
	    super.selectServers(currentTime, clientNode, data);
	    return;
	}

	EPRequestedData epdata = (EPRequestedData)data;
	boolean schedule = false;

	if (epdata.first) { /* first time */
	    //System.out.println("first time: " + epdata);
	    schedule = allocateTasksToAllServers(currentTime, epdata);
	    if (!schedule)
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
	System.err.println(getName() + ": This method is never called.");
	System.exit(3);
    }

    protected boolean allocateTasksToAllServers(
	double currentTime, EPRequestedData epdata
    ) {
	servers = resourceDB.getServerList();
	if (servers.size() == 0)
	    return false;

	EPTask task = epdata.getNextTask();
	while (task != null) {
	    Enumeration e = servers.elements();
	    while (e.hasMoreElements()) {
		ServerNode server = (ServerNode)e.nextElement();
		task.allocateServer(server);
		task = epdata.getNextTask();
		if (task == null)
		    break;
	    }
	}			
	return true;
    }
}
