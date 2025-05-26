package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class RoundNetworkMonitorCreator extends SchedulingUnitCreator {

    // for bricks.tools.ShowUsage
    public RoundNetworkMonitorCreator(){}

    public RoundNetworkMonitorCreator (SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "RoundNetworkMonitor(<String keyOfResourceDB>, " +
	    "<Sequence interprobingTime>, <Sequence probeDataSize>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String key = st.nextToken(" \t,()"); // ResourceDB

    	    Sequence interprobingTime = 
		(Sequence)subComponentFactory.create(st);
	    Sequence probeDataSize = 
		(Sequence)subComponentFactory.create(st);

	    return 
		new RoundNetworkMonitor(key, interprobingTime, probeDataSize);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

