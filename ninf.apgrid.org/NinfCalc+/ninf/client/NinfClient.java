package ninf.client;

import java.io.*;
import java.util.Vector;
import ninf.basic.*;

class TestCallback implements CallbackFunc{
  public void callback(Vector args){
    double d = ((double[])args.elementAt(0))[0];
    System.out.println("Callbacked " + d );
  }
}
class StringDisplay implements CallbackFunc{
  public void callback(Vector args){
    String s = ((String[])args.elementAt(0))[0];
    System.out.println("Callbacked " + s );
  }
}

public class NinfClient {
  protected NinfServerStruct struct;
  protected NinfServerConnection con;

  static String DefaultServer = null;
  static int    DefaultPort = 3000;

  public NinfClient(String h, int p) throws NinfIOException{
    String tmp = h;
    if (h == null){
      tmp = System.getProperty("HOST");
    }
    struct = new NinfServerStruct(tmp, p);
  }

  public NinfClient() throws NinfIOException{
    struct = new NinfServerStruct(DefaultServer, DefaultPort);
  }

  public static String[] parseArg(String arg[]){
    Vector tmpV = new Vector();
    int index = 0;
    for (int i = 0; i < arg.length; i++){
      if (arg[i].equalsIgnoreCase("-server"))
	DefaultServer = arg[++i];
      else if (arg[i].equalsIgnoreCase("-port"))
	DefaultPort = Integer.valueOf(arg[++i]).intValue();
      else 
	tmpV.addElement(arg[i]);
    }
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }

  public void connectServer()throws NinfException{
    con = new NinfServerConnection(struct);
  }

  public void disconnect(){
    if (con != null) con.close();
    con = null;
  }

  public void call(String func, Vector args) throws NinfException{
    long startTime = System.currentTimeMillis();  
    this.connectServer();
    long connected = System.currentTimeMillis();  
    con.call(func, args);
    this.disconnect();
    long endTime = System.currentTimeMillis();
    System.out.println("NinfCall setup takes" + 
      (connected - startTime) + " msec.");
    System.out.println("NinfCall call takes" + 
      (endTime - connected) + " msec.");
  }

/*  public static void test1(String args[]){
    NinfClient tmp = new NinfClient(args[0], 3000);
    NinfServerConnection con = new NinfServerConnection(tmp.server);
    NinfStub stub = con.getStub("sin");
    System.out.println(stub);
    tmp.disconnect();
  }
*/

  public static void main(String args[]){
//    String strings[] = {"caller0","caller1","caller2","caller3","caller4"};
    String string[] = new String[1];
    
    try {
      (new NinfClient((args.length > 0? args[0]: null), 3000)).
	callWith("strtest", "TEST_CALLING", string);
      System.out.println("Got " + string[0]);
    } catch (NinfException e){}
  }

  public static void main3(String args[]){
    try {
      (new NinfClient((args.length > 0? args[0]: null), 3000)).
	callWith("callbacktest", new Double(10.0), new TestCallback());
/*      NinfClient tmp = new NinfClient(args[0], 3000);

      Vector arg = new Vector();
      arg.addElement(new Double(10.0));
      arg.addElement(new TestCallback());

      tmp.call("callbacktest", arg);
*/
    } catch (NinfException e){}
  }

  public void callWith(String name, Object a1) 
      throws NinfException{
      Vector arg = new Vector();
      arg.addElement(a1);
      this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2) 
      throws NinfException{
      Vector arg = new Vector();
      arg.addElement(a1);       arg.addElement(a2);
      this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2, Object a3) 
      throws NinfException{
      Vector arg = new Vector();
      arg.addElement(a1);  arg.addElement(a2); arg.addElement(a3);
      this.call(name, arg);
  }

  public void callWith(String name, Object a1, Object a2, Object a3, Object a4) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    this.call(name, arg);
  }

  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5);
    this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6);
    this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7);
    this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9)
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);
    this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9, Object a10)
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);arg.addElement(a10);
    this.call(name, arg);
  }
  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9, Object a10, Object a11)
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);arg.addElement(a10);arg.addElement(a11);
    this.call(name, arg);
  }

  public void callWith(String name, Object a1, Object a2, Object a3, Object a4, 
		                    Object a5, Object a6, Object a7, Object a8,
		                    Object a9, Object a10, Object a11, Object a12) 
    throws NinfException{
    Vector arg = new Vector();
    arg.addElement(a1); arg.addElement(a2); arg.addElement(a3); arg.addElement(a4);
    arg.addElement(a5); arg.addElement(a6); arg.addElement(a7); arg.addElement(a8);
    arg.addElement(a9);arg.addElement(a10);arg.addElement(a11);arg.addElement(a12);
    this.call(name, arg);
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
