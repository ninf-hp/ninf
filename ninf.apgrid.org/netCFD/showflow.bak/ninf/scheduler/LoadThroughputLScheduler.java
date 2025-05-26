package ninf.scheduler;
import ninf.metaserver.*;

public class LoadThroughputLScheduler extends LoadThroughputScheduler{
  void updateLoad(ServerInformation tmp){
    tmp.load.loadAverage += 1.0;
  }
}
