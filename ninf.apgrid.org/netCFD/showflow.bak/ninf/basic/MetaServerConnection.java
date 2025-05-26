package ninf.basic;
import java.io.*;
import java.net.*;

public class MetaServerConnection{
  Socket socket;
  public PrintStream os;
  public DataInputStream is;
  public final static int RETRY_DEFAULT = 20;
  public final static int SLEEP_INIT = 2000;
  NinfLog dbg = new NinfLog(this);

  MetaServerReference metaserver;
  public MetaServerConnection(MetaServerReference metaserver) throws NinfException {
    this.metaserver = metaserver;
    Exception e = null;
    int count = 0;
    int retry = RETRY_DEFAULT;
    int sleepTime = SLEEP_INIT;
    while (count < retry){
      try {
	socket = new Socket(metaserver.host, metaserver.port);
	os = new PrintStream(socket.getOutputStream());
	is = new DataInputStream(socket.getInputStream());
	return;
      } catch (IOException e0) {
	try {
	  Thread.currentThread().sleep(sleepTime);
	  ++count;
	  e = e0;
	  sleepTime *= 2;
	} catch(InterruptedException ie) {
	  ie.printStackTrace();
	}
      }
    }
    if (e != null) {
      dbg.println("retry over: host = " + metaserver.host + " port = " + metaserver.port);
      throw(new NinfErrorException(NinfError.CANTCONNECTSERVER));
    }
  }

  public void send(NinfCommand com){
    os.println(com.toString());
    os.flush();
  }

  public String readLine() throws NinfIOException {
    try {
      return is.readLine();
    } catch (IOException e){
      throw new NinfIOException(e);
    }
  }

  public void close(){
    try {
      os.close();
      is.close();
      socket.close();
    } catch (IOException e){
    }
  }
}
