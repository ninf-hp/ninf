package ninf.metaserver;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import java.io.DataInputStream;
import java.util.Observable;

public class NinfServerHolder extends Observable{
  static final CommandRepresent acceptCommands[] = {new CommandRepresent("serverInfo", 2)};
  static CommandParser parser = new CommandParser(acceptCommands);

  public NinfServerStruct struct;
  long updateTime;
  public LoadInformation load;
  public ServerCharacter serverChar;

  NinfServerHolder(String str) {
    struct = new NinfServerStruct(str);
  }
  NinfServerHolder(String h, int p) {
    struct = new NinfServerStruct(h,p);
  }
  public NinfServerHolder(String h, String port) {
    struct = new NinfServerStruct(h,port);
  }
  NinfServerHolder(NinfPacketInputStream is) throws NinfException {
    struct = new NinfServerStruct(is);
  }

  NinfServerHolder(DataInputStream is) throws NinfException {
    NinfCommand com =parser.readCommand(is);
    struct = new NinfServerStruct(com.args[0], com.args[1]);
  }

  public NinfServerHolder(NinfServerStruct struct, LoadInformation load){
    this.struct = struct;
    this.load = load;
  }

  void setLoad(LoadInformation load){
    this.load = load;
    setChanged();
    notifyObservers();
  }

/***************** OTHER FUNCTIONS *******************/

  public String toString() {
    return struct.toString();
  }
  public NinfCommand toCommand() {
    return struct.toCommand();
  }
  public int hashCode(){
    return struct.hashCode();
  }
  public boolean equals(Object o){
    if (!(o instanceof NinfServerHolder))
      return false;
    return (struct.equals(((NinfServerHolder)o).struct));
  }
}
