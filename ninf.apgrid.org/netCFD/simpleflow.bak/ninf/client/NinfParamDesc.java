package ninf.client;

import java.io.*;
import java.util.Vector;
import java.util.Stack;
import java.lang.Integer;
import ninf.basic.*;
import ninf.metaserver.*;

public class NinfParamDesc {
/***********************   STATIC VARIABLES   ***********************/
  public static final int     MODE_NONE  = 0;
  public   static final int     MODE_IN    = 1;
  public   static final int     MODE_OUT   = 2;
  public   static final int     MODE_INOUT = 3;
  public   static final int     MODE_WORK  = 4;
  public   static final int     MODE_CALL_BACK_FUNC = 8;  /* call back function */
  public   static final int     MODE_INTERNAL = 16;  /* used only in metaserver */

  public   static final int     MAX_DIM = 10;
  public   static final int     MAX_RDIM = 1;


  public boolean IS_IN_MODE()  { return ((param_inout & MODE_IN)  != 0);}
  public boolean IS_OUT_MODE() { return ((param_inout & MODE_OUT) != 0);}
  public boolean IS_WORK_MODE() { return ((param_inout & MODE_WORK) != 0);}
  public boolean IS_INTERNAL() { return ((param_inout & MODE_INTERNAL) != 0);}
  public boolean IS_SCALAR() { return (ndim == 0);}
  public boolean IS_FUNC() {return (param_inout == MODE_CALL_BACK_FUNC);}

  public static final String modeString[] = {
    "NONE", "IN", "OUT", "INOUT", "WORK", "", "", "", 
    "CALLBACK", "", "", "",  "", "", "", "", 
    "INTERNAL"
  };
/*********************** NON STATIC VARIABLES ***********************/
  public int param_type;    /* argument type */
  public int param_inout; 	/* IN/OUT */
  public int ndim; 	/* number of dimension */
  public NinfDimParam dim[];

  NinfLog dbg = new NinfLog(this);


//==============FOR NINF CALC===========================================
  public int nrdim; 	
         /* 逆向きポインタの数
           (複数の行列サイズが同じサイズを指している場合 ndim は2以上) */
  public NinfRetDimParam rdim[]; /* 引数の値がサイズとなっている行列
                               (calling sequence の行列のみの引数の順番) */



/***********************  INSTANCE  CREATION  ***********************/
  public NinfParamDesc(){
    dim = new NinfDimParam[MAX_DIM];
/** 付け足した部分 **  ここから ****/
// 初期設定
    rdim = new NinfRetDimParam[MAX_RDIM];
    rdim[0] = new NinfRetDimParam();
    nrdim = MAX_RDIM;
/*****  ここまで ****/

  }
  public NinfParamDesc(XDRInputStream istream) throws NinfIOException{
    this();
    try {
      param_type  = istream.readInt();
      param_inout  = istream.readInt();
      ndim  = istream.readInt();
      for (int i = 0; i < Math.abs(ndim); i++)
      dim[i] = new NinfDimParam(istream);
    } catch (IOException e) {
      throw new NinfIOException(e);
    }
  }

  public NinfParamDesc copy(){
    NinfParamDesc tmp = new NinfParamDesc();
    tmp.param_type  = param_type;
    tmp.param_inout  = param_inout;
    tmp.ndim = ndim;
    for (int i = 0; i < Math.abs(ndim); i++)
      tmp.dim[i] = dim[i].copy();
    return tmp;
  }

/***********************     I/O  METHODS     ***********************/

