package ninf.cproxy;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;
import java.net.*;

public class CProxy{
  static final int defaultPort = 3002;
  int parsedPort = 0;
  public static NinfLog dbg = new NinfLog("CProxy");
  CProxyConfig conf;
  CProxyServer cProxyServer;

  MetaServerReference metaServer;
  String myname;
  int myport;

  public String[] parseArg(String arg[]){
    Vector tmpV = new Vector();
    int index = 0;
    for (int i = 0; i < arg.length; i++){
      if (arg[i].equalsIgnoreCase("-port"))
	parsedPort = Integer.valueOf(arg[++i]).intValue();
      else if (arg[i].equalsIgnoreCase("-debug"))
	NinfLog.verbose();
      else if (arg[i].equalsIgnoreCase("-log"))
	NinfLog.log();
      else 
	tmpV.addElement(arg[i]);
    }
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }

  void usage(){
    dbg.println("USAGE: java ninf.cproxy.CProxy [-port PORT] [-debug] CONFIGFILE");
  }

  void getMyName(){
    try {
      InetAddress localaddr = InetAddress.getLocalHost();
      myname = localaddr.getHostName();
      String confname = conf.myhostname();
      if (confname != null)
	myname = confname;
    } catch(UnknownHostException e) {
      dbg.println("getLocalHost(): " + e);
      System.exit(1);
    }
  }

  void readConfig(String args[]){
    if (args.length >= 1){
      try {
	conf = new CProxyConfig(args[0]);
	if (parsedPort != 0)
	  myport = parsedPort;
	else if (conf.port() != 0)
	  myport = conf.port();
	else
	  myport = defaultPort;
	metaServer = conf.metaServer();
	if (conf.getOneArg("log") != null){
	  try {
	    NinfLog.changeOutput(conf.getOneArg("log"));
	  } catch (NinfIOException ie){
	    dbg.log("cannot open log file: "+ conf.getOneArg("log"));
	  }
	}
      } catch(java.io.FileNotFoundException e) {
	dbg.println("Configuration File \"" + args[0]+ "\" was not found");
	System.exit(3);
      } catch(IOException e) {
	e.printStackTrace();
	dbg.println("exit");
      }
    } else {
      usage();
      System.exit(0);
    }
  }

  void start(String args[]){
    //    NinfLog.log();
    args = parseArg(args);
    readConfig(args);
    getMyName();
    cProxyServer = new CProxyServer(myname, myport, this, metaServer);
    cProxyServer.start();
  }

  public static void main(String args[]){
    new CProxy().start(args);
  }
}
