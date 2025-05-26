package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NormalServerMonitorCreator extends SchedulingUnitCreator {

    // for bricks.tools.ShowUsage
    public NormalServerMonitorCreator(){}

    public NormalServerMonitorCreator(SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NormalServerMonitor(<String keyOfResourceDB>, " +
	    "<Sequence interprobingTime>, <double trackingTime>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String key = st.nextToken(" \t,()"); // ResourceDB

    	    Sequence interprobingTime = 
		(Sequence)subComponentFactory.create(st);
	    double trackingTime = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();

	    return 
		new NormalServerMonitor(key, interprobingTime, trackingTime);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}
