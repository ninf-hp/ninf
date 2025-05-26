package bricks.util;
import bricks.environment.*;

public class StaticServerInfo {

    public ServerNode owner;
    public double performance;
    public int priority;

    public StaticServerInfo(ServerNode owner, double performance) {
	this.owner = owner;
	this.performance = performance;
	this.priority = (int)performance;
    }

    public String toString() {
	return "StaticServerInfo[" + owner + " " +
	    Format.format(performance, 3) + " " + priority;
    }
}
