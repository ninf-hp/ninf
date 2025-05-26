package ninf.scheduler;
import ninf.common.*;
import ninf.metaserver.*;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;

public interface Scheduler {
  public ServerIndex schedule(CallInformation info, ServerInformation serverInfos[],
    DirectoryService service, StringBuffer sb);
}
