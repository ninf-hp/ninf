package ninf.basic;
import ninf.basic.*;
import ninf.client.*;


import java.io.*;

public class ForeServer {
  XDRInputStream is; // InputStream from client
  XDROutputStream os; // OutputStream to client
  NinfServerConnection currentConnection; // connection to server
  ShadowServer shadow;
  static NinfLog dbg = new NinfLog("Fore");
  boolean simple = false;

  public ForeServer(XDRInputStream arg_is, XDROutputStream arg_os){
    is = arg_is;
    os = arg_os;
  }

  public void run(NinfPacket pkt, NinfServerConnection con) throws NinfIOException {
    currentConnection = con;
    pkt.write(currentConnection.os);
    shadow = new ShadowServer(currentConnection.is, os);
    serviceRequest();
  }

  public void run(NinfServerConnection con) throws NinfIOException {
    currentConnection = con;
    shadow = new ShadowServer(currentConnection.is, os);
    serviceRequest();
  }

  public void simple(){
    simple = true;
  }

  boolean dispatchNinfRequest() {
    NinfPacket pkt = null;
    boolean ret = true;

    try {
      // read packet from Client
      pkt = new NinfPacket(is);
      /* System.out.println(pkt);*/

    } catch (NinfIOException e) {
      dbg.println("ForeServer fail to read packet: "+e);
      return false;
    }
    if (simple){
      try {                 /* SIMPLE MODE */
	pkt.write(currentConnection.os);
	//	dbg.println(pkt.hdr);
	return true;
      } catch (NinfIOException e) {
	dbg.println(" fail to write packet: "+e);
	stop();
	return false;
      }
    }

    try {
      switch (pkt.hdr.code) {
      case NinfPktHeader.NINF_PKT_TO_STUB:
	dbg.println("NINF_PKT_TO_STUB");
	processToStub(pkt);
	break;
      case NinfPktHeader.NINF_PKT_KILL:      
	dbg.println("NINF_PKT_KILL");
	processKill(pkt);
	return false;
      default:
	dbg.println("Unknown Coded Packet " + pkt.hdr.code);
	ret = false;
	break;
      }
    } catch (Exception e) {
      dbg.println("exception occured in execution " + pkt.hdr.code);
      e.printStackTrace(System.err);
      return false;
    }
    return ret;
  }

  void serviceRequest() {
    while (dispatchNinfRequest())
     ;
    try {
      is.close();
      stop();
    } 
    catch (IOException e){}
  }

  void processKill(NinfPacket pkt)  throws NinfIOException {
    if (currentConnection == null){
      dbg.println("processKill: protocol error");
      return;
    }
    // send NINF_PKT_KILL packet to Server
    pkt.write(currentConnection.os);
    // close Connection
    currentConnection.close();
    currentConnection = null;
    // stop forwarder : Server to Cilent 
    if (shadow != null)
      shadow.stop();
    shadow = null;
  }

  public void stop(){
    if (currentConnection != null)
      currentConnection.close();
    // stop forwarder : Server to Cilent 
    if (shadow != null){
      shadow.stop();
      shadow = null;
    }
  }

  void processToStub(NinfPacket pkt) throws NinfIOException {
    // forward NINF_STUB_TO_STUB packet from Client to Server
    pkt.write(currentConnection.os);
  }
}

// end of ForeServer.java
