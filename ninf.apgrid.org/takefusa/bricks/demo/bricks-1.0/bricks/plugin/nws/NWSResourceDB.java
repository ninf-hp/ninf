package bricks.plugin.nws;
import bricks.environment.*;
import bricks.scheduling.*;
import bricks.util.*;
import nws.api.*;
import java.util.*;

public class NWSResourceDB extends ResourceDB implements SchedulingUnit {

    public boolean forecastLog = false;
    protected NWSAdapter adapter = new NWSAdapter();
    private int displaySize = 100;
    private Hashtable networkForecastCounter;
    private Hashtable serverForecastCounter;

    public NWSResourceDB(
	String nwsMemoryServer, String nwsForecaster, boolean forecastLog
    ) {
	adapter.setNwsMemoryServer(nwsMemoryServer);
	adapter.setNwsForecaster(nwsForecaster);
	this.forecastLog = forecastLog;
    }

    public NWSResourceDB(
	String nwsMemoryServer, String nwsForecaster, boolean forecastLog,
	int numNetworkHistory, int numServerHistory
    ) {
	adapter.setNwsMemoryServer(nwsMemoryServer);
	adapter.setNwsForecaster(nwsForecaster);
	this.forecastLog = forecastLog;
	this.numNetworkHistory = numNetworkHistory;
	this.numServerHistory = numServerHistory;
    }

    /************************* needed method *************************/
    public String getName() {
	return "NWSResourceDB";
    }

    /************************* override *************************/
    public void init(SimulationSet owner) {
	super.init(owner);

	if (forecastLog) {
	    // init network
	    networkForecastCounter = new Hashtable(networkState.size());
	    initCounter(networkForecastCounter, networkState);
	    
	    // init server
	    serverForecastCounter = new Hashtable(serverState.size());
	    initCounter(serverForecastCounter, serverState);
	}
    }

    public void finish() {
	if (forecastLog) {
	    printForecastLog(networkForecastCounter);
	    printForecastLog(serverForecastCounter);
	}
    }

    public void putServerInfo(ServerInfo serverInfo) {

	SimulationDebug.println("NWSResourceDB: insert()");
	super.putServerInfo(serverInfo);

	// insert current_cpu
	double currentCpu = 1.0 / serverInfo.loadAverage;
	if (currentCpu > 1.0) {
	    currentCpu = 1.0;
	}
	adapter.insert(
	    adapter.currentCpuExperiment, (serverInfo.owner).toString(),
	    serverInfo.probeTime, currentCpu
	);
	// insert available_cpu
	double availableCpu = 1.0 / (serverInfo.loadAverage + 1.0);
	serverInfo.availableCpu = availableCpu;
	adapter.insert(
	    adapter.availableCpuExperiment, (serverInfo.owner).toString(),
	    serverInfo.probeTime, availableCpu
	);

	SimulationDebug.println(
	    "NWSResourceDB: " + adapter.currentCpuExperiment + ": " + 
	    serverInfo.probeTime + "  " + currentCpu
	);
	SimulationDebug.println(
	    "NWSResourcDB: " + adapter.availableCpuExperiment + ": " + 
	    serverInfo.probeTime + "  " + availableCpu
	);

	if (forecastLog) {
	    int count = ((Integer)serverForecastCounter.get(serverInfo.owner)).intValue();
	    count++;
	    if (count == displaySize) {
		count = 0;
		printForecastLog(
		    adapter.currentCpuExperiment, serverInfo.owner
		);
		printForecastLog(
		    adapter.availableCpuExperiment, serverInfo.owner
		);
	    }
	    serverForecastCounter.put(serverInfo.owner, new Integer(count));
	}
    }

    public void putNetworkInfo(NetworkInfo networkInfo) {
	super.putNetworkInfo(networkInfo);

	// put bandwidth_tcp
	adapter.insert(
	    adapter.bandwidthTcpExperiment, 
	    ((networkInfo.owner).sourceNode).toString(),
	    ((networkInfo.owner).destinationNode).toString(),
	    networkInfo.probeTime, networkInfo.throughput
	);
	// put latency_tcp
	adapter.insert(
	    adapter.latencyTcpExperiment, 
	    ((networkInfo.owner).sourceNode).toString(),
	    ((networkInfo.owner).destinationNode).toString(),
	    networkInfo.probeTime, networkInfo.latency
	);

	SimulationDebug.println(
	    "NWSResourcDB: " + adapter.bandwidthTcpExperiment + ": " + 
	    networkInfo.probeTime + " " + networkInfo.throughput
	);
	SimulationDebug.println(
	    "NWSResourcDB: " + adapter.latencyTcpExperiment + ": " + 
	    networkInfo.probeTime + " " + networkInfo.latency
	);

	if (forecastLog) {
	    int count = ((Integer)networkForecastCounter.get(networkInfo.owner)).intValue();
	    count++;
	    if (count == displaySize) {
		count = 0;
		printForecastLog(
		    adapter.bandwidthTcpExperiment, networkInfo.owner
		);
		printForecastLog(
		    adapter.latencyTcpExperiment, networkInfo.owner
		);
	    }
	    networkForecastCounter.put(networkInfo.owner, new Integer(count));
	}
    }

    /*************** private / protected method ***************/
    private void initCounter(Hashtable table, Hashtable state) {
	Enumeration e = state.keys();
	while (e.hasMoreElements()) {
	    table.put(e.nextElement(), new Integer(0));
	}
    }

    private void printForecastLog(Hashtable counter) {
	Enumeration e = counter.keys();
	while (e.hasMoreElements()) {
	    Object o = e.nextElement();
	    if (o instanceof NodePair) { // network
		printForecastLog(
		    adapter.bandwidthTcpExperiment, (NodePair)o
		);
		printForecastLog(
		    adapter.latencyTcpExperiment, (NodePair)o
		);
	    } else { // server
		printForecastLog(
		    adapter.availableCpuExperiment, (ServerNode)o
		);
		printForecastLog(
		    adapter.currentCpuExperiment, (ServerNode)o
		);
	    }
	}
    }

    private void printForecastLog(
	String experiment, NodePair nodePair
    ) {
	ForecastCollection[] forecast = adapter.extract(
	    experiment,
	    (nodePair.sourceNode).toString(),
	    (nodePair.destinationNode).toString(),
	    displaySize
	);
	    
	for (int i = 0 ; i < forecast.length ; i++) {
	    System.out.println(
		experiment + " " + nodePair + " (" + i + ") " +
		adapter.toString(forecast[i])
	    );
	}
	System.out.println("");
    }

    private void printForecastLog(
	String experiment, ServerNode serverNode
    ) {
	// debug
	//adapter.printNwsServers();

	ForecastCollection[] forecast = adapter.extract(
	    experiment, serverNode.toString(), displaySize
	);

	for (int i = 0 ; i < forecast.length ; i++) {
	    System.out.println(
		experiment + " " + serverNode + " (" + i + ") " +
		adapter.toString(forecast[i])
	    );
	}
	System.out.println("");
    }
}

