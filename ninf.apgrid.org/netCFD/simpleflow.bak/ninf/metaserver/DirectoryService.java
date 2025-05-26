package ninf.metaserver;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import java.io.*;
import java.util.Hashtable;


public class DirectoryService {
  Set servers;  /* NinfServerHolder Set */
  private boolean lock = false;

  Hashtable functionTable;  /* key = FunctionName, val = FunctionStruct */
  MetaServerRoot masterServer;
  Set serverObservers;


  DirectoryService(MetaServerRoot masterServer){
    servers = new Set();
    functionTable = new Hashtable();
    this.masterServer = masterServer;
    serverObservers = new Set();
  }

  public synchronized void lock(){
    lock = true;
  }
  public synchronized void unlock(){
    lock = false;
    notify();
  }

  
  synchronized ServerIndex getServerIndex(FunctionName name, 
					  NinfServerStruct struct){
    FunctionStruct functionStruct = (FunctionStruct)functionTable.get(name);
    return functionStruct.getServerIndex(struct);
  }

  synchronized NinfServerHolder getHolder(NinfServerStruct struct){
    NinfServerHolder o = 
      (NinfServerHolder)servers.match(new ServerHolderMatch(struct));
    return o;
  }

  synchronized LoadInformation getLoadInformation(NinfServerStruct struct){
    NinfServerHolder o = 
      (NinfServerHolder)servers.match(new ServerHolderMatch(struct));
    if (o == null)
      return null;
    return o.load;
  }

  synchronized ServerCharacter getServerCharacter(NinfServerStruct struct){
    NinfServerHolder o = 
      (NinfServerHolder)servers.match(new ServerHolderMatch(struct));
    if (o == null)
      return null;
    return o.serverChar;
  }

  synchronized void clearServer(NinfServerHolder server){
    //    while (lock)
    //      wait();
    
  }

  synchronized NinfStub getStub(FunctionName name){
    FunctionStruct tmp = (FunctionStruct)functionTable.get(name);
    if (tmp == null) 
      return null;
    return tmp.getStub();
  }

  synchronized NinfServerHolder registerServer(NinfServerHolder tmpServer){
    NinfServerHolder server = (NinfServerHolder)servers.get(tmpServer);
    if (server == null){
      servers.add(tmpServer);
      tmpServer.updateTime = System.currentTimeMillis();
      server = tmpServer;
    }
    addObservable(server);
    return server;
  }

  synchronized Set getNewServers(long time){
    return servers.select(new NewerThan(time));
  }

  synchronized void registerStub(NinfServerHolder server, NinfStub stub, int index){
    FunctionName name;
    name = new FunctionName(stub.module_name, stub.entry_name);

    FunctionStruct tmp = (FunctionStruct)functionTable.get(name);
    if (tmp == null){
      tmp = new FunctionStruct(stub);
      functionTable.put(name, tmp);
    }
    tmp.addServer(new ServerIndex(server.struct, index));
  }    

  /////////////////////////////////////////////////////
  //                 ServerObservers management
  /////////////////////////////////////////////////////

  void addObservable(NinfServerHolder holder){
    serverObservers.doEach(new AddObservable(holder));

  }
  void addObserver(ServerObserver obs){
    serverObservers.add(obs);
    servers.doEach(new AddObserver(obs));
  }

  void removeObserver(ServerObserver obs){
    obs.removeSelf();
    serverObservers.remove(obs);
  }

  /////////////////////////////////////////////////////
  //                 Inner functions  
  /////////////////////////////////////////////////////

  class NewerThan implements BooleanFunction{
    long time;
    public boolean eval(Object o){
      return ((NinfServerHolder)o).updateTime > time;
    }
    NewerThan(long time){
      this.time = time;
    }
  }
  class ServerHolderMatch implements BooleanFunction{
    NinfServerStruct serverStruct;
    public boolean eval(Object o){
      return (((NinfServerHolder)o).struct).equals(serverStruct);
    }
    ServerHolderMatch(NinfServerStruct serverStruct){
      this.serverStruct = serverStruct;
    }
  }
  class AddObserver implements VoidFunction{
    ServerObserver obs;
    AddObserver(ServerObserver obs){
      this.obs = obs;
    }
    public void eval(Object o){
      obs.addObservable((NinfServerHolder)o);
    }
  }
  class AddObservable implements VoidFunction{
    NinfServerHolder holder;
    AddObservable(NinfServerHolder holder){
      this.holder = holder;
    }
    public void eval(Object o){
      ((ServerObserver)o).addObservable(holder);
    }
  }

}



