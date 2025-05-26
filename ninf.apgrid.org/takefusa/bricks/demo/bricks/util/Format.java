package bricks.util;
import java.util.*;

public class Format {

    // %.(decimal)lf
    public static String format(double doubleValue, int decimal) {
	Double dblObj = new Double(doubleValue);
	String str = dblObj.toString();
	String e = new String("E");
	if (str.indexOf(e) >= 0) {
	    StringTokenizer st = new StringTokenizer(str);
	    String mantissa = st.nextToken("E");
	    int exponent = Integer.valueOf(st.nextToken("E")).intValue();
	    int absExponent = Math.abs(exponent);
	    String tmp = "";
	    for (int i = absExponent ; i > 0 ; i--) {
		tmp = tmp + "0";
	    }
	    if (exponent > 0) {
		mantissa = mantissa + tmp;
		//System.out.println("exponent = " + exponent + 
		//", mantissa = " + mantissa);
		int point = mantissa.indexOf(".");
		String value1 = mantissa.substring(0, point-1);
		String value2 = mantissa.substring(point+1, point+1+exponent);
		String value3 = mantissa.substring(point+1+exponent);
		str = value1 + value2 + "." + value3;
	    } else {
		mantissa = tmp + mantissa;
		//System.out.println("exponent = " + exponent + 
		//", mantissa = " + mantissa);
		int point = mantissa.indexOf(".");
		int index = point+exponent+1;
		//System.out.println("point = " + point + ", index = " + index);
		String value1 = mantissa.substring(0, point+exponent+1);
		String value2 = mantissa.substring(point+exponent+1, point);
		String value3 = mantissa.substring(point+1);
		int start = 0;
		for (int i = 0 ; i < value1.length()-1 ; i++) {
		    char[] c1 = new char[1];
		    c1[0] = value1.charAt(i);
		    String s1 = new String(c1);
		    char[] c2 = new char[1];
		    c2[0] = value1.charAt(i+1);
		    String s2 = new String(c2);
		    if ((s1.equals("0")) && (s2.equals("0"))) {
			start++;
		    } else {
			break;
		    }
		}
		str = value1.substring(start) + "." + value2 + value3;
	    }
	}

	int point = str.indexOf(".");
	int end = point + decimal + 1;
	if (end > str.length())
	    end = str.length();
	return str.substring(0, end);
    }

    // %.(decimal)f
    public static String format(float value, int decimal) {
	Float fltObj = new Float(value);
	String str = fltObj.toString();
	int point = str.indexOf(".");

	int end = point + decimal + 1;
	if (end > str.length())
	    end = str.length();
	return str.substring(0, end);
    }

    // for debug
    public static void main(String[] argv) {
	Format instance = new Format();
	double a = 1.2345678;
	System.out.println("a = " + a);
	System.out.println(instance.format(a, 3));

	double b = 1.2;
	System.out.println("b = " + b);
	System.out.println(instance.format(b, 3));

	double c = 5.8E-6;
	System.out.println("c = " + c);
	System.out.println(instance.format(c, 3));

	double d = 5.8E+6;
	System.out.println("d = " + d);
	System.out.println(instance.format(d, 3));
    }
}
