package ninf.client;

import java.io.*;
import java.util.Vector;
import java.util.Stack;
import java.lang.Integer;
import ninf.basic.*;


/** add **/
public class NinfRetDimParam {
  int mat;
  int dim;

  public NinfRetDimParam(){
     this(0, 0);
  }

  public NinfRetDimParam(int m, int d){
     mat = m; dim = d;
  }

  public NinfRetDimParam copy(){
     NinfRetDimParam tmp = new NinfRetDimParam(mat, dim);
     return tmp;
  }

  public void putInfo(int w, int d){
     mat = w; dim = d; 
  }
  public int getMatrixIndex(){ return mat; }
  public int getDim(){ return dim; }


/*  public Point pointValue(){//p.x = mat, p.y = dim;
     return new Point(mat,dim);
  } */

  public boolean IS_MAT(){ return (mat > 0);}
  public boolean IS_SIZE_Y(){ return (dim == 2);}
  public boolean IS_SIZE_X() { return (dim == 1);}
}
/********/



