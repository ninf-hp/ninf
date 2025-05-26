package bricks.util;
import java.util.*;

public class InterpolationCpuCreator extends SubComponentCreator {

    public String usage() {
	return "InterpolationCpu(<Interpolation interpolationType>, " + 
	    "<int numPoints>, <String fileName>, <double performance>)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    String interpolationType = st.nextToken(" \t,()");
	    int numPoints = 
		Integer.valueOf(st.nextToken(" \t,()")).intValue();
	    String fileName = st.nextToken(" \t,()");
	    double throughput = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();

	    InterpolationCpu in = new InterpolationCpu(throughput);
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
