//
// NinfServerConnection.java
//

package ninf.client;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.Vector;
import ninf.basic.*;
import ninf.basic.NinfErrorException;
import ninf.common.*;

public class NinfServerConnection implements Stoppable{

  NinfServerStruct struct;
  public Socket s;
  public XDRInputStream is;
  public XDROutputStream os;
  public int currentIndex;
  public final static int RETRY_DEFAULT = 10;
  public final static int SLEEP_INIT = 2000;
  boolean stopped = false;
  NinfLog dbg = new NinfLog(this);
  ForeServer fs;
  OutputStream stdoutStream, stderrStream;
  NinfExecInfo execInfo;

  //  for ninfQ protocol
  public static final int MATCH_PARTIAL = 0;
  public static final int MATCH_EXACT = 1;


  static synchronized Socket getSocket(String host, int port)throws IOException{
    return new Socket(host, port);
  }

  public NinfServerConnection(NinfServerStruct s) throws NinfException {
    this(s, RETRY_DEFAULT);
  }

  public NinfServerConnection(NinfServerStruct s, OutputStream stdoutStream, 
			       OutputStream stderrStream) throws NinfException {
    this(s, RETRY_DEFAULT);
    this.stdoutStream = stdoutStream;
    this.stderrStream = stderrStream;
  }

  public NinfServerConnection(NinfServerStruct s, int retry)
    throws NinfException {
      struct = s;
      this.connectServer(retry);
  }

  public LoadInformation getLoad() throws NinfException {
    NinfPacket pkt = new NinfPacket(NinfPktHeader.NINF_PKT_LOAD,0,0,0);
    pkt.write(os);
    NinfPacketInputStream pis = new NinfPacketInputStream(is);
    double loadAverage = pis.readDouble();
    double user   = pis.readDouble();
    double system = pis.readDouble();
    double idle   = pis.readDouble();
    return new LoadInformation(loadAverage, user, system, idle);
  }

  public ServerCharacter getServerCharacter() throws NinfException {
    NinfPacket pkt = new NinfPacket(NinfPktHeader.NINF_PKT_CHARACTER,0,0,0);
    pkt.write(os);
    NinfPacketInputStream pis = new NinfPacketInputStream(is);
    return new ServerCharacter(pis);
  }

  public long getThroughput(int size, int mode) throws NinfException {
    /* mode = 0:both, 1:fore, 2: back */
    NinfPacket pkt = new NinfPacket(NinfPktHeader.NINF_PKT_THROUGHPUT,0,size,mode);
    long t1 = System.currentTimeMillis();
    pkt.write(os);
    byte tmp[] = new byte[size];
    if (mode == 0 || mode == 1){
      NinfPacketOutputStream pos = new NinfPacketOutputStream(os);
      pos.write(tmp);
      pos.flush();
    }
    NinfPacketInputStream pis = new NinfPacketInputStream(is);
    if (mode == 0 || mode == 2){
      pis.read(size, tmp);
    }
    long t2 = System.currentTimeMillis();
    return t2 - t1;
  }

