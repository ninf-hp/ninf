package ninf.cproxy;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;
import java.io.*;

class URLResourcePair{
  int argNum;
  URLResource rsc;
  URLResourcePair(int argNum, URLResource rsc){
    this.argNum = argNum;
    this.rsc = rsc;
  }
}


class StubCallableContext {
  int callID;
  URLResourcePair resources[];
  
  StubCallableContext(NinfPacketInputStream is) throws NinfException {
    callID = is.readInt();
    switch (callID){
    case NinfStub.NINF_REQ_CALL:
      break;
    case NinfStub.NINF_REQ_CALL_WITH_STREAM:
      readResources(is);
      break;
    default:
      throw new NinfErrorException(NinfError.NINF_PROTOCOL_ERROR);
    }
  }
    
  void readResources(NinfPacketInputStream is) throws NinfException {
    int argn;
    Vector tmpV = new Vector();

    while ((argn = is.readInt()) >= 0)
      tmpV.addElement(new URLResourcePair(argn, new URLResource(is)));

    resources = new URLResourcePair[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      resources[i] = (URLResourcePair)tmpV.elementAt(i);
  }

  void modifyBuffers(BufferObject buffers[]){
    if (callID == NinfStub.NINF_REQ_CALL)
      return;
    for (int i = 0; i < resources.length; i++){
      BufferObject bo = buffers[resources[i].argNum];
      if (bo == null){
	bo = new BufferObject();
	buffers[resources[i].argNum] = bo;
      }
      bo.rsc = resources[i].rsc;
    }
  }
}
