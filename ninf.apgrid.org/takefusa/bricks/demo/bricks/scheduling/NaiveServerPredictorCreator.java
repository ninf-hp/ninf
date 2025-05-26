package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class NaiveServerPredictorCreator extends PredictorCreator {

    // for bricks.tools.ShowUsage
    public NaiveServerPredictorCreator(){}

    public NaiveServerPredictorCreator(
	SubComponentFactory subComponentFactory
    ) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "NaiveServerPredictor()";
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) 
	throws BricksParseException 
    {
	try {
	    return new NaiveServerPredictor(metaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

