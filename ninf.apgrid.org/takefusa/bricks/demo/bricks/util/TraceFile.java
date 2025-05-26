package bricks.util;
import java.io.*;
import java.util.*;

public class TraceFile extends Sequence implements SubComponent {

    protected double[] t;
    protected double max = -1.0;
    protected int currentPoint = 0;

    public TraceFile(String fileName, int numPoints) {
	try {
	    InputStream is = new FileInputStream(fileName);
	    t = new double[numPoints];
	    init(is, numPoints);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    System.err.println ("Cannot open '" + fileName + "'");
	    throw new RuntimeException();
	}
    }

    public String getName() {
	return "TraceFile";
    }

    public double max() {
	return max;
    }

    public double nextDouble(double currentTime) {
	return t[currentPoint++];
    }

    public double nextDouble() {
	return t[currentPoint++];
    }

    private void init(InputStream is, int numPoints) {
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	try {
	    String line;
	    StringTokenizer st = null;
	    
	    int i = 0;
	    while (i < numPoints) {
		if ((line = br.readLine()) != null) {
		    st = new StringTokenizer(line);
		    while (st.hasMoreElements()) {
			t[i] = 
			    Double.valueOf(st.nextToken(" \t,")).doubleValue();
			if (t[i] > max)
			    max = t[i];
			i++;
		    }
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
