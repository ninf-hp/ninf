package bricks.environment;
import bricks.util.*;
import java.util.*;

public class Packets {
    protected Node owner;
    protected Vector sendBufferList = new Vector(100);
    protected double packetSize;
    protected int currentIndex = 0;
    protected Hashtable interarrivalTimeList = new Hashtable();
    //protected Sequence interarrivalTime;

    // need to modify!
    protected static double EPS = 0.000001;

    public Packets(Node owner, double packetSize) {
	this.owner = owner;
	this.packetSize = packetSize;
    }

    public String toString() {
	return sendBufferList.toString();
    }

    public void putInterarrivalTimeOfPackets(Sequence ng, Node toNode) {
	interarrivalTimeList.put(toNode, ng);
    }

    public boolean isEmpty() {
    //public boolean isEmpty() {
	return sendBufferList.isEmpty();
    }

    public void sendFirstPacket(double currentTime) {
	TrafficData td = firstPacket();
	SimulationDebug.println(
	    "Packets(" + td + "): " + Format.format(currentTime, 3) +
	    " : " + td + ", nextNode = " + td.nextNode()
	);
	if (!td.nextNode().isFull()) {
	    popFirstPacket(currentTime);
	    td.gotoNextNode(currentTime);
	    SimulationDebug.println(
		"Packets.sendFirstPacket: " + td + " : " + 
		Format.format(currentTime, 3) + " next Node[" + 
		td.getIndexOfCurrentNode() + "] = " + td.nextNode() +
		"(" + td.getListOfRoute() + ")"
	    );
	} else {
	    incrementCurrentIndex();
	    TrafficData nextPacket = firstPacket();
	    Sequence interarrivalTime = (Sequence)interarrivalTimeList.get(
	        nextPacket.nextNode()
	    );
	    nextPacket.updateTimeEventComes(
		currentTime, interarrivalTime.nextDouble(currentTime)
	    );
	    //interarrivalTime = (Sequence)interarrivalTimeList.get(
	    //data.nextNode()
	    //);
	    //td.updateTimeEventComes(
	    //currentTime, interarrivalTime.nextDouble(currentTime)
	    //);
	    //SimulationDebug.println(
	    //"Packets(" + td + "): " + Format.format(currentTime, 3) +
	    //" : " + td + ".timeEventComes = " + td.timeEventComes
	    //);
	}
    }

    public TrafficData firstPacket() {
	//System.out.println("sendBufferList = " + sendBufferList.size() + 
	//		   ", currentIndex = " + currentIndex);
	SendBuffer buffer = (SendBuffer)sendBufferList.elementAt(currentIndex);
	return buffer.firstPacket();
    }

    public void divideTrafficData(
	double currentTime, TrafficData data
    ) {
	double db = data.dataSize / packetSize;
	int numPackets = (int)db;
	//System.out.println(
	SimulationDebug.println(
	    "dataSize(" + data.dataSize + ") / packetSize(" + packetSize +
	    ") = numPackets(" + numPackets + ")");
	data.dataSize = data.dataSize - packetSize * numPackets;
	if ((numPackets > 1) && (data.dataSize < EPS)) {
	    data.dataSize = data.dataSize + packetSize;
	    numPackets--;
	}
	Vector list = data.getSubList(currentTime);
	//System.out.println(
	SimulationDebug.println(
	    "Packet(" + owner + "): " + currentTime + ", " + data + 
	    ".numPackets = " + numPackets + ", " + list
	);

	SendBuffer sendBuffer = new SendBuffer(numPackets);
	sendBuffer.put(data);
	for (int i = 1 ; i <= numPackets ; i++ ) {
	    PacketData pd = new PacketData(packetSize, list);
	    sendBuffer.put((TrafficData)pd);
	    //SimulationDebug.println(pd + " timeEventComes = " + 
	    //timeEventComes);
	}
	sendBufferList.addElement(sendBuffer);
	SimulationDebug.println(sendBuffer.toString());

	//System.out.println(
	SimulationDebug.println(
	    "Packets: " + data + ".nextNode = " + data.nextNode() + 
	    ", interarrivalTime := " + 
	    (Sequence)interarrivalTimeList.get(data.nextNode())
	);
    }

    protected void popFirstPacket(double currentTime) {
	SendBuffer buffer = (SendBuffer)sendBufferList.elementAt(currentIndex);
	if(buffer.popFirstPacket()) {
	    incrementCurrentIndex();
	} else {
	    sendBufferList.removeElementAt(currentIndex);
	    /* if last element was removed */
	    if (sendBufferList.size() == currentIndex) {
		currentIndex = 0;
	    }
	}
	//System.out.println(
	SimulationDebug.println(
	    currentTime + " : sendBufferListSize = " + sendBufferList.size() +
	    " : buffer size = " + buffer.size()
	);
	if (!sendBufferList.isEmpty()) {/***********/
	    TrafficData nextPacket = firstPacket();
	    Sequence interarrivalTime = (Sequence)interarrivalTimeList.get(
	        nextPacket.nextNode()
	    );
	    SimulationDebug.println("********** interarrivalTimeList: **********");
	    SimulationDebug.println(owner + " : " + interarrivalTimeList);
	    SimulationDebug.println("****************************************");
	    SimulationDebug.println(
		nextPacket + " 's nextNode() " + nextPacket.nextNode() + 
		" 's interarrivalTime is " + interarrivalTime
		);
	    SimulationDebug.println("owner = " + owner + 
			       ", route: " + nextPacket.showListOfRoute() +
			       ", currentIndex of route = " +
			       nextPacket.getIndexOfCurrentNode());
	    /*
	    if (nextPacket instanceof RequestedData) {
		RequestedData rd = (RequestedData)nextPacket;
		System.out.println(rd.showLog());
	    }
	    */
	    nextPacket.updateTimeEventComes(
		currentTime, interarrivalTime.nextDouble(currentTime)
	    );
	}
    }

    protected void incrementCurrentIndex() {
	currentIndex++;
	if (currentIndex == sendBufferList.size()) {
	    currentIndex = 0;
	}
    }
}
