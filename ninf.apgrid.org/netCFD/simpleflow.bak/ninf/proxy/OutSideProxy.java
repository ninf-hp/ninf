package ninf.proxy;
import ninf.basic.*;
import ninf.client.*;
import java.io.*;
import java.util.Enumeration;

class OutSideProxy extends NServer {
  String myname;
  int myport;
  Proxy masterProxy;
  static final CommandRepresent acceptCommands[] = {new CommandRepresent("register", 2)};
  static NinfLog dbg = new NinfLog("OutSideProxy");
  
  CommandParser parser = new CommandParser(acceptCommands);
  
  OutSideProxy(String myname, int myport, Proxy masterProxy){
    this.myname = myname;
    this.myport = myport;
    this.masterProxy = masterProxy;
  }

  PortManager findPortManager(NinfServerStruct server){
    Enumeration pme = masterProxy.portManagers.elements();
    while(pme.hasMoreElements()){
      PortManager tmp = (PortManager)pme.nextElement();
      if (tmp.server.equals(server))
	return tmp;
    }
    return null;
  }

  PortManager getPortManager(NinfCommand com) throws CommandParseException{
    NinfServerStruct tmp = new NinfServerStruct(com.args[0], (new Integer(com.args[1])).intValue());
    PortManager portManager = findPortManager(tmp);
    if (portManager == null)
      portManager = masterProxy.addPortManager(com);
    return portManager;

  }

  void processConnection(DataInputStream is, PrintStream os) {
    try {
      String line;
      NinfCommand com = parser.readCommand(is);
      dbg.log("readed " + com);
      PortManager manager = getPortManager(com);
      if (manager != null)
	manager.registerToMetaServer(masterProxy.metaServer);
    } catch (NinfException e){
      dbg.log("Some error occurred: " + e);
    }
  }

  public void serviceRequest() {
    DataInputStream is = new DataInputStream(clientInput);
    PrintStream os = new PrintStream(clientOutput);
    dbg.log("["+myport+"] accept connection from " + clientSocket.getInetAddress());
    processConnection(is, os);
  }

  void start(){
    startServer(myport);
    dbg.log("[" +myport+"] start ..");
  }
}
