package bricks.plugin.nws;
import bricks.scheduling.*;
import bricks.util.*;
import java.util.*;

public class NWSServerPredictorCreator extends PredictorCreator {

    // for bricks.tools.ShowUsage
    public NWSServerPredictorCreator(){}

    public NWSServerPredictorCreator(
	SubComponentFactory subComponentFactory
    ) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "bricks.plugin.nws.NWSServerPredictor(<NWSForecasterHost:Port>)";
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) throws RuntimeException {
	try {
	    String nwsForecastorName = st.nextToken(" \t,()");
	    NWSServerPredictor predictor =
		new NWSServerPredictor(metaPredictor, nwsForecastorName);
	    return predictor;
	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new RuntimeException();
	}
    }
}

