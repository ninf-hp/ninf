package ninf.client;
import ninf.basic.*;

import java.io.*;
public class NinfVal {
  public static final int     UNDEF = 0;	/* undefined */
  public static final int     VOID = 1;
  public static final int     CHAR = 2;
  public static final int     SHORT = 3;
  public static final int     INT = 4;
  public static final int     LONG = 5;
  public static final int     LONGLONG = 6;
  public static final int     UNSIGNED_CHAR = 7;
  public static final int     UNSIGNED_SHORT = 8;
  public static final int     UNSIGNED = 9;
  public static final int     UNSIGNED_LONG = 10;
  public static final int     UNSIGNED_LONGLONG = 11;
  public static final int     FLOAT = 12;
  public static final int     DOUBLE = 13;
  public static final int     LONG_DOUBLE = 14;
  public static final int     STRING_TYPE = 15;
  public static final int     FUNC_TYPE = 16;  
  public static final int     BASIC_TYPE_END = 17;
  public static final int     MAX_DATA_TYPE = 17;

  public static final int       VALUE_NONE = 0;	/* default */
  public static final int       VALUE_CONST = 1;	/* default, give by constant */
  public static final int       VALUE_IN_ARG = 2;	/* specified by IN scalar paramter */
  public static final int       VALUE_BY_EXPR = 3; 	/* computed by interpreter */
  public static final int       VALUE_OP = 4;          /* operation code */
  public static final int       VALUE_END_OF_OP = 5;   /* end of expression */
  public static final int       VALUE_ERROR = -1;
  public static final int       VALUE_IN_HEADER = 6;   /* for netsolve*/
  
  static int DATA_TYPE_SIZE[];
  static {
    DATA_TYPE_SIZE = new int[MAX_DATA_TYPE];
    DATA_TYPE_SIZE[CHAR] = 1;
    DATA_TYPE_SIZE[SHORT] = 2;
    DATA_TYPE_SIZE[INT] = 4;
    DATA_TYPE_SIZE[LONG] = 4;
    DATA_TYPE_SIZE[LONGLONG] = 8;
    DATA_TYPE_SIZE[UNSIGNED_CHAR] = 1;
    DATA_TYPE_SIZE[UNSIGNED_SHORT] = 2;
    DATA_TYPE_SIZE[UNSIGNED] = 4;
    DATA_TYPE_SIZE[UNSIGNED_LONG] = 4;
    DATA_TYPE_SIZE[UNSIGNED_LONGLONG] = 8;
    DATA_TYPE_SIZE[FLOAT] = 4;
    DATA_TYPE_SIZE[DOUBLE] = 8;
    DATA_TYPE_SIZE[LONG_DOUBLE] = 16;
  }
}




