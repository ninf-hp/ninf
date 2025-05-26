package ninf.cproxy;
import ninf.basic.*;
import ninf.client.*;
import java.io.PrintStream;
import java.util.Vector;
import java.util.Enumeration;

class ServerTable{

  Vector servers = new Vector();
  Vector pingers = new Vector();

  static int metaInterval  = 100; /* metaserver ping interval in sec */
  static int interval  = 100; /* default ping interval in sec */
  static int size = 10000;    /* size for getting throughput */
  static double measureTime = 1.0; /* measurement goal time in second */
  CProxyServer master;
  ServerTablePinger tablePinger;
  PrintStream throughputLogStream;


  ServerTable(CProxyServer master){
    this.master = master;

    CProxyConfig conf = master.masterProxy.conf;
    if (conf.throughputSize() >= 0)
      size = conf.throughputSize();
    if (conf.interval() >= 0)
      interval = conf.interval();
    if (conf.measureTime() >= 0.0)
      measureTime = conf.measureTime();
    if (conf.metaInterval() >= 0)
      metaInterval = conf.metaInterval();
    throughputLogStream = master.throughputLogStream;

    tablePinger = new ServerTablePinger(master.metaServer, metaInterval, this);
    tablePinger.start();

  }

  public synchronized void addNewServer(NinfServerStruct server){
    int minimumSize, maximumSize;
    if (servers.contains(server))
      return;
    servers.addElement(server);
    CProxyConfig conf = master.masterProxy.conf;
    if (conf.minimumSize() >= 0)
      minimumSize = conf.minimumSize();
    else 
      minimumSize = TPinger.MINIMUMSIZE;
    if (conf.maximumSize() >= 0)
      maximumSize = conf.maximumSize();
    else 
      maximumSize = TPinger.MAXIMUMSIZE;

    TPinger pinger = new TPinger(server, interval, size, minimumSize, maximumSize,
				 measureTime, throughputLogStream);
    pingers.addElement(pinger);
    pinger.start();
  }
  
  public synchronized void sendServers(MetaServerConnection con){
    int serverNum = servers.size();
    con.send(new NinfCommand("servers", "" + serverNum));
    Enumeration enum = servers.elements();
    while (enum.hasMoreElements()){
      NinfServerStruct server = (NinfServerStruct)enum.nextElement();
      server.writeOut(con.os);
    }
  }


}

