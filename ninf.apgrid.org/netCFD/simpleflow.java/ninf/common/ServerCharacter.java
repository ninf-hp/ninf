package ninf.common;
import ninf.basic.*;
import java.io.*;

public class ServerCharacter {
  public int CPUNum;       // number of CPUs
  public int performance;  // kflops
  public int memSize;     // Kbytes

  static final CommandRepresent acceptCommands[] = {new CommandRepresent("serverCharacter", 3)};
  static CommandParser parser = new CommandParser(acceptCommands);

  public ServerCharacter(int CPUNum, int performance, int memSize){
    this.CPUNum = CPUNum;
    this.performance = performance;
    this.memSize = memSize;
  }

  public ServerCharacter(DataInputStream is) throws NinfException{
    NinfCommand com =parser.readCommand(is);
    if (com.argc() != 3)
      throw new CommandParseException("unexpected argument size: " + com);
    this.CPUNum = new Integer(com.args[0]).intValue();
    this.performance = new Integer(com.args[1]).intValue();
    this.memSize = new Integer(com.args[2]).intValue();
  }

  public ServerCharacter(NinfPacketInputStream pis) throws NinfException {
    this.CPUNum = pis.readInt();
    this.performance = pis.readInt();
    this.memSize = pis.readInt();
  }

  public NinfCommand toCommand(){
    return new NinfCommand("serverCharacter", ""+CPUNum, 
			   ""+performance, ""+memSize);
  }

  public String toText(){
    if (memSize > 0)
      return FormatString.format("#CPU %d  %.2lfMflops  %.2lfMBytes memory",
				 new Integer(CPUNum),
				 new Double(performance / 1000.0),
				 new Double(memSize / 1024.0));
    else	
      return FormatString.format("#CPU %d  %.2lfMflops",
				 new Integer(CPUNum),
				 new Double(performance / 1000.0));
  }

  public String toString(){
    return "Server: CPUs = " + CPUNum + ", performance = " + performance + ", memSize = " + memSize;
  }
}