  public void write(XDROutputStream ostream) throws NinfIOException {
    try {
      ostream.writeInt(param_type);
      ostream.writeInt(IS_INTERNAL()? MODE_WORK :param_inout);
      ostream.writeInt(ndim);
      for (int i = 0; i < Math.abs(ndim); i++)
      dim[i].write(ostream);
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }

  public String toString(){
    String tmp = "  type = " + param_type + ", inout = " + param_inout + ", dim = " + ndim + "\t";
    for (int i = 0; i < Math.abs(ndim); i++)
      tmp = tmp + " " + dim[i];
    return (tmp + "\n");
  }

  String makeVarName(int val){
    return (new Character((char)('A' + val))).toString();
  }

  public String toText(int val){
    StringBuffer tmp = new StringBuffer(
      FormatString.format("%6s %10s %s",
			  modeString[param_inout],
			  NinfVal.typeString[param_type],
			  makeVarName(val)));

    for (int i = 0; i < Math.abs(ndim); i++)
      tmp.append(" " + dim[i].toText());
        return tmp.toString();
  }

  // ***********************  NON STATIC METHODS  ***********************

  public void send(BufferObject bo, NinfPacketOutputStream os) throws NinfIOException {
    dbg.println(" sending data:"+ bo.data.length);
    if (ndim < 0)
      for (int i = 0; i < Math.abs(ndim); i++)
	os.writeInt(bo.sizes[i]);
    os.write(bo.data);
  }


  void  readStringsRec(int d_index, ArrayShape shape, XDROutputStream os, NinfPacketInputStream is)
  throws NinfException {
    d_index--;
    int start = shape.dim[d_index].start;
    int end   = shape.dim[d_index].end;
    int step  = shape.dim[d_index].step;
    int size  = shape.dim[d_index].size;
    
    if (d_index == 0){
      for (int i = start; i < end; i+= step){
	try {
	  os.writeString(is.readString());
	} catch (IOException e){
	  throw new NinfIOException(e);
	}
      }
    } else {
      for (int i = start; i < end; i+= step){
	readStringsRec(d_index, shape, os, is);
      }
    }
  }

  void readStrings(ArrayShape shape, BufferObject bo, NinfPacketInputStream is) 
    throws NinfException{
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    XDROutputStream os = new XDROutputStream(bs);
    if (ndim == 0) {
      try {
	os.writeString(is.readString());
      } catch (IOException e){
	throw new NinfIOException(e);
      }
    } else 
      readStringsRec(Math.abs(ndim), shape, os, is);
    bo.data = bs.toByteArray();
  }

  public void read(ArrayShape shape, BufferObject bo, 
		   NinfPacketInputStream is) throws NinfException{
    dbg.println(" reading data:"+ bo.data.length);
    int total = 1;

    if (bo.rsc != null) return;
    if (param_type == NinfVal.STRING_TYPE){
      readStrings(shape, bo, is);
      return;
    }
    if (ndim < 0){
      bo.sizes = new int[Math.abs(ndim)];
      for (int i = 0; i < Math.abs(ndim); i++){
	bo.sizes[i] = is.readInt();
	total *= bo.sizes[i];
      }
      bo.data = new byte[total* NinfVal.DATA_TYPE_SIZE[param_type]];
    }
    is.read(bo.data.length , bo.data);

    //    for (int i = 0; i < bo.data.length; i++)
    //      System.out.println(bo.data[i] + " " + (char)bo.data[i]);

  }

  public int scalarArg(Object o) throws NinfException{
    if (IS_IN_MODE() && ndim == 0){
      switch (param_type){
      case NinfVal.CHAR:
      case NinfVal.UNSIGNED_CHAR:
	return (int)(((Byte)o).byteValue());
      case NinfVal.SHORT:
      case NinfVal.UNSIGNED_SHORT:
	return (int)(((Short)o).shortValue());
      case NinfVal.INT:
      case NinfVal.UNSIGNED:
      case NinfVal.LONG:
      case NinfVal.UNSIGNED_LONG:
	return ((Integer)o).intValue();
	
      case NinfVal.UNSIGNED_LONGLONG:
      case NinfVal.LONGLONG:
	return (int)(((Long)o).longValue());
      case NinfVal.FLOAT:
	return (int)(((Float)o).floatValue());
	
      case NinfVal.DOUBLE:
	return (int)(((Double)o).doubleValue());
	
      case NinfVal.STRING_TYPE:
	return 0;
      case NinfVal.LONG_DOUBLE:
	dbg.log("not supported data types");
	throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
      default:
	dbg.log("unknown data type");
	throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
      }
    }
    return -100;
  }

  public int scalarBuffer(BufferObject bo) throws NinfException{
    byte buf[] = bo.data;

    long tmp = 0;
    int i;
    if (IS_IN_MODE() && ndim == 0){
      switch (param_type){
      case NinfVal.CHAR:
      case NinfVal.UNSIGNED_CHAR:
	return (int)buf[0];
      case NinfVal.SHORT:
      case NinfVal.UNSIGNED_SHORT:
	for (i = 0; i < 2; i++)
	  tmp = (tmp << 8) + (0xff & buf[i]);
	return (int)tmp;
      case NinfVal.INT:
      case NinfVal.UNSIGNED:
      case NinfVal.LONG:
      case NinfVal.UNSIGNED_LONG:
	for (i = 0; i < 4; i++)
	  tmp = (tmp << 8) + (0xff & buf[i]);
	return (int)tmp;
      case NinfVal.UNSIGNED_LONGLONG:
      case NinfVal.LONGLONG:
	for (i = 0; i < 8; i++)
	  tmp = (tmp << 8) + (0xff & buf[i]);
	return (int)tmp;
      case NinfVal.FLOAT:
	for (i = 0; i < 4; i++)
	  tmp = (tmp << 8) + (0xff & buf[i]);
	return (int)(Float.intBitsToFloat((int)tmp));
      case NinfVal.DOUBLE:
	for (i = 0; i < 8; i++)
	  tmp = (tmp << 8) + (0xff & buf[i]);
	return (int)(Double.longBitsToDouble(tmp));
      case NinfVal.STRING_TYPE:
	return 0;
      case NinfVal.LONG_DOUBLE:
	dbg.log("not supported data types");
	throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
      default:
	dbg.log("unknown data type");
	throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
      }
    }
    return -100;
  }

  public BufferObject transformArg(Object o, ArrayShape shape) 
  throws NinfException{
    return transformArg(o, shape, false);
  }

  public BufferObject transformArg(Object o, ArrayShape shape, boolean isCallback) 
  throws NinfException{
    if (IS_FUNC()){
      return new BufferObject(0);
    } else if (param_type != NinfVal.STRING_TYPE && o instanceof String){
      return new BufferObject((String)(o));
    } else if (ndim == 0){
      byte[] tmp = transformArgScalar(o);
      for (int i = 0; i < tmp.length; i++)
	dbg.println("tmp["+i+"] = "+tmp[i]);
      return new BufferObject(tmp, null);
    }
    else
      return transformArgVector(o, shape, isCallback);
  }

  void transformArgVectorRec(Object o, int d_index, XDRBuffer xs, DataAccess access,
			     ArrayShape shape)
  throws NinfException {
    d_index--;
    
    int start = shape.dim[d_index].start;
    int end   = shape.dim[d_index].end;
    int step  = shape.dim[d_index].step;
    int size  = shape.dim[d_index].size;
    
    if (d_index == 0){
      for (int i = start; i < end; i+= step)
	access.access(o, i, xs);
    } else {
      for (int i = start; i < end; i+= step){
	Object[] tmpArray = (Object[])o;
	transformArgVectorRec(tmpArray[i], d_index, xs, access, shape);
      }
    }
  }

  public BufferObject transformArgVector(Object o, ArrayShape shape, boolean isCallback)
  throws NinfException{
    int data_size = NinfVal.DATA_TYPE_SIZE[param_type];
    int sizes[] = new int[Math.abs(ndim)];
    BufferObject bo = setupBuffer(shape);
    if (!IS_IN_MODE() && !isCallback)
      return bo;
    /*
    int size = countSize(shape, sizes) * data_size;
    dbg.println("size = " + size);
    */
    XDRBuffer xs = new XDRBuffer(bo.data);
    DataAccess access = DataAccess.getAccess(param_type);
    transformArgVectorRec(o, Math.abs(ndim), xs, access, shape);
    bo.data = xs.getBuf();
    return bo;
  }

  void  revtransformArgVectorRec(Object o, int d_index, XDRBuffer xs, 
				 DataAccess access, ArrayShape shape)
  throws NinfException {
    d_index--;
    
    int start = shape.dim[d_index].start;
    int end   = shape.dim[d_index].end;
    int step  = shape.dim[d_index].step;
    int size  = shape.dim[d_index].size;
    
    if (d_index == 0){
      for (int i = start; i < end; i+= step)
	access.read(o, i, xs);
    } else {
      if (param_type != NinfVal.STRING_TYPE && o instanceof String)
	return;
      for (int i = start; i < end; i+= step){
	Object[] tmpArray = (Object[])o;
	revtransformArgVectorRec(tmpArray[i], d_index, xs, access, shape);
      }
    }
  }

  void reverseTransform(BufferObject bo, ArrayShape shape, Object o)
  throws NinfException{
    if (bo.rsc != null) return;  /* the argument is specified by URL */
    /*    if (bo.sizes != null)
          countSizeWithSize(context, bo.sizes);
    else {
      int sizes[] = new int[Math.abs(ndim)];
      countSize(iarg, sizes);
    }*/

    byte transed[] = bo.data;
    XDRBuffer xs = new XDRBuffer(transed);

    DataAccess access = DataAccess.getAccess(param_type);
    int i;
    revtransformArgVectorRec(o, Math.abs(ndim), xs, access, shape);
  }

  int setupBufferRec(int d_index, ArrayShape shape, int sizes[])
  throws NinfException {
    d_index--;
    
    int start = shape.dim[d_index].start;
    int end   = shape.dim[d_index].end;
    int step  = shape.dim[d_index].step;
    int size  = shape.dim[d_index].size;
    
    int tmp = 0;
    for (int i = start; i < end; i+= step)
      tmp ++;

    sizes[d_index] = tmp;
    if (d_index == 0){
      return tmp;
    } else {
      return setupBufferRec(d_index, shape, sizes) * tmp;
    }
  }
  
  ArrayShape setupArrayShape(int iargs[]) throws NinfException{
    ArrayShape tmp = new ArrayShape(Math.abs(ndim));
    for (int i = 0; i < ndim; i++)
      tmp.dim[i] = dim[i].setupArrayShape(iargs);
    return tmp;
  }

  public long getSize(ArrayShape shape) throws NinfException{
      int data_size = NinfVal.DATA_TYPE_SIZE[param_type];
      int sizes[] = new int[Math.abs(ndim)];
      return countSize(shape, sizes) * data_size;
  }

  public BufferObject setupBuffer(ArrayShape shape) throws NinfException{
    int size = 0;
    int data_size = 0;
    if (IS_WORK_MODE())
      return new BufferObject(0);
    if (IS_FUNC())
      return new BufferObject(0);
    if (ndim < 0)
      return new BufferObject();
    data_size = NinfVal.DATA_TYPE_SIZE[param_type];
    int sizes[] = new int[Math.abs(ndim)];
    size = countSize(shape, sizes) * data_size;
    if (param_type == NinfVal.CHAR  || param_type == NinfVal.UNSIGNED_CHAR ||
	param_type == NinfVal.SHORT || param_type == NinfVal.UNSIGNED_SHORT)
      size += (4 - size % 4) % 4;   /* alignment for XDR convension */
    return new BufferObject(size, sizes);
  }

  public Object setupArg(ArrayShape shape) throws NinfException{
    DataAccess access = DataAccess.getAccess(param_type);
    /*    int sizes[] = new int[Math.abs(ndim)];
    int size = countSize(shape, sizes); */
    return access.setupArg(shape);
  }


  public int countSize(ArrayShape shape, int sizes[])
  throws NinfException{
    if (ndim < 0)
      return 0;
    return setupBufferRec(ndim, shape, sizes);
  }

  public int countSizeWithSize(ArrayShape shape, int sizes[])
  throws NinfException{
    return setupBufferRec(Math.abs(ndim), shape, sizes);
  }

  public BufferObject setupScalarBuffer(){
    int size = NinfVal.DATA_TYPE_SIZE[param_type];
    int padding = 4;
    size += (padding - (size % padding)) % padding;
    dbg.println("alloc scalar buffer " + size + "bytes");
    return new BufferObject(size);
  }

  public byte[] transformArgScalar(Object o) throws NinfException{
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    XDROutputStream xs = new  XDROutputStream(bs);
    byte tmp[];
    try {
      switch (param_type){
      case NinfVal.CHAR:
      case NinfVal.UNSIGNED_CHAR:
	xs.writeByte((byte)(((Byte)o).byteValue()));
	return bs.toByteArray();
      case NinfVal.SHORT:
      case NinfVal.UNSIGNED_SHORT:
	xs.writeShort((short)(((Short)o).shortValue()));
	return bs.toByteArray();
      case NinfVal.INT:
      case NinfVal.UNSIGNED:
      case NinfVal.LONG:
      case NinfVal.UNSIGNED_LONG:
	xs.writeInt((int)(((Integer)o).intValue()));
	return bs.toByteArray();
      case NinfVal.LONG_DOUBLE:
      case NinfVal.UNSIGNED_LONGLONG:
	xs.writeLong((((Long)o).longValue()));
	return bs.toByteArray();
      case NinfVal.FLOAT:
	xs.writeFloat(((Float)o).floatValue());
	return bs.toByteArray();
      case NinfVal.DOUBLE:
	xs.writeDouble(((Double)o).doubleValue());
	return bs.toByteArray();
      case NinfVal.STRING_TYPE:
	xs.writeString((String)o);
	return bs.toByteArray();
      case NinfVal.LONGLONG:
	dbg.log("not supported data types");
	throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
      default:
	dbg.log("unknown data type");
	throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
      }
    } catch (IOException e){
      throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
    }
  }

  public void shift(int sh){
    for (int i = 0; i < ndim; i++)
      dim[i].shift(sh);
  }

  public void modify(DependInfo info){
    //    System.out.print("modify" + info);
    //    System.out.println("  " + info.isNoLower() + ", " + info.isNoUpper());
    if ((!info.isNoLower()) || (!info.isNoUpper()))
      param_inout &= MODE_INTERNAL;
  }


//================== << メソッドの追加 -- １４個 -- >> =======================
// 次元数が 0 であるかどうか（配列であるかどうか）
  public boolean IS_DIM_SIZE() { return (ndim == 0 && rdim[0].mat > 0);}

// size が 「VALUE_IN_ARG」（引数により与えられる）かどうか
  public boolean IS_DIM_SIZE_VALUE_IN_ARG(int i){
    return dim[i].IS_SIZE_VALUE_IN_ARG();
  }

// end が 「VALUE_IN_ARG」（引数により与えられる）かどうか
  public boolean IS_DIM_END_VALUE_IN_ARG(int i){
    return dim[i].IS_END_VALUE_IN_ARG();
  }

// size が 「VALUE_CONST」（定数）かどうか
  public boolean IS_DIM_SIZE_VALUE_CONST(int i){
    return dim[i].IS_SIZE_VALUE_CONST();
  }

// end が 「VALUE_CONST」（定数）かどうか
  public boolean IS_DIM_END_VALUE_CONST(int i){
    return dim[i].IS_END_VALUE_CONST();
  }

// end が記述されているかどうか
  public boolean IS_DIM_END_TYPE(int i){
    return dim[i].IS_END_TYPE();
  }

// 引数の i 番目の size の値を得る
  public int getDimSize(int i){
    return dim[i].getSizeVal();
  }

// 引数の i 番目の end の値を得る
  public int getDimEnd(int i){
    return dim[i].getEndVal();
  }

// 「行列サイズを示す引数 → 行列」の検索ポインタをつける 
  public void inputRdim(int mat, int size){
    if(rdim[nrdim -1].IS_MAT()){
       nrdim++;
       NinfRetDimParam[] tmp = new NinfRetDimParam[nrdim];
       for(int i = 0; i < nrdim -1; i++){
          tmp[i] = rdim[i].copy();
       }
       tmp[nrdim -1] = new NinfRetDimParam(mat, size);
       rdim = tmp;
       return;
    }else{
       rdim[nrdim -1].putInfo(mat, size);
       return;
    }
  }

// 引数が逆向き検索ポインタを持っているか
  public boolean IS_RDIM(){ return rdim[0].IS_MAT();}

// 引数が表すサイズは 行列の１次元のサイズか
  public boolean IS_RDIM_SIZE_X(int i){
    return rdim[i].IS_SIZE_X();
  }

// 引数が表すサイズは 行列の２次元のサイズか
  public boolean IS_RDIM_SIZE_Y(int i){
    return rdim[i].IS_SIZE_Y();
  }

// 引数が表す行列の順番（calling sequence の行列のみの引数の順番）
  public int getRdimMatIndex(int i){
    return rdim[i].mat;
  }

// 引数が表す行列サイズの次元
  public int getRdimDim(int i){
    return rdim[i].dim;
  }



/****************** to make netsolve problem ***************************/
  /*
  public boolean isUsedAsSize(int index){
    for (int i = 0; i < Math.abs(ndim); i++)
      if (dim[i].isUsedAsSize(index))
	return true;
    return false;
  }

  void makeMnemonicsSubSub(String s, int in_num, int out_num, Vector m){
    if (in_num >= 0)
      m.addElement(s + "I" + in_num);
    if (out_num >= 0)
      m.addElement(s + "O" + out_num);
  }


  void addConsts(String s, int in_num, int out_num, int val, Vector con){
    if (in_num >= 0)
      con.addElement(new Constant(s + "I" + in_num, val));
    //    if (out_num >= 0)
    //      con.addElement(new Constant(s + "O" + out_num, val));
  }

							  
  public void makeMnemonics(int in_num, int out_num, Vector mnemonics[], Vector constants) 
    throws CannotConvertException{
    if (Math.abs(ndim) > 2){
      System.err.println("netsolve cannot manage more than 3 dimensional array");
      throw new CannotConvertException();
    }
    if (ndim == 0)
      return;
    if (ndim == 1){
      if (dim[0].start.type != NinfVal.VALUE_NONE ||
	  dim[0].step.type  != NinfVal.VALUE_NONE){
	System.err.println("netsolve cannot manage step and start ");
	throw new CannotConvertException();
      }      
      //    if (dim[0].end.type != NinfVal.VALUE_NONE){
      makeMnemonicSub(dim[0].size, "m", in_num, out_num, mnemonics,  constants);
    } else {
      if (dim[0].start.type != NinfVal.VALUE_NONE ||
	  dim[0].step.type  != NinfVal.VALUE_NONE ||
	  dim[1].start.type != NinfVal.VALUE_NONE ||
	  dim[1].step.type  != NinfVal.VALUE_NONE ||
	  dim[1].end.type   != NinfVal.VALUE_NONE ){

	System.err.println("netsolve cannot manage step and start ");
	throw new CannotConvertException();
      }
      if (dim[0].end.type != NinfVal.VALUE_NONE){
	makeMnemonicSub(dim[0].end,  "m", in_num, out_num, mnemonics,  constants);
	makeMnemonicSub(dim[0].size, "l", in_num, out_num, mnemonics,  constants);
      } else {
	makeMnemonicSub(dim[0].size, "m", in_num, out_num, mnemonics,  constants);
	makeMnemonicSub(dim[0].size, "l", in_num, out_num, mnemonics,  constants);
      }
      makeMnemonicSub(dim[1].size, "n", in_num, out_num, mnemonics,  constants);
    }
  }

  void makeMnemonicSub(NinfDimParamElem elem, String s, int in_num, int out_num,
		       Vector mnemonics[], Vector constants){
    switch (elem.type){
    case NinfVal.VALUE_NONE:
      addConsts(s, in_num, out_num, 1, constants);
      break;
    case NinfVal.VALUE_CONST:
      addConsts(s, in_num, out_num, elem.val, constants);
      break;
    case NinfVal.VALUE_IN_ARG:
      makeMnemonicsSubSub(s, in_num, out_num, mnemonics[elem.val]);
      break;
    }
  }

  */
}
