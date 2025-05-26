package ninf.basic;

import java.io.IOException;
import java.io.*;
import java.io.PrintStream;

public class NinfPacketInputStream {
  XDRInputStream is;
  OutputStream stdoutStream, stderrStream;
  NinfPacket cpacket;
  int cp, max;
  private static int XDRLIMIT = 4;
  NinfLog dbg = new NinfLog(this);

  static final int buflen = NinfPacket.MAX_PKT_LEN - NinfPktHeader.PktHeaderSize;
  
  public NinfPacketInputStream(XDRInputStream s, OutputStream stdoutStream, 
			       OutputStream stderrStream){
    this(s);
    this.stdoutStream = stdoutStream;
    this.stderrStream = stderrStream;
  }

  public NinfPacketInputStream(XDRInputStream s){
    is = s;
    cp = 0;
    max = 0;
  }
  public NinfPacketInputStream(NinfPacket p, XDRInputStream s){
    cpacket = p;
    is = s;
    cp = 0;
    max = p.hdr.size;
  }
  void readPacket() throws NinfException {
    cpacket = new NinfPacket(is);
    cp = 0;
    max = cpacket.hdr.size;

    if (cpacket.hdr.code == NinfPktHeader.NINF_PKT_ERROR)
      throw new NinfErrorException(cpacket.hdr.arg1);

    if (!(cpacket.hdr.code == NinfPktHeader.NINF_PKT_STDOUT ||
	  cpacket.hdr.code == NinfPktHeader.NINF_PKT_STDERR))
      return;

    if (cpacket.hdr.code == NinfPktHeader.NINF_PKT_STDOUT &&
	stdoutStream != null){
      try{
	stdoutStream.write(cpacket.buf);
      }catch (IOException e){}
    } else if (cpacket.hdr.code == NinfPktHeader.NINF_PKT_STDERR &&
	stderrStream != null){
      try{
	stderrStream.write(cpacket.buf);
      }catch (IOException e){}
    }
    readPacket();
  }	

  public int rest(){
    return (max - cp);
  }

  public void dumpRest(PrintStream os){
    for (int i = 0; i < 20; i++){
      for (int j = 0; j < 20 && (i * 20 + j) < (max - cp); j++){
	os.print(cpacket.buf[(i * 20 + j) + cp] + " ");
      }
      os.println("");
    }
  }

  public int readInt() throws NinfException {

    byte b[] = new byte[4];
      read(4, b);
    return ((b[0] << 24) & (0xFF << 24)) |
           ((b[1] << 16) & (0xFF << 16)) |
	   ((b[2] <<  8) & (0xFF <<  8)) |
	   ((b[3] <<  0) & (0xFF <<  0));

/*    return b[0] * 0x1000000 + b[1] * 0x10000 + b[2] * 0x100 + b[3];
    ByteArrayInputStream bs = new ByteArrayInputStream(b);
    XDRInputStream tmp = new XDRInputStream(bs);
    return tmp.readInt();
*/
  }

  public long readLong() throws NinfException {

    byte b[] = new byte[8];
      read(8, b);
    return ((b[0] << 56) & (0xFF << 56)) |
           ((b[0] << 48) & (0xFF << 48)) |
           ((b[0] << 40) & (0xFF << 40)) |
           ((b[0] << 32) & (0xFF << 32)) |
           ((b[0] << 24) & (0xFF << 24)) |
           ((b[1] << 16) & (0xFF << 16)) |
	   ((b[2] <<  8) & (0xFF <<  8)) |
	   ((b[3] <<  0) & (0xFF <<  0));
  }

  public double readDouble() throws NinfException {
    byte b[] = new byte[8];
    read(8, b);

    ByteArrayInputStream bs = new ByteArrayInputStream(b);
    XDRInputStream tmp = new XDRInputStream(bs);
    try {
      return tmp.readDouble();
    } catch (IOException e){ 
      throw new NinfIOException(e);
    }
  }

  public String readString() throws NinfException {
    int length = this.readInt();
    byte cont[] = new byte[length];
    int rest = length % XDRLIMIT;
    rest = (rest != 0)? XDRLIMIT - rest: rest;
    for (int i = 0; i < length; i++){
      cont[i] = this.readByte();
    }
    for (int i = 0; i < rest; i++){
      byte tmp = this.readByte();
    }
    return new String(cont, 0);
  }

  public byte readByte() throws NinfException{
    byte tmp[] = new byte[1];
    read(1, tmp);
    return tmp[0];
  }

  public void read(int length, byte buf[]) throws NinfException {
    int pointer = 0;
    //System.out.println("Reading from Packet "+
    //(max - cp) + " ," + (length - pointer));
    
    if (cpacket == null) readPacket();
    while ((max - cp) < length - pointer){
      System.arraycopy(cpacket.buf, cp, buf, pointer, max - cp);
      pointer += max - cp;
      readPacket();
    }
    System.arraycopy(cpacket.buf, cp, buf, pointer, length - pointer);
    cp += length - pointer;
  }

  public NinfPacket getRestPacket() throws NinfException{
    int restsize = rest();
    byte rest[] = new byte[restsize];
    read(restsize, rest);
    return new NinfPacket(cpacket.hdr.code, restsize, 
			  cpacket.hdr.arg1, cpacket.hdr.arg2, rest);
  }


  public int getCode() throws NinfException{
    if (cpacket == null) readPacket();
    return cpacket.hdr.code;
  }
}
