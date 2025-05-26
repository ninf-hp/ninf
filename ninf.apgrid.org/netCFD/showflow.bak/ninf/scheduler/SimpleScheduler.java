package ninf.scheduler;
import ninf.metaserver.*;
import ninf.common.*;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;

public class SimpleScheduler implements Scheduler {
  static NinfLog dbg = new NinfLog("SimpleScheduler");

  public ServerIndex schedule(CallInformation info, ServerInformation serverInfos[], 
			      DirectoryService service, StringBuffer sb){
    
    dbg.println(info);
    for (int i = 0; i < serverInfos.length; i++){
      dbg.println(serverInfos[i]);
    }
    if (serverInfos.length == 0)
      return null;
    return serverInfos[0].serverIndex;
  }
}
