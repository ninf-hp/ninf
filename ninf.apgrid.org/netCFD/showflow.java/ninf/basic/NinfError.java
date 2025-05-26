package ninf.basic;


public class NinfError {
  public static final int   NOERROR = 0;
  
  public static final int   CANTFINDSTUB = 1;
  public static final int   CANTEXECUTESTUB = 2;
  public static final int   STUBREMOVED = 3;
  public static final int   STUBALREADY = 4;

  public static final int   STUBREADFAIL = 5;
  public static final int   DATASENDFAIL = 6;
  public static final int   DATARECVFAIL = 7;
  
  public static final int   CANTCONNECTSERVER = 8;
  
  public static final int   CANTFINDSESSION = 9;
  public static final int   CANTALLOCSESSION = 10;

  public static final int   SERVER_CANNOT_ALLOC = 11;

  public static final int   HTTP_CANNOT_CONNECT = 12;
  public static final int   HTTP_NOT_FOUND  = 13;
  public static final int   HTTP_HEADER_READ_ERROR  = 14;
  public static final int   HTTP_UNKNOWN_FORMAT  = 15;
  public static final int   HTTP_FORMAT_ERROR = 16;
  public static final int   HTTP_TYPE_MISMATCH = 17;

  public static final int   NINF_PROTOCOL_ERROR = 18;
  public static final int   NINF_INTERNAL_ERROR = 19;
  public static final int   NINF_AUTHENTICATION_FAILED = 20;
  public static final int   NINF_ERROR_SESSION_ISNOT_DONE = 21;
  public static final int   NINF_ERROR_SESSION_CANCELED = 22;
  public static final int   NINF_ERROR_SESSION_FINISHED = 23;
  public static final int   NINF_FILE_CANNOT_OPEN = 24;
  public static final int   NINF_FILE_CANNOT_WRITE = 25;
  public static final int   MALFORMED_URL = 26;
  public static final int   DUMMY_9 = 27;

  public static final int   CIM_OK = 28;
  public static final int   CIM_ERROR_NOT_DEFINED = 29;
  public static final int   CIM_ERROR_TYPE_MISMATCH = 30;
  public static final int   CIM_ERROR_CANNOT_ALLOC = 31;
  public static final int   CIM_ERROR_ARRAY_BOUND = 32;

  public static final int   CPROXY_CANNOT_SCHEDULE = 33;

  static final String errorString[] = {
    "Normal execution.",
    "Can't find the stub.",
    "Can't execute the stub.",
    "Specified stub was removed.",
    "The stub was already executed.",
    
    "Stub read failed: May be died.", 
    "Data send failed.", 
    "Data recv failed.", 
    
    "Can't connect Server.",
    
    "Can't find Session.",
    "Can't alloc Session.",
    
    "Server side: cannot alloc enough memory.",
    
    "Server side: cannot connect the specified httpd.",
    "Server side: the URL is not found.",
    "Server side: cannot read the http header.",
    "Server side: cannot read the format.",
    "Server side: unexpected content format.",
    "Server side: the specified content does not match with the expected type.",
    
    "Protocol mismatch: check version no. of client and server",
    "Ninf Internal Error: Contact ninf person",
    "Authentication Failed: Server refused the connection.",
    "The session is not done, yet.",
    "The session was canceled.",
    "The session was already finished.",
    "The specified file cannot be opened.",
    "Failed to write to the specified file.",
    "The specified url is malformed.",
    "DUMMY9",
    
    "CIM OK ",
    "CIM : routine not defined",
    "CIM : in user routine: type mismatch",
    "CIM : in user routine: cannot alloc",
    "CIM : in user routine: array boundary over run ",

    "CProxy: Cannot Schedule."

  };

  public static String getString(int no){
    String tmp;
    try {
      tmp = errorString[no];
    } catch (ArrayIndexOutOfBoundsException e){
      tmp = "No such error no:" + no;
    }    
    return tmp;
  }
}
