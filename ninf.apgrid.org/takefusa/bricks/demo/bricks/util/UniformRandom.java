package bricks.util;
import java.util.*;

/**
 * UniformRandom returns uniform random sequence.<BR>
 * Usage: UniformRandom([double min], [double max], [long seed])
 *        or UniformRandom([double min], [double max]).
 **/
public class UniformRandom extends Sequence implements SubComponent {
    protected Random random;
    protected double min;
    protected double max;

    public UniformRandom(double min, double max) {
	random = new Random();
	this.min = min;
	this.max = max;
	this.mean = (min + max) / 2.0;
    }

    public UniformRandom(double min, double max, long seed) {
	random = new Random(seed);
	this.min = min;
	this.max = max;
	this.mean = (min + max) / 2.0;
    }

    public String getName() {
	return "UniformRandom";
    }

    public double max() {
	return max;
    }

    public double nextDouble() {
	return min + (max - min) * random.nextDouble();
    }

    public int nextInt() {
	return (int)nextDouble();
    }

    public double nextDouble(double currentTime) {
	return nextDouble();
    }
}
