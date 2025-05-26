package ninf.client;

import java.io.*;
import java.util.Vector;
import java.util.Stack;
import java.lang.Integer;
import ninf.basic.*;


public class NinfDimParam {
  NinfDimParamElem size;
  NinfDimParamElem start;
  NinfDimParamElem end;
  NinfDimParamElem step;

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

  public String toText(){
    String sizeStr = size.toText();
    String startStr = start.toText();
    String endStr = end.toText();  
    String stepStr = step.toText();  

    if (startStr == null && endStr == null && stepStr == null)
      return FormatString.format("[%s]", sizeStr);
    if (startStr == null && stepStr == null)
      return FormatString.format("[%s: %s]",
			       sizeStr, endStr);
    if (stepStr == null)
      return FormatString.format("[%s: %s, %s]",
			       sizeStr, startStr, endStr);
    return FormatString.format("[%s: %s, %s, %s]",
			       sizeStr, startStr, endStr, stepStr);
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

  ArrayShapeElem setupArrayShape(int iargs[])throws NinfTypeErrorException {
    ArrayShapeElem tmp = new ArrayShapeElem();
    tmp.size  = size. calc_real_val(iargs, 0);
    tmp.start = start.calc_real_val(iargs, 0);
    tmp.end   = end.  calc_real_val(iargs, tmp.size);
    tmp.step  = step. calc_real_val(iargs, 1);
    return tmp;
  }

  ArrayShapeElem setupArrayShape(int iargs[], int size_in_header)
    throws NinfTypeErrorException {
    ArrayShapeElem tmp = new ArrayShapeElem();
    tmp.size  = size. calc_real_val(iargs, 0, size_in_header);
    tmp.start = start.calc_real_val(iargs, 0, 0);
    tmp.end   = end.  calc_real_val(iargs, tmp.size, size_in_header);
    tmp.step  = step. calc_real_val(iargs, 1, 0);
    return tmp;
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
