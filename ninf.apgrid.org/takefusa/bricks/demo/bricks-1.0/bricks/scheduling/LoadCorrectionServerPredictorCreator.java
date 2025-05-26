package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class LoadCorrectionServerPredictorCreator extends PredictorCreator {

    // for bricks.tools.ShowUsage
    public LoadCorrectionServerPredictorCreator(){}

    public LoadCorrectionServerPredictorCreator(
	SubComponentFactory subComponentFactory
    ) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "LoadCorrectionServerPredictor(<double loadUnit>)";
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) 
	throws BricksParseException 
    {
	try {
	    double loadUnit = 
		Double.valueOf(st.nextToken(" \t,()")).doubleValue();
	    return new LoadCorrectionServerPredictor(metaPredictor, loadUnit);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}

