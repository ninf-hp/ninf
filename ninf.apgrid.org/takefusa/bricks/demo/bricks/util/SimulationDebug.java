// NinfLog.java is modified to SimulationDebug.java
//package ninf.basic;

package bricks.util;
import java.util.Date;
import java.io.*;

public class SimulationDebug {
    public static int mode = 0;    /* quiet = 0, log = 1, verbose = 2*/
    String name;

    public static void quiet()   { mode = 0; }
    public static void log()     { mode = 1; }
    public static void verbose() { mode = 2; }

    public static PrintWriter os = new PrintWriter(System.out, true);

    public static void changeOutput(String fileName) throws IOException {
	try {
	    DataOutputStream s =
		new DataOutputStream(new FileOutputStream(fileName, true));
	    os = new PrintWriter(s);
	}catch (IOException e){
	    e.printStackTrace();
	    throw new IOException();
	}
    }

    public static void changeOutput(OutputStream s) {
	os = new PrintWriter(s, true);
    }

    public static void println(String msg) {
	if (mode > 1) {
	    os.println(msg + "\n");
	}
    }

    public static void print(String msg) {
	if (mode > 1) {
	    os.println(msg);
	}
    }

    public static void println(Object o) {
	if (mode > 1) {
	    os.println(o + "\n");
	}
    }

    public static void print(Object o) {
	if (mode > 1) {
	    os.print(o);
	}
    }

    public static void primaryPrint(String msg) {
	if (mode > 0) {
	    os.print(msg);
	}
    }

    public static void primaryPrintln(String msg) {
	if (mode > 0) {
	    System.out.println(msg);
	    os.print(msg + "\n");
	}
    }

    public static int changeMode(int m) {
	int ret = mode;
	mode = m;
	return ret;
    }
}
