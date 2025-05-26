// NinfLog.java

package ninf.basic;

import java.util.Date;
import java.io.*;

public class NinfLog {
  static int mode = 1;    /* quiet = 0, log = 1, verbose = 2*/
  String name;

  public static void quiet()   { mode = 0; }
  public static void log()     { mode = 1; }
  public static void verbose() { mode = 2; }

  public static PrintStream os = System.out;

  public NinfLog(String className) {
    name = className;
  }

  public NinfLog(Object obj) {
    name = obj.getClass().getName();
  }

  public static void changeOutput(String fileName) throws NinfIOException {
    try {
      DataOutputStream s =
	new DataOutputStream(new FileOutputStream(fileName, true));
      os = new PrintStream(s);
    }catch (IOException e){
      throw new NinfIOException(e);
    }
  }
  public static void changeOutput(PrintStream s) {
    os = s;
  }

  public void println(String msg) {
    if (mode > 1) {
      os.println(name + ":" + msg);
    }
  }

  public void println(Object o) {
    if (mode > 1) {
      os.println(name + ":" + o);
    }
  }

  public void print(String msg) {
    if (mode > 1) {
      os.print(name + ":" + msg);
    }
  }

  public void log(Object o) {
    if (mode > 0) {
      os.println((new Date()).toLocaleString() + " " + name + ":" + o);
    }
  }

  public static int changeMode(int m) {
    int ret = mode;
    mode = m;
    return ret;
  }
}

// end of NinfLog.java
