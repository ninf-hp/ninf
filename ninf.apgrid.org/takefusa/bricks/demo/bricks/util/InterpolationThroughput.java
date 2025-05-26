package bricks.util;
import java.io.*;

public class InterpolationThroughput extends Sequence implements SubComponent {

    private Interpolation interpolation;
    private String interpolationType;

    public void setInterpolation(
	String interpolationType, int numPoints, String fileName
    ) throws BricksParseException {

	try {
	    this.interpolationType = interpolationType;
	    interpolation = Interpolation.get(
	        interpolationType, numPoints, fileName
	    );
	} catch (BricksParseException e) {
	    throw e;
	}
    }

    public String getName() {
	return interpolationType + "Throughput";
    }

    public double max() {
	return interpolation.yMax();
    }

    public double nextDouble(double currentTime) {
	return interpolation.getPoint(currentTime);
    }

    public double nextDouble() {
	return interpolation.getPoint();
    }
}
