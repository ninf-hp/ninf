package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NormalResourceDBCreator extends SchedulingUnitCreator {

    // for bricks.tools.ShowUsage
    public NormalResourceDBCreator(){}

    public NormalResourceDBCreator (SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NormalResourceDB((<int numNetworkHistory>, <int numServerHistory>))";
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    if (st.hasMoreTokens()) {
		int numNetworkHistory = 
		    Integer.valueOf(st.nextToken(" \t,()")).intValue();
		int numServerHistory = 
		    Integer.valueOf(st.nextToken(" \t,()")).intValue();
		return new NormalResourceDB(numNetworkHistory, numServerHistory);
	    } else {
		return new NormalResourceDB();
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

