package ninf.scheduler;
import ninf.metaserver.*;

public class LoadThroughputLTScheduler extends LoadThroughputTScheduler {
  void updateLoad(ServerInformation tmp){
    tmp.load.loadAverage += 1.0;
  }
}
