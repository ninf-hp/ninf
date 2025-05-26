package bricks.scheduling;
import bricks.util.*;

public class ServerPrediction {

    public ServerInfo serverInfo;
    public ServerInfo predictedServerInfo;
    public double error = -1.0;

    public ServerPrediction(
	ServerInfo serverInfo, ServerInfo predictedServerInfo
    ) {
	this.serverInfo = serverInfo;
	this.predictedServerInfo = predictedServerInfo;
    }

    public ServerPrediction(
	ServerInfo serverInfo, ServerInfo predictedServerInfo, double error
    ) {
	this.serverInfo = serverInfo;
	this.predictedServerInfo = predictedServerInfo;
	this.error = error;
    }
}
