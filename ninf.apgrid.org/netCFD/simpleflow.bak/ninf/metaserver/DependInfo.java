package ninf.metaserver;

import ninf.basic.*;
import ninf.client.*;


import java.io.IOException;

public class DependInfo {
  // *********************** NON STATIC VARIABLES ***********************
  public ArgPosition inputDepend;
  public ArgPosition outputDepends[];

  // ***********************  INSTANCE  CREATION  ***********************
  public DependInfo(NinfPacketInputStream is) throws NinfException{
    read(is);
  }

  // ***********************     I/O  METHODS     ***********************
  public void read(NinfPacketInputStream is) throws NinfException {
    int inputNum = is.readInt();
    if (inputNum != 0)
      inputDepend = new ArgPosition(is);
    int outputNum = is.readInt();
    outputDepends = new ArgPosition[outputNum];
    for (int i = 0; i < outputNum; i++)
      outputDepends[i] = new ArgPosition(is);
  }

  public String toString() {
//    String tmp = "dependInfo input:" + ((inputDepend == null)? 0 : 1) 
//               + " output: " + outputDepends.length;
    String tmp =
      (inputDepend == null) ? "{:" : ("{(" + inputDepend.functionIndex + "," +
				      inputDepend.argNum + "):");

    boolean first = true;
    for (int i = 0; i < outputDepends.length; i++) {
      if (!first) tmp += ",";
      first = false;
      tmp = tmp + "(" +
	outputDepends[i].functionIndex + "," + outputDepends[i].argNum + ")";
    }
    tmp += "}";
    return tmp;
  }

  // ***********************  NON STATIC METHODS  ***********************
  public boolean isNoLower() {
    if (outputDepends.length == 0)
      return true;
    return false;
  }

  public boolean isNoUpper() {
    if (inputDepend == null)
      return true;
    return false;
  }
}


// end of DependInfo.java
