import ninf.basic.*;
import ninf.client.*;
import java.io.*;

class LogServer extends NServer {

  public static void main(String args[]){
    int port = 3040;
    if (args.length > 0)
      port = (new Integer(args[0])).intValue();
    new LogServer().startServer(port);
  }
  public void serviceRequest() {
    byte buf[] = new byte[300];
    int n;
    try {
      while ((n = clientInput.read(buf, 0, buf.length)) >= 0) {
	System.out.write(buf, 0, n);
      }
    } catch (Exception e) {
      System.err.println("serviceRequest: read failure " + e);
    }
  }  
}
