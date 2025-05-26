package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

public class RandomScheduler extends Scheduler implements SchedulingUnit {

    protected Random random;
    protected Vector servers;

    public RandomScheduler(
	String keyOfMetaPredictor, long seed
    ) {
	this.keyOfMetaPredictor = keyOfMetaPredictor;
	random = new Random(seed);
	schedulingOverhead = 0.0;
    }


/************************* needed method *************************/
    public String getName() {
	return "RandomScheduler";
    }

    public void selectServers (
	double currentTime, ClientNode clientNode, RequestedData data
    ) throws BricksNotScheduledException {
	servers = resourceDB.getServerList();
	if (servers.size() == 0)
	    throw new BricksNotScheduledException(this.toString());

	ServerNode serverNode = (ServerNode)servers.elementAt(
	    Math.abs(random.nextInt()) % servers.size()
	);
	updateStatus(currentTime, clientNode, serverNode, data);
    }
}

