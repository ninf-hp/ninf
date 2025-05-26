package bricks.util;

public abstract class Sequence {
    public double mean;

    public abstract double nextDouble(double currentTime);
    public abstract double nextDouble();

    public int nextInt() {
	System.err.println(this + ": nextInt() should not been called.");
	System.exit(3);
	return 0;
    };

    public double max() {
	return -1;
    }
}
