package bricks.scheduling;
import bricks.util.*;
import bricks.environment.*;
import java.util.*;
import java.io.*;

public abstract class ServerMonitor extends Obj implements SchedulingUnit {

    protected PrintWriter logWriter;

    protected Sequence interprobingTime;
    protected double trackingTime;
    protected String keyOfResourceDB;
    protected ResourceDB resourceDB;

    // nodes
    protected Vector serverList;

    // for event flag
    protected int event;
    protected static final int PROBE = 0;
    protected static final int SEND = 1;

/************************* needed method *************************/
    public String toInitString() {
	return getName() + " : " + key + "\n";
    }

    public void init(SimulationSet owner) {
	this.owner = owner;
	this.logWriter = owner.serverMonitorLogWriter;
	this.resourceDB = owner.getResourceDB(keyOfResourceDB);
	//System.out.println("serverList: " + serverList);
    }

    /**************************************************/

    public void initSession(double currentTime) {
	updateNextEvent(currentTime);
    }

    public void printLog(String log) {
	if (logWriter != null)
	    logWriter.println(log);
    }

    public void updateNextEvent(double currentTime) {
	event = PROBE;
	nextEventTime = 
	    nextEventTime + interprobingTime.nextDouble(currentTime);
    }
    
    public void processEvent(double currentTime) {
	probe(currentTime);
    }

/************************* public method *************************/
    public Enumeration servers() {
	
	return resourceDB.servers();
    }

    public int numServers() {
	serverList = resourceDB.getServerList();
	return serverList.size();
    }

    public ServerNode serverElementAt(int index) {
	serverList = resourceDB.getServerList();
	return (ServerNode)serverList.elementAt(index);
    }

    public void registerNewServer(ServerNode node) {
	serverList = resourceDB.getServerList();
	serverList.addElement(node);
    }

    public void deleteServer(ServerNode node) {
	serverList = resourceDB.getServerList();
	serverList.removeElement(node);
    }

/************************* protected method *************************/

    protected void probe(double currentTime) {
	Enumeration e = servers();
	while (e.hasMoreElements()) {
	    ServerNode serverNode = (ServerNode) e.nextElement();
	    ServerInfo serverInfo = serverNode.getServerInfo(
		currentTime, trackingTime
	    );
	    printLog(this + " " + serverInfo);
	    SimulationDebug.println("ServerMonitor: putServerInfo()");
	    resourceDB.putServerInfo(serverInfo);
	}
    }
}
