package ninf.scheduler;
import ninf.metaserver.*;
import ninf.common.*;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;

public class LoadOnlyScheduler implements Scheduler {
  static NinfLog dbg = new NinfLog("LoadOnlyScheduler");
  int counter = 0;

  double predict(CallInformation info, ServerInformation serverInfo){
    double average = serverInfo.load.loadAverage;
    double performance = serverInfo.serverChar.performance * 1000.0;
    long order = info.order;
    double calc = (order / (performance * (1.0 /(average + 1.0))));
    double sum = calc;
    dbg.log(serverInfo.serverIndex + ": sum = " +sum);
    return sum;
  }

  void updateLoad(ServerInformation tmp){
  }

  public ServerIndex schedule(CallInformation info, ServerInformation serverInfos[],
    DirectoryService service, StringBuffer sb){
    int n = 0;
    double min = Double.MAX_VALUE;

    dbg.println(info);
    double predictedTime[] = new double[serverInfos.length];

    if (serverInfos.length == 0)
      return null;
    for (int i = 0; i < serverInfos.length; i++){
      predictedTime[i] = predict(info, serverInfos[i]);
      if (predictedTime[i] < min){
	min = predictedTime[i];
	n = i;
      }
      dbg.println(serverInfos[i] + " predicted: " + predictedTime[i]);
    }
    dbg.println("select " + n + " th Server: "+ serverInfos[n]);
    ServerInformation tmp = serverInfos[n];
    updateLoad(tmp);
    return tmp.serverIndex;
  }
}
