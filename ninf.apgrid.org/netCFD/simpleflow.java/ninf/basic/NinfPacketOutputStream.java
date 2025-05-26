package ninf.basic;

import java.io.*;

public class NinfPacketOutputStream {
  private static int XDRLIMIT = 4;

  XDROutputStream os;
  byte buffer[];
  static final int buflen = NinfPacket.MAX_PKT_LEN - NinfPktHeader.PktHeaderSize;
  NinfLog dbg = new NinfLog(this);

  int code = NinfPktHeader.NINF_PKT_TO_STUB;
  int size;
  boolean usearg=false;
  int arg1=0;
  int arg2=0;

  /**
    Construct NinfPacket Stream
    (code = toStub ? NINF_PKT_TO_STUB : NINF_PKT_TO_CLIENT)
   */
  public NinfPacketOutputStream (XDROutputStream s, boolean toStub){
    os = s;
    buffer = new byte[buflen];
    size = 0;
    usearg = false;
    if (!toStub)
      code = NinfPktHeader.NINF_PKT_TO_CLIENT;
  }

  /**
    Construct NinfPacket Stream (code = NINF_PKT_TO_STUB)
   */
  public NinfPacketOutputStream (XDROutputStream s){
    this(s, true);
  }

  /**
    Construct NinfPacket Stream
    (code = arg_code)
   */
  public NinfPacketOutputStream(XDROutputStream s, int arg_code) {
    os = s;
    buffer = new byte[buflen];
    size = 0;
    code = arg_code;
    usearg = false;
  }

  /**
    Construct NinfPacket Stream
    (code = arg_code, arg1 = a1, arg2 = a2)
   */
  public NinfPacketOutputStream(XDROutputStream s,
				int arg_code, int a1, int a2) {
    this(s, arg_code);
    arg1 = a1;
    arg2 = a2;
    usearg = true;
  }

  public void writeString(String s)  throws NinfIOException {
    int length = s.length();
    this.writeInt(length);
    int rest = length % XDRLIMIT;
    rest = (rest != 0)? XDRLIMIT - rest: rest;
    for (int i = 0; i < length; i++){
      this.writeByte((byte)(s.charAt(i)));
    }
    for (int i = 0; i < rest; i++){
      this.writeByte((byte)0);
    }
  }

  public void writeInt(int i) throws NinfIOException {
    try{
      ByteArrayOutputStream bs = new ByteArrayOutputStream();
      XDROutputStream tmp = new XDROutputStream(bs);
      tmp.writeInt(i);
      write(bs.toByteArray());
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }

  public void writeDouble(double d) throws NinfIOException {
    try{
      ByteArrayOutputStream bs = new ByteArrayOutputStream();
      XDROutputStream tmp = new XDROutputStream(bs);
      tmp.writeDouble(d);
      write(bs.toByteArray());
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }
  public void writeByte(byte b) throws NinfIOException  {
    byte tmp[] = new byte[1];
    tmp[0] = b;
    write(tmp);
  }


  public void write(byte[] data) throws NinfIOException {
    int pointer = 0;
    while (data.length - pointer >  buflen - size){
      int len = buflen - size;
      System.arraycopy(data, pointer, buffer, size, len);
      size += len;
      pointer += len;
      //      dbg.print("flushing, rest " + (data.length - pointer)  + ": ");
      flush();
    }
    System.arraycopy(data, pointer, buffer, size, data.length - pointer );
    size += data.length - pointer;
  }

  /*
  public void write(byte[] data) throws NinfIOException {
    int pointer = 0;
    if (size != 0)
      flush();
    while (data.length - pointer >  buflen){
      writeUnder(data, pointer, buflen);
      pointer += buflen;
      //      dbg.print("flushing, rest " + (data.length - pointer)  + ": ");
    }
    System.arraycopy(data, pointer, buffer, size, data.length - pointer );
    size += data.length - pointer;
  }
  */
  void writeUnder(byte[] buf, int start, int len) throws NinfIOException {
    NinfPacket pkt =
      usearg
      ? new NinfPacket(code, len, arg1, arg2, buf, start)
      : new NinfPacket(code, len, buf, start);
    pkt.write(os);
    //    dbg.println("flushed: "+ size+ "bytes (code=" + code + ")");
    size = 0;
  }

  public void flush() throws NinfIOException {
    NinfPacket pkt =
      usearg
      ? new NinfPacket(code, size, arg1, arg2, buffer)
      : new NinfPacket(code, size, buffer);
    pkt.write(os);
    try {
      os.flush();
    } catch (IOException e){
      throw new NinfIOException(e);
    }
    //    dbg.println("flushed: "+ size+ "bytes (code=" + code + ")");
    size = 0;
  }

  public void close() throws NinfIOException {
    try {
      os.close();
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }
}

