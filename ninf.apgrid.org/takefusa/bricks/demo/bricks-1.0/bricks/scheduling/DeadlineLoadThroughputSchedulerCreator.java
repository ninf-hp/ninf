package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class DeadlineLoadThroughputSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "DeadlineLoadThroughputScheduler(" + 
	    "<String keyOfPredictor>, <double optimisticFactor>, " +
	    "<double interval>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    double optimisticFactor = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    double interval = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    return new DeadlineLoadThroughputScheduler(
		    keyOfMetaPredictor, optimisticFactor, interval
		    );

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

