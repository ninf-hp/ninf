package bricks.util;
import java.io.*;
import java.util.*;

public abstract class Interpolation {

    protected int numPoints = -1;
    protected float[] xp;
    protected float[] yp;
    protected float[] y;

    protected static int mm = 10;
    protected float dx = 0;

    protected int currentPoint = 1;
    protected int totalOutPoint = 0;
    protected float yMax = (float)0.0;

    protected abstract float getY(float x);
    protected abstract float getOldY(float x0);
    public abstract void hokan();

    protected void calc() {;}

    /*************** public method of initialization ***************/
    public static Interpolation get(
	String interpolationType, int numPoints, String fileName
    ) throws BricksParseException {

	InputStream isOfDataFile;
	try {
	    isOfDataFile = new FileInputStream(fileName);

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    throw new BricksParseException("Cannot open [" + fileName + "]");
	}

	Interpolation interpolation = null;
	if (interpolationType.equalsIgnoreCase("spline")) {
	    interpolation = new Spline(numPoints);

	} else if (interpolationType.equalsIgnoreCase("splineorlinear")) {
	    interpolation = new SplineOrLinear(numPoints);

	} else if (interpolationType.equalsIgnoreCase("linear")) {
	    interpolation = new Linear(numPoints);

	} else if (interpolationType.equalsIgnoreCase("non")) {
	    interpolation = new NonInterpolation(numPoints);

	} else {
	    throw new BricksParseException(
		"Bad InterpolationType!: " + interpolationType
	    );
	}
	try {
	    interpolation.init(isOfDataFile, interpolationType);
	    return interpolation;
	} catch (BricksParseException e) {
	    throw e;
	}
    }

    /************************* public method *************************/
    public double yMax() {
	return (double)yMax;
    }

    public void print() {
	float x = xp[0];
	for (int i = 0 ; i < mm+1 ; i++) {
	    System.out.print(x + "\t");
	    System.out.println(y[i]);
	    x += dx;
	}
    }

    public double getPoint(double currentTime) {
	float x = (float)currentTime;
	if (x > xp[numPoints-1]) {
	    error();
	}
	return (double)getY(x);
    }

    public double getPoint() {
	if (currentPoint > numPoints) {
	    error();
	}
	if (dx == 0)
	    dx = (xp[numPoints-1] - xp[0]) / mm;
	float x = xp[0] + dx * totalOutPoint++;
	//System.out.println("x = " + x);
	return (double)getY(x);
    }

    public double getIntegral(double currentTime, double trackingTime) {
	float x = (float)currentTime;
	if (x > xp[numPoints-1]) {
	    error();
	}
	return (double)integral(getPoints((float)currentTime, (float)trackingTime));
    }


    /************************* protected method *************************/
    protected Vector getPoints(float currentTime, float trackingTime) {
	Vector xy = new Vector();
	float x0 = (float)(currentTime - trackingTime);
	if (x0 < (float)0.0)
	    x0 = (float)0.0;
	Point p = new Point(currentTime, getY(currentTime));
	xy.insertElementAt(p, 0);
	for (int i = currentPoint ; i >= 0 ; i--) {
	    if (currentTime <= xp[i]) {
		continue;
	    } else if (xp[i] < x0) {
		break;
	    }
	    p = new Point(xp[i], yp[i]);
	    xy.insertElementAt(p, 0);
	}
	p = new Point(x0, getOldY(x0));
	xy.insertElementAt(p, 0);
	return xy;
    }

    protected float integral(Vector xy) {
	if (xy.size() == 1) {
	    Point p = (Point)xy.firstElement();
	    return p.y;
	}
	Enumeration e = xy.elements();
	Point p0 = (Point)e.nextElement();
	float dx = p0.x;
	float sum = (float)0.0;
	while (e.hasMoreElements()) {
	    Point p1 = (Point)e.nextElement();
	    sum = sum + (p1.y + p0.y) * (p1.x - p0.x);
	    p0 = p1;
	    //System.out.println("(p.x, p.y) = (" + p1.x + ", " + p1.y + 
	    //"), sum = " + sum);
	}
	dx = p0.x - dx;
	//System.out.println("dx = " + dx);
	float integral = sum * (float)0.5 / dx;
	//System.out.print("current: " + p0.x + " : Integral = " + integral);
	if (integral > (float)1.0) {
	    integral = (float)1.0;
	}
	//System.out.println(" (" + integral + ")");
	return integral;
    }

    /*
    protected void init(InputStream isOfDataFile) throws RuntimeException {
	BufferedReader br = new BufferedReader(
	    new InputStreamReader(isOfDataFile)
	);
	try {
	    String line;
	    StringTokenizer st = null;
	    //int dbg = 0;
	    while ((line = br.readLine()) != null) {
		//dbg++;
		//System.out.println("line : " + dbg);
		st = new StringTokenizer(line);
		if (!st.hasMoreElements()) {
		    continue;
		} else {
		    numPoints = 
			Integer.valueOf(st.nextToken(" \t,")).intValue();
		    break;
		}
	    }
	    init(br);

	} catch (IOException e) {
	    e.printStackTrace();
	    error();
	}
	calc();
    }
    */

    protected void init(InputStream isOfDataFile, String interpolationType) 
	throws BricksParseException 
    {
	try {
	    BufferedReader br = new BufferedReader(
	        new InputStreamReader(isOfDataFile)
	    );
	    String line;
	    StringTokenizer st = null;
	    xp = new float[numPoints];
	    yp = new float[numPoints];

	    for (int i = 0 ; i < numPoints ; i++) {
		if ((line = br.readLine()) == null)
		    throw new BricksParseException(interpolationType + 
			": The file does not have enough data.");
		st = new StringTokenizer(line);
		//System.out.println("i = " + i);
		xp[i] = Float.valueOf(st.nextToken(" \t,")).floatValue();
		yp[i] = Float.valueOf(st.nextToken(" \t,")).floatValue();
		if (yp[i] > yMax)
		    yMax = yp[i];
	    }
	    calc();

	} catch (IOException e) {
	    e.printStackTrace();
	    throw new BricksParseException(interpolationType);
	    
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(interpolationType);
	}
    }

    protected void error() throws RuntimeException {
	System.err.println("Interpolation Error:: Bad data file!!");
	throw new RuntimeException();
    }

    /************************* debug *************************/
    public static void test1(Interpolation interpolation) {
	interpolation.hokan();
	interpolation.print();
    }

    public static void test2(Interpolation interpolation) {
	for (int i = 0 ; i < 11 ; i++)
	    System.out.println(interpolation.getPoint());
    }
	
    public static void test3(Interpolation interpolation) {
	System.out.println("1.25 " + interpolation.getPoint(1.25));
	System.out.println("1.75 " + interpolation.getPoint(1.75));
	System.out.println("2.25 " + interpolation.getPoint(2.25));
	System.out.println("2.75 " + interpolation.getPoint(2.75));
	System.out.println("3.25 " + interpolation.getPoint(3.25));
	System.out.println("3.75 " + interpolation.getPoint(3.75));
	System.out.println("4.25 " + interpolation.getPoint(4.25));
	System.out.println("4.75 " + interpolation.getPoint(4.75));
    }
}
