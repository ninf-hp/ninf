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
      return new ShortAccess();
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
    throws NinfException {}
  public void read(Object o, int i, XDRBuffer is)
    throws NinfException {}
  public void accessSingle(Object o, int i, XDRBuffer os)
    throws NinfException {
      access(o, i, os);
  }
  public void readSingle(Object o, int i, XDRBuffer is)
    throws NinfException {
      read(o, i, is);
  }
  Object[][] setupArg3D(int i){return null;}
  Object[] setupArg2D(int i){return null;}
  Object setupArg(int i){return null;}

  //  Object setupArgRec(ArrayShapeElem dim[], int index){
  //    index--;
  //    if (index == 0)
  //      return setupArg(dim[index].size);
  //    Object tmp[];
  //    if (index == 1)
  //      tmp = setupArg2D(dim[index].size);
  //    else 
  //      tmp = setupArg3D(dim[index].size);
  //    for (int i = 0; i < dim[index].size; i++){
  //      tmp[i] = setupArgRec(dim, index);
  //    }
  //    return tmp;
  //  }

  Object setupArg(ArrayShape shape) throws NinfException{
    if(shape.dim.length == 1){
      return setupArg(shape.dim[0].size);
    } else if(shape.dim.length == 2){
      Object [] tmp = setupArg2D(shape.dim[1].size);
      for (int i = 0; i < shape.dim[1].size; i++)
	tmp[i] = setupArg(shape.dim[0].size);
      return tmp;
    } else if(shape.dim.length == 3){
      Object [] tmp = setupArg3D(shape.dim[2].size);
      for(int i = 0; i < shape.dim[2].size; i++){
	Object [] tmp1 = setupArg2D(shape.dim[2].size);
	for(int j = 0; j < shape.dim[1].size; j++)
	  tmp1[j] = setupArg(shape.dim[0].size);
	tmp[i] = tmp1;
      }
      return tmp;
    } else {
      throw new NinfException("give up");
    }
  }
}

class ByteAccess extends DataAccess {
  public int size() {return 1;}
  public void access(Object o, int i, XDRBuffer os)throws NinfException{
    os.writeByte(((byte[])o)[i]);
  }
  public void accessSingle(Object o, int i, XDRBuffer os)throws NinfException{
    access(o, i, os);
    os.align();
  }
  public void read(Object o, int i, XDRBuffer is)throws NinfException{
    ((byte[])o)[i] = is.readByte();
  }
  public void readSingle(Object o, int i, XDRBuffer is)throws NinfException{
    read(o, i, is);
    is.align();
  }
  Object[][] setupArg3D(int i){return new byte[i][][];}
  Object[] setupArg2D(int i){return new byte[i][];}
  Object setupArg(int i){return new byte[i];}
}

class ShortAccess extends DataAccess {
  public int size() {return 2;}
  public void access(Object o, int i, XDRBuffer os) throws NinfException{
    os.writeShort(((short[])o)[i]);
  }
  public void accessSingle(Object o, int i, XDRBuffer os)throws NinfException{
    access(o, i, os);
    os.align();
  }
  public void read(Object o, int i, XDRBuffer is)throws NinfException{
    ((short[])o)[i] = is.readShort();
  }
  public void readSingle(Object o, int i, XDRBuffer is)throws NinfException{
    read(o, i, is);
    is.align();
  }
  Object[][] setupArg3D(int i){return new char[i][][];}
  Object[] setupArg2D(int i){return new char[i][];}
  Object setupArg(int i){return new char[i];}
}

class IntAccess extends DataAccess {
  public int size() {return 4;}
  public void access(Object o, int i, XDRBuffer os) throws NinfException{
    os.writeInt(((int[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws NinfException{
    ((int[])o)[i] = is.readInt();
  }
  Object[][] setupArg3D(int i){return new int[i][][];}
  Object[] setupArg2D(int i){return new int[i][];}
  Object setupArg(int i){return new int[i];}
}
class LongAccess extends DataAccess {
  public int size() {return 8;}
  public void access(Object o, int i, XDRBuffer os) throws NinfException{
    os.writeLong(((long[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws NinfException{
    ((long[])o)[i] = is.readLong();
  }
  Object[][] setupArg3D(int i){return new long[i][][];}
  Object[] setupArg2D(int i){return new long[i][];}
  Object setupArg(int i){return new long[i];}
}
class FloatAccess extends DataAccess {
  public int size() {return 4;}
  public void access(Object o, int i, XDRBuffer os) throws NinfException{ 
    os.writeFloat(((float[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws NinfException{
    ((float[])o)[i] = is.readFloat();
  }
  Object[][] setupArg3D(int i){return new float[i][][];}
  Object[] setupArg2D(int i){return new float[i][];}
  Object setupArg(int i){return new float[i];}
}
class DoubleAccess extends DataAccess {
  public int size() {return 8;}
  public void access(Object o, int i, XDRBuffer os) throws NinfException {
    os.writeDouble(((double[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws NinfException{
    ((double[])o)[i] = is.readDouble();
  }
  Object[][] setupArg3D(int i){return new double[i][][];}
  Object[] setupArg2D(int i){return new double[i][];}
  Object setupArg(int i){return new double[i];}
}

class StringAccess extends DataAccess {
  public int size() {return 0;}
  public void access(Object o, int i, XDRBuffer os) throws NinfException {
    os.writeString(((String[])o)[i]);
  }
  public void read(Object o, int i, XDRBuffer is)throws NinfException{
    ((String[])o)[i] = is.readString();
  }
  Object[][] setupArg3D(int i){return new String[i][][];}
  Object[] setupArg2D(int i){return new String[i][];}
  Object setupArg(int i){return new String[i];}
}

