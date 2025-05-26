package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public abstract class PredictorCreator implements Creator {

    public SubComponentFactory subComponentFactory;

    public abstract Predictor create(
        StringTokenizer st, MetaPredictor metaPredictor
    ) throws BricksParseException;

    public void set(SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }
}
