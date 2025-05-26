package bricks.util;
import java.io.*;
import java.util.*;

public class Linear extends Interpolation {

    public Linear(int numPoints, float[] xp, float[] yp) {;}

    public Linear(int numPoints, float delta) {
	this.numPoints = numPoints;
	this.dx = delta;
    }

    public Linear(int numPoints) {
	this.numPoints = numPoints;
    }

    /******************** public/protected method ********************/
    protected float getY(float x) {
	for (int i = currentPoint ; i < numPoints ; i++) {
	    if (x < xp[i]) {
		currentPoint = i;
		break;
	    }
	}
	return ((yp[currentPoint] - yp[currentPoint-1]) * x +
		xp[currentPoint]*yp[currentPoint-1] -
		xp[currentPoint-1]*yp[currentPoint]) /
	    (xp[currentPoint] - xp[currentPoint-1]);
    }

    protected float getOldY(float x0) {
	int tmp = 0;
	for (int i = currentPoint ; i >= 0 ; i--) {
	    if (xp[i] < x0) {
		tmp = i+1;
		break;
	    }
	}
	return ((yp[tmp] - yp[tmp-1]) * x0 + xp[tmp] * yp[tmp-1] -
		xp[tmp-1] * yp[tmp]) / (xp[tmp] - xp[tmp-1]);
    }

    public void hokan() {
	y = new float[mm+1];
	float xmin = xp[0];
	float xmax = xp[numPoints-1];
	if (dx == 0)
	    dx = (xmax - xmin) / mm;
	int k = 1;
	for (int i = 0 ; i < mm ; i++) {
	    float x = xmin + dx * i;
	    if (x >= xp[k]) {
		//System.out.println("x = " + x + ", xp[" + k + "] = " + xp[k]);
		k++;
	    }
	    //System.out.println("i = " + i);
	    y[i] = ((yp[k] - yp[k-1]) * x + xp[k]*yp[k-1] - xp[k-1]*yp[k])
		/ (xp[k] - xp[k-1]);
	    //System.out.println(x + "\t" + y[i]);
	}
	y[mm] = yp[numPoints-1];
	//System.out.println("");
    }

    /************************* Debug *************************/

    public static void main(String[] argv) {
	if (argv.length < 3) {
	    System.err.println("Usage: java Linear [in:data file name] [#points] [#hokan]");
	    System.err.println("----- data file -----");
	    System.err.println("xp[0],   yp[0]");
	    System.err.println("xp[1],   yp[1]");
	    System.err.println("       :");
	    System.err.println("xp[n-1], yp[n-1]");
	    return;
	}

	int numPoints = Integer.valueOf(argv[1]).intValue();
	mm = Integer.valueOf(argv[2]).intValue();
	InputStream isOfDataFile;
	//System.out.println("mm = " + mm);
	try {
	    isOfDataFile = new FileInputStream(argv[0]);

	    Interpolation interpolation = new Linear(numPoints);
	    interpolation.init(isOfDataFile, "Linear");
	    
	    test1(interpolation);
	    //test2(interpolation);
	    //test3(interpolation);

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    BricksUtil.abort("Cannot open '" + argv[0] + "'");

	} catch (BricksParseException e) {
	    BricksUtil.abort(e.toString());
	}
    }
}
