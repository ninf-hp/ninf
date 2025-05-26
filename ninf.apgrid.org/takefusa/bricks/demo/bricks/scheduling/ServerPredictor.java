package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;

public abstract class ServerPredictor {

    MetaPredictor owner;
    ResourceDB resourceDB;
    
    protected void init(MetaPredictor owner) {
	this.owner = owner;
	this.resourceDB = owner.resourceDB;
	owner.put(this);
    }

    public void addServerLoad(
        double currentTime, ServerNode serverNode, RequestedData data
    ) {;}
    public void addServerLoad(double currentTime, EPTask task) {;}

    public ServerPrediction getServerPrediction(
	double currentTime, ServerNode serverNode, ServerInfo serverInfo
    ) {
	return null;
    }
}
