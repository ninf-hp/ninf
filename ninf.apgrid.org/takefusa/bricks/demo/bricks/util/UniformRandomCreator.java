package bricks.util;
import java.util.*;

public class UniformRandomCreator extends SubComponentCreator {

    public String usage() {
	return "UniformRandom(<double min>, <double max>(, <long seed>))";
    }

    public SubComponent create(StringTokenizer st) throws BricksParseException {
	try {
	    double min = Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    double max = Double.valueOf(st.nextToken(" \t,()")).doubleValue();

	    if (st.hasMoreTokens()) {
		long seed = Long.valueOf(st.nextToken(" \t,()")).longValue();
		return new UniformRandom(min, max, seed);

	    } else {
		return new UniformRandom(min, max);
	    }

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

