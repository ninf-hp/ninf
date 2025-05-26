package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

public class LoadCorrectionServerPredictor extends ServerPredictor implements Predictor {

    double loadUnit;

    public LoadCorrectionServerPredictor(MetaPredictor owner, double loadUnit) {
	init(owner);
	this.loadUnit = loadUnit;
    }

/************************* needed method *************************/
    public String getName() {
	return "LoadCorrectionServerPredictor";
    }

    public ServerPrediction getServerPrediction(
	double currentTime, ServerNode serverNode, ServerInfo serverInfo
    ) {
	Vector v = resourceDB.getSchedulingInfo(serverNode);
	if (v.size() == 0)
	    return null;

	int num = 0;
	double finish = 0.0;
	Enumeration e = v.elements();
	while (e.hasMoreElements()) {
	    Object o = e.nextElement();
	    if (o instanceof RequestedData) {
		RequestedData data = (RequestedData)o;
		finish = data.estimatedSendDuration + 
		    data.estimatedExecDuration;
	    } else if (o instanceof EPTask) {
		EPTask task = (EPTask)o;
		finish = 
		    task.estimatedSendDuration + task.estimatedExecDuration;
	    } else {
		System.err.println(this + ": bad scheduling info.");
		System.exit(3);
	    }
	    if (finish > currentTime)
		num++;
	    }
	ServerInfo info = (ServerInfo)serverInfo.clone();
	info.loadAverage = info.loadAverage + num * loadUnit;
	return new ServerPrediction(serverInfo, info);
    }
}
