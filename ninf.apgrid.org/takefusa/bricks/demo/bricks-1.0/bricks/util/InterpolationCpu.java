package bricks.util;
import java.io.*;

/**
 * InterpolationCpu returns current processing performance assuming 
 * machine load from [file].<BR>
 * Usage: InterpolationCpu([String interpolationType], [int numPoint], [String fileName], [double throughput]).
 **/
public class InterpolationCpu extends Sequence implements SubComponent {
    private Interpolation interpolation;
    private double throughput;
    private String interpolationType;

    public InterpolationCpu(double throughput) {
	this.throughput = throughput;
    }

    public void setInterpolation(
	String interpolationType, int numPoints, String fileName
    ) throws BricksParseException {

	this.interpolationType = interpolationType;
	try {
	    this.interpolation = Interpolation.get(
	        interpolationType, numPoints, fileName
	    );
	} catch (BricksParseException e) {
	    throw e;
	}
    }

    public String getName() {
	return interpolationType + "Cpu";
    }

    public double max() {
	return throughput;
    }

    public double nextDouble(double currentTime) {
	return throughput * interpolation.getPoint(currentTime);
    }

    public double nextDouble() {
	return throughput * interpolation.getPoint();
    }

    public double getCpuUtilization(double currentTime, double trackingTime) {
	//return 1.0 - interpolation.getPoint(currentTime);
	return 1.0 - interpolation.getIntegral(currentTime, trackingTime);

	// debug
	//double integral = interpolation.getIntegral(currentTime, trackingTime);
	//System.out.print("integral at getCpuU : " + integral);
	//double tmp = 1.0 - integral;
	//System.out.println(" => " + tmp);
	//return 1.0 - integral;
    }

    public double getLoad(double currentTime, double trackingTime) {
	//return 1.0 / interpolation.getPoint(currentTime) - 1;
	return 1.0 / interpolation.getIntegral(currentTime, trackingTime) - 1.0;
	//double integral = interpolation.getIntegral(currentTime, trackingTime);
	//System.out.println("integral at getLoad : " + integral);
	//double tmp = 1.0 / integral - 1.0;
	//System.out.println(" => " + tmp);
	//return 1.0 / integral - 1.0;
    }
}
