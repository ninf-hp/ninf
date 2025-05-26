package bricks.scheduling;
import bricks.environment.*;
import bricks.util.*;
import java.util.*;

public class MetaPredictor implements SchedulingUnit {
    public String key;
    protected String keyOfResourceDB;
    protected SimulationSet owner;
    protected ResourceDB resourceDB;

    protected Vector serverPredictors = new Vector();
    protected Vector networkPredictors = new Vector();

    public MetaPredictor(
	String key, String keyOfResourceDB, SimulationSet owner
    ) {
	this.key = key;
	this.keyOfResourceDB = keyOfResourceDB;
	this.owner = owner;
	resourceDB = owner.getResourceDB(keyOfResourceDB);
    }

    public String toString() {
	return getName();
    }

    public String getName() {
	String s = "Predictor(";
	Enumeration e = networkPredictors.elements();
	while (e.hasMoreElements()) {
	    s = s + (NetworkPredictor)e.nextElement() + ", ";
	}
	e = serverPredictors.elements();
	while (e.hasMoreElements()) {
	    s = s + (ServerPredictor)e.nextElement() + ", ";
	}
	return s + ")";
    }

    public void put(ServerPredictor predictor) {
	serverPredictors.addElement(predictor);
    }
	
    public void put(NetworkPredictor predictor) {
	networkPredictors.addElement(predictor);
    }

    public void init() {
	if (serverPredictors.isEmpty()) {
	    serverPredictors.addElement(new NonServerPredictor(this));
	}
	if (!networkPredictors.isEmpty()) {
	    networkPredictors.addElement(new NonNetworkPredictor(this));
	}
    }

    public ResourceDB getResourceDB() {
	return resourceDB;
    }

    public ServerInfo getServerInfo(
	double currentTime, ServerNode serverNode
    ) {
	ServerInfo serverInfo = resourceDB.getServerInfo(serverNode);

	if (serverInfo != null) {
	    double minError = 0.0;
	    ServerPrediction suitablePrediction = null;
	    Enumeration e = serverPredictors.elements();
	    while (e.hasMoreElements()) {
		ServerPredictor predictor = (ServerPredictor)e.nextElement();
		ServerPrediction prediction = predictor.getServerPrediction(
		    currentTime, serverNode, serverInfo
	        );
		if (prediction != null) {
		    if ((suitablePrediction == null) || 
			(prediction.error < minError)) {
			suitablePrediction = prediction;
			minError = prediction.error;
		    }
		}
	    }
	    if (suitablePrediction != null) {
		serverInfo = suitablePrediction.predictedServerInfo;
	    }
	}
	return serverInfo;
    }

    public NetworkInfo getNetworkInfo(
	double currentTime, Node sourceNode, Node destinationNode
    ) {
	NetworkInfo networkInfo = 
	    resourceDB.getNetworkInfo(sourceNode, destinationNode);

	if (networkInfo != null) {
	    double minError = 0.0;
	    NetworkPrediction suitablePrediction = null;
	    Enumeration e = networkPredictors.elements();
	    while (e.hasMoreElements()) {
		NetworkPredictor predictor = (NetworkPredictor)e.nextElement();
		NetworkPrediction prediction = predictor.getNetworkPrediction(
		    currentTime, networkInfo
		);
		if (prediction != null) {
		    if ((suitablePrediction == null) || 
			(prediction.error < minError)) {
			suitablePrediction = prediction;
			minError = prediction.error;
		    }
		}
	    }
	    if (suitablePrediction != null) {
		networkInfo = suitablePrediction.predictedNetworkInfo;
	    }
	}
	return networkInfo;
    }

    public void addServerLoad(
        double currentTime, ServerNode serverNode, RequestedData data
    ) {
	Enumeration e = serverPredictors.elements();
	while (e.hasMoreElements()) {
	    ServerPredictor predictor = (ServerPredictor)e.nextElement();
	    predictor.addServerLoad(currentTime, serverNode, data);
	}
    }

    public void addServerLoad(double currentTime, EPTask task) {
	Enumeration e = serverPredictors.elements();
	while (e.hasMoreElements()) {
	    ServerPredictor predictor = (ServerPredictor)e.nextElement();
	    predictor.addServerLoad(currentTime, task);
	}
    }

    public void addNetworkLoad(
	double currentTime, Node sourceNode, Node destinationNode, 
	RequestedData data
    ) {
	Enumeration e = networkPredictors.elements();
	while (e.hasMoreElements()) {
	    NetworkPredictor predictor = (NetworkPredictor)e.nextElement();
	    predictor.addNetworkLoad(
		currentTime, sourceNode, destinationNode, data
	    );
	}
    }

    public void addNetworkLoad(double currentTime, EPTask task) {
	Enumeration e = networkPredictors.elements();
	while (e.hasMoreElements()) {
	    NetworkPredictor predictor = (NetworkPredictor)e.nextElement();
	    predictor.addNetworkLoad(currentTime, task);
	}
    }
}
