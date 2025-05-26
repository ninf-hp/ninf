package ninf.common;
import ninf.basic.*;
import java.io.*;

public class CommunicationInformation{
  static final CommandRepresent acceptCommands[] = {new CommandRepresent("communicationInformation", 4)};
  static NinfLog dbg = new NinfLog("CommunicationInformation");
  static CommandParser parser = new CommandParser(acceptCommands);

  public long rtt;
  public int  size;
  public int  mode;

  public long when;   // measured time 

  public double throughput;

  public CommunicationInformation(DataInputStream is) throws NinfException{
    NinfCommand com =parser.readCommand(is);
    rtt  = new Integer(com.args[0]).intValue();
    size = new Integer(com.args[1]).intValue();
    mode = new Integer(com.args[2]).intValue();
    when = System.currentTimeMillis() - new Integer(com.args[3]).intValue();
      // simple calibration:  I know its not enough.
    throughput = (double)size * 1000 * (mode == 0? 2: 1)/ (double)rtt;
  }
  public CommunicationInformation(long rtt, int size, int mode){
    this.rtt = rtt;
    this.size = size;
    this.mode = mode;
    this.when = System.currentTimeMillis();
    throughput = (double)size * 1000 * (mode == 0? 2: 1)/ (double)rtt;
  }

  public NinfCommand toCommand(){
    long past = System.currentTimeMillis() - when;
    return new NinfCommand("communicationInformation", ""+rtt, ""+size, ""+mode, ""+past);
  }

}
