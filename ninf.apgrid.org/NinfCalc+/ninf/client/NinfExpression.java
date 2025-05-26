//
// NinfExpression.java
//

package ninf.client;

import java.io.IOException;
import java.util.Stack;
import java.lang.Integer;
import ninf.basic.*;

public class NinfExpression {
  public static final int OP_VALUE_PLUS  = 1;
  public static final int OP_VALUE_MINUS = 2;
  public static final int OP_VALUE_MUL   = 3;
  public static final int OP_VALUE_DIV   = 4;
  public static final int OP_VALUE_MOD   = 5;
  public static final int OP_VALUE_UN_MINUS = 6;
  public static final int OP_VALUE_BEKI = 7;
  public static final int OP_VALUE_EQ    = 8;    /*  ==  */
  public static final int OP_VALUE_NEQ   = 9;    /*  !=  */
  public static final int OP_VALUE_GT    = 10;   /*  >  */
  public static final int OP_VALUE_LT    = 11;   /*  <  */
  public static final int OP_VALUE_GE    = 12;   /*  >=  */
  public static final int OP_VALUE_LE    = 13;   /*  <=  */
  public static final int OP_VALUE_TRY   = 14;   /*  ? : */

  public static final int NINF_EXPRESSION_LENGTH = 20;

  public int type[];
  public int val[];

  public NinfExpression(){
    type = new int[NINF_EXPRESSION_LENGTH];
    val = new int[NINF_EXPRESSION_LENGTH];
  }

  public NinfExpression(int ty[], int va[]){
    type = ty;
    val = va;
  }

  public NinfExpression copy(){
    int ty[] = new int[NINF_EXPRESSION_LENGTH];
    int va[] = new int[NINF_EXPRESSION_LENGTH];
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++){
      ty[i] = type[i];
      va[i] = val[i];
    }
    return new NinfExpression(ty, va);
  }
  
  public void read(XDRInputStream is) throws IOException {
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++){
      type[i] = is.readInt();
      val[i] = is.readInt();
    }
  }

  public void write(XDROutputStream os) throws IOException {
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++){
      os.writeInt(type[i]);
      os.writeInt(val[i]);
    }
  }

  public String toString(){
    String tmp = "";
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++)
      tmp += "[" + type[i] + ", " + val[i] + "]";
    return tmp;
  }

  public int calc(int arg[]) throws NinfTypeErrorException {
    Stack stack = new Stack();
    try {
      for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++){
	switch (type[i]){
	case NinfVal.VALUE_NONE:
	  stack.push(new Integer(0));
	  break;
	case NinfVal.VALUE_CONST:
	  stack.push(new Integer(val[i]));
	  break;
	case NinfVal.VALUE_IN_ARG:
	  stack.push(new Integer(arg[val[i]]));
	  break;	
	case NinfVal.VALUE_OP:
	  switch(val[i]){
	  case OP_VALUE_PLUS:
	    stack.push(new Integer( ((Integer)(stack.pop())).intValue() + 
                                    ((Integer)(stack.pop())).intValue()));
	    break;
	  case OP_VALUE_MINUS:
	    {
	      int tmp = ((Integer)(stack.pop())).intValue();
	      stack.push(new Integer (((Integer)(stack.pop())).intValue() -
				      tmp));
	    }
	    break;
	  case OP_VALUE_MUL:
	    stack.push(new Integer (((Integer)(stack.pop())).intValue() *
				    ((Integer)(stack.pop())).intValue()));
	    break;
	  case OP_VALUE_DIV:
	    {
	      int tmp = ((Integer)(stack.pop())).intValue();
	      stack.push(new Integer(((Integer)(stack.pop())).intValue() /
				     tmp));
	    }
	    break;
	  case OP_VALUE_MOD:
	    {
	      int tmp = ((Integer)(stack.pop())).intValue();
	      stack.push(new Integer(((Integer)(stack.pop())).intValue() %
				     tmp));
	    }
	    break;
	  case OP_VALUE_UN_MINUS:
	    stack.push(new Integer(- (((Integer)(stack.pop())).intValue())));
	    break;
	  case OP_VALUE_BEKI:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    stack.push(new Integer(tmp2 ^ tmp1));
	    break;
	  }
	  case OP_VALUE_EQ:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    stack.push(new Integer(tmp2 == tmp1 ? 1 : 0));
	    break;
	  }
	  case OP_VALUE_NEQ:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    stack.push(new Integer(tmp2 != tmp1 ? 1 : 0));
	    break;
	  }
	  case OP_VALUE_GT:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    stack.push(new Integer(tmp2 > tmp1 ? 1 : 0));
	    break;
	  }
	  case OP_VALUE_LT:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    stack.push(new Integer(tmp2 < tmp1 ? 1 : 0));
	    break;
	  }
	  case OP_VALUE_GE:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    stack.push(new Integer(tmp2 >= tmp1 ? 1 : 0));
	    break;
	  }
	  case OP_VALUE_LE:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    stack.push(new Integer(tmp2 <= tmp1 ? 1 : 0));
	    break;
	  }
	  case OP_VALUE_TRY:{
	    int tmp1 = ((Integer)(stack.pop())).intValue();
	    int tmp2 = ((Integer)(stack.pop())).intValue();
	    int tmp3 = ((Integer)(stack.pop())).intValue();
System.out.println(tmp3 + " ? " +tmp2 + " : " + tmp1 );
	    stack.push(new Integer(tmp3 == 1 ? tmp2 : tmp1));
	    break;
	  }
	  
	  default:
	    throw new NinfTypeErrorException();
	  }
	  break;
	case NinfVal.VALUE_END_OF_OP:
	  return ((Integer)(stack.pop())).intValue();
	default:
	  throw new NinfTypeErrorException();
	}
      }
      return ((Integer)(stack.pop())).intValue();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(2);
    }
    return 0; /* dummy to avoid compile error */
  }

  public void shift(int sh){
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++){
      if (type[i] == NinfVal.VALUE_IN_ARG)
	val[i] += sh;
    }
  }

  public boolean isUsedAsSize(int index){
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++)
      if (type[i] == NinfVal.VALUE_IN_ARG && val[i] == index)
	return true;
    return false;
  }

}

// end of NinfExpression.java
