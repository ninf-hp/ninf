// NinfServervStruct.java

package ninf.client;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import ninf.basic.*;
import ninf.MetaServer.*;

/**
  Information about NinfServer
  now includes host, port.
  must includes distance, etc..
*/
public class NinfServerStruct {
  /** host name of NinfServer */
  public String host;
  /** port no of NinfServer */
  public int port;

  /** load value which assigned to the server*/
  int load = 0;

  public long rtt = 0;
  public int performance;

  public FunctionManager manager;

  public void addManager(FunctionManager manager){
    this.manager = manager;
  }

/***************** INSTANCE CREATION *******************/
  public NinfServerStruct(String str) {
    try {
      StringTokenizer st = new StringTokenizer(str, ":");
      host = st.nextToken();
      if (st.hasMoreTokens())
	port = Integer.valueOf(st.nextToken()).intValue();
      else
	port = NinfClient.DefaultPort;
    } catch (Exception e){
      host = "localhost";
      port = NinfClient.DefaultPort;
    }
    performance = 0;
  }
  public NinfServerStruct(String h, int p) {
    host = h; 
    port = p;
    performance = 0;
  }
  public NinfServerStruct(NinfPacketInputStream is) throws NinfIOException {
    host = is.readString(); 
    port = is.readInt();
    performance = 0;
  }
  public NinfServerStruct(String h, int p, int a_performance) {
    host = h; 
    port = p;
    performance = a_performance;
  }
  public void writeTo(NinfPacketOutputStream os) throws NinfIOException {
    os.writeString(host);
    os.writeInt(port);
    os.writeInt(load);
  }
  public void readFrom(NinfPacketInputStream is) throws NinfIOException {
    host = is.readString();
    port = is.readInt();
  }

  public NinfServerConnection connect() throws NinfException {
    return new NinfServerConnection(this);
  }


/***************** OTHER FUNCTIONS *******************/
  public String toString() {
    return (host + ":" + port);
  }
  public int hashCode(){
    return host.hashCode() + port;
  }
  public boolean equals(Object o){
    if (!(o instanceof NinfServerStruct))
      return false;
    if (host.equals(((NinfServerStruct)o).host) && 
	port ==(((NinfServerStruct)o).port))
      return true;
    return false;
  }

/***************** LOAD MANAGEMENT *******************/
  public synchronized int load(){
    return load;
  }
  public void loadDecrement(){
    boolean flag = false;
    synchronized (this){  // to avoid dead lock
      load--;
      System.err.println(this + ": load is decremented to " + load);
      if (load <= 0)
	flag = true;
    }
    if (flag)
      notifyFunctions();
  }

  public synchronized void loadIncrement(){
    load++;
    System.err.println(this + ": load is incremented to " + load);
  }

  public synchronized void loadAdd(int l){
    load += l;
    System.err.println(this + ": load is added to " + load);
  }

  public synchronized boolean getLock(){
    System.err.println("getting lock: " + Thread.currentThread());
    while (load > 0)
      return false;
    loadIncrement();
    System.err.println("got lock: " + Thread.currentThread());
    return true;
  }

  /* Function Management */
  
  Vector funcs;

  public void registerFunction(ServerNotifiable func, int index){
    if (funcs == null)
      funcs = new Vector();
    funcs.addElement(new FunctionIndex(func, index));
  }
  
  public void notifyFunctions(){
//    if (funcs != null)
//      for (int i = 0; i < funcs.size(); i++){
//	FunctionIndex tmp = (FunctionIndex)funcs.elementAt(i);
//	tmp.func.notifyFree(this, tmp.index);
//      }
    if (manager != null) 
      manager.serverReleased();
  }

}



// end of NinfServerStruct.java


