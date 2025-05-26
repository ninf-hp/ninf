package bricks.util;
import java.util.*;

public class ExponentRandomCreator extends SubComponentCreator {

    public String usage() {
	return "ExponentRandom(<double mean>, <long seed>)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    double mean = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    return new ExponentRandom(seed, mean);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }

    // for DataSize of OthersData for NetworkNode
    public SubComponent create(StringTokenizer st, double packetSize) 
	throws BricksParseException 
    {
	try {
	    double mean = firstArg(st.nextToken(" \t,()"), packetSize);
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    return new ExponentRandom(seed, mean);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }

    // for interarrivalTimeofPackets information of LinkCreator
    public SubComponent create(double mean, StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    return new ExponentRandom(seed, mean);

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

