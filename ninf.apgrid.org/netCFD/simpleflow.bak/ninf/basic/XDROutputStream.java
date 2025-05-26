package ninf.basic;

import java.io.*;

public class XDROutputStream extends DataOutputStream {
  private static int XDRLIMIT = 4;

  public XDROutputStream(OutputStream i){
    super(i);
  }

  public void writePad(int len) throws IOException {
    for (int i = 0; i < len; i++)
      write((byte)0);

  }
  public void writeString(String s)  throws IOException {
    int length = s.length();
    this.writeInt(length);
    int rest = length % XDRLIMIT;
    rest = (rest != 0)? XDRLIMIT - rest: rest;
    for (int i = 0; i < length; i++){
      this.writeByte((byte)(s.charAt(i)));
    }
    for (int i = 0; i < rest; i++){
      this.writeByte(0);
    }
  }

}
