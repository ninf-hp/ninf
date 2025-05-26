package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class EPSelfSchedulerCreator extends SchedulingUnitCreator {

    public String usage() {
	return "EPSelfScheduler(<String keyOfPredictor>)";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String keyOfMetaPredictor = st.nextToken(" \t,()");
	    return new EPSelfScheduler(keyOfMetaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}
