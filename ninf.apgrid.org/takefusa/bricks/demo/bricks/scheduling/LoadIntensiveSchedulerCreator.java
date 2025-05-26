package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class LoadIntensiveSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "LoadIntensiveScheduler(<String keyOfPredictor>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    return new LoadIntensiveScheduler(keyOfMetaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

