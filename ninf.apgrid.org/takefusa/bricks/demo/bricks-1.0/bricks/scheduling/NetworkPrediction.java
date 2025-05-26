package bricks.scheduling;
import bricks.util.*;

public class NetworkPrediction {

    public NetworkInfo networkInfo;
    public NetworkInfo predictedNetworkInfo = null;
    public double error = Double.POSITIVE_INFINITY;

    public NetworkPrediction(
	NetworkInfo networkInfo, NetworkInfo predictedNetworkInfo
    ) {
	this.networkInfo = networkInfo;
	this.predictedNetworkInfo = predictedNetworkInfo;
    }

    public NetworkPrediction(
	NetworkInfo networkInfo, NetworkInfo predictedNetworkInfo, double error
    ) {
	this.networkInfo = networkInfo;
	this.predictedNetworkInfo = predictedNetworkInfo;
	this.error = error;
    }
}
