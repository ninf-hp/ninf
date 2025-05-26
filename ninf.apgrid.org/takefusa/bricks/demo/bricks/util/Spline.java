package bricks.util;
import java.io.*;
import java.util.*;

public class Spline extends Interpolation {

    protected float[] u;
    protected float[] a2;
    protected float[] a3;

    public Spline(int numPoints, float[] xp, float[] yp) {;}

    public Spline(int numPoints, float delta) {
	this.numPoints = numPoints;
	this.dx = delta;
    }

    public Spline(int numPoints) {
	this.numPoints = numPoints;
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
	//System.out.println("currentPoint = " + currentPoint +
	//", totalOutPoint = " + totalOutPoint);
	return ((a3[currentPoint] * t + a2[currentPoint]) * t +
		u[currentPoint-1]) * t + yp[currentPoint-1];
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
	return ((a3[tmp] * t + a2[tmp]) * t + u[tmp-1]) * t + yp[tmp-1];
    }

    // override
    protected void calc() {
	u = new float[numPoints];
	a2 = new float[numPoints];
	a3 = new float[numPoints];
	float[] a = new float[numPoints];
	float[] b = new float[numPoints];
	float[] c = new float[numPoints];
	float[] d = new float[numPoints];

	// calc coefficient
	for (int i = 1 ; i < numPoints - 1 ; i++) {
	    float hl = xp[i] - xp[i-1];
	    float hr = xp[i+1] - xp[i];
	    a[i] = hl / (hl + hr);
	    b[i] = 1 - a[i];
	    float c1 = (yp[i+1] - yp[i]) / hr;
	    float c2 = (yp[i] - yp[i-1]) / hl;
	    c[i] = (float)6.0 * ((c1 - c2) / (hl + hr));
	    d[i] = (float)2.0;
	}

	// calc simultaneous equations
	for (int i = 1 ; i < numPoints - 2 ; i++) {
	    b[i] = b[i] / d[i];
	    c[i] = c[i] / d[i];
	    d[i+1] = d[i+1] - a[i+1] * b[i];
	    c[i+1] = c[i+1] - a[i+1] * c[i];
	}
	float[] s = new float[numPoints];
	//System.out.println("Spline: calc() : numPoints = " + numPoints);
	s[numPoints - 2] = c[numPoints - 2] / d[numPoints - 2];
	for (int i = numPoints - 3 ; i > 0 ; i--) {
	    s[i] = c[i] - b[i] * s[i+1];
	}

	// calc differential coefficient
	float h1 = xp[1] - xp[0];
	u[0] = (yp[1] - yp[0]) / h1 - h1 * s[1] / (float)6.0;
	for (int i = 1 ; i < numPoints ; i++) {
	    float hl = xp[i] - xp[i-1];
	    u[i] = u[i-1] + hl * (s[i-1] + s[i]) / (float)2.0;
	}
	for (int i = 1 ; i < numPoints ; i++) {
	    float hl = xp[i] - xp[i-1];
	    a3[i] = (s[i] - s[i-1]) / ((float)6.0 * hl);
	    a2[i] = s[i-1] / (float)2.0;
	}
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
	    //System.out.println(x + "\t" + y[i]);
	}
	y[mm] = yp[numPoints-1];
	//System.out.println("");
    }


    /************************* Debug *************************/

    public static void main(String[] argv) {
	if (argv.length < 3) {
	    System.err.println("Usage: java Spline [in:data file name] [#point] [#hokan]");
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

	    Interpolation interpolation = new Spline(numPoints);
	    interpolation.init(isOfDataFile, "Spline");
	    
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
