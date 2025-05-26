package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class PredictorFactory {
    protected SubComponentFactory subComponentFactory;
    protected Hashtable hashtableOfCreator = new Hashtable();

    public PredictorFactory(SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
	init();
    }

    public Predictor create(StringTokenizer st, MetaPredictor metaPredictor) 
	throws BricksParseException 
    {
	String key = null;
	try {
	    //key = (st.nextToken(" \t(),")).toLowerCase();
	    key = st.nextToken(" \t(),");
	    PredictorCreator creator =
		(PredictorCreator)hashtableOfCreator.get(key.toLowerCase());
	    
	    SimulationDebug.println("PredictorFactory: key = " + key);
	    SimulationDebug.println("PredictorFactory: Creator = " + creator);

	    Predictor predictor = null;
	    if (creator == null) {
		creator = (PredictorCreator)Class.forName(key + "Creator").newInstance();
		creator.set(subComponentFactory);
		hashtableOfCreator.put(key.toLowerCase(), creator);
	    }
	    predictor = creator.create(st, metaPredictor);
	    return predictor;

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException();

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (InstantiationException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (BricksParseException e) {
	    throw e;
	}
    }

/************************* protected method *************************/
    protected String noEntryError(String key) {
	return this + ": no creator entry for key [" + key + "]";
    }

    // init Hashtable
    protected void init() {
	// ServerPredictor
	PredictorCreator creator = 
	    new NonServerPredictorCreator(subComponentFactory);
	hashtableOfCreator.put("nonserverpredictor", creator);

	creator = new NaiveServerPredictorCreator(subComponentFactory);
	hashtableOfCreator.put("naiveserverpredictor", creator);

	creator = new LoadCorrectionServerPredictorCreator(subComponentFactory);
	hashtableOfCreator.put("loadcorrectionserverpredictor", creator);

	//creator = new NWSServerPredictorCreator(subComponentFactory);
	//hashtableOfCreator.put("nwsserverpredictor", creator);
 
	// NetworkPredictor
	creator = new NonNetworkPredictorCreator(subComponentFactory);
	hashtableOfCreator.put("nonnetworkpredictor", creator);

	creator = new NaiveNetworkPredictorCreator(subComponentFactory);
	hashtableOfCreator.put("naivenetworkpredictor", creator);

	//creator = new NWSNetworkPredictorCreator(subComponentFactory);
	//hashtableOfCreator.put("nwsnetworkpredictor", creator);
   }
}
