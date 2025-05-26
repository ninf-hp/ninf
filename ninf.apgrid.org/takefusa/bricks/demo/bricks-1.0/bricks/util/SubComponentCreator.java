package bricks.util;
import java.util.StringTokenizer;

public abstract class SubComponentCreator implements Creator {

    public abstract SubComponent create(StringTokenizer st) 
	throws BricksParseException;

    // overriden by Sequence for OthersData size of Network
    public SubComponent create(StringTokenizer st, double packetSize) 
	throws BricksParseException 
    {
	throw new BricksParseException(this + ": invalid number of argument");
    }

    // for interarrivalTimeofPackets information of LinkCreator
    public SubComponent create(double mean, StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    return create(st);
	} catch (BricksParseException e) {
	    e.printStackTrace();
	    throw new BricksParseException();
	}
    }

    protected double firstArg(String str, double size) 
	throws BricksParseException
    {
	if (str.equalsIgnoreCase("packet_size")) {
	    return size;

	} else {
	    try {
		return Double.valueOf(str).doubleValue();

	    } catch (NumberFormatException e) {
		e.printStackTrace();
		throw new BricksParseException(
		    "[" + str +"] is not number format."
		);
	    }
	}
    }
}
