package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class LoadThroughputSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "LoadThroughputScheduler(<String keyOfPredictor>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    return new LoadThroughputScheduler(keyOfMetaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

