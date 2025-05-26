package bricks.environment;
import bricks.util.*;
import java.util.*;

public abstract class NodeWithQueue extends Node {
    public Queue queue;
    protected Sequence interarrivalTime;

    // for event flag
    protected int event;
    protected static final int OTHERSDATA = 0;
    protected static final int QUEUE = 1;
    protected static final int SEND = 2;

    public abstract void processEvent(double currentTime);
    public abstract void updateNextEvent(double currentTime);

/****************************** for idle log ******************************/
    protected static final int IDLE = 0;
    protected static final int BUSY = 1;
    protected int previousState = IDLE;
    protected double totalIdleTime = 0.;
    protected double timeWhenIdleStarted = 0.;

/************************* needed method *************************/
    public boolean isFull() {
	return queue.isFull();
    }

    public void schedule(double currentTime, Data data) {
	queue.enqueue(currentTime, data);
	updateNextEvent(currentTime);
    }

    protected void outQueueLength(double currentTime, int size) {
	printLog(currentTime, " " + size);
    }

    // printLog is overriden by ServerNode for track
    protected void printLog(double currentTime, String log) {
	if (logWriter != null)
	    logWriter.println(
		this + " " + Format.format(currentTime, 3) + " " + log
	    );
    }
}

