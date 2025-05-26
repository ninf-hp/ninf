package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class RandomSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "RandomScheduler(<String keyOfPredictor>, <long seed>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    return new RandomScheduler(keyOfMetaPredictor, seed);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

