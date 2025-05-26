package bricks.util;
import java.io.*;
import java.util.*;

public class SplineOrLinear extends Spline {

    public SplineOrLinear(int numPoints, float[] xp, float[] yp) {
	super(numPoints, xp, yp);
    }

    public SplineOrLinear(int numPoints, float delta) {
	super(numPoints, delta);
    }

    public SplineOrLinear(int numPoints) {
	super(numPoints);
    }

    /************************* public method *************************/
    public float getY(float x) {
	for (int i = currentPoint ; i < numPoints ; i++) {
	    if (x < xp[i]) {
		currentPoint = i;
		break;
	    }
	}
	float t = x - xp[currentPoint-1];
	float yValue = ((a3[currentPoint] * t + a2[currentPoint]) * t +
			 u[currentPoint-1]) * t + yp[currentPoint-1];
	if (yValue < 0.0) {
	    yValue = ((yp[currentPoint] - yp[currentPoint-1]) * x +
		      xp[currentPoint]*yp[currentPoint-1] -
		      xp[currentPoint-1]*yp[currentPoint]) /
		(xp[currentPoint] - xp[currentPoint-1]);
	}
	return yValue;
    }

    public float getOldY(float x0) {
	int tmp = 0;
	for (int i = currentPoint ; i >= 0 ; i--) {
	    if (xp[i] < x0) {
		tmp = i+1;
		break;
	    }
	}
	float t = x0 - xp[tmp-1];
	float yValue = ((a3[tmp] * t + a2[tmp]) * t +
			 u[tmp-1]) * t + yp[tmp-1];
	if (yValue < 0.0) {
	    yValue = ((yp[tmp] - yp[tmp-1]) * x0 + xp[tmp] * yp[tmp-1] -
		      xp[tmp-1] * yp[tmp]) / (xp[tmp] - xp[tmp-1]);
	}
	return yValue;
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
	    float t = x - xp[k-1];
	    y[i] = ((a3[k] * t + a2[k]) * t + u[k-1]) * t + yp[k-1];
	    if (y[i] < 0.0) {
		y[i] = ((yp[currentPoint] - yp[currentPoint-1]) * x +
			xp[currentPoint]*yp[currentPoint-1] -
			xp[currentPoint-1]*yp[currentPoint]) /
		    (xp[currentPoint] - xp[currentPoint-1]);
	    }
	    //System.out.println(x + "\t" + y[i]);
	}
	y[mm] = yp[numPoints-1];
	//System.out.println("");
    }


    /************************* Debug *************************/
    public static void main(String[] argv) {
	if (argv.length < 3) {
	    System.err.println("Usage: java SplineOrLinear [in:data file name] [#points] [#hokan]");
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
	try {
	    isOfDataFile = new FileInputStream(argv[0]);
	    Interpolation interpolation = new SplineOrLinear(numPoints);
	    interpolation.init(isOfDataFile, "SplineOrLinear");
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
