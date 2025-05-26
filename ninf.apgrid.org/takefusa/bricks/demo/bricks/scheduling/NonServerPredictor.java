package bricks.scheduling;

public class NonServerPredictor extends ServerPredictor implements Predictor {

    public NonServerPredictor(MetaPredictor owner) {
	init(owner);
    }

/************************* needed method *************************/
    public String getName() {
	return "NonServerPredictor";
    }
}

