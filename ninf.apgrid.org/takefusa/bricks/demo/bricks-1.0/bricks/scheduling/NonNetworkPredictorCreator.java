package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NonNetworkPredictorCreator extends PredictorCreator {

    // for bricks.tools.ShowUsage
    public NonNetworkPredictorCreator(){}

    public NonNetworkPredictorCreator(
	SubComponentFactory subComponentFactory
    ) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NonNetworkPredictor()";
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) 
	throws BricksParseException 
    {
	try {
	    return new NonNetworkPredictor(metaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

