package ninf.proxy;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;
import java.net.*;

public class Proxy{
  static final int defaultPort = 3001;
  int parsedPort = 0;
  public static NinfLog dbg = new NinfLog("Proxy");
  ProxyConfig conf;
  OutSideProxy oproxy;

  MetaServerReference metaServer;
  Hashtable portManagers;
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
      else 
	tmpV.addElement(arg[i]);
    }
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }

  void usage(){
    dbg.println("USAGE: java ninf.proxy.Proxy [-port PORT] [-debug] CONFIGFILE");
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
	conf = new ProxyConfig(args[0]);
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

  PortManager addPortManager(NinfCommand com) throws CommandParseException{
    int serverport = (new Integer(com.args[1])).intValue();
    int port;
    if (serverport == 0)
      throw new CommandParseException(""+com);
    NinfServerStruct server = new NinfServerStruct(com.args[0], serverport);
    PortManager tmp = new PortManager(server, 0, this);
    port = tmp.start();
    portManagers.put(new Integer(port), tmp);
    return tmp;
  }

  void startPortManagers(){
    portManagers = new Hashtable();
    Vector stringsVec = conf.getContent("server");
    for (int i = 0; i < stringsVec.size(); i++){
      String args[] = (String[])stringsVec.elementAt(i);
      if (args.length < 2)
	dbg.log("informal server definition in the Configfile");
      NinfServerStruct server = new NinfServerStruct(args[0], (new Integer(args[1])).intValue());
      int port = args.length > 2 ? (new Integer(args[2])).intValue(): 0;
      PortManager tmp = new PortManager(server, port, this);
      port = tmp.start();
      portManagers.put(new Integer(port), tmp);
      tmp.registerToMetaServer(metaServer);
    }
  }

  void start(String args[]){
    NinfLog.log();
    args = parseArg(args);
    readConfig(args);
    getMyName();
    startPortManagers();
    oproxy = new OutSideProxy(myname, myport, this);
    oproxy.start();
  }

  public static void main(String args[]){
    new Proxy().start(args);
  }
}
