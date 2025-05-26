package bricks.plugin.nws;
import bricks.scheduling.*;
import bricks.util.*;
import java.util.*;

public class NWSNetworkPredictorCreator extends PredictorCreator {

    // for bricks.tools.ShowUsage
    public NWSNetworkPredictorCreator(){}

    public NWSNetworkPredictorCreator(
	SubComponentFactory subComponentFactory
    ) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "bricks.plugin.nws.NWSNetworkPredictor(<NWSForecasterHost:Port>)";
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) throws RuntimeException {
	try {
	    String nwsForecastorName = st.nextToken(" \t,()");
	    NWSNetworkPredictor predictor = 
		new NWSNetworkPredictor(metaPredictor, nwsForecastorName);
	    return predictor;
	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new RuntimeException();
	}
    }
}

