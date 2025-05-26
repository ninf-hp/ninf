package bricks.util;
import bricks.environment.*;

public class NetworkInfo implements Cloneable {

    public NodePair owner;
    public double probeTime = Double.NEGATIVE_INFINITY;
    public double throughput;
    public double latency = Double.NEGATIVE_INFINITY;

    public NetworkInfo(
	Node fromNode, Node toNode, double throughput
    ) {
	this.owner = new NodePair(fromNode, toNode);
	this.throughput = throughput;
    }

    public NetworkInfo(
	NodePair owner, double throughput
    ) {
	this.owner = owner;
	this.throughput = throughput;
    }

    public NetworkInfo(
	NodePair owner, double probeTime, double throughput
    ) {
	this.owner = owner;
	this.probeTime = probeTime;
	this.throughput = throughput;
    }

    public NetworkInfo(
	NodePair owner, double probeTime, 
	double throughput, double latency
    ) {
	this.owner = owner;
	this.probeTime = probeTime;
	this.throughput = throughput;
	this.latency = latency;
    }

    public String toString() {
	return owner + " " + Format.format(probeTime, 3) + " " +
	    Format.format(throughput, 3) + " " + 
	    Format.format(latency, 3);
    }

    /*
    public String toString() {
	return owner + " " + Format.format(probeTime, 3) + " " +
	    Format.format(throughput, 3);
    }
    */

    public synchronized Object clone() {
        try {
	    NetworkInfo n = (NetworkInfo)super.clone();
	    n.owner = owner;
	    n.probeTime = probeTime;
	    n.throughput = throughput;
	    n.latency = latency;
	    return n;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
