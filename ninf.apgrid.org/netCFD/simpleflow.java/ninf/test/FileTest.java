import java.io.*;
import java.util.Vector;
import ninf.basic.*;
import ninf.client.*;


public class FileTest{
  static boolean verbose = false;
  static int N = 10;

  static boolean fileTest(String URL){
    double a[], b[];
    int i, result;
    
    a = new double[N];
    b = new double[N];
    for (i = 0; i < N; i++){
      a[i] = (double)i;
      b[i] = 0.0;
    }
    
    try {
      System.out.print("FileTest with " + URL + " ...");      
      (new NinfClient()).callWith("test/double_test", new Integer(N), a, URL);
      (new NinfClient()).callWith("test/double_test", new Integer(N), URL, b);
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
    for (i = 0; i < N; i++){
      if (a[i] != b[i]){
	if (verbose) System.out.println(
		FormatString.format("file_test: a[%d](%lf) != b[%d](%lf)",
				    new Integer(i), new Double(a[i]),
				    new Integer(i), new Double(b[i])));
	return false;
      }
    }
    return true;
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
    
    if (fileTest("file:///saru")) System.out.println(" OK"); else System.out.println(" failed");
    if (fileTest("http://ninf.etl.go.jp/paradise/tmp.ninfbin")) System.out.println(" OK"); else System.out.println(" failed");
  }
}
