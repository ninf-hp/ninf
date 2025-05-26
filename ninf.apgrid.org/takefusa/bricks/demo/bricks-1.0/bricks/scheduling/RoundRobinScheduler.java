package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

public class RoundRobinScheduler extends Scheduler implements SchedulingUnit {

    protected Vector servers;
    protected int index;

    public RoundRobinScheduler(String keyOfMetaPredictor, int index) {
	this.keyOfMetaPredictor = keyOfMetaPredictor;
	this.index = index - 1;
	schedulingOverhead = 0.0;
    }

    public RoundRobinScheduler(String keyOfMetaPredictor) {
	this.keyOfMetaPredictor = keyOfMetaPredictor;
	this.index = -1;
	schedulingOverhead = 0.0;
    }

/************************* needed method *************************/
    public String getName() {
	return "RoundRobinScheduler";
    }

    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {
	servers = resourceDB.getServerList();
	if (servers.size() == 0)
	    throw new BricksNotScheduledException(this.toString());

	addIndex();
	ServerNode serverNode = (ServerNode)servers.elementAt(index);

	SimulationDebug.println(
	    "index/servers.size() = " + index + "/" + 
	    servers.size() + ", ServerNode = " + serverNode
	);

	updateStatus(currentTime, clientNode, serverNode, data);
    }

    private void addIndex() {
	index = ++index % servers.size();
    }
}

