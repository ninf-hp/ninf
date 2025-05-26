/**
 * Bricks Grid Computing Simulation
 **/

package bricks;
import bricks.util.*;
import java.io.*;
import java.util.*;

public class Simulate {

    protected static final String USAGE = 
	"java bricks.Simulate [-times <times> | -fin <the time finished simulation>] [-prefix <prefix of log files>] [-network_log] [-server_log] [-no_monitoring_log] [-info] <configuration file>\n";
//	"java bricks.Simulate [-times <times> | -fin <the time finished simulation>] [-prefix <prefix of log files>] [-network_log] [-server_log] [-no_monitoring_log] [-buffer <buffer size>] [-info] <configuration file>\n";

    protected static int numRequestedData = 1;
    protected static double timeFinishedSimulation = -1;

    protected static int bufferSize = -1;
    protected static long start = System.currentTimeMillis();

    protected static boolean networkLog = false;
    protected static boolean serverLog = false;
    protected static boolean monitoringLog = true;
    protected static boolean info = false;
    protected static String prefix = "tmp";

    protected static int initialize(String[] argv) {
	int index = 0;
	while (index < argv.length-1) {
	    if (argv[index].equalsIgnoreCase("-times")) {
		numRequestedData = Integer.valueOf(argv[++index]).intValue();

	    } else if (argv[index].equalsIgnoreCase("-fin")) {
		timeFinishedSimulation = 
		    Double.valueOf(argv[++index]).doubleValue();

	    } else if (argv[index].equalsIgnoreCase("-prefix")) {
		prefix = argv[++index];

	    } else if (argv[index].equalsIgnoreCase("-network_log")) {
		networkLog = true;

	    } else if (argv[index].equalsIgnoreCase("-server_log")) {
		serverLog = true;

	    } else if (argv[index].equalsIgnoreCase("-no_monitoring_log")) {
		monitoringLog = false;

	    } else if (argv[index].equalsIgnoreCase("-info")) {
		info = true;

	    } else if (argv[index].equalsIgnoreCase("-buffer")) {
		bufferSize = Integer.valueOf(argv[++index]).intValue();

	    } else {
		System.out.println("The [ " + argv[index] + " ] option is wrong.");
		System.out.println(USAGE);
		return -1;
	    }
	    index++;
	}
	return index;
    }

    protected static void showInfo(String fileName, long init) {
	long end = System.currentTimeMillis();
	double total = (end - start) * 0.001;
	double ini = (init - start) * 0.001;
	System.out.println(fileName + " : Total " + total +
			   "(" + Format.format(ini, 3) + ")[sec], " + 
			   Data.serialNumber + " objects were generated.");
	Runtime r = Runtime.getRuntime();
	System.out.println("Free Memory = " + 
			   r.freeMemory() + " / " + r.totalMemory());
    }

    public static void main(String[] argv) {

	if (argv.length == 0) {
	    System.out.println(USAGE);
	    return;
	}

	int index;
	if ((index = initialize(argv)) < 0)
	    return;

	// initialize simulation
	String fileName = argv[index];
	InputStream isOfSimulationSetFile = 
	    BricksUtil.getInputStream(fileName);
	OutputStream osOfRequestedDataLog = 
	    BricksUtil.getOutputStream(prefix + "_c.log", bufferSize);

	SimulationSet simulationSet = new SimulationSet(osOfRequestedDataLog);

	if (monitoringLog) {
	    simulationSet.setNetworkMonitorLog(
		BricksUtil.getOutputStream(prefix + "_nm.log", bufferSize)
	    );
	    simulationSet.setServerMonitorLog(
		BricksUtil.getOutputStream(prefix + "_sm.log", bufferSize)
	    );
	}

	if (networkLog)
	    simulationSet.setNetworkLog(
		BricksUtil.getOutputStream(prefix + "_n.log", bufferSize)
	    );

	if (serverLog)
	    simulationSet.setServerLog(
		BricksUtil.getOutputStream(prefix + "_s.log", bufferSize)
	    );

	try {
	    simulationSet.init(isOfSimulationSetFile);
	} catch (BricksParseException e) {
	    BricksUtil.abort(e.toString());
	}

	long init = System.currentTimeMillis();

	Session session = new Session(simulationSet);
	session.nextTimeStep();

	while (timeFinishedSimulation < 0.0 ?
	       !session.finish(numRequestedData) :
	       !session.finish(timeFinishedSimulation)) {
	    session.step();
	    session.nextTimeStep();
	}

	if (info)
	    showInfo(fileName, init);
    }
}
