package ninf.metaserver;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;

class ProbeRoot {
  MetaServerRoot masterServer;
  DirectoryService service;
  Vector lPingers = new Vector();
  static NinfLog dbg = new NinfLog("ProbeRoot");
  int interval = 30;		

  ProbeRoot(MetaServerRoot masterServer){
    this.masterServer = masterServer;
    service = masterServer.directoryService;
    int tmpInterval = masterServer.masterServer.conf.loadInterval();
    if (tmpInterval > 0)
      interval = tmpInterval;
  }

  NinfStub getStub(NinfServerHolder server, FunctionName name)
  throws NinfException {
    return server.struct.connect().getStub(name.toString());
  }

  void registerServer(NinfServerHolder server) throws NinfException {
    LPinger pinger = new LPinger(server, interval);
    new Thread(pinger).start();
    lPingers.addElement(pinger);

    NinfServerConnection con = server.struct.connect();
    server.serverChar = con.getServerCharacter();
    dbg.log("Character of server:" + server + " is " + server.serverChar);
    int indexes[] = con.getIndexes("");
    NinfStub stubs[] = con.query(indexes);
    con.close();

    NinfServerHolder newServer = service.registerServer(server);

    for (int i = 0; i < stubs.length; i++)
      service.registerStub(newServer, stubs[i], indexes[i]);
  }

  

}
