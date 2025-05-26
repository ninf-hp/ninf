package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NormalNetworkMonitorCreator extends SchedulingUnitCreator {

    // for bricks.tools.ShowUsage
    public NormalNetworkMonitorCreator(){}

    public NormalNetworkMonitorCreator(SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NormalNetworkMonitor(<String keyOfRresourceDB>, " +
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
		new NormalNetworkMonitor(key, interprobingTime, probeDataSize);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

