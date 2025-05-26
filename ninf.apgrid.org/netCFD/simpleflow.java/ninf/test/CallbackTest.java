import java.io.*;
import java.util.Vector;
import ninf.basic.*;
import ninf.client.*;

class TestCallback implements CallbackFunc{
  double sum = 0.0;
  public void callback(Vector args){
    double d = ((double[])args.elementAt(0))[0];
    sum += d;
  }
}

class TestCallbackString implements CallbackFunc{
  String string;
  public void callback(Vector args){
    string = ((String[])args.elementAt(0))[0];
  }
}

class TestCallback2D implements CallbackFunc{
  double tmp[][];

  public void callback(Vector args){
    tmp = (double[][])args.elementAt(0);
  }
}

class TestCallbackReturn implements CallbackFunc{
  int N;
  TestCallbackReturn(int N){
    this.N = N;
  }

  public void callback(Vector args){
    double[][] tmp1  = (double[][])args.elementAt(0);
    double[][] tmp2  =  (double[][])args.elementAt(1);
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++){
	tmp2[i][j] = tmp1[i][j];
      }
  }
}

public class CallbackTest{
  static boolean verbose = false;

  static boolean callbackTest(){
    int times = 10;
    double initial = 100.0, lsum = 0.0;
    try {
      NinfClient tmp = new NinfClient();
      TestCallback func = new TestCallback();
      System.out.print("CallbackTest.. ");      
      NinfExecInfo info = tmp.callWith("test/callback_test", new Double(initial), 
				       new Integer(times), func);
				       
      if (verbose){System.out.println(info);}

      double d = 0.0;
      for (int i = 0; i < times; i++){
	d += initial;
	lsum += d;
      }
      if (func.sum != lsum){
	if (verbose)
	  System.out.println("sum = " + func.sum + "(should be " + lsum + ")");
	return false;
      }
      return true;
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
  }

  static boolean callback2DTest(){
    try {
      int N = 10;
      double a[][] = new double[N][N];
      for (int i = 0; i < N; i++)
	for (int j = 0; j < N; j++)
	  a[i][j] = i * N + j;
      
      NinfClient tmp = new NinfClient();
      TestCallback2D func = new TestCallback2D();
      System.out.print("Callback2DTest.. ");      
      NinfExecInfo info = 
	tmp.callWith("test/callback2D_test", new Integer(N), a, func);
      if (verbose){System.out.println(info);}
      for (int i = 0; i < N; i++)
	for (int j = 0; j < N; j++)
	  if (a[i][j] != func.tmp[i][j])
	    return false;
      return true;
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
  }


  static boolean callbackReturnTest(){
    try {
      boolean success = true;
      int N = 10;
      double a[][] = new double[N][N];
      double b[][] = new double[N][N];
      for (int i = 0; i < N; i++)
	for (int j = 0; j < N; j++){
	  a[i][j] = i * N + j;
	  b[i][j] = 0.0;
	}
      
      NinfClient tmp = new NinfClient();
      TestCallbackReturn func = new TestCallbackReturn(N);
      System.out.print("CallbackReturnTest.. ");      
      NinfExecInfo info = 
	tmp.callWith("test/callback_return_test", new Integer(N), a, b, func);
      if (verbose){System.out.println(info);}
      for (int i = 0; i < N; i++)
	for (int j = 0; j < N; j++)
	  if (a[i][j] != b[i][j]){
	    if (verbose) System.out.println(
               FormatString.format("file_test: a[%d][%d](%lf) != b[%d][%d](%lf)",
				   new Integer(i), new Integer(j), new Double(a[i][j]),
				   new Integer(i), new Integer(j), new Double(b[i][j])));
	    success = false;
	  }
      return success;
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
  }

  static boolean callbackMultiTest(){
    try {
      boolean success = true;
      int N = 10;
      double a[][] = new double[N][N];
      double b[][] = new double[N][N];
      for (int i = 0; i < N; i++)
	for (int j = 0; j < N; j++){
	  a[i][j] = i * N + j;
	  b[i][j] = 0.0;
	}
      
      NinfClient tmp = new NinfClient();
      TestCallbackReturn func = new TestCallbackReturn(N);
      System.out.print("CallbackMultiTest.. ");      
      NinfExecInfo info = 
	tmp.callWith("test/callback_multi_test", new Integer(N), a, b, func, func);
      if (verbose){System.out.println(info);}
      for (int i = 0; i < N; i++)
	for (int j = 0; j < N; j++)
	  if (a[i][j] != b[i][j]){
	    if (verbose) System.out.println(
               FormatString.format("file_test: a[%d][%d](%lf) != b[%d][%d](%lf)",
				   new Integer(i), new Integer(j), new Double(a[i][j]),
				   new Integer(i), new Integer(j), new Double(b[i][j])));
	    success = false;
	  }
      return success;
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
  }

  static boolean callbackStringTest(){
    try {
      String strings[] = {"caller0","caller1","caller2","caller3","caller4"};
      String front = "";
      NinfClient tmp = new NinfClient();
      TestCallbackString func = new TestCallbackString();
      System.out.print("CallbackStringTest.. ");      
      NinfExecInfo info = tmp.callWith("test/callbackstr", strings, func);
      if (verbose){System.out.println(info);}

      for (int i = 0; i < 5; i++)
	front += strings[i];
      if (verbose){
	System.out.println("callbacke = " + func.string);
	System.out.println("frontend = " + front);
      }
      if (front.equals(func.string))
	return true;
      return false;

    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
  }

  static String[] parseMyArg(String arg[]){
    Vector tmpV = new Vector();
    int index = 0;
    for (int i = 0; i < arg.length; i++){
      if (arg[i].equalsIgnoreCase("-verbose"))
	verbose = true;
      else 
	tmpV.addElement(arg[i]);
    }
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }
  public static void main(String args[]){
    args = NinfClient.parseArg(args);
    args = parseMyArg(args);
    
    if (callbackTest()) 
      System.out.println(" OK"); else System.out.println(" failed");
    if (callback2DTest()) 
      System.out.println(" OK"); else System.out.println(" failed");
    if (callbackReturnTest()) 
      System.out.println(" OK"); else System.out.println(" failed");
    if (callbackStringTest()) 
      System.out.println(" OK"); else System.out.println(" failed");
    if (callbackMultiTest()) 
      System.out.println(" OK"); else System.out.println(" failed");
  }
}
