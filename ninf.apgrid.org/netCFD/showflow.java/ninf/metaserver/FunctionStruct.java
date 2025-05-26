package ninf.metaserver;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;


public class FunctionStruct {
  public NinfStub stub;
  public Set serverIndexList;

  public FunctionStruct(NinfStub stub){
    this.stub = stub;
    this.serverIndexList = new Set();
  }

  class ServerStructMatch implements BooleanFunction{
    NinfServerStruct serverStruct;
    public boolean eval(Object o){
      return (((ServerIndex)o).server).equals(serverStruct);
    }
    ServerStructMatch(NinfServerStruct serverStruct){
      this.serverStruct = serverStruct;
    }
  }

  ServerIndex getServerIndex(NinfServerStruct serverStruct){
    return (ServerIndex)serverIndexList.match(new ServerStructMatch(serverStruct));
  }

  void addServer(ServerIndex server){
    serverIndexList.add(server);
  }

  public NinfStub getStub(){
    return stub;
  }
}
