package bricks.util;
import java.util.*;

public class QueueFCFS extends Queue implements SubComponent {

    public QueueFCFS (Sequence throughput) {
	this.queue = new Vector(10000);
	this.throughput = throughput;
	if (throughput instanceof InterpolationCpu) {
	    interpolationCpu = true;
	}
    }

/************************* needed method *************************/
    public String getName() {
	return "QueueFCFS";
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
