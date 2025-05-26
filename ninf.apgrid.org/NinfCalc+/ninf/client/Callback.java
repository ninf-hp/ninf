package ninf.client;

import java.io.*;
import java.util.Vector;
import ninf.basic.*;

class Callback implements NinfConstant{
  CallbackFunc func;
  int index;
  int iarg[];
  NinfParamDesc params[];
  Vector args;
  BufferObject buffer[];
  NinfLog dbg = new NinfLog(this);

  Callback(NinfParamDesc o_params[], int index, int max){
    this.index = index;
    int end = max;
//dbg.println("end=" + end);
//dbg.println("index=" + index);

    for (int i = index + 1; i < max; i++)
      if (o_params[i].IS_FUNC()){
	end = i;
	break;
      }
    int len = end - index -1;
    params = new NinfParamDesc[len];
    for (int i = 0; i < len; i++)
      params[i] = o_params[i + index + 1];
    args = new Vector();
    buffer = new BufferObject[len];
  }

  int setFunc(Vector o_args, int findex, int iarg[]){
// dbg.println("params.length=" + params.length);
    func = (CallbackFunc)(o_args.elementAt(findex));
    for (int i = 0; i < params.length; i++){
      args.addElement(params[i].setupArg(iarg));
      buffer[i] = params[i].setupBuffer(iarg);
    }
    this.iarg = iarg;
    return params.length;
  }

  public void receive(NinfPacketInputStream istream)
    throws NinfIOException {
    for (int i = 0; i < nparam(); i++) {
      if (params[i].IS_IN_MODE()){
dbg.println("read params[" + i + "], buffer = " + buffer[i]);
	params[i].read(buffer[i], istream);
      }
    }
  }

  public void reverseTrans() {
    for (int i = 0; i < nparam(); i++)
      if (params[i].IS_IN_MODE())
	params[i].reverseTransform(buffer[i], iarg, args.elementAt(i));
  }

  public void trans(){}

  public void send(NinfPacketOutputStream ostream) 
    throws NinfIOException {
    ostream.writeInt(NINF_CALLBACK_ACK_OK);
    ostream.flush();
  }

  void call(NinfPacketInputStream istream, 
	  NinfPacketOutputStream ostream)  throws NinfIOException {
    receive(istream);
    reverseTrans();
    func.callback(args);
    trans();
    send(ostream);
  } 

  int nparam(){
    return params.length;
  }
}
