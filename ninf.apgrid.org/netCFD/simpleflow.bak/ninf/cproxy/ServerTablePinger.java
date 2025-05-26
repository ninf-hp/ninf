package ninf.cproxy;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;
import java.net.*;

class ServerTablePinger implements Runnable{
  static CommandParser newserversParser = new CommandParser(new CommandRepresent("newServers", 2));
  static CommandParser serverInfoParser = new CommandParser(new CommandRepresent("serverInfo", 2));

  int interval;               /* interval in second */
  MetaServerReference target;
  MetaServerConnection con;
  ServerTable table;
  static NinfLog dbg = new NinfLog("ServerTablePinger");
  long time = 0;

  ServerTablePinger(MetaServerReference target, int interval, ServerTable table){
    this.target = target;
    this.interval = interval;
    this.table = table;
  }

  void receiveReply(MetaServerConnection con) throws NinfException {
    NinfCommand com = newserversParser.readCommand(con.is);
    int count = new Integer(com.args[0]).intValue();
    time = new Long(com.args[1]).longValue();
    for (int i = 0; i < count; i++){
      com = serverInfoParser.readCommand(con.is);
      NinfServerStruct serverStruct = new NinfServerStruct(com.args[0], com.args[1]);
      table.addNewServer(serverStruct);
    }
  }

  void sendRequest(MetaServerConnection con) throws NinfException {
    new NinfCommand("getNewServers", ""+ time).send(con.os);
  }

  void ping(){
    try {
      con = target.connect();
      sendRequest(con);
      receiveReply(con);
    } catch (NinfException e){
      System.err.println(e);
    }
  }

  void start(){
    new Thread(this).start();
  }

  public void run(){
    while (true){
      ping();
      try {
	Thread.sleep(interval * 1000);
      } catch (InterruptedException e){
      }
    }
  }
}
