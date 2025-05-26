package bricks.util;
import java.io.*;
import java.util.*;

public class NonInterpolation extends Interpolation {

    public NonInterpolation(int numPoints, float[] xp, float[] yp) {;}

    public NonInterpolation(int numPoints, float delta) {
	this.numPoints = numPoints;
	this.dx = delta;
    }

    public NonInterpolation(int numPoints) {
	this.numPoints = numPoints;
    }

    /************************* public method *************************/
    public float getY(float x) {
	//System.out.println("x = " + x);
	for (int i = currentPoint ; i < numPoints ; i++) {
	    if (x < (xp[i] + xp[i-1]) * 0.5) {
		/*
		int j = i-1;
		System.out.println("x = " + x +
				   ", xp[" + j + "] = " + xp[j] +
				   ", xp[" + i + "] = " + xp[i]);
		System.out.println("y = " + yp[j] +
				   ", yp[" + j + "] = " + yp[j] +
				   ", yp[" + i + "] = " + yp[i]);
				   */
		currentPoint = i;
		break;
	    }
	}
	return yp[currentPoint-1];
    }

    public float getOldY(float x0) {
	int tmp = 0;
	for (int i = currentPoint ; i >= 0 ; i--) {
	    if ((xp[i] + xp[i+1]) * 0.5 < x0) {
		tmp = i+1;
		break;
	    }
	}
	return yp[tmp];
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
	    if (2*x >= xp[k] + xp[k-1]) {
		//System.out.println("x = " + x + ", xp[" + k + "] = " + xp[k]);
		k++;
	    }
	    //System.out.println("i = " + i);
	    y[i] = yp[k-1];
	    //System.out.println(x + "\t" + y[i]);
	}
	y[mm] = yp[numPoints-1];
	//System.out.println("");
    }

    /************************* Debug *************************/

    public static void main(String[] argv) {
	if (argv.length < 3) {
	    System.err.println("Usage: java NonInterpolation [in:data file name] [#points] [#hokan]");
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
	    Interpolation interpolation = new NonInterpolation(numPoints);
	    interpolation.init(isOfDataFile, "NonInterpolation");
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
