package ninf.basic;
import ninf.basic.*;
import ninf.client.*;


import java.io.*;

public class ShadowServer implements Runnable{
  XDRInputStream istream; // InputStream from Server
  XDROutputStream ostream; // OutputStream to client
  static NinfLog dbg = new NinfLog("Back");

  Thread current;

  public ShadowServer (XDRInputStream is, XDROutputStream os){
    istream = is;
    ostream = os;
    current = new Thread(this);
    current.start();
  }

  public void stop() {
    current.stop();
    try {
      istream.close();
      ostream.close();
    } catch (IOException e){}
  }

  public void run() {
    try {
      while(true){
	// input from Server and forward to client
	NinfPacket pkt = new NinfPacket(istream);
	//	dbg.println(pkt.hdr);
	pkt.write(ostream);
      }
    } catch (NinfIOException e){}
  }
}
