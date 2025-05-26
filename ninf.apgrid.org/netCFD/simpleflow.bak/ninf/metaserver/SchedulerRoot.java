package ninf.metaserver;
import ninf.basic.*;
import ninf.scheduler.Scheduler;
import ninf.common.*;
import ninf.client.*;
import java.io.*;
import java.util.Vector;

class SchedulerRoot {
  static CommandParser serversParser = 
     new CommandParser(new CommandRepresent("servers", 1));
  static CommandParser serverInfoParser = 
     new CommandParser(new CommandRepresent("serverInfo", 2));
  static CommandParser endScheduleParser = 
     new CommandParser(new CommandRepresent("endSchedule",0));
  static NinfLog dbg = new NinfLog("SchedulerRoot");

  static String packageString = "ninf.scheduler";

  Scheduler scheduler;
  DirectoryService service;

  SchedulerRoot(String schedulerString, DirectoryService service){
    this.scheduler = getScheduler(schedulerString);
    if (this.scheduler == null){
      dbg.log("cannot get scheduler. aborting ...");
      System.exit(3);
    }
    this.service = service;
  }

  static Scheduler getScheduler(String str){
    str = packageString + "." + str;
    try {
      Class schedulerClass = Class.forName(str);
      return (Scheduler)schedulerClass.newInstance();
    } catch (ClassCastException e){
      dbg.println("the class '" + str + "' is not a Scheduler.");
      return null;
    } catch (ClassNotFoundException e){
      dbg.println("Cannot Find class named: " + str);
      return null;
    } catch (InstantiationException e){
      dbg.println("got InstantiationException: for " + str);
      return null;
    } catch (IllegalAccessException e){
      dbg.println("got IllegalAccessException: for " + str);
      return null;
    }
  }

  String getCurrentScheduler(){
    if (scheduler == null)
      return null;
    return scheduler.getClass().getName();
  }

  void readEndSchedule(DataInputStream is) throws NinfException {
    endScheduleParser.readCommand(is);
  }

  int readServers(DataInputStream is) throws NinfException {
    NinfCommand com = serversParser.readCommand(is);
    return new Integer(com.args[0]).intValue();
  }

  ServerInformation readServerInfo(DataInputStream is, FunctionName funcName) throws NinfException {
    NinfCommand com = serverInfoParser.readCommand(is);
    String host = com.args[0];
    int port = new Integer(com.args[1]).intValue();
    NinfServerStruct struct = new NinfServerStruct(host, port);
    ServerIndex serverIndex = service.getServerIndex(funcName, struct);
    CommunicationInformation throughput = new CommunicationInformation(is);
    CommunicationInformation latency    = new CommunicationInformation(is);
    NinfServerHolder holder = service.getHolder(struct);
    LoadInformation load = holder.load;
    ServerCharacter serverChar = holder.serverChar;
    return new ServerInformation(serverIndex, throughput, latency, load, serverChar);
  }

  boolean newScheduler(String str, PrintStream os){
    Scheduler newScheduler;
    if ((newScheduler = getScheduler(str)) != null){
      scheduler = newScheduler;
      os.println("+OK");
    } else {
      os.println("-ERR");
    }
    return true;
  }

  synchronized boolean doSchedule(DataInputStream is, PrintStream os, int serial) 
                                  throws NinfException{
    ServerIndex serverIndex = schedule(is, serial);
    NinfCommand com;
    if (serverIndex == null)
      com = new NinfCommand("noServerAvailable");
    else 
      com = new NinfCommand("serverIndex", serverIndex.server.host, 
			    ""+serverIndex.server.port, ""+serverIndex.index);
    com.send(os);
    return true;
  }

  ServerIndex schedule(DataInputStream is, int serial) throws NinfException {
    FunctionName funcName = new FunctionName(is);
    CallInformation callInfo = new CallInformation(is);
    int n = readServers(is);
    Vector v = new Vector();
    for (int i = 0; i < n; i++){
      ServerInformation tmp = readServerInfo(is, funcName);
      if (tmp != null)
	v.addElement(tmp);
    }
    readEndSchedule(is);

    ServerInformation serverInfos[] = new ServerInformation[v.size()];
    for (int i = 0; i < v.size(); i++){
      serverInfos[i] = (ServerInformation)(v.elementAt(i));
    }
    StringBuffer sb = new StringBuffer();

    ServerIndex result = scheduler.schedule(callInfo, serverInfos, service, sb);

    dbg.log(funcName.toString() + " is scheduled " + result.server);
    if (MetaServerRoot.scheduleLogStream != null)
      MetaServerRoot.scheduleLogStream.println(FormatString.format(
		"%.2lf %3d %s %s %s %s", 
		new Double(System.currentTimeMillis() / 1000.0),
		new Integer(serial),
		result.server,
		callInfo.logString(),
		scheduler.getClass().getName(),
                sb));
    return result; 
  }
}

