package silf.rjava;

import silf.util.*;
import java.io.*;
import java.util.*;

public class RemoteFileOutputStream extends FilterOutputStream {
  static HashMap map = new HashMap();

  String tmpFileName;
  String remoteFileName;

  static private void putFile(String name, byte[] buffer) throws IOException{
    FileOutputStream fos = new FileOutputStream(name);
    fos.write(buffer);
    fos.close();
  }

  static OutputStream getFileOutputStream(String filename, boolean append)
      throws IOException{      
    if (RJavaServerCore.instance != null){
      File tmpFile = File.createTempFile("rjava", "");
      map.put(filename, tmpFile.getPath());

      if (append == true){	
	byte [] buffer = RJavaServerCore.instance.getFileBytes(filename);
	putFile(tmpFile.getPath(), buffer);
      }
      return new FileOutputStream(tmpFile.getPath(), true);
    } else {
      return new FileOutputStream(filename, append);
    }
  }

  public RemoteFileOutputStream(String filename) throws IOException {
    super(getFileOutputStream(filename, false));
    remoteFileName = filename;
    tmpFileName = (String) map.get(filename);
  }

  public RemoteFileOutputStream(String filename, boolean append) 
       throws IOException {
    super(getFileOutputStream(filename, append));
    remoteFileName = filename;
    tmpFileName = (String) map.get(filename);
  }

  private byte[] getFile(String name) throws IOException{
    File file = new File(name);
    int length = (int)file.length();
    byte [] buffer = new byte[length];
    FileInputStream fis = new FileInputStream(file.getPath());
    int read = 0;
    while (read < length){
      int tmp = fis.read(buffer, read, length - read);
      read += tmp;
    }
    return buffer;
  }

  public void close() throws IOException {
    out.close();
    if (tmpFileName != null){
      RJavaServerCore core = RJavaServerCore.instance;
      byte[] buffer = getFile(tmpFileName);
      core.putRemoteFile(remoteFileName, buffer);
      (new File(tmpFileName)).delete();
    }
  }


}



