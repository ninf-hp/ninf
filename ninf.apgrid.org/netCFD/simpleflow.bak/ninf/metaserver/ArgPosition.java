package ninf.metaserver;

import ninf.basic.*;
import ninf.client.*;

import java.io.IOException;
public class ArgPosition{
  public int functionIndex;
  public int argNum;

  public ArgPosition(NinfPacketInputStream is) throws NinfException{
    read(is);
  }

  public void read(NinfPacketInputStream is) throws NinfException {
    functionIndex = is.readInt();
    argNum = is.readInt();
  }

  public int serialize(int argNums[]) {
    return argNums[functionIndex] + argNum;
  }
}
