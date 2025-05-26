package silf.util;

import java.util.Date;
import java.text.DateFormat;
import java.io.*;

public class Logger {
  static int mode = 2;    /* quiet = 0, log = 1, verbose = 2*/

  String name;

  public static void quiet()   { mode = 0; }
  public static void log()     { mode = 1; }
  public static void verbose() { mode = 2; }

  public static PrintStream os = System.err;

  public Logger(String className) {
    name = className;
  }

  public Logger(Object obj) {
    name = obj.getClass().getName();
  }

  public static void changeOutput(String fileName) throws IOException {
      DataOutputStream s =
	  new DataOutputStream(new FileOutputStream(fileName, true));
      os = new PrintStream(s);
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

  public void printStack(Throwable e) {
    if (mode > 1) {
      e.printStackTrace(os);
    }
  }

  public void print(String msg) {
    if (mode > 1) {
      os.print(name + ":" + msg);
    }
  }

  public void log(Object o) {
    if (mode > 0) {
      String date = DateFormat.getDateInstance().format(new Date());
      os.println(date + " " + ":" + o);
    }
  }

  public void logStack(Throwable e) {
    if (mode > 0) {
      e.printStackTrace(os);
    }
  }
  public static int mode(){
    return mode;
  }
  public static boolean isVerbose(){
    return mode == 2;
  }

  public static boolean isLog(){
    return mode >= 1;
  }

  public static int changeMode(int m) {
    int ret = mode;
    mode = m;
    return ret;
  }
}
