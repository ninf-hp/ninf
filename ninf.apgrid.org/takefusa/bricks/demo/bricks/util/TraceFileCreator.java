package bricks.util;
import java.util.*;

public class TraceFileCreator extends SubComponentCreator {

    public String usage() {
	return "TraceFile(<String fileName>, <int numPoints>)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String fileName = st.nextToken(" \t,()");
	    int numPoint = Integer.valueOf(st.nextToken(" \t,()")).intValue();
	    return new TraceFile(fileName, numPoint);
	    
	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }

    // override
    // for DataSize of OthersData for NetworkNode
    public SubComponent create(StringTokenizer st, double packetSize) 
	throws BricksParseException 
    {
	try {
	    String fileName = st.nextToken(" \t,()");
	    int numPoint = Integer.valueOf(st.nextToken(" \t,()")).intValue();
	    return new TraceFile(fileName, numPoint);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }

    // override
    // for interarrivalTimeofPackets information of LinkCreator
    public SubComponent create(double mean, StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String fileName = st.nextToken(" \t,()");
	    int numPoint = Integer.valueOf(st.nextToken(" \t,()")).intValue();
	    return new TraceFile(fileName, numPoint);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

