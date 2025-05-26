package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class EPRoundRobinSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "EPRoundRobinScheduler(" + 
	    "<String keyOfPredictor>(, <int index>))";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    if (st.hasMoreTokens()) {
		int index = Integer.valueOf(st.nextToken(" \t,()")).intValue();
		return new EPRoundRobinScheduler(keyOfMetaPredictor, index);
	    } else {
		return new EPRoundRobinScheduler(keyOfMetaPredictor);
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
