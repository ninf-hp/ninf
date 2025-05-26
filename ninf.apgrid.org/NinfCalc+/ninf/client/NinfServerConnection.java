//
// NinfServerConnection.java
//

package ninf.client;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.Vector;
import ninf.basic.*;
import ninf.MetaServer.*;

public class NinfServerConnection implements Stoppable{

  NinfServerStruct struct;
  public Socket s;
  public XDRInputStream is;
  public XDROutputStream os;
  public int currentIndex;
  public final static int RETRY_DEFAULT = 3;
  boolean stopped = false;
  NinfLog dbg = new NinfLog(this);
  ForeServer fs;

  //  for ninfQ protocol
  public static final int MATCH_PARTIAL = 0;
  public static final int MATCH_EXACT = 1;


  static synchronized Socket getSocket(String host, int port)throws IOException{
    return new Socket(host, port);
  }

  public NinfServerConnection(NinfServerStruct s) throws NinfException {
    this(s, RETRY_DEFAULT);
  }

  public NinfServerConnection(NinfServerStruct s, int retry)
    throws NinfException {
      struct = s;
      this.connectServer(retry);
      if (s.rtt == 0) {
	getRTT();
      }
  }

  void getRTT() {
    try {
      NinfPacket pkt = new NinfPacket(NinfPktHeader.NINF_PKT_RTT_1,0,0,0);
      long t1 = System.currentTimeMillis();
      pkt.write(os);
      new NinfPacket(is);
      long t2 = System.currentTimeMillis();
      struct.rtt = t2 - t1;
      dbg.println("RTT = " + struct.rtt);
    } catch(Exception e) {}
  }

  public void connectServer(int retryCount) throws NinfIOException {
//    dbg.println("host = " + struct.host + " port = " + struct.port);
    int count = 0;
    Exception e = null;
    while (count < retryCount) {
      try{
	s = getSocket(struct.host, struct.port);
	is = new XDRInputStream(s.getInputStream());
	os = new XDROutputStream(s.getOutputStream());
	String peer = s.getInetAddress().getHostName();
	dbg.println("Connected to "+struct.host+ "("+ peer +") at " + struct.port + " T:" + System.currentTimeMillis()/1000.0);
	return;
      } catch (IOException e0) {
	dbg.println("caught execption " + struct.host + ":" + struct.port);
	try {
	  Thread.currentThread().sleep(1000);
	  ++count;
	  e = e0;
	} catch(InterruptedException ie) {
	  ie.printStackTrace();
	}
      }
    }
    if (e != null) {
      dbg.println("retry over: host = " + struct.host + " port = " + struct.port);
      throw(new NinfIOException());
    }
  }


  public NinfStub getStub(String str) throws NinfException {
    NinfPacket.getStubPacket(str).write(os);
    NinfPacket tmp = new NinfPacket(is);
    if (tmp.hdr.isRpyStubInfo()) {
      currentIndex = tmp.hdr.arg1;
      return new NinfStub(tmp.buf);
    } else{
   
      dbg.println("Can't read Stub:" + tmp);
      return null;
    }
  }

  public NinfStub[] query(int indexes[]) throws NinfIOException {
    NinfStub tmp[] = null;
    if (indexes != null){
      tmp = new NinfStub[indexes.length];
      for (int i = 0; i < indexes.length; i++){
	tmp[i] = getStub(indexes[i]);
      }
    }
    return tmp;
  }
  public NinfStub[] query(String key) throws NinfIOException {
    int indexes[] = getIndexes(key, NinfPktHeader.PARTIAL, 100);
    return query(indexes);
  }

