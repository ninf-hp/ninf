package bricks.util;
import java.util.Random;

/** 
 * ExponentRandom(double mean, long seed)
 **/
public class ExponentRandom extends Sequence implements SubComponent {
    protected Random random;

    public ExponentRandom(long seed, double mean) {
	random = new Random(seed);
	this.mean = mean;
    }

    public String getName() {
	return "ExponentRandom";
    }

    public double nextDouble(double currentTime) {
	return nextDouble();
    }

    public double nextDouble() {
	return - mean * Math.log(1 - random.nextDouble());
	//return - mean * Math.log(random.nextDouble());
    }

    public int nextInt() {
	return (int)nextDouble();
    }

    // for debug
    public static void main(String[] argv) {
	if (argv.length < 1) {
	    System.out.println(
		"Usage: java ExponetRandom [mean]"
	    );
	    return;
	}
	double mean = Double.valueOf(argv[0]).doubleValue();
	ExponentRandom e = new ExponentRandom(123, mean);
	for (int i = 0 ; i < 10 ; i++) {
	    System.out.println(e.nextDouble());
	}
    }
}
