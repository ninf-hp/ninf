// ScanfInputStream.java

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

class ScanfInputStream{
  DataInputStream dis;
  Vector tokens = null;
  int index = 0;

  ScanfInputStream(InputStream is){
    dis = new DataInputStream(is);
  }

  Vector tokenize(String s) {  
    Vector v = null;
    StringTokenizer st = new StringTokenizer(s, ", \t\n");
    while (st.hasMoreTokens()) {
      if (v == null) v = new Vector();
      v.addElement(st.nextToken());
    }
    return v;
  }
  int readInt() throws Exception {
    return (Integer.valueOf(readString())).intValue();
  }
  float readFloat() throws Exception {
    return (Float.valueOf(readString())).floatValue();
  }

  String readString() throws Exception {
    if (tokens == null || index >= tokens.size()){
      String s = dis.readLine();
      tokens = tokenize(s);
      index = 0;
    }
    return (String)tokens.elementAt(index++);
  }
}

// end of MetaServerConfig.java
