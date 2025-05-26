//
// NServer - replacement of NetworkServer
//

package ninf.basic;

import java.io.*;
import java.net.*;

/** Network Server class like NetworkServer */
public class NServer implements Runnable,Cloneable {
  protected ServerSocket masterSocket = null;
  protected Socket clientSocket = null;
  protected boolean amIMaster;
  protected Thread masterServerInstance;
  protected NinfLog dbg = new NinfLog(this);

  protected PrintStream clientOutput;
  protected InputStream clientInput;

  static final int sleepTime = 1000;

  public void close() {
    try {
      clientOutput.close();
      clientInput.close();
      clientSocket.close();
    } catch (Exception e) {
      dbg.println("NServer.close: " + e);
    }
    clientOutput = null;
    clientInput = null;
    clientSocket = null;
  }      
  final public void run() {
    if (amIMaster) {
      while (true) {
	Socket s = null;
	try {
	  s = masterSocket.accept();
	} catch (Exception e) {
	  System.err.println("NServer.run: accept: " + e);
	  try {
	    Thread.currentThread().sleep(sleepTime);
	  } catch(InterruptedException ie) {
	    ie.printStackTrace();
	  }
	  continue;
	}
	try {
	  NServer handler = (NServer)this.clone();
	  handler.clientSocket = s;
	  handler.amIMaster = false;
	  new Thread(handler).start();
	} catch (Exception e) {
	  System.err.println("NServer.run: can't clone " + e);
	  break;
	}
      }
    } else {
      try {
	clientOutput = new PrintStream(clientSocket.getOutputStream());
	clientInput = clientSocket.getInputStream();
      } catch (Exception e) {
	System.err.println("NServer.run: can't create stream" + e);
      }
      serviceRequest();
      close();
      //Thread.currentThread().yield();
    }
  }
  public void serviceRequest() {
    byte buf[] = new byte[300];
    int n;
    clientOutput.print("Echo server " + getClass().getName() + "\n");
    clientOutput.flush();
    try {
      while ((n = clientInput.read(buf, 0, buf.length)) >= 0) {
	clientOutput.write(buf, 0, n);
      }
      clientOutput.print("close.\n");
      clientOutput.flush();
    } catch (Exception e) {
      System.err.println("serviceRequest: read failure " + e);
    }
  }
  public final void startServer(int port) {
    try {
      masterSocket = new ServerSocket(port);
    } catch(Exception e) {
      System.err.println("Nserver.startServer: " + e);
      return;
    }
    masterServerInstance = new Thread(this);
    amIMaster = true;
    masterServerInstance.start();
  }
}

// end of NServer.java
