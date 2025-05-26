package ninf.client;

import java.io.*;
import java.util.Vector;
import ninf.basic.*;

public class NinfClient {
  protected NinfServerStruct struct;
  protected NinfServerConnection con;
  OutputStream stdoutStream, stderrStream;

  static String DefaultServer = null;
  static int    DefaultPort = 3000;

  public NinfClient(String h, int p) throws NinfIOException{
    String tmp = h;
    if (h == null){
      tmp = System.getProperty("HOST");
    }
    struct = new NinfServerStruct(tmp, p);
    init();
  }

  public NinfClient() throws NinfIOException{
    struct = new NinfServerStruct(DefaultServer, DefaultPort);
    init();
  }

  private void init(){
    stdoutStream = System.out;
    stderrStream = System.err;
  }

  public static void verbose(){
    NinfLog.verbose();
  }
  public static void quiet(){
    NinfLog.quiet();
  }

  public void setStdoutStream(OutputStream stdoutStream){
    this.stdoutStream = stdoutStream;
  }
  public void setStderrStream(OutputStream stderrStream){
    this.stderrStream = stderrStream;
  }

  public static String[] parseArg(String arg[]){
    Vector tmpV = new Vector();
    int index = 0;
    for (int i = 0; i < arg.length; i++){
      if (arg[i].equalsIgnoreCase("-server"))
	DefaultServer = arg[++i];
      else if (arg[i].equalsIgnoreCase("-port"))
	DefaultPort = Integer.valueOf(arg[++i]).intValue();
      else if (arg[i].equalsIgnoreCase("-debug"))
	verbose();
      else if (arg[i].equalsIgnoreCase("-quiet"))
	quiet();
      else 
	tmpV.addElement(arg[i]);
    }
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }

  public void connectServer()throws NinfException{
    con = new NinfServerConnection(struct, stdoutStream, stderrStream);
  }

  public void disconnect(){
    if (con != null) con.close();
    con = null;
  }

  public NinfExecInfo call(String func, Vector args) throws NinfException{
    this.connectServer();
    NinfExecInfo execInfo = con.call(func, args);
    this.disconnect();
    return execInfo;
  }

  public NinfExecInfo callWith(String name, Object a1) 
      throws NinfException{
      Vector arg = new Vector();
      arg.addElement(a1);
      return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2) 
      throws NinfException{
      Vector arg = new Vector();
      arg.addElement(a1);       arg.addElement(a2);
      return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3) 
      throws NinfException{
      Vector arg = new Vector();
      arg.addElement(a1);  arg.addElement(a2); arg.addElement(a3);
      return this.call(name, arg);
  }

  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    return this.call(name, arg);
  }

  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5);
    return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6);
    return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7);
    return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9)
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);
    return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9, Object a10)
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);arg.addElement(a10);
    return this.call(name, arg);
  }
  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9, Object a10, Object a11)
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);arg.addElement(a10);arg.addElement(a11);
    return this.call(name, arg);
  }

  public NinfExecInfo callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9, Object a10, Object a11, Object a12) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);arg.addElement(a10);arg.addElement(a11);arg.addElement(a12);
    return this.call(name, arg);
  }


// For ninf calc
//《getStubWithRdim()》
// 「行列サイズを示す引数 ←→ 行列」間の双方向の検索可能な 
//  ルーチンのインターフェース情報を得る

  public NinfStub getStubWithRdim(String func) throws NinfException{
    connectServer();
    NinfStub tmp = con.getStub(func);
    if(tmp == null) return null;
    tmp.inputRRetrieval();
    return tmp;
  }


}
