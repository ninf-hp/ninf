package ninf.client;

import java.io.*;
import java.util.Vector;
import java.util.Stack;
import java.lang.Integer;
import ninf.basic.*;
import ninf.common.*;

public class NinfStub implements NinfConstant{

/*********************** NON STATIC VARIABLES ***********************/
  public String program_name;
  public int version_major, version_minor; 	/* protocol version */
  public int info_type; 		/* information type */
  public String module_name;	/* module name */
  public String entry_name;	/* entry name */
  public int nparam;
  public NinfParamDesc params[];
  public Callback callbacks[];
  public int order_type;
  public NinfExpression order;
  public String description;
  public int shrink;

  NinfLog dbg = new NinfLog(this);

/***********************    STATIC METHODS    ***********************/
  public static NinfStub getEmptyStub(int size) {
    NinfStub tmp = new NinfStub();
    tmp.program_name = "";
    tmp.version_major = 0; 
    tmp.version_minor = 0; 
    tmp.info_type = 0;
    tmp.module_name = "";
    tmp.entry_name = "";
    tmp.nparam = 0;
    tmp.params = new NinfParamDesc[size];
    return tmp;
  }
  
/***********************  INSTANCE  CREATION  ***********************/
  public NinfStub(){}

  public NinfStub(byte buf[]) throws NinfException{
      this.readStub(new XDRInputStream(new ByteArrayInputStream(buf)));
      this.setupCallback();
  }

  public NinfStub(XDRInputStream istream) throws NinfException {
    this.readStub(istream);
    this.setupCallback();
  }
  
  public void setupCallback(){
    Vector v = new Vector();
    for (int i = 0; i < nparam; i++){
//      dbg.println("param["+ i +"] = "+ params[i].param_inout);
      if (params[i].IS_FUNC()){
	v.addElement(new Callback(params, i, nparam));
      }
    }
    callbacks = new Callback[v.size()];
    for (int i = 0; i < v.size(); i++)
      callbacks[i] = (Callback)(v.elementAt(i));

  }

  public NinfStub copy() {
    NinfStub tmp = new NinfStub();
    tmp.program_name = program_name;
    tmp.version_major = version_major;
    tmp.version_minor = version_minor;
    tmp.info_type = info_type;
    tmp.module_name = module_name;
    tmp.entry_name = entry_name;
    tmp.nparam = nparam;
    tmp.params = new NinfParamDesc[nparam];
    for (int i = 0; i < nparam; i++)
      tmp.params[i] = params[i].copy();
    return tmp;
  }

