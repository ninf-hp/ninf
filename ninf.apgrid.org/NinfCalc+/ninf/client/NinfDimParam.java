package ninf.client;

import java.io.*;
import java.util.Vector;
import java.util.Stack;
import java.lang.Integer;
import ninf.basic.*;


public class NinfDimParam {
  public NinfDimParamElem size;
  public NinfDimParamElem start;
  public NinfDimParamElem end;
  public NinfDimParamElem step;

  public NinfDimParam(NinfDimParamElem si, NinfDimParamElem st,
			NinfDimParamElem en, NinfDimParamElem sp){
    size = si; start = st; end = en; step = sp;
  }
  public NinfDimParam(NinfDimParamElem si){
    size = si; 
    start = new NinfDimParamElem(); 
    end = new NinfDimParamElem(); 
    step = new NinfDimParamElem(); 
  }
  public NinfDimParam(NinfDimParamElem si, NinfDimParamElem en){
    size = si; 
    start = new NinfDimParamElem(); 
    end = en;
    step = new NinfDimParamElem(); 
  }
  
  public NinfDimParam(XDRInputStream istream) throws IOException {
    size = new NinfDimParamElem(istream);
    start = new NinfDimParamElem(istream);
    end = new NinfDimParamElem(istream);
    step = new NinfDimParamElem(istream);
  }
  /******************/
  public String toString(){
    return "Param: size="+size+", start="+start+", end="+end+", step="+step;
  }


  /******************/

  public NinfDimParam copy(){
    return new NinfDimParam(size.copy(), start.copy(), end.copy(), step.copy());
					   
  }

  public void write(XDROutputStream ostream)  throws IOException {
    size.write(ostream);
    start.write(ostream);
    end.write(ostream);
    step.write(ostream);
  }

  public void calc_real_val(int arg[]) throws NinfTypeErrorException {
    size. calc_real_val(arg, 0);
    start.calc_real_val(arg, 0);
    end.  calc_real_val(arg, size.computed_val);
    step. calc_real_val(arg, 1);
  }

  public void calc_real_val(int arg[], int size_in_header) throws NinfTypeErrorException {
    size. calc_real_val(arg, 0, size_in_header);
    start.calc_real_val(arg, 0, 0);
    end.  calc_real_val(arg, size.computed_val, size_in_header);
    step. calc_real_val(arg, 1, 0);
  }



  public void shift(int i){
    size.shift(i);
    start.shift(i);
    end.shift(i);
    step.shift(i);
  }


/**** add ****/
  public boolean IS_SIZE_VALUE_IN_ARG(){return (size.IS_VALUE_IN_ARG());}
  public boolean IS_END_VALUE_IN_ARG(){return (end.IS_VALUE_IN_ARG());}

  public boolean IS_SIZE_VALUE_CONST(){ return size.IS_VALUE_CONST();}
  public boolean IS_END_VALUE_CONST(){ return end.IS_VALUE_CONST();}

  public boolean IS_END_TYPE(){return (end.IS_TYPE());}

  public int getSizeVal(){return size.val;}
  public int getEndVal(){return end.val;}


/****************** to make netsolve problem ***************************/
  public boolean isUsedAsSize(int index){
    if (size.isUsedAsSize(index))
      return true;
    if (start.isUsedAsSize(index))
      return true;
    if (end.isUsedAsSize(index))
      return true;
    if (step.isUsedAsSize(index))
      return true;
    return false;
  }

}
