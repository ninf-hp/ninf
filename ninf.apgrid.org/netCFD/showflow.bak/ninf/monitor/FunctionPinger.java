package ninf.monitor;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import ninf.metaserver.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;

class FunctionPinger implements Runnable {
  Monitor monitor;
  MetaServerConnection con;
  MetaServerReference server;

  static final CommandRepresent acceptCommands[] = 
       {new CommandRepresent("functionNames", 1),
        new CommandRepresent("stub", 0)}; 
  static CommandParser parser = new CommandParser(acceptCommands);


  int interval = 30;  // 30 second

  FunctionPinger(Monitor monitor){
    this.monitor = monitor;
    server = monitor.server;
  }

  NinfStub getStub(FunctionName fullName){
    try {
      MetaServerConnection con = server.connect();
      con.send(new NinfCommand("getStub", fullName.toString()));
      String line = con.is.readLine();
      
      NinfCommand ack = parser.readCommand(line);
      //    if (ack.argc() != 2)
      //      throw new CommandParseException(line);
      if (ack.command.equals("stub")){
	NinfStub stub = new NinfStub(new XDRInputStream(con.is));
	con.close();
	return stub;
      } else {
System.out.println("failed to read stub");
	return null;
      }
    }catch (NinfException e){
System.out.println("failed to read stub " + e);
      return null;
    } catch (IOException e){
System.out.println("failed to read stub" + e);
      return null;
    }
  }

  void updateTable(FunctionName names[]){
    boolean updated = false;
    Hashtable oldtable = monitor.functionTable;
    Hashtable newtable = new Hashtable();
    for (int i = 0; i < names.length; i++){
      FunctionStruct struct = null;
      if ((struct = (FunctionStruct)oldtable.get(names[i])) == null){
	updated = true;
	struct = new FunctionStruct(getStub(names[i]));
      }	
      newtable.put(names[i], struct);
    }
    if (updated)
      monitor.updateFunctionTable(newtable);

  }

  void disconnect(){
    if (con != null)
      con.close();
    con = null;
  }


  void ping(){
    try {
      if (server == null)
	return;
      if (con == null)
	con = server.connect();
      con.os.println("getFunctionNames");
      NinfCommand com = null;
      try {
	com = parser.readCommand(con.is);
      } catch (NinfException e){
	con = null;
	return;
      }
      int count = (new Integer(com.args[0])).intValue();
      FunctionName names[] = new FunctionName[count];
      for (int i = 0; i < count; i++){
	names[i] = new FunctionName(con.is.readLine());
	System.out.println(names[i]);
      }
      updateTable(names);
    } catch (IOException e){
      con = null;
    } catch (NinfException e){
      con = null;
    }
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
