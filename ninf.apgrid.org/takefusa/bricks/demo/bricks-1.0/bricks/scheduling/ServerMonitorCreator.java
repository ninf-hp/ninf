package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class ServerMonitorCreator extends ComponentCreator {

    SchedulingUnitFactory schedulingUnitFactory;

    // for bricks.tools.ShowUsage
    public ServerMonitorCreator(){}

    public ServerMonitorCreator(
	SimulationSet owner, SchedulingUnitFactory schedulingUnitFactory,
	SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.schedulingUnitFactory = schedulingUnitFactory;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "ServerMonitor <String key> <ServerMonitor serverMonitor>";
    }

    public void create(String str) throws BricksParseException {
	SimulationDebug.println("create ServerMonitor...");
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // servermonitor

	    String key = st.nextToken(" \t(),");
	    ServerMonitor serverMonitor =
		(ServerMonitor) schedulingUnitFactory.create(st);
	    serverMonitor.key = key;
	    SimulationDebug.println(
		"ServerMonitorCreator : key = " + serverMonitor + 
		" : " + serverMonitor.key
	    );
	    owner.register(key, serverMonitor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
