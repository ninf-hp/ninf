package bricks.util;
import java.util.*;

public class InterpolationThroughputCreator extends SubComponentCreator {

    public String usage() {
	return "InterpolationThroughput(<Interpolation interpolationType>, " +
	    "<int numPoints>, <String fileName>)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String interpolationType = st.nextToken(" \t,()");
	    int numPoints = 
		Integer.valueOf(st.nextToken(" \t,()")).intValue();
	    String fileName = st.nextToken(" \t,()");
	    InterpolationThroughput in = new InterpolationThroughput();
	    in.setInterpolation(interpolationType, numPoints, fileName);
	    return in;

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}

