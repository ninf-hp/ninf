package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class EPRandomSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "EPRandomScheduler(" + 
	    "<String keyOfPredictor>, <long seed>, <double interval>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    double interval = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();

	    return new EPRandomScheduler(keyOfMetaPredictor, seed, interval);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

