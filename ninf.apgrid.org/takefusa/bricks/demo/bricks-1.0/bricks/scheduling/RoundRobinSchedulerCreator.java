package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class RoundRobinSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "RoundRobinScheduler(<String keyOfPredictor>(, <int index>))";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    if (st.hasMoreTokens()) {
		int index = Integer.valueOf(st.nextToken(" \t,()")).intValue();
		return new RoundRobinScheduler(keyOfMetaPredictor, index);
	    } else {
		return new RoundRobinScheduler(keyOfMetaPredictor);
	    }

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

