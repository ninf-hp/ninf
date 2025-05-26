import java.io.*;
import java.util.Vector;
import ninf.basic.*;
import ninf.client.*;

public class DataTest{
  static boolean verbose = false;
  static int N = 1;
  static int SKIP = 3;
  static int TIMES = 10;


  static boolean byteTest(){
    byte a[], b[];
    int i, result;
    
    a = new byte[N];
    b = new byte[N];
    for (i = 0; i < N; i++){
      a[i] = (byte)i;
      b[i] = 0;
    }
    
    try {
      System.out.print("CharTest ");
      (new NinfClient()).callWith("test/char_test", new Byte((byte)N), a, b);
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
    for (i = 0; i < N; i++){
      if (a[i] != b[i]){
	if (verbose) System.out.println(
		FormatString.format("file_test: a[%d](%d) != b[%d](%d)",
				    new Integer(i), new Byte(a[i]),
				    new Integer(i), new Byte(b[i])));
	return false;
      }
    }
    return true;
  }

  static boolean shortTest(){
    short a[], b[];
    int i, result;
    
    a = new short[N];
    b = new short[N];
    for (i = 0; i < N; i++){
      a[i] = (short)i;
      b[i] = 0;
    }
    
    try {
      System.out.print("ShortTest ");
      (new NinfClient()).callWith("test/short_test", new Short((short)N), a, b);
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
    for (i = 0; i < N; i++){
      if (a[i] != b[i]){
	if (verbose) System.out.println(
		FormatString.format("file_test: a[%d](%d) != b[%d](%d)",
				    new Integer(i), new Short(a[i]),
				    new Integer(i), new Short(b[i])));
	return false;
      }
    }
    return true;
  }

  static boolean intTest(){
    int a[], b[];
    int i, result;
    
    a = new int[N];
    b = new int[N];
    for (i = 0; i < N; i++){
      a[i] = (int)i;
      b[i] = 0;
    }
    
    try {
      System.out.print("IntTest ");
      (new NinfClient()).callWith("test/int_test", new Integer(N), a, b);
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
    for (i = 0; i < N; i++){
      if (a[i] != b[i]){
	if (verbose) System.out.println(
		FormatString.format("file_test: a[%d](%d) != b[%d](%d)",
				    new Integer(i), new Integer(a[i]),
				    new Integer(i), new Integer(b[i])));
	return false;
      }
    }
    return true;
  }

  static boolean floatTest(){
    float a[], b[];
    int i, result;
    
    a = new float[N];
    b = new float[N];
    for (i = 0; i < N; i++){
      a[i] = (float)i;
      b[i] = 0.0f;
    }
    
    try {
      System.out.print("FloatTest ");
      (new NinfClient()).callWith("test/float_test", new Integer(N), a, b);
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      return false;
    }
    for (i = 0; i < N; i++){
      if (a[i] != b[i]){
	if (verbose) System.out.println(
		FormatString.format("file_test: a[%d](%f) != b[%d](%f)",
				    new Integer(i), new Float(a[i]),
				    new Integer(i), new Float(b[i])));
	return false;
      }
    }
    return true;
  }
  static boolean doubleTest(){
    double a[], b[];
    int i, result;
    
    a = new double[N];
    b = new double[N];
    for (i = 0; i < N; i++){
      a[i] = (double)i;
      b[i] = 0.0;
    }
    
    try {
      System.out.print("DoubleTest ");
      (new NinfClient()).callWith("test/double_test", new Integer(N), a, b);
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

  static boolean skipIntTest(){
    int in[], out[];
    in = new int[SKIP * TIMES];
    out = new int[SKIP * TIMES];

    System.out.print("SkipIntTest ");
    for (int i = 0; i < SKIP * TIMES; i++)
      in[i] = i;
    for (int i = 0; i < SKIP; i++){
      try {
	(new NinfClient()).
	  callWith("test/skip_int_test", 
		   new Integer(SKIP * TIMES), new Integer(i), 
		   new Integer(SKIP * TIMES), new Integer(SKIP), in, out);
      } catch (NinfException e){
	if (verbose){System.out.println(e);}
	return false;
      }
    }
    for (int i = 0; i < SKIP * TIMES; i++){
      if (out[i] != (SKIP * TIMES - i -1))
	return false;
      if (verbose) System.out.print(out[i] + " ");
    }
    if (verbose) System.out.println("");
    return true;
  }

  static boolean skipByteTest(){
    byte in[], out[];
    in = new byte[SKIP * TIMES];
    out = new byte[SKIP * TIMES];

    System.out.print("SkipByteTest ");
    for (int i = 0; i < SKIP * TIMES; i++)
      in[i] = (byte)i;
    for (int i = 0; i < SKIP; i++){
      try {
	(new NinfClient()).
	  callWith("test/skip_char_test", 
		   new Integer(SKIP * TIMES), new Integer(i), 
		   new Integer(SKIP * TIMES), new Integer(SKIP), in, out);
      } catch (NinfException e){
	if (verbose){System.out.println(e);}
	return false;
      }
    }
    for (int i = 0; i < SKIP * TIMES; i++){
      if (out[i] != (SKIP * TIMES - i -1))
	return false;
      if (verbose) System.out.print(out[i] + " ");
    }
    if (verbose) System.out.println("");
    return true;
  }

  static boolean skipShortTest(){
    short in[], out[];
    in = new short[SKIP * TIMES];
    out = new short[SKIP * TIMES];

    System.out.print("SkipShortTest ");
    for (int i = 0; i < SKIP * TIMES; i++)
      in[i] = (short)i;
    for (int i = 0; i < SKIP; i++){
      try {
	(new NinfClient()).
	  callWith("test/skip_short_test", 
		   new Integer(SKIP * TIMES), new Integer(i), 
		   new Integer(SKIP * TIMES), new Integer(SKIP), in, out);
      } catch (NinfException e){
	if (verbose){System.out.println(e);}
	return false;
      }
    }
    for (int i = 0; i < SKIP * TIMES; i++){
      if (out[i] != (SKIP * TIMES - i -1))
	return false;
      if (verbose) System.out.print(out[i] + " ");
    }
    if (verbose) System.out.println("");
    return true;
  }

    



 /****************************************************************/
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
    
    if (byteTest()) System.out.println(" OK"); else System.out.println(" failed");
    if (shortTest()) System.out.println(" OK"); else System.out.println(" failed");
    if (intTest()) System.out.println(" OK"); else System.out.println(" failed");
    if (floatTest()) System.out.println(" OK"); else System.out.println(" failed");
    if (doubleTest()) System.out.println(" OK"); else System.out.println(" failed");
    if (skipShortTest()) System.out.println(" OK"); 
    else System.out.println(" failed");
    if (skipByteTest()) System.out.println(" OK"); 
    else System.out.println(" failed");
    if (skipIntTest()) System.out.println(" OK"); 
    else System.out.println(" failed");
  }

}