  public NinfStub getStub(int index) {
    try {
      NinfPacket.getStubPacket(index).write(os);
      NinfPacket tmp = new NinfPacket(is);
      if (tmp.hdr.isRpyStubInfo()) {
	currentIndex = tmp.hdr.arg1;
	return new NinfStub(tmp.buf);
      }
      else{
	dbg.println("Can't read Stub:" + tmp);
	return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public int[] getIndexes(String keyword){
    return getIndexes(keyword, 0, 100);
  }

  public int[] getIndexes(String keyword, int option, int max){
    try{
      NinfPacket.getReqStubIndexListPacket(keyword, option).write(os);
      NinfPacket tmp = new NinfPacket(is);
      if (tmp.hdr.code != NinfPktHeader.NINF_PKT_RPY_STUB_INDEX_LIST) {
	dbg.println("Can't get reply stub index list");
	return null;
      }
      dbg.println("Got call reply (NINF_PKT_RPY_STUB_INDEX_LIST)");
      
      int tmpRep[] = new int[tmp.hdr.arg1];
      XDRInputStream is = new XDRInputStream(new ByteArrayInputStream(tmp.buf));
      for (int i = 0; i < tmp.hdr.arg1; i++)
	tmpRep[i] = is.readInt();
      return tmpRep;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public void sendCall(int index) {
    dbg.println("Now, send call : index =" + index + " (NINF_PKT_REQ_CALL)");
    try{
      NinfPacket.getCallPacket(index).write(os);
      NinfPacket tmp = new NinfPacket(is);
      if (!tmp.hdr.isRpyCall()) {
	dbg.println("Can't get reply call");
	System.exit(2);
      }
      dbg.println("Got call reply (NINF_PKT_RPY_CALL)");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void receive(NinfStub currentStub, Vector tmpBuffer) throws NinfException{
    int ack;
    NinfPacketInputStream istream = new NinfPacketInputStream(is);
    NinfPacketOutputStream ostream = new NinfPacketOutputStream(os);
    while (true){
      ack = istream.readInt();
      switch(ack){
      case NinfStub.NINF_ACK_OK:
	currentStub.receive(tmpBuffer, istream);
	return;
      case NinfStub.NINF_CALLBACK:
	currentStub.callback(istream, ostream);
	break;
      case NinfStub.NINF_ACK_ERROR:
	/* read error code */
	break;
      default:
dbg.println("ack = " + ack);
	throw new NinfTypeErrorException();
      }
    }
  }



  public void call(String func, Vector args) throws NinfException {
    Vector tmp;
    NinfStub currentStub = getStub(func);
    tmp = currentStub.transformArgs(args);
    sendCall(currentIndex);
    currentStub.sendArgs(tmp, new NinfPacketOutputStream(os), new int[currentStub.nparam][]);
    receive(currentStub, tmp);
    killStubProgram();
    currentStub.reverseTrans(tmp, args);
  }

  public void callNative(NinfStub stub, int index, Vector args) 
                 throws NinfException{
    try {		   
      currentIndex = index;
      sendCall(currentIndex);
      
      stub.sendArgs(args, new NinfPacketOutputStream(os), new int[stub.nparam][]);
      receive(stub, args);
      //      stub.receive(args, new NinfPacketInputStream(is));
      killStubProgram();
      dbg.println("callNative for "+struct.host+ " at " + struct.port + "end. T:" + System.currentTimeMillis());
    } catch (NinfException e){
      if (stopped)
	throw new NinfStoppedByUserException();
      throw e;
    }
  }

  public void stop(){
    stopped = true;
    dbg.println("stopped");
    close();
  }

  public void close() {
    try {
      s.close();
      if (fs != null){
	ForeServer tmp = fs;
	fs = null;
	tmp.stop();

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void killStubProgram() throws NinfException {
      NinfPacket.getKillPacket().write(os);
  }

  public void startForward(NinfPacket pkt, int index, XDRInputStream is, XDROutputStream os)
     throws NinfIOException{
    pkt.hdr.arg1 = index; /* change the index in header to that in server */
    fs = new ForeServer(is, os);
    fs.run(pkt, this);
  }



  public void startForward(NinfStub stub, int index, Vector args, XDRInputStream is, 
			   XDROutputStream os) throws NinfException{
    currentIndex = index;
    sendCall(currentIndex);
    stub.sendArgs(args, new NinfPacketOutputStream(this.os), new int[stub.nparam][]);
    fs = new ForeServer(is, os);
    fs.run(this);
  }
}

// end of NinfServerConnection.java



