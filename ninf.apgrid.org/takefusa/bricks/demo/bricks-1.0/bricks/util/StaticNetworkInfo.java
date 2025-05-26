package bricks.util;
import bricks.environment.*;

public class StaticNetworkInfo {

    public NodePair owner;
    public double maxThroughput;

    public StaticNetworkInfo(NodePair owner, double maxThroughput) {
	this.owner = owner;
	this.maxThroughput = maxThroughput;
    }

    public String toString() {
	return "StaticNetworkInfo[" + owner + " " + 
	    Format.format(maxThroughput, 3);
    }
}
