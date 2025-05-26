package ninf.metaserver;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;
import java.net.*;
import java.io.*;

public class MetaServer {
  static final int defaultPort = 3030;
  int parsedPort = 0;
  public static NinfLog dbg = new NinfLog("MetaServer");
  MetaServerConfig conf;
  MetaServerRoot metaServer;
  String schedulerString;

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
    dbg.println("USAGE: java ninf.metaserver.MetaServer [-port PORT] [-debug] CONFIGFILE");
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
	conf = new MetaServerConfig(args[0]);
	if (parsedPort != 0)
	  myport = parsedPort;
	else if (conf.port() != 0)
	  myport = conf.port();
	else
	  myport = defaultPort;
	if (conf.getOneArg("log") != null){
	  try {
	    NinfLog.changeOutput(conf.getOneArg("log"));
	  } catch (NinfIOException ie){
	    dbg.log("cannot open log file: "+ conf.getOneArg("log"));
	  }
	}
	if ((schedulerString = conf.scheduler()) == null){
	  dbg.log("cannot get scheduler. aborting ...");
	  System.exit(3);
	}
      } catch(java.io.FileNotFoundException e) {
	dbg.println("Configuration File \"" + args[0]+ "\" was not found");
	System.exit(0);
      } catch(IOException e) {
	dbg.println("failed in reading config file: abort...");
	System.exit(3);	
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
    MetaServerRoot metaServer = new MetaServerRoot(myname, myport, this);
    metaServer.start();
  }

  public static void main(String args[]){
    new MetaServer().start(args);
  }

}
