package bricks.environment;
import bricks.util.*;
import java.io.*;
import java.util.*;

/** 
 * ForwardNode forwards data from [data.previousNode] to [data.nextNode]
 **/
public class ForwardNode extends NodeWithQueue {

    /**
     * Constructs a ForwardNode with<BR>
     *     owner : a SimulationSet of your configuration,<BR>
     *     key : a key of the ForwardNode,<BR>
     *     logWriter : PrintWriter for a ForwardNodes log file.<BR>
     **/
    public ForwardNode(
	SimulationSet owner, String key, PrintWriter logWriter
    ) {
	this.owner = owner;
	this.key = key;
	this.logWriter = logWriter;
	nextLink = new Vector();
   }

    /**
     * Constructs a ForwardNodeNoLog with<BR>
     *     owner : a SimulationSet of your configuration,<BR>
     *     key : a key of the ForwardNode.<BR>
     **/
    public ForwardNode(SimulationSet owner, String key) {
	this.owner = owner;
	this.key = key;
	nextLink = new Vector();
   }

/************************* needed method *************************/
    /** 
     * returns a type of the ForwardNode.
     **/
    public String getName() {
	return "ForwardNode";
    }

    public String toOriginalString(double currnetTime) {
	String str = "  [" + getName() + " " + key + "]\n";
	str += queue.toOriginalString();
	return str;
    }

    public String toInitString() {
	String str = "  [" + getName() + " " + key + "] : ";
	str += queue.toOriginalString();
	return str;
    }

    public void updateNextEvent(double currentNode) {
	error("This is ForwardNode!!");
    }

    public void processEvent(double currentTime) {
	error("This is ForwardNode!!");
    }

    // override
    public void schedule(double currentTime, Data data) {
	if(logWriter != null)
	    logWriter.println(" thru-" + key);
	data.gotoNextNode(currentTime);
    }

    protected Data getNextData(double currentTime) {
	error("This is ForwardNode!!");
	return null;
    }
}
