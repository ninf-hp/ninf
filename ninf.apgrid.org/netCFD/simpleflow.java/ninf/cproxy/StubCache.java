package ninf.cproxy;
import ninf.basic.*;
import ninf.client.*;
import ninf.common.*;
import java.util.Vector;
import java.util.Hashtable;

class StubCache{
  CProxyServer master;

  static final CommandRepresent acceptCommands[] = {new CommandRepresent("stub", 0), new CommandRepresent("nostub", 0)};
  static CommandParser parser = new CommandParser(acceptCommands);
  static NinfLog dbg = new NinfLog("StubCache");
  long previousTime = 0;
  Hashtable stubCallables = new Hashtable();
  Vector callables = new Vector();
  

  StubCache(CProxyServer master){
    this.master = master;
  }

  int currentIndex = 0;

  synchronized int getIndex(){
    return currentIndex++;
  }

  Callable getCallable(int index) throws NinfException {
    try {
      Callable tmp = (Callable)callables.elementAt(index);
      if (tmp != null)
	return tmp;
      throw new NinfErrorException(NinfError.STUBREMOVED);
    } catch (ArrayIndexOutOfBoundsException e){
      throw new NinfErrorException(NinfError.STUBREMOVED);
    }
  }

  StubCallable getStubCallable(FunctionName fullName) throws NinfException {
    StubCallable stub = (StubCallable)stubCallables.get(fullName);
    if (stub != null)
      return stub;
    stub = retrieveStubInfo(master.metaServer, fullName);
    if (stub != null)
      return stub;
    throw new NinfErrorException(NinfError.CANTFINDSTUB);
  }
  
  StubCallable retrieveStubInfo(MetaServerReference metaServer, FunctionName fullName) throws NinfException{
    MetaServerConnection con = metaServer.connect();
    con.send(new NinfCommand("getStub", fullName.toString()));
    String line = con.readLine();
    NinfCommand ack = parser.readCommand(line);
    //    if (ack.argc() != 2)
    //      throw new CommandParseException(line);
    if (ack.command.equals("stub")){
      NinfStub stub = new NinfStub(new XDRInputStream(con.is));
      con.close();
      return registerStub(fullName, stub);
    } else {
      throw new NinfErrorException(NinfError.CANTFINDSTUB);
    }
  }
  StubCallable registerStub(FunctionName fullName, NinfStub stub){
    int index = getIndex();
    StubCallable tmp = new StubCallable(stub, index, master);
    if (callables.size() <= index)
      callables.setSize(index + 10);
   callables.setElementAt(tmp, index);
    stubCallables.put(fullName, tmp);
    return tmp;
  }
}
