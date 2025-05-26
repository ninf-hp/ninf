package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

public class NaiveServerPredictor extends ServerPredictor implements Predictor {

    protected Hashtable predictInfos = new Hashtable();

    public NaiveServerPredictor(MetaPredictor owner) {
	init(owner);
    }

/************************* inner class *************************/
    protected class PredictInfo {
	ServerInfo serverInfo;
	int numConflict;

	protected PredictInfo(ServerInfo serverInfo) {
	    this.serverInfo = serverInfo;
	    this.numConflict = 0;
	}

	protected void add() {
	    numConflict++;
	}
	
	protected int getNumOfConflict() {
	    return numConflict;
	}
    }

/************************* needed method *************************/
    public String getName() {
	return "NaiveServerPredictor";
    }

    public ServerPrediction getServerPrediction(
	double currentTime, ServerNode serverNode, ServerInfo serverInfo
    ) {
	PredictInfo predictInfo = (PredictInfo)predictInfos.get(serverNode);
	if ((predictInfo != null) &&
	    (predictInfo.serverInfo.probeTime == serverInfo.probeTime)) {
	    // Current PredictInfo is there.
	    ServerInfo info = (ServerInfo)serverInfo.clone();
	    info.loadAverage = 
		info.loadAverage + predictInfo.getNumOfConflict();
	    return new ServerPrediction(serverInfo, info);

	} else {
	    // PredictInfo is old or nothing.
	    predictInfos.put(serverNode, new PredictInfo(serverInfo));
	    return null;
	}
    }

    public void addServerLoad(
        double currentTime, ServerNode serverNode, RequestedData data
    ) {
	addServerLoad(serverNode, data.numInstructions);
    }

    public void addServerLoad(double currentTime, EPTask task) {
	addServerLoad((ServerNode)task.server, task.numInstructions);
    }

    public void addServerLoad(
	ServerNode serverNode, double numInstructions
    ) {
	PredictInfo predictInfo = (PredictInfo)predictInfos.get(serverNode);
	if (predictInfo == null) {
	    ServerInfo serverInfo = resourceDB.getServerInfo(serverNode);
	    predictInfo = new PredictInfo(serverInfo);
	    predictInfos.put(serverNode, predictInfo);
	}
	predictInfo.add();
    }
}
