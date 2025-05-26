package bricks.plugin.nws;
import bricks.scheduling.*;
import bricks.util.*;
import java.util.*;

public class NWSResourceDBCreator extends SchedulingUnitCreator {

    // for bricks.tools.ShowUsage
    public NWSResourceDBCreator(){}

    public NWSResourceDBCreator (SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "bricks.plugin.nws.NWSResourceDB(<string NWSMemoryHost:Port>, <string NWSForecasterHost:Port>, <boolean forecastLog>(, <int numNetworkHistory>, <int numServerHistory>))";
    }

    public SchedulingUnit create(StringTokenizer st) throws RuntimeException {
	try {
	    NWSResourceDB db = null;
	    String nwsMemoryServerName = st.nextToken(" \t,()");
	    String nwsForecasterName = st.nextToken(" \t,()");
	    boolean forecastLog = 
		Boolean.valueOf((st.nextToken(" \t,()").toLowerCase())).booleanValue();
	    if (st.hasMoreTokens()) {
		int numNetworkHistory = 
		    Integer.valueOf(st.nextToken(" \t,()")).intValue();
		int numServerHistory = 
		    Integer.valueOf(st.nextToken(" \t,()")).intValue();
		db = new NWSResourceDB(
		    nwsMemoryServerName, nwsForecasterName, forecastLog, 
		    numNetworkHistory, numServerHistory
		);
	    } else {
		db = new NWSResourceDB(
		    nwsMemoryServerName, nwsForecasterName, forecastLog
		);
	    }
	    return db;
	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new RuntimeException();
	}
    }
}

