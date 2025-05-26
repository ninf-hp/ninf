package bricks.environment;
import bricks.util.*;
import java.util.*;

/* timeEventComes(first element) > ... > timeEventComes(last element) */
// for merge + FIFO
public class SendBuffer {
    protected Vector buffer;

    public SendBuffer() {
	buffer = new Vector();
    }

    public SendBuffer(int size) {
	buffer = new Vector(size);
    }

    public int size() {
	return buffer.size();
    }

    public boolean isEmpty() {
	return buffer.isEmpty();
    }

    public TrafficData firstPacket() {
	return (TrafficData)buffer.lastElement();
    }

    public boolean popFirstPacket() {
	int index = buffer.size() - 1;
	buffer.removeElementAt(index);
	if (index == 0) 
	    return false;
	else 
	    return true;
    }

    public void put(TrafficData trafficData) {
	buffer.addElement(trafficData);
    }

    /*
    // merge sort start
    Enumeration packets;
    RequestedData packetsData;
    Vector newBuffer;

    void mergeStart() {
	newBuffer = new Vector();
	packets = buffer.elements();
	if (packets.hasMoreElements()) {
	    packetsData = (RequestedData)packets.nextElement();
	    //System.out.println("buffer has some packets");
	} else {
	    packetsData = null;
	    //System.out.println("buffer is empty");
	}
    }

    void mergePut(RequestedData requestedData) {
	if (packetsData == null) {
	    newBuffer.addElement(requestedData);
	} else {
	    if (packetsData.timeEventComes < requestedData.timeEventComes) {
		newBuffer.addElement(requestedData);
	    } else {
		newBuffer.addElement(packetsData);
		while (packets.hasMoreElements()) {
		    packetsData = (RequestedData)packets.nextElement();
		    if (packetsData.timeEventComes < requestedData.timeEventComes) {
			newBuffer.addElement(requestedData);
			break;
		    } else {
			newBuffer.addElement(packetsData);
		    }
		}
		if (!packets.hasMoreElements()) {
		    packetsData = null;
		    newBuffer.addElement(requestedData);
		}
	    }
	}			
    }

    void mergeEnd() {
	buffer = newBuffer;
	//printSendBuffer();
    }
    // merge sort finish
    */

    // debug
    public void printSendBuffer() {
	Enumeration e = buffer.elements();
	while (e.hasMoreElements()) {
	    TrafficData td = (TrafficData)e.nextElement();
	    //System.out.print(Format.format(td.timeEventComes, 4) + ", ");
	    System.out.print(td + ", ");
	}
	System.out.println("\n");
    }

    public String toString() {
	String string = " ";
	Enumeration e = buffer.elements();
	while (e.hasMoreElements()) {
	    TrafficData td = (TrafficData)e.nextElement();
	    //System.out.print(Format.format(td.timeEventComes, 4) + ", ");
	    string = string + td + ", ";
	}
	string = string + "\n";
	return string;
    }
}