  public void connectServer(int retryCount) throws NinfException {
//    dbg.println("host = " + struct.host + " port = " + struct.port);
    int count = 0;
    Exception e = null;
    execInfo = new NinfExecInfo();
    int sleepTime = SLEEP_INIT;
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
	  Thread.currentThread().sleep(sleepTime);
	  ++count;
	  e = e0;
	  sleepTime *= 2;
	} catch(InterruptedException ie) {
	  ie.printStackTrace();
	}
      }
    }
    if (e != null) {
      dbg.println("retry over: host = " + struct.host + " port = " + struct.port);
      throw(new NinfErrorException(NinfError.CANTCONNECTSERVER));
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
      throw new NinfErrorException(tmp.hdr.arg1);
    }
  }

  public NinfStub[] query(int indexes[]) throws NinfException {
    NinfStub tmp[] = null;
    if (indexes != null){
      tmp = new NinfStub[indexes.length];
      for (int i = 0; i < indexes.length; i++){
	tmp[i] = getStub(indexes[i]);
      }
    }
    return tmp;
  }
  public NinfStub[] query(String key) throws NinfException {
    int indexes[] = getIndexes(key, NinfPktHeader.PARTIAL, 100);
    return query(indexes);
  }

  public NinfStub getStub(int index) throws NinfException{
    NinfPacket.getStubPacket(index).write(os);
    NinfPacket tmp = new NinfPacket(is);
    if (tmp.hdr.isRpyStubInfo()) {
      currentIndex = tmp.hdr.arg1;
      return new NinfStub(tmp.buf);
    }else{
      dbg.println("Can't read Stub:" + tmp);
      throw new NinfErrorException(NinfError.STUBREADFAIL);
    }
  }

  public int[] getIndexes(String keyword) throws NinfException{
    return getIndexes(keyword, 0, 100);
  }

  public int[] getIndexes(String keyword, int option, int max) throws NinfException{
    NinfPacket.getReqStubIndexListPacket(keyword, option).write(os);
    NinfPacket tmp = new NinfPacket(is);
    if (tmp.hdr.code != NinfPktHeader.NINF_PKT_RPY_STUB_INDEX_LIST) {
      dbg.println("Can't get reply stub index list");
      throw new NinfErrorException(NinfError.NINF_PROTOCOL_ERROR);
    }
    dbg.println("Got call reply (NINF_PKT_RPY_STUB_INDEX_LIST)");
    
    int tmpRep[] = new int[tmp.hdr.arg1];
    XDRInputStream is = new XDRInputStream(new ByteArrayInputStream(tmp.buf));
    try {
      for (int i = 0; i < tmp.hdr.arg1; i++)
	tmpRep[i] = is.readInt();
    } catch (IOException e){
      throw new NinfIOException();
    }
    return tmpRep;
  }

  public void sendCall(int index) throws NinfException{
    dbg.println("Now, send call : index =" + index + " (NINF_PKT_REQ_CALL)");
    if (execInfo != null)
      execInfo.start();
    NinfPacket.getCallPacket(index).write(os);
    NinfPacket tmp = new NinfPacket(is);
    if (!tmp.hdr.isRpyCall()) {
      dbg.println("Can't get reply call:" + tmp.hdr);
      throw new NinfErrorException(NinfError.NINF_PROTOCOL_ERROR);
    }
    int serial = (new NinfPacketInputStream(tmp, is)).readInt();
    dbg.println("Got call reply (NINF_PKT_RPY_CALL): "+ serial);
    if (execInfo != null)
      execInfo.call_serial = serial;
  }

  public void receive(NinfStub currentStub, CallContext context) 
  throws NinfException{
    int ack;
    NinfPacketInputStream istream = 
      new NinfPacketInputStream(is, stdoutStream, stderrStream);
    NinfPacketOutputStream ostream = new NinfPacketOutputStream(os);
    while (true){
      ack = istream.readInt();
      switch(ack){
      case NinfStub.NINF_ACK_OK:
	currentStub.receive(context, istream);
	if (execInfo == null)
	  execInfo = new NinfExecInfo();
	execInfo.read_data(istream);
	execInfo.end();
	execInfo.calc_client_exec();
	return;
      case NinfStub.NINF_CALLBACK:
	currentStub.callback(istream, ostream);
	break;
      case NinfStub.NINF_ACK_ERROR:
	/* read error code */
	int errorCode = istream.readInt();
	throw new NinfErrorException(errorCode);
      default:
dbg.println("ack = " + ack);
	throw new NinfTypeErrorException();
      }
    }
  }

  public double getTime() throws NinfException{
    NinfPacketOutputStream pos = 
      new NinfPacketOutputStream(os, NinfPktHeader.NINF_PKT_GETTIME);
    pos.flush();
    NinfPacketInputStream pis = new NinfPacketInputStream(is);
    double time = pis.readDouble();
    return time;
  }

  public NinfExecInfo call(String func, Vector args) throws NinfException {
    CallContext context;
    NinfStub currentStub = getStub(func);
    context = currentStub.transformArgs(args);
    sendCall(currentIndex);
    currentStub.sendArgs(context, new NinfPacketOutputStream(os), new int[currentStub.nparam][]);
    receive(currentStub, context);
    killStubProgram();
    currentStub.reverseTrans(context, args);
    return execInfo;
  }

  /*
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
  */

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
      dbg.println("failed to close, ignore." );
    }
  }

  public void killStubProgram() throws NinfException {
    NinfPacket.getKillPacket().write(os);
    NinfPacketInputStream istream = new NinfPacketInputStream(is, stdoutStream, stderrStream);
    try {
      if (istream.getCode() != NinfPktHeader.NINF_PKT_RPY_KILL)
	throw new NinfErrorException(NinfError.NINF_PROTOCOL_ERROR);
    }catch(NinfIOException e){
      throw new NinfErrorException(NinfError.NINF_PROTOCOL_ERROR);
    }
  }

  public void startForward(XDRInputStream is, XDROutputStream os)
     throws NinfIOException{
    fs = new ForeServer(is, os);
    fs.simple();  // simple mode
    fs.run(this);
  }

  public void startForward(NinfPacket pkt, int index, XDRInputStream is, XDROutputStream os)
     throws NinfIOException{
    pkt.hdr.arg1 = index; /* change the index in header to that in server */
    fs = new ForeServer(is, os);
    fs.run(pkt, this);
  }

  public void startForward(NinfStub stub, int index, CallContext context,
			   XDRInputStream is, 
			   XDROutputStream os) throws NinfException{
    currentIndex = index;
    sendCall(currentIndex);
    stub.sendArgs(context, new NinfPacketOutputStream(this.os), 
		  new int[stub.nparam][]);
    fs = new ForeServer(is, os);
    fs.run(this);
  }

  public void startForwardWithScalar(NinfStub stub, int index, CallContext context,
				     NinfPacket pkt, XDRInputStream is, 
			   XDROutputStream os) throws NinfException{
    currentIndex = index;
    sendCall(currentIndex);
    NinfPacketOutputStream ops = new NinfPacketOutputStream(this.os);
    stub.sendCall(context, ops);
    stub.sendScalar(context, ops);
    ops.flush();
    fs = new ForeServer(is, os);
    fs.simple();  // simple mode
    fs.run(pkt, this);
  }
}

// end of NinfServerConnection.java



