package bricks.scheduling;

public class NonNetworkPredictor extends NetworkPredictor implements Predictor {

    public NonNetworkPredictor(MetaPredictor owner) {
	init(owner);
    }

/************************* needed method *************************/
    public String getName() {
	return "NonNetworkPredictor";
    }
}

