package ninf.basic;

import java.io.*;

public class NinfPacket {
  public NinfPktHeader hdr;
  public byte buf[];
  int start = 0;
  public static final int MAX_PKT_LEN = 0x1000;
  static NinfLog dbg = new NinfLog("NinfPacket");

  
  /** make req_stub_packet */
  public static NinfPacket getStubPacket(String str) {
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    XDROutputStream tmp = new XDROutputStream(bs);
    try {
      tmp.writeString(str);
    }catch (IOException e){
      /* never happen */
    }
    return new NinfPacket(NinfPktHeader.NINF_PKT_REQ_STUB_INFO, 
			 bs.size(), bs.toByteArray());
  }

  public static NinfPacket getStubPacket(int index) {
    return new NinfPacket(NinfPktHeader.NINF_PKT_REQ_STUB_BY_INDEX, 
			  0, index, 0);
  }

  /** make call_packet */
  public static NinfPacket getCallPacket(int index){
    return new NinfPacket(NinfPktHeader.NINF_PKT_REQ_CALL, 0, index, 0);
  }

  /** make kill_stub_packet */
  public static NinfPacket getKillPacket(){
    return new NinfPacket(NinfPktHeader.NINF_PKT_KILL, 0, 0, 0);
  }

  /** make erro_packet */
  public static NinfPacket getErrorPacket(int code){
    return new NinfPacket(NinfPktHeader.NINF_PKT_ERROR, 0, code, 0);
  }

  /** make rpy_stub_packet */
  public static NinfPacket getRpyStubPacket(byte[] b, int i){
    //    dbg.println("getRpyStubPacket: stub packet size "+ b.length );
    return new NinfPacket(NinfPktHeader.NINF_PKT_RPY_STUB_INFO, 
			  b.length, i, 0, b);
  }

  /** make rpy_call_packet */
  public static NinfPacket getRpyCallPacket(){
    return new NinfPacket(NinfPktHeader.NINF_PKT_RPY_CALL, 0, 0, 0);
  }

  public static NinfPacket getReqStubIndexListPacket(String keyword, int option){
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    XDROutputStream tmp = new XDROutputStream(bs);
    try {			 
      tmp.writeString(keyword);
    }catch (IOException e){
      // never happen
    }
    return new NinfPacket(NinfPktHeader.NINF_PKT_REQ_STUB_INDEX_LIST, 
			  bs.size(), option, 0, bs.toByteArray());
  }

  public NinfPacket(int code, int size, byte b[]){
    hdr = new NinfPktHeader(code, size);
    buf = b;
  }
  public NinfPacket(int code, int size, byte b[], int start){
    hdr = new NinfPktHeader(code, size);
    buf = b;
    this.start = start;
  }

  public NinfPacket(int code, int size, int arg1, int arg2){
    hdr = new NinfPktHeader(code, size, arg1, arg2);
  }

  public NinfPacket(int code, int size, int arg1, int arg2, byte b[]){
    hdr = new NinfPktHeader(code, size, arg1, arg2);
    buf = b;
  }

  public NinfPacket(int code, int size, int arg1, int arg2, byte b[], int start){
    hdr = new NinfPktHeader(code, size, arg1, arg2);
    buf = b;
    this.start = start;
  }

  public String toString(){
    String tmp = "code = " + hdr.code + ", size = " + hdr.size
             + ", arg1 = " + hdr.arg1 + ", arg2 = " + hdr.arg2 + "\n";
    StringBuffer sb = new StringBuffer(tmp);
    for (int i = 0; i < buf.length; i++)
      sb.append(buf[i] + " ");
    sb.append("\n");
    return sb.toString();
  }


  public NinfPacket(XDRInputStream is) throws NinfIOException {
    try {
      //      dbg.println("read a packet header");
      hdr = new NinfPktHeader(is);
      // dbg.println("size = " + hdr.size + ", code = " + hdr.code);
      buf = new byte[hdr.size];
      is.readBytes(buf, 0, hdr.size);
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }

  public void write(XDROutputStream os) throws NinfIOException{
    try {
      hdr.writeTo(os);
      //      dbg.println("writing size = " + hdr.size);    
      if (buf != null) os.write(buf, start, hdr.size);
      os.flush();
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }

  public String readString() throws NinfIOException {
    String s;
    try {
      ByteArrayInputStream bs = new ByteArrayInputStream(buf);
      XDRInputStream tmp = new XDRInputStream(bs);
      s = tmp.readString();
    } catch (IOException e){
      throw new NinfIOException(e);
    }
    return s;
  }
}

