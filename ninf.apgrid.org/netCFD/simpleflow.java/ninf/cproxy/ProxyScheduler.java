package ninf.cproxy;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;


class ProxyScheduler{
  static final CommandRepresent acceptCommands[] = {new CommandRepresent("serverIndex", 3),
						    new CommandRepresent("noServerAvailable", 0)};
  static CommandParser parser = new CommandParser(acceptCommands);
  static NinfLog dbg = new NinfLog("ProxyScheduler");

  ServerTable serverTable;
  MetaServerReference metaServer;
  MetaServerConnection con;

  ProxyScheduler(ServerTable serverTable, MetaServerReference metaServer){
    this.metaServer = metaServer;
    this.serverTable = serverTable;
  }

  ServerIndex schedule(NinfStub stub, CallContext context, int serial)
  throws NinfException{
      dbg.println(stub.getCallInformation(context));
      con = metaServer.connect();
      
      con.send(new NinfCommand("schedule", ""+serial));
      stub.getName().toCommand().send(con.os);
      stub.getCallInformation(context).toCommand().send(con.os);
      serverTable.sendServers(con);
      con.send(new NinfCommand("endschedule"));
      
      NinfCommand reply = parser.readCommand(con.is);
      if (reply.is("noServerAvailable")){
	dbg.log("Cannot find Ninf server for '" + stub.getName() + "' on metaserver");
	return null;
      }
      return new ServerIndex(new NinfServerStruct(reply.args[0], reply.args[1]), new Integer(reply.args[2]).intValue());

    //    return new ServerIndex(new NinfServerStruct("hpc:3020"), 0);
  }
}
