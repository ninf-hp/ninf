package bricks.util;
import bricks.environment.*;
import java.util.*;

public class EPTask {

    public EPRequestedData owner;

    public ClientNode client;
    public ServerNode server;

    public int taskId;
    public double dataSizeForSend;
    public double dataSizeForReceive;
    public double numInstructions;

    public double deadlineFactor;
    public int priority;
    public double startTime = Double.NEGATIVE_INFINITY;

    public double estimatedSendDuration;
    public double estimatedExecDuration;
    public double estimatedRecvDuration;
    //public double estimatedDuration;

    public double sendDuration;
    public double execDuration;
    public double recvDuration;
    //public double duration;

    public int reschedule = 0;

    /* status: NOT_SCHEDULED, SCHEDULED, DISPATCHED, FINISHED */
    public String status = "NOT_SCHEDULED";

    public EPTask (
	EPRequestedData owner, int taskId, 
	double dataSizeForSend, double dataSizeForReceive, 
	double numInstructions
    ) {
	this.owner = owner;
	this.taskId = taskId;
	this.dataSizeForSend = dataSizeForSend;
	this.dataSizeForReceive = dataSizeForReceive;
	this.numInstructions = numInstructions;
	this.client = owner.client;

	SimulationDebug.println(
            "owner = " + owner + ", taskId = " + taskId + " [" + 
	    Format.format(dataSizeForSend, 3) + ", " + 
	    Format.format(dataSizeForReceive, 3) + ", " + 
	    Format.format(numInstructions, 3) + "]"
	);
    }

    public void allocateServer(ServerNode server) {
	this.server = server;
	this.status = "SCHEDULED";
    }

    /** 
     * caller: scheduler
     **/
    public void update(int priority) {
	this.priority = priority;
    }

    /** 
     * caller: scheduler
     **/
    public void putEstimate(double send, double recv, double exec) {
	estimatedSendDuration = send;
	estimatedExecDuration = exec;
	estimatedRecvDuration = recv;
    }

    /** 
     * caller: ResourceDB.removeSchedulingInfo()
     **/
    public boolean equals(Object object) {
	if (object instanceof EPTask) {
	    EPTask task2 = (EPTask)object;
	    if (this.taskId == task2.taskId)
		return true;
	}
	return false;
    }

    /**
     * for fallback
     **/
    public void flash() {
	server = null;
	status = "NOT_SCHEDULED";
	reschedule++;
    }
}

