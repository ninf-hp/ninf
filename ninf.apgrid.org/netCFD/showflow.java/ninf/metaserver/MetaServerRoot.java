package ninf.metaserver;
import ninf.basic.*;
import ninf.client.*;
import ninf.common.*;
import java.io.*;
import java.util.Enumeration;

class MetaServerRoot extends NServer {
  String myname;
  int myport;
  MetaServer masterServer;

  ProbeRoot probeRoot;
  DirectoryService directoryService;
  SchedulerRoot schedulerRoot;

  static PrintStream scheduleLogStream = null;
  static PrintStream loadLogStream = null;

  static final CommandRepresent acceptCommands[] = 
          {new CommandRepresent("register", 2), 
	   new CommandRepresent("getStub",1),
	   new CommandRepresent("getNewServers", 1),
	   new CommandRepresent("getServerCharacter", 0),
	   new CommandRepresent("schedule", 1),
	   new CommandRepresent("time", 0),
	   new CommandRepresent("inform", 0),
	   new CommandRepresent("getLog", 0),
	   new CommandRepresent("getScheduler", 0),
	   new CommandRepresent("getFunctionNames", 0),
	   new CommandRepresent("scheduler", 1)};
  static CommandParser parser = new CommandParser(acceptCommands);

  static NinfLog dbg = new NinfLog("MetaServerRoot");
  
  MetaServerRoot(String myname, int myport, MetaServer masterServer){
    this.myname = myname;
    this.myport = myport;
    this.masterServer = masterServer;
    initStreams(masterServer);
    
    directoryService = new DirectoryService(this);
    probeRoot = new ProbeRoot(this);
    schedulerRoot = new SchedulerRoot(masterServer.schedulerString, directoryService);
  }

  static void initStreams(MetaServer masterServer){
    String scheduleLogName = masterServer.conf.getOneArg("schedulelog");
    if (scheduleLogName != null){
      try {
	scheduleLogStream = new PrintStream(new FileOutputStream(scheduleLogName));
      } catch(IOException e) {
	dbg.log("Can't open data log file: " + scheduleLogName);
      }
    }
    String loadLogName = masterServer.conf.getOneArg("loadlog");
    if (loadLogName != null){
      try {
	loadLogStream = new PrintStream(new FileOutputStream(loadLogName));
      } catch(IOException e) {
	dbg.log("Can't open data log file: " + loadLogName);
      }
    }

  }


  boolean register(NinfCommand com, PrintStream os) throws NinfException{
    NinfServerHolder server = 
      new NinfServerHolder(com.args[0], 
			   (new Integer(com.args[1])).intValue());
    probeRoot.registerServer(server);
    return true;
  }

  boolean getLog(PrintStream os) throws NinfException{
    ForkOutputStream fos = new ForkOutputStream();
    fos.addStream(NinfLog.os);
    fos.addStream(os);    
    PrintStream tmp = new PrintStream(fos);
    NinfLog.changeOutput(tmp);
    return true;
  }

  boolean getStub(NinfCommand com, PrintStream os) throws NinfException{
    NinfStub stub = directoryService.getStub(new FunctionName(com.args[0]));
    if (stub == null){
      os.println("nostub");
      return true;     
    }
    os.println("stub");
    stub.writeTo(new XDROutputStream(os));
    return false;   /* it means that this connection should be closed, because of binary transmission */
  }

  boolean getNewServers(NinfCommand com, PrintStream os) throws NinfException{
    long now = System.currentTimeMillis();
    long time = new Long(com.args[0]).longValue();
    Set servers = directoryService.getNewServers(time);
    int count = servers.size();
    Enumeration enum = servers.elements();

    new NinfCommand("newServers", ""+count , ""+ now ).send(os);
    while (enum.hasMoreElements()){
      NinfServerHolder o = (NinfServerHolder)enum.nextElement();
      o.toCommand().send(os);
    }
    return true;
  }

  boolean getServerCharacter(DataInputStream is, PrintStream os) 
                              throws NinfException{
    NinfServerHolder holder = new NinfServerHolder(is);
    ServerCharacter sChar = directoryService.getServerCharacter(holder.struct);
    if (sChar == null)
      os.println("-ERR");
    sChar.toCommand().send(os);
    return true;
  }

  boolean returnTime(PrintStream os) throws NinfException{
    os.println(FormatString.format("%.2lf", 
	new Double(System.currentTimeMillis() / 1000.0)));
    return false;
  }

  boolean getScheduler(PrintStream os) throws NinfException{
    String tmp = schedulerRoot.getCurrentScheduler();
    os.println(tmp);
    return true;
  }

  boolean getFunctionNames(PrintStream os) throws NinfException{
    Enumeration tmp = directoryService.functionTable.keys();
    int n = directoryService.functionTable.size();
    os.println("functionNames " + n);
    for (int i = 0; i < n; i++){
      FunctionName name = (FunctionName)(tmp.nextElement());
      os.println(name);
    }
    return true;
  }

  boolean setInform(PrintStream os) throws NinfException{
    ServerObserver obs = new ServerObserver(os);
    //    try {
      directoryService.addObserver(obs);
      //    }finally{
      //      directoryService.removeObserver(obs);
      //    }
    return true;
  }

  boolean processConnection(DataInputStream is, PrintStream os) 
             throws NinfException{
    NinfCommand com;
    try {
      com = parser.readCommand(is);
    } catch (NinfIOException e){
      return false;
    }
    dbg.println("readed " + com);
    if (com.command.equals("register")){
      return register(com, os);
    } else if (com.command.equals("getStub")){
      return getStub(com, os);
    } else if (com.command.equals("getNewServers")){
      return getNewServers(com, os);
    } else if (com.command.equals("schedule")){
      return schedulerRoot.doSchedule(is, os, (new Integer(com.args[0])).intValue());
    } else if (com.command.equals("scheduler")){
      return schedulerRoot.newScheduler(com.args[0], os);
    } else if (com.command.equals("time")){
      return returnTime(os);
    } else if (com.command.equals("inform")){
      return setInform(os);
    } else if (com.command.equals("getLog")){
      return getLog(os);
    } else if (com.command.equals("getScheduler")){
      return getScheduler(os);
    } else if (com.command.equals("getFunctionNames")){
      return getFunctionNames(os);
    } else if (com.command.equals("getServerCharacter")){
      return getServerCharacter(is, os);
    } else {
      dbg.log("Some error occurred: ");
      throw new NinfErrorException(NinfError.NINF_INTERNAL_ERROR);
    }
  }

  public void serviceRequest() {
    DataInputStream is = new DataInputStream(clientInput);
    PrintStream os = new PrintStream(clientOutput);
    dbg.log("["+myport+"] accept connection from " + clientSocket.getInetAddress());
    try {
      while (processConnection(is, os))
	;
    } catch (NinfException e){
      dbg.log("Some error occurred: " + e);
      e.printStackTrace();
      /* error condition and some string should be sent to the client */
    }
    try {
      is.close();
      os.flush();
      os.close();
    } catch (IOException e){}
  }

  void start(){
    startServer(myport);
    dbg.log("[" +myport+"] start ..");
  }
}
