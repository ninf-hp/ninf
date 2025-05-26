/**
 * Bricks Grid Computing Simulation on a remote host using rjava.
 **/
package bricks;
import bricks.util.*;
import java.io.*;
import java.util.*;
import silf.rjava.*;

public class RemoteSimulate extends Simulate {

    private static InputStream getRemoteFileInputStream(String fileName) {
	try {
	    InputStream is = new RemoteFileInputStream(fileName);
	    return is;
	} catch (FileNotFoundException fnfe) {
	    fnfe.printStackTrace();
	    System.err.println ("Cannot open '" + fileName + "'");
	    throw new RuntimeException();
	}
    }

    private static OutputStream getRemoteFileOutputStream(
	String fileName, int bufferSize
    ) {
	try {
	    OutputStream os = new RemoteFileOutputStream(fileName);
	    if (bufferSize < 0)
		return os;
	    else
		return new BufferedOutputStream(os, bufferSize);

	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.err.println ("Cannot open '" + fileName + "'");
	    throw new RuntimeException();
	}
    }

    public static void main(String[] argv) {

	if (argv.length == 0) {
	    System.out.println(USAGE);
	    return;
	}

	int index;
	if ((index = initialize(argv)) < 0)
	    return;

	// initialize simulation for rjava
	String fileName = argv[index];
	InputStream isOfSimulationSetFile = getRemoteFileInputStream(fileName);
	OutputStream osOfRequestedDataLog = 
	    getRemoteFileOutputStream(prefix + "_c.log", bufferSize);

	SimulationSet simulationSet = new SimulationSet(osOfRequestedDataLog);

	if (monitoringLog) {
	    simulationSet.setNetworkMonitorLog(
		getRemoteFileOutputStream(prefix + "_nm.log", bufferSize)
	    );
	    simulationSet.setServerMonitorLog(
		getRemoteFileOutputStream(prefix + "_sm.log", bufferSize)
	    );
	}

	if (networkLog)
	    simulationSet.setNetworkLog(
		getRemoteFileOutputStream(prefix + "_n.log", bufferSize)
	    );

	if (serverLog)
	    simulationSet.setServerLog(
		getRemoteFileOutputStream(prefix + "_s.log", bufferSize)
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
