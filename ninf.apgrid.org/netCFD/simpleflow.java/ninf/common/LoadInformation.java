package ninf.common;
import ninf.basic.*;
import java.io.*;

public class LoadInformation{
  static final CommandRepresent acceptCommands[] = {new CommandRepresent("load", 4)};
  static NinfLog dbg = new NinfLog("LoadInformation");
  static CommandParser parser = new CommandParser(acceptCommands);

  public double loadAverage;

  public double user;
  public double system;
  public double idle;
  
  long time;

  public LoadInformation(DataInputStream is) throws NinfException{
    NinfCommand com =parser.readCommand(is);
    if (com.argc() != 4)
      throw new CommandParseException("unexpected argument size: " + com);
    loadAverage = new Double(com.args[0]).doubleValue();
    user        = new Double(com.args[1]).doubleValue();
    system      = new Double(com.args[2]).doubleValue();
    idle        = new Double(com.args[3]).doubleValue();
    
    time = System.currentTimeMillis();
  }
  public LoadInformation(NinfCommand com) throws NinfException{
    if (com.argc() != 4)
      throw new CommandParseException("unexpected argument size: " + com);
    loadAverage = new Double(com.args[0]).doubleValue();
    user        = new Double(com.args[1]).doubleValue();
    system      = new Double(com.args[2]).doubleValue();
    idle        = new Double(com.args[3]).doubleValue();
    time = System.currentTimeMillis();
  }

  public NinfCommand toCommand(){
    return new NinfCommand("loadInformation", 
			   ""+loadAverage,
			   ""+user,
			   ""+system,
			   ""+idle);
  }

  public LoadInformation(double loadAverage, double user, double system, double idle) {
    this.loadAverage = loadAverage;
    this.user        = user;
    this.system      = system;
    this.idle        = idle;
    
    time = System.currentTimeMillis();
  }

  public String logString(){
    return 
    FormatString.format("%2.2f %2.2f %2.2f %2.2f", 
			new Double(loadAverage),
			new Double(user),
			new Double(system),
			new Double(idle)
			);
  }


  public String toString(){
    return 
    FormatString.format("load,user,system,idle %.2f %.2f %.2f %.2f\n", 
			new Double(loadAverage),
			new Double(user),
			new Double(system),
			new Double(idle)
			);
  }
}


