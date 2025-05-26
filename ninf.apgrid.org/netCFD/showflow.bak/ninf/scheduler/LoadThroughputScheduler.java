package ninf.scheduler;
import ninf.metaserver.*;
import ninf.common.*;
import ninf.basic.*;
import ninf.client.*;
import java.util.Vector;
import java.util.Hashtable;

public class LoadThroughputScheduler implements Scheduler {
  static NinfLog dbg = new NinfLog("LoadThroughputScheduler");
  int counter = 0;
  Hashtable pairTable = new Hashtable();
  TimePair[] pairs;

  void updateTable(ServerInformation serverInfo, TimePair pair){
    Vector timePairs = (Vector)pairTable.get(serverInfo.serverIndex);
    if (timePairs == null){
      timePairs = new Vector();
      pairTable.put(serverInfo.serverIndex, timePairs);
    }
    timePairs.addElement(pair);
  }

  double calcThroughput(ServerInformation serverInfo){
    return serverInfo.throughput.throughput;
  }

  double[] predict(CallInformation info, ServerInformation serverInfo, TimePair pair){
    double result[] = new double[4];
    double average = serverInfo.load.loadAverage;
    double throughput = serverInfo.throughput.throughput;
    double performance = serverInfo.serverChar.performance * 1000.0;
    long order = info.order;
    long communication = info.foreSize +info.backSize;

    long span = (long )((info.foreSize / throughput) * 1000);
    long now = System.currentTimeMillis();
    pair.from = now;
    pair.to = now + span;

    double calc = (order / (performance * (1.0 /(average + 1.0))));
    double foreComm = (info.foreSize /throughput);
    double backComm = (info.backSize /throughput);
    
    result[0] = calc + foreComm + backComm;
    result[1] = foreComm;
    result[2] = calc;
    result[3] = backComm;
    
    return result;
  }

  void updateLoad(ServerInformation tmp){
  }

  public ServerIndex schedule(CallInformation info, ServerInformation serverInfos[],
    DirectoryService service, StringBuffer sb){
    int n = 0;
    double min = Double.MAX_VALUE;

    dbg.println(info);
    double predictedTime[][] = new double[serverInfos.length][];
    pairs = new TimePair[serverInfos.length];
    for (int i = 0; i < serverInfos.length; i++)
      pairs[i] = new TimePair(0,0);
    if (serverInfos.length == 0)
      return null;
    for (int i = 0; i < serverInfos.length; i++){
      predictedTime[i] = predict(info, serverInfos[i], pairs[i]);
      if (predictedTime[i][0] < min){
	min = predictedTime[i][0];
	n = i;
      }
      dbg.log(serverInfos[i] + " predicted: " + predictedTime[i][0]);
    }
    dbg.log("select " + n + " th Server: "+ serverInfos[n]);
    sb.append(FormatString.format(
		"%10.4f %10.4f %10.4f", 
		new Double(predictedTime[n][1]),
		new Double(predictedTime[n][2]),
		new Double(predictedTime[n][3])));

    updateTable(serverInfos[n], pairs[n]);

    ServerInformation tmp = serverInfos[n];
    updateLoad(tmp);
    return tmp.serverIndex;
  }
}