  /***********************     I/O  METHODS     ***********************/
  public void readStub(XDRInputStream istream) throws NinfException {
    try {
      version_major = istream.readInt();
      version_minor = istream.readInt();
      info_type = istream.readInt();
      module_name = istream.readString();
      entry_name = istream.readString();
      nparam = istream.readInt();
      
      params = new NinfParamDesc[nparam];
      
      for (int i = 0; i < nparam; i++) {
	params[i] = new NinfParamDesc(istream);
      }
      
      order_type = istream.readInt();
      if (order_type == NinfVal.VALUE_BY_EXPR) {
	order = new NinfExpression();
	order.read(istream);
      } else {
	order = null;
      }
      description = istream.readString();
      shrink = istream.readInt();
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }

  public void writeTo(XDROutputStream ostream) throws NinfIOException {
    try {
      ostream.writeInt(version_major);
      ostream.writeInt(version_minor);
      ostream.writeInt(info_type);
      ostream.writeString(module_name);
      ostream.writeString(entry_name);
      ostream.writeInt(nparam);
      for (int i = 0; i < nparam; i++)
	params[i].write(ostream);
      ostream.writeInt(order_type);
      if (order_type == NinfVal.VALUE_BY_EXPR)
	order.write(ostream);
      ostream.writeString(description == null? "" : description);
      ostream.writeInt(shrink);
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }

  public String toString() {
    String tmp = "NinfStub:" + module_name + ":" + entry_name +"\n" + nparam;
    for (int i = 0; i < nparam; i++)
     tmp = tmp + "param["+i+"]"+ params[i];
    tmp += "\n\n" + description;
    return tmp;
  }

  public String toText() {
    StringBuffer tmp = new StringBuffer(module_name + "/" + entry_name);
    tmp.append("\n------------\n");
    for (int i = 0; i < nparam; i++)
     tmp.append("param["+i+"] = "+ params[i].toText(i) + "\n");
    tmp.append("------------\n" + description);

    if (order_type == NinfVal.VALUE_BY_EXPR)
      tmp.append("Calcuration Order: " + order.toText());

    return tmp.toString();
  }


  public byte[] toByteArray() throws NinfIOException{
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    XDROutputStream tmp = new XDROutputStream(bs);
    this.writeTo(tmp);
    return bs.toByteArray();
  }

  // ***********************  NON STATIC METHODS  ***********************
  public FunctionName getName(){
    return new FunctionName(module_name, entry_name);
  }  

  public void setProgramName(String str) {
    program_name = str;
  }

  private boolean hasResource(CallContext context){
    for (int i = 0; i < context.buffers.length; i++){
      BufferObject bo = context.buffers[i];
      if (bo != null && bo.hasResource())
	return true;
    }
    return false;
  }

  public void sendCall(CallContext context, NinfPacketOutputStream ostream) throws NinfIOException{
    if (!(hasResource(context))){
      ostream.writeInt(NINF_REQ_CALL);
    } else {
      ostream.writeInt(NINF_REQ_CALL_WITH_STREAM);
      for (int i = 0; i < context.buffers.length; i++){
	BufferObject bo = context.buffers[i];
	if (bo != null && bo.hasResource()){
	  ostream.writeInt(i);
	  bo.rsc.writeTo(ostream);
	}
      }
      ostream.writeInt(-1);
    }
  }

  public void sendScalar(CallContext context, NinfPacketOutputStream ostream)
  throws NinfException{
    for (int i = 0; i < nparam; i++) {
      if (params[i].IS_FUNC()) break;
      if (params[i].IS_IN_MODE() && params[i].IS_SCALAR())
	params[i].send(context.buffers[i], ostream);
    }
  }

  public void sendArgs(CallContext context, NinfPacketOutputStream ostream, 
		       int sizes[][]) 
  throws NinfException{
      sendCall(context, ostream);
      sendScalar(context, ostream);
      for (int i = 0; i < nparam; i++) {
	if (params[i].IS_FUNC()) break;
	if (params[i].IS_IN_MODE() && (!(params[i].IS_SCALAR())))
	  if (!(context.buffers[i].hasResource()))
	    params[i].send(context.buffers[i], ostream);
      }
      ostream.flush();
  }

  public void callback(NinfPacketInputStream istream, 
		       NinfPacketOutputStream ostream)  
  throws NinfException{
    int funcindex = istream.readInt();
    Callback callback = getCallback(funcindex);
    if (callback == null)
      throw new NinfTypeErrorException();
    callback.call(istream, ostream);
  }

  public void receive(CallContext context, NinfPacketInputStream istream)
    throws NinfException {
    for (int i = 0; i < nparam; i++) {
      if (params[i].IS_FUNC()) break;
      if (params[i].IS_OUT_MODE())
	if (!(context.buffers[i].hasResource()))
	  params[i].read(context.shapes[i], context.buffers[i], istream);
    }
  }

  public CallContext setupJustScalarBuffers(NinfPacketInputStream istream)
    throws NinfException{
    return setupBuffers(istream, true);    
  }

  public CallContext setupBuffers(NinfPacketInputStream istream)
    throws NinfException{
    return setupBuffers(istream, false);
  }

  public CallContext setupBuffers(NinfPacketInputStream istream,
			    boolean scalarOnly)
  throws NinfException{
    CallContext context = new CallContext(nparam);
    for (int i = 0; i < nparam; i++) {
      if (params[i].IS_IN_MODE() && params[i].IS_SCALAR()) {
	context.buffers[i] = params[i].setupScalarBuffer();
	params[i].read(context.shapes[i], context.buffers[i], istream);
      }
    }
    setupContext(context);
    if (!scalarOnly){
      for (int i = 0; i < nparam; i++) {
	if (!(params[i].IS_IN_MODE() && params[i].IS_SCALAR())) {
	  //	System.err.print(i + " ");
	  context.buffers[i] = params[i].setupBuffer(context.shapes[i]);
	}
      }
    }
    return context;
  }

  void setupContext(CallContext context) throws NinfException{
    for (int i = 0; i < nparam; i++)
      if (params[i].IS_IN_MODE() && params[i].IS_SCALAR()) {
	context.iargs[i] = params[i].scalarBuffer(context.buffers[i]);
      }
    for (int i = 0; i < nparam; i++){
      if (!params[i].IS_SCALAR()) {
	context.shapes[i] = params[i].setupArrayShape(context.iargs);
      }
    }
  }

  public void serverReceive(CallContext context, NinfPacketInputStream istream)
    throws NinfException {
    for (int i = 0; i < nparam; i++) {
      if (params[i].IS_IN_MODE() && !(params[i].IS_SCALAR()))
	params[i].read(context.shapes[i], context.buffers[i], istream);
    }
  }

  public void serverSend(CallContext context, NinfPacketOutputStream ostream)
  throws NinfException {
    dbg.println("server sending data");
    ostream.writeInt(NINF_ACK_OK);
    dbg.println("ack returned ");
    for (int i = 0; i < nparam; i++) {
      if (params[i].IS_OUT_MODE() && (!(params[i].IS_SCALAR())))
	params[i].send(context.buffers[i], ostream);
    }
    ostream.flush();
  }

/*
  public int[] getIarg(Vector org){
    int iarg[] = new int[nparam];
    for (int i = 0; i < org.size(); i++) 
      iarg[i] = params[i].scalarArg(org.elementAt(i));
    return iarg;
  }
  */

  public void reverseTrans(CallContext context, Vector org) throws NinfException{
    //    int iarg[] = getIarg(org);
    for (int i = 0; i < org.size(); i++)
      if (params[i].IS_OUT_MODE())
	params[i].reverseTransform(context.buffers[i], context.shapes[i], 
				   org.elementAt(i));
  }

  public CallContext transformArgs(Vector args) throws NinfException {
    CallContext context = new CallContext(nparam);

    if (args.size() + callbackCounts() != nparam) {
//      dbg.println("argsize = " + args.size() + ": callbackCounts = " + 
//	callbackCounts());
      throw (new NinfTypeErrorException());
    }
    for (int i = 0; i < args.size(); i++) 
      context.iargs[i] = params[i].scalarArg(args.elementAt(i));
    for (int i = 0; i < nparam; i++){
      if (!params[i].IS_SCALAR()) {
	context.shapes[i] = params[i].setupArrayShape(context.iargs);
      }
    }

    int func_count = 0;
    for (int i = 0; i < args.size(); i++) {
      if (params[i].IS_FUNC()) break;
      context.buffers[i] = 
	params[i].transformArg(args.elementAt(i), context.shapes[i]);
    }

    int findex = 0;
    for (int i = 0; i < args.size(); i++) {
      if (params[i+findex].IS_FUNC()) {
	findex += callbacks[func_count].setFunc(args, i, context);
	func_count++;
      }
    }
    return context;
  }

  int callbackCounts(){
    int sum = 0;
    for (int i = 0; i < callbacks.length; i++)
      sum += callbacks[i].nparam();
    return sum;
  }

  Callback getCallback(int index){
    for (int i = 0; i < callbacks.length; i++)
      if (callbacks[i].index == index)
	return callbacks[i];
    return null;
  }

  public CallInformation getCallInformation(CallContext context){
    return new CallInformation(calcForeSize(context.shapes), 
			       calcBackSize(context.shapes), 
			       calcOrder(context.iargs));
  }

  public long calcForeSize(ArrayShape shapes[]){
    long tmp = 0;
    try {
      for (int i = 0; i < nparam; i++) 
	if (params[i].IS_IN_MODE() && !params[i].IS_SCALAR()) 
	  tmp += params[i].getSize(shapes[i]);
    } catch (NinfException e){
      dbg.println("failed to calc");
      dbg.println(e);
      return 0;
    }
    return tmp;
  }

  public long calcBackSize(ArrayShape shapes[]){
    long tmp = 0;
    try {
      for (int i = 0; i < nparam; i++) 
	if (params[i].IS_OUT_MODE() && !params[i].IS_SCALAR()) 
	  tmp += params[i].getSize(shapes[i]);
    } catch (NinfException e){
      dbg.println("failed to calc");
      dbg.println(e);
      return 0;
    }
    return tmp;
  }

  public long calcOrder(int iarg[]){
    try {
      if (order == null)
	return 0;
      return order.calc(iarg);
    } catch (NinfException e){
      dbg.println("failed to calc");
      dbg.println(e);
      return 0;
    }
  }


/******  CONVERTING STUB ********/
  private void growParams(int n){
    NinfParamDesc tmp[];
    tmp = new NinfParamDesc[params.length + n];
    for (int i = 0; i < nparam; i++)
      tmp[i] = params[i];
    params = tmp;
  }

  public void add(NinfStub s) {
    s.shift(nparam);
    while (params.length < nparam + s.nparam)
      growParams(30);
    for (int i = 0; i < s.nparam; i++) {
      params[nparam+i] = s.params[i];
    }
    nparam += s.nparam;
  }

  public void shift(int sh) {
    for (int i = 0; i < nparam; i++)
      params[i].shift(sh);
  }

  /* for netsolve

  public boolean isUsedAsSize(int index){
    for (int i = 0; i < nparam; i++)
      if (params[i].isUsedAsSize(index))
	return true;
    return false;
  }
  */

  /********* for ninf calc *************/
// Ninf のインターフェース情報に「行列サイズを示す引数 → 行列」の
// 検索ポインタをつける
// ( NinfClient.getStubWithRdim() で使用される )

  public void inputRRetrieval(){
 
    int mcount = 0;
    for(int i = 0; i < nparam; i++){
      NinfParamDesc p = params[i];
      if(!p.IS_SCALAR()){
        mcount++;
        if(p.IS_IN_MODE()){
	  for(int j = p.ndim-1 ; j >= 0; j--){ 
	    NinfDimParam pd = p.dim[j];
	    if(pd.IS_END_TYPE()){
	      if(pd.IS_END_VALUE_IN_ARG()){
                inputRParam(pd.getEndVal(),mcount,3-p.ndim+j);
	      }
	    } else if(pd.IS_SIZE_VALUE_IN_ARG()){
	      inputRParam(pd.getSizeVal(),mcount,3-p.ndim+j);
	    }
	  }
	}
      }
    }
    
//  for dimension size to be written as only size parameter
    mcount = 0;
    for(int i = 0; i < nparam; i++){
      NinfParamDesc p = params[i];
      if(!p.IS_SCALAR()){
        mcount++;
        if(p.IS_IN_MODE()){
	  for(int j = 0 ; j < p.ndim; j++){
	    NinfDimParam pd = p.dim[j];
	    if(pd.IS_END_TYPE() && pd.IS_SIZE_VALUE_IN_ARG()){ 
	      inputRParam(pd.getSizeVal(),mcount,j+1);
	    }
	  }
	}
      }
    }
  }
//----------------------------------------------------------------------------

//-------------------------《inputRParam()》----------------------------------
// calling sequence の ref 番目の引数がスカラでなかったら、行列と見なし、
//「行列サイズを示す引数 → 行列」の検索ポインタをつける
// ( NinfStub.inputRRetrieval() で使用される )

  public void inputRParam(int ref, int mat, int size){
    int count = 0;
    NinfParamDesc p = params[ref];
    if(!p.IS_SCALAR()){
      dbg.println("ERROR:not scalar!!");
      return;
    }
    p.inputRdim(mat,size);
    return;
  } 

}
