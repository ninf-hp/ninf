package bricks.util;
import java.util.Random;

public class HyperexponentRandom extends Sequence implements SubComponent {
    protected Random random;
    protected double standard;

    public HyperexponentRandom(
	double mean, double standard, long seed
    ) {
	random = new Random(seed);
	this.mean = mean;
	this.standard = standard;
    }

    public String getName() {
	return "HyperexponentRandom";
    }

    public double nextDouble(double currentTime) {
	return nextDouble();
    }

    public double nextDouble() {
	double cv = standard / mean;
	double z = cv * cv;
	double p = 0.5 * (1.0 - Math.sqrt((z - 1.0) / (z + 1.0)));
	z = (random.nextDouble() > p) ? (mean / (1.0 - p)) : (mean / p);
	return -0.5 * z * Math.log(random.nextDouble());
    }

    public int nextInt() {
	return (int)nextDouble();
    }

    // for debug
    public static void main(String[] argv) {
	if (argv.length < 2) {
	    System.out.println(
		"Usage: java HyperexponetRandom [mean] [standard]"
	    );
	    return;
	}
	double mean = Double.valueOf(argv[0]).doubleValue();
	double standard = Double.valueOf(argv[1]).doubleValue();
	HyperexponentRandom h = new HyperexponentRandom(
	    mean, standard, 123
	);
	for (int i = 0 ; i < 10 ; i++) {
	    System.out.println(h.nextDouble());
	}
    }
}
