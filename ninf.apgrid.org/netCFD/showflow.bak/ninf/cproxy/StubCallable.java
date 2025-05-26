package ninf.cproxy;
import ninf.basic.*;
import ninf.basic.NinfErrorException;
import ninf.client.*;
import java.util.Vector;
import java.io.*;

class StubCallable extends Callable {
  NinfStub stub;
  CProxyServer master;
  static NinfLog dbg = new NinfLog("StubCallable");


  StubCallable(NinfStub stub, int index, CProxyServer master){
    this.stub = stub;
    this.index = index;
    this.master = master;
  }

  NinfStub getStub(){
    return stub;
  }

  public StubCallableContext receiveNinfCode(NinfPacketInputStream is) throws NinfException {
    return new StubCallableContext(is);
  }

  public Stoppable call(NinfPacket pkt, XDRInputStream is, XDROutputStream os) 
  throws NinfException{
    NinfServerConnection con;
    StubCallableContext context;
    NinfPacketOutputStream pos = 
      new NinfPacketOutputStream(os, NinfPktHeader.NINF_PKT_RPY_CALL);
    int serial = master.getSerial();
    pos.writeInt(serial);
    pos.flush();

    NinfPacketInputStream pis = new NinfPacketInputStream(pkt, is);    
    context = receiveNinfCode(pis);

    CallContext callContext = stub.setupJustScalarBuffers(pis);
    //    stub.serverReceive(buffers, pis);
    context.modifyBuffers(callContext.buffers);
    ServerIndex server;    
    try {
      server = master.schedule(stub, callContext, serial);
    } catch (NinfException e){
      throw new NinfErrorException(NinfError.CPROXY_CANNOT_SCHEDULE);
    }

    if (server != null){
      con = server.server.connect();
      con.startForwardWithScalar(stub, server.index, callContext, pis.getRestPacket(), is, os);
      return con;
    }
    dbg.log(stub.module_name + "/" + stub.entry_name + ": failed to be scheduled");
    NinfPacket.getErrorPacket(NinfError.CANTEXECUTESTUB).write(os);
    try {
      os.close();
    } catch (java.io.IOException e) {}
    return null;
  }

}
