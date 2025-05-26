package bricks.util;
import java.util.*;

public class ConstantCreator extends SubComponentCreator {

    public String usage() {
	return "ConstantCreator(<double constant>)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    double mean = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    return new Constant(mean);
	    
	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
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
	    String str = st.nextToken(" \t,()");
	    if (str == null)
		throw new BricksParseException(usage());
	    double mean = firstArg(str, packetSize);
	    return new Constant(mean);

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }

    // override
    // for interarrivalTimeofPackets information of LinkCreator
    public SubComponent create(double mean, StringTokenizer st) 
	throws RuntimeException 
    {
	return new Constant(mean);
    }
}

