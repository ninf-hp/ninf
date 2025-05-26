package bricks.util;

/** Emuration for "printf" library function in "C" */
public class FormatString{
  /* usage:
    System.out.print(FormatString.format("%f\n", new Double(1.2))); */
    
  static void formatnext(StringBuffer tmp, char form[], int index, 
		     Object[] objs, int oindex){
    try {
      if (form[index] == '%'){
	tmp.append("%");
	format(tmp, form, index+1, objs, oindex);
	return;
      }
      FormatSpecifier spec = new FormatSpecifier();
      if (form[index] == '+'){
	index++;
      } else if (form[index] == '-'){
	spec.isleft = true;
	index++;
      }
      if (form[index] == '0'){
	spec.pad = '0';
	index++;
      }

      int mode = 1;  /* mode = 1:width,2:precesion */
      int val = -1;
      
      for (int i = index; i< form.length; i++){
	char ch = form[i];
	switch (ch){
	case '0': case '1': case '2': case '3': case '4':
	case '5': case '6': case '7': case '8': case '9':      
	  if (val == -1)
	    val = 0;
	  val = val * 10 + (ch - '0');
	  break;
	case '.':	
	  if (mode == 1){
	    spec.width = val;
	    mode = 2;
	    val = -1;
	    continue;
	  } else {
	    return;
	  }
	case 'l':
	  continue;
	case 'd': case 'f': case 's': case 'c': case 'e': case 'E': case 'x':
	  if (val != -1){
	    if (mode == 1)
	      spec.width = val;
	    if (mode == 2)
	      spec.precision = val;
	  }
	  spec.mode = ch;
	  spec.format(tmp, objs[oindex++]);
	  format(tmp, form, i + 1, objs, oindex);
	  return;
	default:
	  tmp.append(" unsuported " + ch);
	  return;
	}
      
      }

    } catch (ArrayIndexOutOfBoundsException e){
      tmp.append('%');
      for (int i = index; i< form.length; i++)
	tmp.append(form[i]);
    }
  }
  
  static void format(StringBuffer tmp, char form[], int index, 
		     Object[] objs, int oindex){
    
    for (int i = index; i< form.length; i++){
      if (form[i] == '%'){
	formatnext(tmp, form, i+1, objs, oindex);
	return;
      }
      tmp.append(form[i]);
    }
  }

  public static String format(String formstr, Object[] objs){
    StringBuffer tmp = new StringBuffer();
    char chars[] = formstr.toCharArray();
    format(tmp, chars, 0, objs, 0);
    return tmp.toString();
  }


  public static String format(String formstr){
    Object objs[] = new Object[0];
    return format(formstr, objs);
  }

  public static String format(String formstr, Object obj0){
    Object objs[] = new Object[1];
    objs[0] = obj0;
    return format(formstr, objs);
  }
  public static String format(String formstr, Object obj0, Object obj1){
    Object objs[] = new Object[2];
    objs[0] = obj0; objs[1] = obj1;
    return format(formstr, objs);
  }
  public static String format(String formstr, Object obj0, Object obj1, Object obj2){
    Object objs[] = new Object[3];
    objs[0] = obj0; objs[1] = obj1; objs[2] = obj2;
    return format(formstr, objs);
  }
  public static String format(String formstr, Object obj0, Object obj1, Object obj2, 
		       Object obj3){
    Object objs[] = new Object[4];
    objs[0] = obj0; objs[1] = obj1; objs[2] = obj2; 
    objs[3] = obj3;
    return format(formstr, objs);
  }
  public static String format(String formstr, Object obj0, Object obj1, Object obj2, 
		       Object obj3, Object obj4){
    Object objs[] = new Object[5];
    objs[0] = obj0; objs[1] = obj1; objs[2] = obj2; 
    objs[3] = obj3; objs[4] = obj4;
    return format(formstr, objs);
  }
  public static String format(String formstr, Object obj0, Object obj1, Object obj2, 
		       Object obj3, Object obj4, Object obj5){
    Object objs[] = new Object[6];
    objs[0] = obj0; objs[1] = obj1; objs[2] = obj2; 
    objs[3] = obj3; objs[4] = obj4; objs[5] = obj5;
    return format(formstr, objs);
  }

  /**************  Main for TEST  ********************/

