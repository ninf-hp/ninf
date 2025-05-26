package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NetworkMonitorCreator extends ComponentCreator {

    SchedulingUnitFactory schedulingUnitFactory;

    // for bricks.tools.ShowUsage
    public NetworkMonitorCreator(){}

    public NetworkMonitorCreator(
	SimulationSet owner, SchedulingUnitFactory schedulingUnitFactory,
	SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.schedulingUnitFactory = schedulingUnitFactory;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NetworkMonitor <String key> <NetworkMonitor networkMonitor>";
    }

    public void create(String str) throws BricksParseException {
	SimulationDebug.println("create NetworkMonitor...");
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // networkmonitor
	    String key = st.nextToken(" \t(),");
	    NetworkMonitor networkMonitor =
		(NetworkMonitor) schedulingUnitFactory.create(st);
	    networkMonitor.key = key;
	    SimulationDebug.println(
		"NetworkMonitorCreator : key = " + networkMonitor +
		" : " + networkMonitor.key
	    );
	    owner.register(key, networkMonitor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
