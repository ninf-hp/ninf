package ninf.monitor;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import java.io.*;

public class MonitorReceive implements Runnable{
  DataInputStream is;
  MonitorPanel panel;
  Monitor monitor;

  static final CommandRepresent acceptCommands[] = {
    new CommandRepresent("loadupdate", 0)
  };
  static CommandParser parser = new CommandParser(acceptCommands);
  static final CommandRepresent loadUpdateCommands[] = {
    new CommandRepresent("loadInformation", 4),
    new CommandRepresent("serverInfo", 2)};
  static CommandParser loadupdateParser = new CommandParser(loadUpdateCommands);  
  
  MonitorReceive(DataInputStream is, MonitorPanel panel, Monitor monitor){
    this.is = is;
    this.panel = panel;
    this.monitor = monitor;
  }

  public void run(){
    while (processConnection())
      ;
  }

  boolean processUpdate(DataInputStream is){
    NinfCommand com1,com2;
    try {
      com1 = loadupdateParser.readCommand(is);
      com2 = loadupdateParser.readCommand(is);
      if (!com1.command.equals("serverInfo"))
	panel.println("protocolError.." + com1);
      if (!com2.command.equals("loadInformation"))
	panel.println("protocolError.." + com2 );

      NinfServerStruct struct = new NinfServerStruct(com1.args[0], com1.args[1]);
      LoadInformation load = new LoadInformation(com2);
      monitor.serverLoad(struct, load);
    } catch (NinfException e){
      return false;
    }
    return true;
  }


  boolean processConnection(){
    NinfCommand com;
    try {
      com = parser.readCommand(is);
      if (com.command.equals("loadupdate"))
	return processUpdate(is);
    } catch (NinfException e){
      return false;
    }
    panel.println("readed " + com);
    return true;
  }

}