  public static void main(String args[]){
    /*    System.out.print(
FormatString.format("test %s, %d, %.2e, %.2e, %.9e\n", "saru", new Integer(123), new Double(-111.2456), new Double(-0.1112456), new Double(-0.1112456)));
    System.out.print(
FormatString.format("%10.2lfsaru\n", new Double(21.23456)));
    System.out.print(
FormatString.format("%-010.2lfsaru\n", new Double(21.23456)));

    for (int i = 0; i < 0xff; i+= 0x01){
      System.out.print(
	FormatString.format("%02x, %c \n", new Integer(i), new Integer(i)));
    }
    */
    System.out.print(
    FormatString.format("%9lf\n", new Double(1.000440500093)));

  }

  private static class FormatSpecifier{
    char mode;
    int width = -1;
    int precision = -1;
    boolean isleft = false;
    char pad = ' ';
    
    long getLong(Object obj){
      long val = 0;
      if (obj instanceof Double)
	val = ((Double)obj).longValue();
      if (obj instanceof Float)
	val = ((Float)obj).longValue();
      if (obj instanceof Integer)
	val = ((Integer)obj).intValue();
      if (obj instanceof Long)
	val = ((Long)obj).longValue();
      //    if (obj instanceof Short)
      //      val = ((Short)obj).longValue();
      if (obj instanceof Character)
	val = ((Character)obj).charValue();
      return val;
    }
    
    void formatHex(StringBuffer tmp, Object obj){
      long val = getLong(obj);
      tmp.append(""+ (Long.toString(val, 16)) );
    }
    
    void formatInt(StringBuffer tmp, Object obj){
      long val = getLong(obj);
      tmp.append(""+val);
    }
    
    void formatChar(StringBuffer tmp, Object obj){
      long val = getLong(obj);
      Character ch = new Character((char)val);
      tmp.append(ch.toString());
    }
    
    double getDouble(Object obj){
      double val = 0;
      if (obj instanceof Double)
	val = ((Double)obj).doubleValue();
      if (obj instanceof Float)
	val = ((Float)obj).doubleValue();
      if (obj instanceof Integer)
	val = ((Integer)obj).intValue();
      if (obj instanceof Long)
	val = ((Long)obj).longValue();
      //    if (obj instanceof Short)
      //      val = ((Short)obj).longValue();
      if (obj instanceof Character)
	val = ((Character)obj).charValue();
      return val;
    }
    
    void formatFloatSub(StringBuffer tmp, double val){
      if (precision == -1)
	precision = 6;
      if (val < 0){
	tmp.append("-");
	val = Math.abs(val);
      }
      double hosei = 0.5;
      for (int i = 0; i < precision; i++)
	hosei /= 10;
      val += hosei;
      
      long ltmp = (long)val;
      tmp.append(""+ltmp);
      if (precision >= 1)
	tmp.append(".");
      val -= ltmp;
      for (int i = 0; i < precision; i++){
	val *= 10;
	ltmp = (long)val;
	tmp.append(""+ltmp);
	val -= ltmp;
      }
    }
    
    void formatFloat(StringBuffer tmp, Object obj){
      double val = getDouble(obj);
      formatFloatSub(tmp, val);
    }
    
    void formatFloatE(StringBuffer tmp, Object obj){
      double val = getDouble(obj);
      int exp = 0;
      
      while (val >= 10.0 || val <= -10.0){
	exp++;
	val /= 10.0;
      }
      while (val < 1.0 && val > -1.0){
	exp--;
	val *= 10.0;
      }
      
      formatFloatSub(tmp, val);
      tmp.append(mode + ((exp > 0) ? "+" : "")  + exp);
    }
    
    
    void formatString(StringBuffer tmp, Object obj){
      String str = obj.toString();
      if (precision < 0 || precision >= str.length())
	tmp.append(str);
      else
	tmp.append(str.substring(0, precision));
    }
    
    
    
    void format(StringBuffer sb, Object obj){
      StringBuffer tmp = new StringBuffer();
      switch(mode){
      case 'd':
	formatInt(tmp, obj);
	break;
      case 's':
	formatString(tmp, obj);
	break;
      case 'c':
	formatChar(tmp, obj);
	break;
      case 'f':
	formatFloat(tmp, obj);
	break;
      case 'x':
	formatHex(tmp,obj);
	break;
      case 'e':
      case 'E':
	formatFloatE(tmp, obj);
	break;
      }

      if (tmp.length() < width || width < 0){
	if (isleft)
	  sb.append(tmp);	
	for (int i = 0; i < width - tmp.length(); i++)
	  sb.append(pad);
	if (!isleft)
	  sb.append(tmp);	
      } else {
	sb.append(tmp);	
      }       
    }
  }
  
}

