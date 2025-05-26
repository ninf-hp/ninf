package ninf.client;

import java.io.*;
import java.util.Vector;
import ninf.basic.*;

class Callback implements NinfConstant{
  CallbackFunc func;
  int index;
  NinfParamDesc params[];
  Vector args;
  NinfLog dbg = new NinfLog(this);
  CallContext context;

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
  }

  void setLocalContext(CallContext outContext) throws NinfException{
    context = new CallContext(params.length);
    for (int i = 0; i < params.length; i++)
      context.shapes[i] = outContext.shapes[i + index + 1];
    for (int i = 0; i < params.length; i++)
      context.buffers[i] = params[i].setupBuffer(context.shapes[i]);
  }

  int setFunc(Vector o_args, int findex, CallContext outContext) 
  throws NinfException{
// dbg.println("params.length=" + params.length);
    func = (CallbackFunc)(o_args.elementAt(findex));
    setLocalContext(outContext);
    for (int i = 0; i < params.length; i++){
      args.addElement(params[i].setupArg(context.shapes[i]));
    }
    return params.length;
  }

  public void receive(NinfPacketInputStream istream)
    throws NinfException {
    for (int i = 0; i < nparam(); i++) {
      if (params[i].IS_IN_MODE()){
dbg.println("read params[" + i + "], buffer = " + context.buffers[i]);
	params[i].read(context.shapes[i], context.buffers[i], istream);
      }
    }
  }

  public void reverseTrans() throws NinfException{
    for (int i = 0; i < nparam(); i++)
      if (params[i].IS_IN_MODE())
	params[i].reverseTransform(context.buffers[i], context.shapes[i], 
				   args.elementAt(i));
  }

  public void trans() throws NinfException {
    for (int i = 0; i < nparam(); i++)
      if (params[i].IS_OUT_MODE()){
	context.buffers[i] =
	  params[i].transformArg(args.elementAt(i), context.shapes[i], true);
      }
  }

  public void send(NinfPacketOutputStream ostream) 
    throws NinfIOException {
    ostream.writeInt(NINF_CALLBACK_ACK_OK);
    for (int i = 0; i < nparam(); i++) {
      if (params[i].IS_OUT_MODE()){
	params[i].send(context.buffers[i], ostream);
      }
    }
    ostream.flush();
  }

  void dumpBuffer(BufferObject bo){
    int size = bo.data.length;
    for (int i = 0; i < size; i++)
      System.out.print(" " + bo.data[i]);
    System.out.println("");
  }


  void call(NinfPacketInputStream istream, 
	  NinfPacketOutputStream ostream)  throws NinfException {
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
