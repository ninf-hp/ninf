package ninf.common;
import ninf.basic.*;
import java.io.*;

public class CallInformation{
  static final CommandRepresent acceptCommands[] = {new CommandRepresent("callInformation", 3)};
  static NinfLog dbg = new NinfLog("CallInformation");
  static CommandParser parser = new CommandParser(acceptCommands);

  public long foreSize;
  public long backSize;
  public long order;

  public CallInformation(long foreSize, long backSize, long order){
    this.foreSize = foreSize;
    this.backSize = backSize;
    this.order = order;
  }

  public CallInformation(DataInputStream is) throws NinfException{
    NinfCommand com = parser.readCommand(is);
    this.foreSize = new Long(com.args[0]).longValue(); 
    this.backSize = new Long(com.args[1]).longValue(); 
    this.order =    new Long(com.args[2]).longValue(); 
  }

  public NinfCommand toCommand(){
    return new NinfCommand("callInformation", ""+foreSize, ""+backSize, ""+order);
  }

  public String logString(){
    return FormatString.format(
           "%11.3lf %11.3lf %7.2lf",
	   new Double(foreSize / 1024.0),
	   new Double(backSize / 1024.0),
	   new Double(order / (1024.0 * 1024.0)));
  }

  public String toString(){
    return "fore: " + foreSize + ", back: " + backSize + 
	    ", calc order: " + order;
  }

}
