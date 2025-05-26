package bricks.scheduling;
import bricks.environment.*;
import bricks.util.*;
import java.util.*;

public class LoadIntensiveScheduler extends Scheduler implements SchedulingUnit {

    public LoadIntensiveScheduler(String keyOfMetaPredictor) {
	this.keyOfMetaPredictor = keyOfMetaPredictor;
	schedulingOverhead = 0.0;
    }

/************************* needed method *************************/
    public String getName() {
	return "LoadIntensiveScheduler";
    }

    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {
	double minScore = Double.POSITIVE_INFINITY;
	double maxScore = -1.0;
	ServerNode selectedServer = null;
	Enumeration e = resourceDB.servers(data);

	while (e.hasMoreElements()) {
	    ServerNode serverNode = (ServerNode)e.nextElement();
	    ServerInfo serverInfo = metaPredictor.getServerInfo(
		currentTime, serverNode
	    );

	    StaticServerInfo info = resourceDB.getStaticServerInfo(serverNode);
	    double performance = info.performance;

	    if (serverInfo.availableCpu < 0.0) {
		double score = serverInfo.loadAverage / performance;
		if (minScore > score) {
		    minScore = score;
		    selectedServer = serverNode;
		}
		SimulationDebug.println(serverInfo.toString());
		SimulationDebug.println("bandwidth = " + performance);
		SimulationDebug.println(serverNode + " : load score = " + score);
	    } else {  // NWS
		double score = serverInfo.availableCpu * performance;
		if (maxScore < score) {
		    maxScore = score;
		    selectedServer = serverNode;
		}
	    }
	}
	if (selectedServer == null)
	    throw new BricksNotScheduledException(this.toString());

	//debug
	SimulationDebug.primaryPrintln(
	    "LoadIntensiveScheduler : select " + selectedServer
	);
	updateStatus(currentTime, clientNode, selectedServer, data);
    }
}

