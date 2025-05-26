package ninf.client;
import ninf.basic.*;

public class DataAccess {
  static NinfLog dbg = new NinfLog("ninf.DataAccess");

  public static DataAccess getAccess(int flag){
    switch(flag){
    case NinfVal.CHAR:
    case NinfVal.UNSIGNED_CHAR:
      return new ByteAccess();
    case NinfVal.SHORT:
    case NinfVal.UNSIGNED_SHORT:
      return new CharAccess();
    case NinfVal.INT:
    case NinfVal.UNSIGNED:
    case NinfVal.LONG:
    case NinfVal.UNSIGNED_LONG:
      return new IntAccess();
    case NinfVal.LONG_DOUBLE:
    case NinfVal.UNSIGNED_LONGLONG:
      return new LongAccess();
    case NinfVal.FLOAT:
      return new FloatAccess();
    case NinfVal.DOUBLE:
      return new DoubleAccess();
    case NinfVal.STRING_TYPE:
      return new StringAccess();
    case NinfVal.LONGLONG:
      dbg.println("not supported data types");
      break;
    default:
      System.err.println("unknown data type");
      break;
    }
    return new DataAccess();
  }

  public int size(){return 0;}
  public void access(Object o, int i, XDRBuffer os)
  throws Exception {}
  public void read(Object o, int i, XDRBuffer is)
throws Exception {}
  Object setupArg(int i){return null;}
}

class ByteAccess extends DataAccess {
  public int size() {return 1;}
  public void access(Object o, int i, XDRBuffer os)throws Exception{
    os.writeByte(((byte[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws Exception{
    ((byte[])o)[i] = is.readByte();
  }
  Object setupArg(int i){return new byte[i];}
}
class CharAccess extends DataAccess {
  public int size() {return 2;}
  public void access(Object o, int i, XDRBuffer os) throws Exception{
    os.writeChar(((char[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws Exception{
    ((char[])o)[i] = is.readChar();
  }
  Object setupArg(int i){return new char[i];}
}
class IntAccess extends DataAccess {
  public int size() {return 4;}
  public void access(Object o, int i, XDRBuffer os) throws Exception{
    os.writeInt(((int[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws Exception{
    ((int[])o)[i] = is.readInt();
  }
  Object setupArg(int i){return new int[i];}
}
class LongAccess extends DataAccess {
  public int size() {return 8;}
  public void access(Object o, int i, XDRBuffer os) throws Exception{
    os.writeLong(((long[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws Exception{
    ((long[])o)[i] = is.readLong();
  }
  Object setupArg(int i){return new long[i];}
}
class FloatAccess extends DataAccess {
  public int size() {return 4;}
  public void access(Object o, int i, XDRBuffer os) throws Exception{ 
    os.writeFloat(((float[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws Exception{
    ((float[])o)[i] = is.readFloat();
  }
  Object setupArg(int i){return new float[i];}
}
class DoubleAccess extends DataAccess {
  public int size() {return 8;}
  public void access(Object o, int i, XDRBuffer os) throws Exception {
    os.writeDouble(((double[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws Exception{
    ((double[])o)[i] = is.readDouble();
  }
  Object setupArg(int i){return new double[i];}
}

class StringAccess extends DataAccess {
  public int size() {return 0;}
  public void access(Object o, int i, XDRBuffer os) throws Exception {
    os.writeString(((String[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws Exception{
    ((String[])o)[i] = is.readString();
  }
  Object setupArg(int i){return new String[i];}
}

