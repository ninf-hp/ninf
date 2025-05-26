// NinfServervStruct.java

package ninf.client;

import java.io.IOException;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.NoSuchElementException;
import ninf.basic.*;

/**
  Information about NinfServer
  now includes host, port.
  must includes distance, etc..
*/
public class NinfServerStruct {
  /** host name of NinfServer */
  public String host;
  public InetAddress address;

  /** port no of NinfServer */
  public int port;

/***************** INSTANCE CREATION *******************/
  public NinfServerStruct(String str) {
    try {
      StringTokenizer st = new StringTokenizer(str, ":");
      host = st.nextToken();
      if (st.hasMoreTokens())
	port = Integer.valueOf(st.nextToken()).intValue();
      else
	port = NinfClient.DefaultPort;
    } catch (NoSuchElementException e){
      host = "localhost";
      port = NinfClient.DefaultPort;
    }
    initInetAddr();
  }
  public NinfServerStruct(String h, int p) {
    host = h; 
    port = p;
    initInetAddr();
  }
  public NinfServerStruct(String h, String port) {
    host = h; 
    this.port = new Integer(port).intValue();
    initInetAddr();
  }
  public NinfServerStruct(NinfPacketInputStream is) throws NinfException {
    host = is.readString(); 
    port = is.readInt();
    initInetAddr();
  }
  void initInetAddr(){
    try {
      address = InetAddress.getByName(host);
    } catch (UnknownHostException e){
      address = null;
    } catch (SecurityException e){
      address = null;
    }
  }
  
  public void writeTo(NinfPacketOutputStream os) throws NinfException {
    os.writeString(host);
    os.writeInt(port);
  }
  public void readFrom(NinfPacketInputStream is) throws NinfException {
    host = is.readString();
    port = is.readInt();
  }
  public void readFrom(NinfCommand com){
    host = com.args[0];
    port = (new Integer(com.args[1])).intValue();
  }


  public NinfServerConnection connect() throws NinfException {
    return new NinfServerConnection(this);
  }


/***************** OTHER FUNCTIONS *******************/
  public String toString() {
    return (host + ":" + port);
  }

  public NinfCommand toCommand() {
    return new NinfCommand("serverInfo", host, ""+port);
  }

  public int hashCode(){
    return host.hashCode() + port;
  }
  public boolean equals(Object o){
    if (!(o instanceof NinfServerStruct))
      return false;
    NinfServerStruct s = (NinfServerStruct)o;
    if (address != null && s.address != null)
      if (address.equals(s.address) && port == s.port)
	return true;
    if (host.equals(s.host) && port == s.port)
      return true;
    return false;
  }
}



// end of NinfServerStruct.java


