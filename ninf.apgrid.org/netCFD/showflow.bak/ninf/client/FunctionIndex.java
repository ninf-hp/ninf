// NinfServervStruct.java

package ninf.client;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import ninf.basic.*;


class FunctionIndex {
  public ServerNotifiable func;
  public int index;

  public FunctionIndex(ServerNotifiable func, int index){
    this.func = func;
    this.index = index;
  }
}
