package ninf.scheduler;
import ninf.metaserver.*;
import ninf.common.*;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;

public class RoundRobinScheduler implements Scheduler {
  static NinfLog dbg = new NinfLog("RoundRobinScheduler");
  int counter = 0;

  public ServerIndex schedule(CallInformation info, ServerInformation serverInfos[],
    DirectoryService service, StringBuffer sb){
    
    dbg.println(info);
    for (int i = 0; i < serverInfos.length; i++){
      dbg.println(serverInfos[i]);
    }
    if (serverInfos.length == 0)
      return null;

    int n = (counter++) % serverInfos.length;
    dbg.log("select " + n + " th Server");
    return serverInfos[n].serverIndex;
  }
}
