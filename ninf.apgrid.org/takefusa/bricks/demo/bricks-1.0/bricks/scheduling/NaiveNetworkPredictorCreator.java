package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NaiveNetworkPredictorCreator extends PredictorCreator {

    // for bricks.tools.ShowUsage
    public NaiveNetworkPredictorCreator(){}

    public NaiveNetworkPredictorCreator(
	SubComponentFactory subComponentFactory
    ) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NaiveNetworkPredictor()";
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) 
	throws BricksParseException 
    {
	try {
	    return new NaiveNetworkPredictor(metaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

