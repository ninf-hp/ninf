package bricks.util;
import java.util.*;

public class QueueFCFSFiniteBuffer extends Queue implements SubComponent {

    public QueueFCFSFiniteBuffer(Sequence throughput, int bufferMax) {
	this.queue = new Vector(bufferMax);
	this.throughput = throughput;
	this.bufferMax = bufferMax;
	if (throughput instanceof InterpolationCpu) {
	    interpolationCpu = true;
	}
    }

/************************* needed method *************************/
    //override
    public boolean isFull() {
	boolean b = false;
	//return bufferMax <= queue.size();
	if (bufferMax == queue.size()) {
	    b = true;
	} else if (bufferMax < queue.size()) {
	    error("");
	}
	return b;
    }

    public String getName() {
	return "QueueFCFSFiniteBuffer";
    }

    public String toOriginalString() {
	String str = "    QueueFCFS(" + throughput + ")[" + size() + "] : ";
	Enumeration e = queue.elements();
	while (e.hasMoreElements()) {
	    Data data = (Data)e.nextElement();
	    str += data + ", ";
	}
	return str + "\n";
    }
}
