package bricks.util;
import java.util.*;

public class HyperexponentRandomCreator extends SubComponentCreator {

    public String usage() {
	return "HyperexponentRandom(" +
	    "<double mean>, <double standard>, <long seed>)" +
	    " (standard <= mean)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    double mean = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    double standard =
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    if (standard <= mean)
		throw new BricksParseException(usage());
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    return new HyperexponentRandom(mean, standard, seed);

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
	    double standard =
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    if (standard <= mean)
		throw new BricksParseException(usage());
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    return new HyperexponentRandom(mean, standard, seed);

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
	    double standard =
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    if (standard <= mean)
		throw new BricksParseException(usage());
	    long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
	    return new HyperexponentRandom(mean, standard, seed);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

