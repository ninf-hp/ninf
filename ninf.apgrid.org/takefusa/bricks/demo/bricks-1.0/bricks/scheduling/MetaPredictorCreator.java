package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class MetaPredictorCreator extends ComponentCreator {

    //SchedulingUnitFactory schedulingUnitFactory;
    PredictorFactory predictorFactory;

    // for bricks.tools.ShowUsage
    public MetaPredictorCreator(){}

    public String usage() {
	return "Predictor <String key> (<String keyOfResourceDBName>, " +
	    "<Predictor predictor1>, <Predictor predictor2>, ...)";
    }

    public MetaPredictorCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	//this.schedulingUnitFactory = schedulingUnitFactory;
	this.subComponentFactory = subComponentFactory;
	this.predictorFactory = new PredictorFactory(subComponentFactory);
    }

    public void create(String str) throws BricksParseException {
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // predictor
	    String key = null;
	    String keyOfResourceDB = null;
	    
	    if (st.hasMoreTokens())
		key = st.nextToken(" \t(),");
	    else
		throw new BricksParseException(usage());

	    if (st.hasMoreTokens())
		keyOfResourceDB = st.nextToken(" \t(),");
	    else
		throw new BricksParseException(usage());

	    MetaPredictor metaPredictor = 
		new MetaPredictor(key, keyOfResourceDB, owner);

	    while (st.hasMoreElements()) {
		Predictor predictor = predictorFactory.create(
		    st, metaPredictor
		);
	    }
	    metaPredictor.init();
	    //SimulationDebug.println(
	    //"PredictorCreator : key = " + metaPredictor
	    //+ " : " + predictor.key
	    //);
	    owner.register(key, metaPredictor);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
