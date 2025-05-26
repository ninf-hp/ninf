package bricks.util;
import bricks.environment.*;
import java.util.*;

public abstract class Queue implements SubComponent {

    public boolean interpolationCpu = false;
    public NodeWithQueue owner;
    protected Vector  queue;
    public Sequence throughput;
    public double maxThroughput;

    // for isFull of NodeWithQueue
    protected int bufferMax = Integer.MAX_VALUE;

    public abstract String toOriginalString();

/************************* default method *************************/
    public void setMaxThroughput() {
	if((maxThroughput = throughput.max()) < 0.0) {
	    error("Bad Sequence for Throughput");
	}
    }

    public int size() {
	return queue.size();
    }

    public boolean isFull() {
	return false;
    }

    public boolean isEmpty() {
	return queue.isEmpty();
    }	

    public boolean isInterpolationCpu() {
	return interpolationCpu;
    }

    public Data top() {
	return (Data)queue.firstElement();
    }

    public Data last() {
	return (Data)queue.lastElement();
    }

    public double getTimeQueueEventComes() {
	return top().timeEventComes;
    }

    public void processEvent(double currentTime) {
	top().updateDataSize();
	top().gotoNextNode(currentTime);
	// slide queueing jobs
	updateQueue(currentTime);
    }

    public void processEventForPackets(double currentTime) {
	//top().returnRoute();
	top().updateDataSize();
	// slide queueing jobs
	updateQueue(currentTime);
    }

    protected void updateQueue(double currentTime) {
	dequeue(currentTime);
	updateNextTop(currentTime);
    }

    protected void updateNextTop(double currentTime) {
	if (!isEmpty()) {
	    top().updateProcessingTime(
	        currentTime, throughput.nextDouble(currentTime)
	    );
	    top().updateTimeEventComes(currentTime);
	}
    }

    public void dequeue(double currentTime) {
	if (!queue.isEmpty()) {
	    queue.removeElementAt(0);
	} else {
	    error("no dequeue data");
	}
    }

    public void enqueue(double currentTime, Data data) {
	queue.addElement(data);
	if (queue.size() == 1) {
	    updateNextTop(currentTime);
	}	    
    }

    // if (interpolationCpu == true)
    public double getLoad(double currentTime, double trackingTime) {
	if (!interpolationCpu) {
	    error(this + " do not have the InterpolationCpu Queue.");
	}
	return ((InterpolationCpu)throughput).getLoad(
	    currentTime, trackingTime
	);
    }

    public double getCpuUtilization(double currentTime, double trackingTime) {
	if (!interpolationCpu) {
	    error(this + " do not have the InterpolationCpu Queue.");
	}
	return ((InterpolationCpu)throughput).getCpuUtilization(
	    currentTime, trackingTime
	);
    }

    // for fallback
    public double getEstimation(double currentTime, RequestedData data) {
	double total = 0.0;
	Enumeration e = queue.elements();
	while (e.hasMoreElements()) {
	    total += ((Data)e.nextElement()).getDataSize();
	}
	return (total + data.getDataSize())/ throughput.nextDouble(currentTime);
    }

    protected void error(String message) throws RuntimeException {
	System.err.println("Queue Error:: " + message);
	throw new RuntimeException();
    }
}
