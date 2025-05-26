package bricks.util;
import java.io.*;

public class InterpolationInterarrivalTime extends Sequence implements SubComponent {

    private Interpolation interpolation;
    private double interarrivalTimeOfPackets;
    private double throughput;
    private double packetSize;
    private String interpolationType;

    public void setInterpolation(
	String interpolationType, int numPoints, String fileName
    ) throws BricksParseException {
	try {
	    this.interpolationType = interpolationType;
	    this.interpolation = Interpolation.get(
	        interpolationType, numPoints, fileName
	    );
	} catch (BricksParseException e) {
	    throw e;
	}
    }

    public String getName() {
	return interpolationType + "InterarrivalTime";
    }

    public void init(double throughput, double packetSize) {
	this.throughput = throughput;
	this.packetSize = packetSize;
	this.interarrivalTimeOfPackets = throughput / packetSize;
    }

    public double max() {
	return interpolation.yMax();
    }

    public double nextDouble(double currentTime) {
	return 
	    (throughput / interpolation.getPoint(currentTime) - 1) *
	    interarrivalTimeOfPackets;
    }

    public double nextDouble() {
	return 
	    (throughput / interpolation.getPoint() - 1) *
	    interarrivalTimeOfPackets;
    }
}
