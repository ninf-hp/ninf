package ninf.scheduler;
import ninf.metaserver.*;

public class LoadOnlyLScheduler extends LoadOnlyScheduler {
  void updateLoad(ServerInformation tmp){
    tmp.load.loadAverage += 1.0;
  }
}
