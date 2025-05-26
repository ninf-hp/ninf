package bricks.environment;
import bricks.util.*;
import java.io.*;
import java.util.*;

/** 
 * FallbackServerNode.java
 * <pre>
 * FallbackServerNode is a ServerNode which triggers fallback.
 * </pre>
 */
public class FallbackServerNode extends ServerNode {

    protected int numFallback;

    public FallbackServerNode(
	SimulationSet owner, String key, Queue queue,
	Sequence numInstructions, Sequence interarrivalTime,
	int numFallback
    ){
	super(owner, key, queue, numInstructions, interarrivalTime);
	this.numFallback = numFallback;
    }

    public FallbackServerNode(
	SimulationSet owner, String key, Queue queue,
	Sequence numInstructions, Sequence interarrivalTime,
	int numFallback, int traceBufferSize
    ){
	super(owner, key, queue, numInstructions, interarrivalTime,
	      traceBufferSize);
	this.numFallback = numFallback;
    }

/************************* needed method *************************/
    /** 
     * returns a type of the FallbackServerNode.
     */
    public String getName() {
	return "FallbackServerNode";
    }

    // override in order for fallback.
    public void schedule(double currentTime, Data data) {

	if (data instanceof RequestedData) {
	    RequestedData rd = (RequestedData)data;
	    if ((rd.numSchedule() < numFallback) && (rd.elapsedTime > 0.0)) {
		double total = rd.sendDuration + rd.estimatedRecvDuration +
		    queue.getEstimation(currentTime, rd);
		if (total > rd.elapsedTime) {
		    rd.fallback(currentTime);
		    updateNextEvent(currentTime);
		    return;
		}
	    }
	}
	super.schedule(currentTime, data);
    }
}
