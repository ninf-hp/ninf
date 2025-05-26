package ninf.cproxy;
import ninf.basic.*;
import ninf.client.*;

abstract public class Callable {
  abstract public Stoppable call(NinfPacket pkt, XDRInputStream is, XDROutputStream os) 
    throws NinfException;
  abstract NinfStub getStub();
  int index;
}

// end of NinfCallable.java
