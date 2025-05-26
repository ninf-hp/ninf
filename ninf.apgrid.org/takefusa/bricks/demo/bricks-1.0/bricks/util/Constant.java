package bricks.util;

public class Constant extends Sequence implements SubComponent {
    public Constant(double mean) {
	this.mean = mean;
    }

    public String getName() {
	return "Constant";
    }

    public double max() {
	return mean;
    }

    public double nextDouble(double currentTime) {
	return mean;
    }

    public int nextInt() {
	return (int)mean;
    }

    public double nextDouble() {
	return mean;
    }
}
