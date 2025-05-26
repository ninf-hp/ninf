import ninf.client.*;
import ninf.basic.*;
import java.io.*;
import java.util.Vector;

public class MatTest{
  static boolean success = true;;
  static boolean verbose = false;

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
    int matsize = 103;
    args = NinfClient.parseArg(args);
    args = parseMyArg(args);
    if (args.length != 0)
      matsize = Integer.valueOf(args[0]).intValue();
    double a[][], b[][], c[][], c0[][];

    a = new double[matsize][matsize];
    b = new double[matsize][matsize];
    c = new double[matsize][matsize];
    c0 = new double[matsize][matsize];

    for (int i = 0; i < matsize; i++)
      for (int j = 0; j < matsize; j++){
	a[i][j] = 100.0; //i * matsize +j ; //Math.random();
	b[i][j] = i * matsize +j ;// Math.random();
	c[i][j] = 0;
      }
    
    try {
      NinfClient tmp = new NinfClient();

      System.out.print("MatTest.. ");      
      NinfExecInfo info = tmp.callWith("test/matmul", new Integer(matsize), a, b, c);
      if (verbose){System.out.println(info);}
    } catch (NinfException e){
      if (verbose){System.out.println(e);}
      System.out.println(" failed");
      System.exit(3);
    }

    mmul(matsize, a, b, c0);
    for (int i = 0; i < matsize; i++)
      for (int j = 0; j < matsize; j++){
	if (c[i][j] != c0[i][j]){
	  System.out.println("c [" + i +"][" + j + "] =" +  c[i][j]);
	  System.out.println("c0[" + i +"][" + j + "] =" +  c0[i][j]);
	  success = false;
	}
      }
    if (!success)System.out.println(" failed");
    else System.out.println(" OK");
  }

  static void mmul(int N, double A[][], double B[][], double C[][]){
    double t;

    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
	t = 0;
	for (int k = 0; k < N; k++){
	  t += A[i][k] * B[k][j];	/* inner product */
	}
	C[i][j] = t;
      }
    }
  }
}
