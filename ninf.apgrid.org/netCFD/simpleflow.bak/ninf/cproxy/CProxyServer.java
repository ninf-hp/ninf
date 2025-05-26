package ninf.cproxy;
import ninf.basic.*;
import ninf.client.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class CProxyServer extends NServer {
  String myname;
  int myport;
  MetaServerReference metaServer;
  CProxy masterProxy;
  static NinfLog dbg = new NinfLog("CProxyServer");
  StubCache stubCache;
  ServerTable serverTable;
  int callSerial;
  PrintStream throughputLogStream;
    
  CProxyServer(String myname, int myport, CProxy masterProxy, MetaServerReference metaServer){
    this.myname = myname;
    this.myport = myport;
    this.masterProxy = masterProxy;
    this.metaServer = metaServer;
    stubCache = new StubCache(this);
    String throughputLogName = masterProxy.conf.getOneArg("throughputlog");
    if (throughputLogName != null){
      try {
	throughputLogStream = new PrintStream(new FileOutputStream(throughputLogName));
      } catch(IOException e) {
	dbg.log("Can't open throughputlog file: " + throughputLogName);
      }
    }
    serverTable = new ServerTable(this);
  }

  ServerIndex schedule(NinfStub stub, CallContext callContext, int serial)
    throws NinfException{
      ProxyScheduler proxyScheduler = new ProxyScheduler(serverTable, metaServer);
      return proxyScheduler.schedule(stub, callContext, serial);
  }

  public void addNewServer(NinfServerStruct server){
    serverTable.addNewServer(server);
  }

  void sendBackStub(Callable callable, XDROutputStream os) throws NinfException {
    NinfPacketOutputStream nos = 
      new NinfPacketOutputStream(os, NinfPktHeader.NINF_PKT_RPY_STUB_INFO, callable.index, 0);
    nos.write(callable.getStub().toByteArray());
    nos.flush();
  }

  void processReqStubInfo(NinfPacket pkt, XDROutputStream os) throws NinfException {
    try {
      String entry = pkt.readString();
      Callable stubCallable = stubCache.getStubCallable(new FunctionName(entry));
      sendBackStub(stubCallable, os);
      return;
    } catch (NinfIOException e) {
      dbg.println("processReqStubInfo: " + e);
      e.printStackTrace();
      return;
    }
  }

  void processReqCall(NinfPacket pkt, XDRInputStream is, XDROutputStream os)  
  throws NinfException {
    dbg.println("processReqCall()");
    try {
      Callable callable = stubCache.getCallable(pkt.hdr.arg1);
      callable.call(pkt, is, os);
    } finally {
      //      os.flush();
    }
  }

  void processGetTime(NinfPacket pkt, XDRInputStream is, XDROutputStream os)  
  throws NinfException {
    dbg.println("processGetTime()");
    try {
      NinfPacketOutputStream pos = 
	new NinfPacketOutputStream(os, NinfPktHeader.NINF_PKT_RPY_GETTIME);
      pos.writeDouble(System.currentTimeMillis() / 1000.0);
      pos.flush();
    } finally {
      //      os.flush();
    }
  }

  /** dispatch jobs according to packet code */
  public boolean dispatchNinfRequest(XDRInputStream is, XDROutputStream os) {
    boolean ret = true;
    NinfPacket pkt = null;
    try {
      pkt = new NinfPacket(is);
    } catch (NinfIOException e) {
      //      dbg.println("dispatchNinfRequst: " + e);
      //e.printStackTrace(dbg.os);
      return false;
    }
    try {
      dbg.log("["+myport+"] accept connection from " + clientSocket.getInetAddress());
      dbg.println("CProxy Read Pkt" + pkt.hdr);
      switch (pkt.hdr.code) {
      case NinfPktHeader.NINF_PKT_REQ_STUB_INFO:
	processReqStubInfo(pkt, os);
	break;

	//      case NinfPktHeader.NINF_PKT_REQ_COMP:
	//      processReqCompound(pkt, is, os);
	//      break;

      case NinfPktHeader.NINF_PKT_REQ_CALL:
	processReqCall(pkt, is, os);
	break;
      case NinfPktHeader.NINF_PKT_GETTIME:
	processGetTime(pkt, is, os);
	break;
	//      case NinfPktHeader.NINF_PKT_KILL:
	//	processKill(pkt);
	//	break;
      default:
	dbg.log("Unknown Coded Packet " + pkt.hdr.code);
	ret = false;
	break;
      }
    } catch (NinfException e) {
      dbg.println("exception occured in execution " + pkt.hdr.code);
      e.printStackTrace(dbg.os);
      int errorNo = 0;
      if (e instanceof NinfErrorException)
	errorNo = ((NinfErrorException)e).errorNo;
      NinfPacketOutputStream pos =
	new NinfPacketOutputStream(os, NinfPktHeader.NINF_PKT_ERROR, errorNo, 0);
      try {pos.flush();} catch(Exception ie){
	dbg.println("Can't return error pkt");
      }
      ret = false;
    }
    return ret;
  }

  public void serviceRequest() {
    XDRInputStream is = new XDRInputStream(clientInput);
    XDROutputStream os = new XDROutputStream(clientOutput);
    
    while (dispatchNinfRequest(is, os))
      ;
  }

  synchronized int getSerial(){
    return callSerial++;
  }

  void start(){
    startServer(myport);
    dbg.log("[" +myport+"] start ..");
  }
}
