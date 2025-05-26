package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;

public abstract class NetworkPredictor {

    MetaPredictor owner;
    ResourceDB resourceDB;
    
    protected void init(MetaPredictor owner) {
	this.owner = owner;
	this.resourceDB = owner.resourceDB;
	owner.put(this);
    }

    public void addNetworkLoad(
	double currentTime, Node sourceNode, Node destinationNode, 
	RequestedData data
    ) {;}
    public void addNetworkLoad(double currentTime, EPTask task) {;}

    public NetworkPrediction getNetworkPrediction(
	double currentTime, NetworkInfo networkInfo
    ) {
	return null;
    }
}
