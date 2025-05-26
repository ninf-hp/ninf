package silf.util;

public class EnhancedDataOutputStream extends java.io.DataOutputStream {
  public EnhancedDataOutputStream(java.io.OutputStream os){
    super(os);
  }

  public void writeString(String str)
       throws java.io.IOException {
    byte [] array = str.getBytes();
    writeInt(array.length);
    write(array);
  }
}
