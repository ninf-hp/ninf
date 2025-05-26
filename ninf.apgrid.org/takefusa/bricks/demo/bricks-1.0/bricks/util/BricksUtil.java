package bricks.util;
import java.util.*;
import java.io.*;

/** Collection of misc. methods for Bricks */
public class BricksUtil {

    public static double getGlobalTimeStamp(){
	return System.currentTimeMillis() / 1000.0;
    }

    /* for Exceptions */
    public static void abort(String str){
	System.err.println(FormatString.format(str));
	System.exit(3);
    }
    
    public static void abort(String str, Object o){
	System.err.println(FormatString.format(str, o));
	System.exit(3);
    }
    
    public static void abort(String str, Object o0, Object o1){
	System.err.println(FormatString.format(str, o0, o1));
	System.exit(3);
    }
    
    /* for parseing Strings */
    public static void skipNextToken(StringTokenizer st, String delim) {
	String tmp = st.nextToken(delim);
	return;
    }

    public static int getInt(String str) {
	return new Integer(str).intValue();
    }

    public static int getInt(StringTokenizer st, String delim) throws RuntimeException {
	return new Integer(st.nextToken(delim)).intValue();
    }

    public static double getDouble(String str) {
	return new Double(str).doubleValue();
    }

    public static double getDouble(StringTokenizer st, String delim) throws RuntimeException {
	return new Double(st.nextToken(delim)).doubleValue();
    }

    /* for IO */
    public static PrintWriter getWriter(OutputStream os) {
	//return new PrintWriter(new OutputStreamWriter(os), true);
	return new PrintWriter(new OutputStreamWriter(os), false);
    }

    public static OutputStream getOutputStream(String fileName, int bufferSize) {
	try {
	    FileOutputStream fos = new FileOutputStream(fileName);
	    if (bufferSize < 0)
		return fos;
	    else
		return new BufferedOutputStream(fos, bufferSize);

	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.err.println ("Cannot open '" + fileName + "'");
	    throw new RuntimeException();
	}
    }

    public static InputStream getInputStream(String fileName) {
	try {
	    FileInputStream fis = new FileInputStream(fileName);
	    return fis;
	} catch (FileNotFoundException fnfe) {
	    fnfe.printStackTrace();
	    System.err.println ("Cannot open '" + fileName + "'");
	    throw new RuntimeException();
	}
    }
}

