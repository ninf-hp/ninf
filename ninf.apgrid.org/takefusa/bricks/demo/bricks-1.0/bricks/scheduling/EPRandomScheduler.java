package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

/**
 * EPRandomScheduler is a random allocation scheduler for EP programs.<BR>
 * Usage:<BR>
 * EPRandomScheduler(String keyOfMetaPredictor, Long seed, double interval)
 **/
public class EPRandomScheduler extends RandomScheduler {

    protected double interval;

    public EPRandomScheduler(
	String keyOfMetaPredictor, long seed, double interval
    ) {
	super(keyOfMetaPredictor, seed);
	this.interval = interval;
    }

/************************* needed method *************************/
    public String getName() {
	return "EPRandomScheduler";
    }

    // first
    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {
	if (!(data instanceof EPRequestedData)) {
	    super.selectServers(currentTime, clientNode, data);
	    return;
	}
	EPRequestedData epdata = (EPRequestedData)data;

	boolean schedule = false;
	SimulationDebug.println("EPRandomScheduler.selectServers");
	servers = resourceDB.getServerList();

	ServerNode serverNode = nextServer();
	while (serverNode != null) {
	    EPTask task = epdata.getNextTask();
	    task.allocateServer(serverNode);
	    schedule = true;
	    serverNode = nextServer();
	    SimulationDebug.println("allocate task: " + task);
	}

	if (schedule) { /* schedule successed */
	    updateStatus(currentTime, epdata);
	} else { /* schedule failed */
	    updateStatus(epdata, interval);
	    throw new BricksNotScheduledException(this.toString());
	}
    }

    // second or more
    public void selectServers (
	double currentTime, ClientNode clientNode, 
	EPRequestedData epdata, EPTask eptask
    ) throws BricksNotScheduledException {
	boolean schedule = false;
	servers = resourceDB.getServerList();

	ServerNode serverNode = nextServer();
	if (serverNode != null) {
	    EPTask task = epdata.getNextTask();
	    task.allocateServer(serverNode);
	    schedule = true;
	}

	if (schedule) { /* schedule successed */
	    updateStatus(currentTime, epdata);
	} else { /* schedule failed */
	    updateStatus(epdata, interval);
	    throw new BricksNotScheduledException(this.toString());
	}
    }

    protected ServerNode nextServer() {
	//System.out.println("***** servers.size = " + servers.size());
	if (servers.size() == 0)
	    return null;
	ServerNode serverNode = (ServerNode)servers.elementAt(
	    Math.abs(random.nextInt()) % servers.size()
	);
	servers.removeElement(serverNode);
	return serverNode;
    }
}

