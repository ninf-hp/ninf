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
  String makeVarName(int val){
    return (new Character((char)('A' + val))).toString();
  }

  public String toText(){
    Stack stack = new Stack();
    try {
      for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++){
	switch (type[i]){
	case NinfVal.VALUE_NONE:
	  stack.push("");
	  break;
	case NinfVal.VALUE_CONST:
	  stack.push(""+val[i]);
	  break;
	case NinfVal.VALUE_IN_ARG:
	  stack.push(makeVarName(val[i]));
	  break;	
	case NinfVal.VALUE_OP:
	  switch(val[i]){
	  case OP_VALUE_PLUS:
	    stack.push("("+(String)(stack.pop()) + " + " +(String)(stack.pop())+ ")");
	    break;
	  case OP_VALUE_MINUS:
	    {
	      String tmp = (String) (stack.pop());
	      stack.push("("+(String)(stack.pop()) + " - " + tmp + ")");
	    }
	    break;
	  case OP_VALUE_MUL:
	    stack.push("("+(String)(stack.pop()) + " * " +(String)(stack.pop())+ ")");
	    break;
	  case OP_VALUE_DIV:
	    {
	      String tmp = (String) (stack.pop());
	      stack.push("("+(String)(stack.pop()) + " / " + tmp + ")");
	    }
	    break;
	  case OP_VALUE_MOD:
	    {
	      String tmp = (String) (stack.pop());
	      stack.push("("+(String)(stack.pop()) + " % " + tmp + ")");
	    }
	    break;
	  case OP_VALUE_UN_MINUS:
	    stack.push("-(" + (String) (stack.pop()) + ")");
	    break;
	  case OP_VALUE_BEKI:{
	      String tmp = (String) (stack.pop());
	      stack.push("("+(String)(stack.pop()) + " ^ " + tmp + ")");
	    }
	    break;
	  case OP_VALUE_EQ:{
	    String tmp1 = (String) (stack.pop());
	    String tmp2 = (String) (stack.pop());
	    stack.push("(" + tmp1 + " == " + tmp2 + ")");
	    break;
	  }
	  case OP_VALUE_NEQ:{
	    String tmp1 = (String) (stack.pop());
	    String tmp2 = (String) (stack.pop());
	    stack.push("(" + tmp1 + " != " + tmp2 + ")");
	    break;
	  }
	  case OP_VALUE_GT:{
	    String tmp1 = (String) (stack.pop());
	    String tmp2 = (String) (stack.pop());
	    stack.push("(" + tmp2 + " > " + tmp1 + ")");
	    break;
	  }
	  case OP_VALUE_LT:{
	    String tmp1 = (String) (stack.pop());
	    String tmp2 = (String) (stack.pop());
	    stack.push("(" + tmp2 + " < " + tmp1 + ")");
	    break;
	  }
	  case OP_VALUE_GE:{
	    String tmp1 = (String) (stack.pop());
	    String tmp2 = (String) (stack.pop());
	    stack.push("(" + tmp2 + " >= " + tmp1 + ")");
	    break;
	  }
	  case OP_VALUE_LE:{
	    String tmp1 = (String) (stack.pop());
	    String tmp2 = (String) (stack.pop());
	    stack.push("(" + tmp2 + " <= " + tmp1 + ")");
	    break;
	  }
	  case OP_VALUE_TRY:{
	    String tmp1 = (String) (stack.pop());
	    String tmp2 = (String) (stack.pop());
	    String tmp3 = (String) (stack.pop());
	    stack.push("(" + tmp3 + " ? " + tmp2 + ":" + tmp1 + ")");
	    break;
	  }
	  
	  default:
	    throw new NinfTypeErrorException();
	  }
	  break;
	case NinfVal.VALUE_END_OF_OP:
	  return (String) (stack.pop());
	default:
	  throw new NinfTypeErrorException();
	}
      }
      return (String) (stack.pop());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public String toString(){
    String tmp = "";
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++)
      tmp += "[" + type[i] + ", " + val[i] + "]";
    return tmp;
  }

  public long calc(int arg[]) throws NinfTypeErrorException {
    Stack stack = new Stack();
    for (int i = 0; i < NINF_EXPRESSION_LENGTH; i++){
      switch (type[i]){
      case NinfVal.VALUE_NONE:
	stack.push(new Long(0));
	break;
      case NinfVal.VALUE_CONST:
	stack.push(new Long(val[i]));
	break;
      case NinfVal.VALUE_IN_ARG:
	stack.push(new Long(arg[val[i]]));
	break;	
      case NinfVal.VALUE_OP:
	switch(val[i]){
	case OP_VALUE_PLUS:
	  stack.push(new Long( ((Long)(stack.pop())).intValue() + 
			       ((Long)(stack.pop())).intValue()));
	  break;
	case OP_VALUE_MINUS:
	  {
	    int tmp = ((Long)(stack.pop())).intValue();
	    stack.push(new Long (((Long)(stack.pop())).intValue() -
				 tmp));
	  }
	break;
	case OP_VALUE_MUL:
	  stack.push(new Long (((Long)(stack.pop())).intValue() *
			       ((Long)(stack.pop())).intValue()));
	  break;
	case OP_VALUE_DIV:
	  {
	    int tmp = ((Long)(stack.pop())).intValue();
	    stack.push(new Long(((Long)(stack.pop())).intValue() /
				tmp));
	  }
	break;
	case OP_VALUE_MOD:
	  {
	    int tmp = ((Long)(stack.pop())).intValue();
	    stack.push(new Long(((Long)(stack.pop())).intValue() %
				tmp));
	  }
	break;
	case OP_VALUE_UN_MINUS:
	  stack.push(new Long(- (((Long)(stack.pop())).intValue())));
	  break;
	case OP_VALUE_BEKI:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long((int)Math.pow((double)tmp2,(double)tmp1)));
	  break;
	}
	case OP_VALUE_EQ:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long(tmp2 == tmp1 ? 1 : 0));
	  break;
	}
	case OP_VALUE_NEQ:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long(tmp2 != tmp1 ? 1 : 0));
	  break;
	}
	case OP_VALUE_GT:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long(tmp2 > tmp1 ? 1 : 0));
	  break;
	}
	case OP_VALUE_LT:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long(tmp2 < tmp1 ? 1 : 0));
	  break;
	}
	case OP_VALUE_GE:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long(tmp2 >= tmp1 ? 1 : 0));
	  break;
	}
	case OP_VALUE_LE:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long(tmp2 <= tmp1 ? 1 : 0));
	  break;
	}
	case OP_VALUE_TRY:{
	  int tmp1 = ((Long)(stack.pop())).intValue();
	  int tmp2 = ((Long)(stack.pop())).intValue();
	  int tmp3 = ((Long)(stack.pop())).intValue();
	  stack.push(new Long(tmp3 == 1 ? tmp2 : tmp1));
	  break;
	}
	
	default:
	  throw new NinfTypeErrorException();
	}
	break;
      case NinfVal.VALUE_END_OF_OP:
	return ((Long)(stack.pop())).intValue();
      default:
	throw new NinfTypeErrorException();
      }
    }
    return ((Long)(stack.pop())).intValue();
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
