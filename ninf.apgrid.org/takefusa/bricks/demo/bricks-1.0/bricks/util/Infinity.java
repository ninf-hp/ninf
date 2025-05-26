package bricks.util;

public class Infinity extends Sequence implements SubComponent {

    public Infinity() {
	mean = Double.POSITIVE_INFINITY;
    }

    public String getName() {
	return "Infinity";
    }

    public double nextDouble(double currentTime) {
	return mean;
    }

    public double nextDouble() {
	return mean;
    }
}
