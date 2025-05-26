package bricks.util;
import bricks.environment.*;

public class ServerInfo implements Cloneable {

    public ServerNode owner;
    public double probeTime = Double.NEGATIVE_INFINITY;
    public double trackingTime = -1.0;
    public double loadAverage;
    public double cpuUtilization = 100.0;
    public double availableCpu = -1.0;

    public ServerInfo(ServerNode owner, double loadAverage) {
	this.owner = owner;
	this.loadAverage = loadAverage;
    }

    public ServerInfo(
	ServerNode owner, double loadAverage, double cpuUtilization
    ) {
	this.owner = owner;
	this.loadAverage = loadAverage;
	this.cpuUtilization = cpuUtilization;
    }

    public ServerInfo(
	ServerNode owner, double probeTime, double trackingTime, 
	double loadAverage, double cpuUtilization
    ) {
	this.owner = owner;
	this.probeTime = probeTime;
	this.trackingTime = trackingTime;
	this.loadAverage = loadAverage;
	this.cpuUtilization = cpuUtilization;
    }

    public String toString() {
	return owner + " " + Format.format(probeTime, 3) + " " + 
	    Format.format(trackingTime, 3) + " " + 
	    Format.format(loadAverage , 6) + " " + 
	    Format.format(cpuUtilization, 6);
    }

    public synchronized Object clone() {
        try {
	    ServerInfo s = (ServerInfo)super.clone();
	    s.owner = owner;
	    s.probeTime = probeTime;
	    s.trackingTime = trackingTime;
	    s.loadAverage = loadAverage;
	    s.cpuUtilization = cpuUtilization;
	    return s;
        } catch (CloneNotSupportedException e) {
            // This shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
