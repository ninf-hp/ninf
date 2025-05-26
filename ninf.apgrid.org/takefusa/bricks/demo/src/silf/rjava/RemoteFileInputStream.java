package silf.rjava;

import silf.util.*;
import java.io.*;

public class RemoteFileInputStream extends FilterInputStream {
  RJavaServerCore core;
  
  static InputStream getFileInputStream(String filename) 
     throws FileNotFoundException {
    if (RJavaServerCore.instance != null){
      byte [] buffer = RJavaServerCore.instance.getFileBytes(filename);
      return new ByteArrayInputStream(buffer);
    }
    else 
      return new FileInputStream(filename);
  }

  public RemoteFileInputStream(String filename) throws FileNotFoundException {
    super(getFileInputStream(filename));
  }

}
