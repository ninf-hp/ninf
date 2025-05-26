package ninf.client;


import java.io.*;
import ninf.basic.*;

public class NinfDimParamElem {
/*********************** NON STATIC VARIABLES ***********************/
  public int type;
  public int val;
  public int computed_val;
  NinfExpression exp;

/***********************  INSTANCE  CREATION  ***********************/
  public NinfDimParamElem(XDRInputStream istream) throws IOException {
    type = istream.readInt();
    val  = istream.readInt();
    exp  = new NinfExpression();

    if (type == NinfVal.VALUE_BY_EXPR) exp.read(istream);
  }
  public NinfDimParamElem(int ty, int va, NinfExpression ex){
    type = ty; 
    val  = va;
    exp  = ex;
  }
  public NinfDimParamElem(int ty, int va){
    type = ty; 
    val  = va;
    exp  = new NinfExpression();
  }
  public NinfDimParamElem(){
    type = 0; 
    val  = 0;
    exp  = new NinfExpression();
  }

  public NinfDimParamElem copy(){
    return new NinfDimParamElem(type, val, exp.copy());
  }

  public String toString(){
    return "("+type+","+ val+ (type == NinfVal.VALUE_BY_EXPR? (", " + exp): ""  )+ ")";
  }
/***********************     I/O  METHODS     ***********************/
  public void write(XDROutputStream ostream)  throws IOException {
    ostream.writeInt(type);
    ostream.writeInt(val);
    if (type == NinfVal.VALUE_BY_EXPR) exp.write(ostream);
  }
/***********************  NON STATIC METHODS  ***********************/
  public int calc_real_val(int arg[], int default_value) throws NinfTypeErrorException {
    return calc_real_val(arg, default_value, 0);
  }

  public int calc_real_val(int arg[], int default_value, int value_in_header) throws NinfTypeErrorException {
    switch(type){
    case NinfVal.VALUE_NONE:
      computed_val = default_value;
      break;
    case NinfVal.VALUE_CONST:
      computed_val = val;
      break;
    case NinfVal.VALUE_IN_ARG:
      computed_val = arg[val];
      break;
    case NinfVal.VALUE_BY_EXPR:
      computed_val = exp.calc(arg);
      break;
    case NinfVal.VALUE_IN_HEADER:
      computed_val = value_in_header;
      break;
    default:
      throw new NinfTypeErrorException();
    }
    return computed_val;
  }

  public void shift(int i){
    if (type == NinfVal.VALUE_IN_ARG)
      val += i;
    exp.shift(i);
  }

//================== FOR NINF CALC ===================
// 「VALUE_IN_ARG」（引数により設定される）かどうか
  public boolean IS_VALUE_IN_ARG(){return (type == NinfVal.VALUE_IN_ARG);}
// 「VALUE_CONST」（定数）かどうか
  public boolean IS_VALUE_CONST(){return (type == NinfVal.VALUE_CONST);}
// type が記述されているか
  public boolean IS_TYPE(){return (type > 0);}

  public boolean isUsedAsSize(int index){
    if (type == NinfVal.VALUE_IN_ARG && val == index)
      return true;
    if (type == NinfVal.VALUE_BY_EXPR)
      return exp.isUsedAsSize(index);
    return false;
  }

}


