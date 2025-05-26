package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NonServerPredictorCreator extends PredictorCreator {

    // for bricks.tools.ShowUsage
    public NonServerPredictorCreator(){}

    public NonServerPredictorCreator(
	SubComponentFactory subComponentFactory
    ) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NonServerPredictor()";
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) 
	throws BricksParseException 
    {
	try {
	    return new NonServerPredictor(metaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

