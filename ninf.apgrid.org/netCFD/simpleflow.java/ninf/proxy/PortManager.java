package ninf.proxy;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;
import java.util.Hashtable;


/** PortManager listens for a specified port and forwards requests */

class PortManager extends NServer{

  NinfServerStruct server;
  Proxy masterProxy;
  int port;
  static NinfLog dbg = new NinfLog("PortManager");

  PortManager(NinfServerStruct server, int port, Proxy masterProxy){
    this.server = server;
    this.port = port;
    this.masterProxy = masterProxy;
  }

  PortManager(NinfServerStruct server, Proxy masterProxy){
    this(server, 0, masterProxy); /*  anonymous port */
  }

  void processConnection(XDRInputStream is, XDROutputStream os){
    NinfServerConnection con;
    try {
      con = server.connect();
      con.startForward(is, os);
    } catch (NinfException e){
      dbg.log("["+port+"] [" + server + "] connection failed." );      
      try {
	NinfPacket.getErrorPacket(NinfError.CANTCONNECTSERVER).write(os);
      } catch (NinfException e1){}
      // inform metaServer;
    }
  }

  void registerToMetaServer(MetaServerReference metaServer){
    try {
      String newArgs[] = new String[2];
      newArgs[0] = masterProxy.myname;
      newArgs[1] = "" + port;
      if (metaServer == null){
	dbg.log("no metaserver specified");
	return;
      }
      MetaServerConnection con = masterProxy.metaServer.connect();
      NinfCommand com = new NinfCommand("register", newArgs);
      con.os.println(com);      
      con.close();
    } catch (NinfException e){
      dbg.log("Some error occurred: " + e);
    }
  }

  public void serviceRequest() {
    XDRInputStream is = new XDRInputStream(clientInput);
    XDROutputStream os = new XDROutputStream(clientOutput);
    dbg.log("["+port+"] accept connection from " + clientSocket.getInetAddress());
    processConnection(is, os);
  }

  int start() {
    this.startServer(port);
    port = masterSocket.getLocalPort();
    dbg.log("["+port+"] for Server [" + server + "] startup .." );
    return port;
  }
}
