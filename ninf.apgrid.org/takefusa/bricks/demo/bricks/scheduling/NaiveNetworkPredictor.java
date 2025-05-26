package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;

public class NaiveNetworkPredictor extends NetworkPredictor implements Predictor {

    protected Hashtable predictInfos = new Hashtable();

    public NaiveNetworkPredictor(MetaPredictor owner) {
	init(owner);
    }

/************************* inner class *************************/
    protected class PredictInfo {
	NetworkInfo networkInfo;
	int numConflict;

	protected PredictInfo(NetworkInfo networkInfo) {
	    this.networkInfo = networkInfo;
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
	return "NaiveNetworkPredictor";
    }

    public NetworkPrediction getNetworkPrediction(
	double currentTime, NetworkInfo networkInfo
    ) {
	SimulationDebug.println("NaiveNetworkPredictor: getNetworkInfo()...");

	NodePair pair = networkInfo.owner;
	PredictInfo predictInfo = (PredictInfo)predictInfos.get(pair);
	//System.out.println(networkInfo);
	//System.out.println(predictInfo);
	if ((predictInfo != null) && 
	    (predictInfo.networkInfo.probeTime == networkInfo.probeTime)) {
	    // Current PredictInfo is there.
	    NetworkInfo info = (NetworkInfo)networkInfo.clone();
	    info.throughput = 
		info.throughput / ( 1 + predictInfo.getNumOfConflict());
	    return new NetworkPrediction(networkInfo, info);

	} else {
	    // PredictInfo is old or nothing.
	    predictInfos.put(pair, new PredictInfo(networkInfo));
	}
	return null;
    }

    public void addNetworkLoad(
	double currentTime, Node sourceNode, Node destinationNode, 
	RequestedData data
    ) {
	addNetworkLoad(
	    currentTime, sourceNode, destinationNode, data.dataSizeForSend
	);
    }

    public void addNetworkLoad(double currentTime, EPTask task) {
	addNetworkLoad(
	    currentTime, task.client, task.server, task.dataSizeForSend
	);
    }

    public void addNetworkLoad(
	double currentTime, Node sourceNode, Node destinationNode, double size
    ) {
	SimulationDebug.println("NaiveNetworkPredictor: addNetworkLoad()...");
	NodePair pair = new NodePair(sourceNode, destinationNode);
	PredictInfo predictInfo = (PredictInfo)predictInfos.get(pair);
	predictInfo.add();
    }
}
