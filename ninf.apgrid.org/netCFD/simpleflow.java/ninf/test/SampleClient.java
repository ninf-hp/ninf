import ninf.client.*;
import java.util.*;

class SampleClient {
  public static void main(String args[]) {
    try {
      args = NinfClient.parseArg(args);
      NinfClient nc = new NinfClient();
      int N = 4;

      double A[][] = new double[N][N];
      double B[][] = new double[N][N];      
      double C[][] = new double[N][N]; 

      for (int i = 0; i < N; i++){
	for (int j = 0; j < N; j++){
	  A[i][j] = i + j * 0.1;
	  B[i][j] = (i == j)? 1: 0;
	  C[i][j] = 0;
	}
      }
      nc.callWith("mmul", new Integer(N), "http://janus.etl.go.jp/testdata.coordination", A, 
		  "http://janus.etl.go.jp/outjava.ninfbin");

      nc.callWith("mThrough", new Integer(N), "http://janus.etl.go.jp/outjava.ninfbin", C);

      for (int i = 0; i < N; i++){
	for (int j = 0; j < N; j++){
	  System.out.print(C[i][j] + " ");
	}
	System.out.println("");
      }
      
    } catch (Exception e) {
      System.err.println("*** NINF CLIENT ERROR. ***");
      e.printStackTrace();
      System.exit(1);
    }
  }
}

