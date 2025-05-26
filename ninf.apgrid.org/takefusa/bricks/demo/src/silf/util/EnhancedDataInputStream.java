package silf.util;

public class EnhancedDataInputStream extends java.io.DataInputStream {
  public EnhancedDataInputStream(java.io.InputStream is){
    super(is);
  }

  public String readString()
       throws java.io.IOException {
    int len = readInt();
    byte [] buffer = new byte[len];
    read(buffer, 0, len);
    return new String(buffer, 0, len);
  }

  public int readfully(byte[ ] buffer, int off, int len) 
       throws java.io.IOException {
    int readed = 0;

    while (readed < len){
      int tmp = read(buffer, readed + off, len - readed);
      readed += tmp;
    }
    return len;
  }


}
