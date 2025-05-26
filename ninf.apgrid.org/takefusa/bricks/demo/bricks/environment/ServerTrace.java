package bricks.environment;

public class ServerTrace {
    public int queueLength;
    public double time;
    public boolean idle;

    public ServerTrace(double time, boolean idle, int queueLength) {
	this.time = time;
	this.idle = idle;
	this.queueLength = queueLength;
    }
}
