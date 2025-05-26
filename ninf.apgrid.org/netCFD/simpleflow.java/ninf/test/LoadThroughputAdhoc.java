package ninf.metaserver;
import ninf.common.*;
import ninf.basic.*;
import ninf.client.*;
import java.util.Hashtable;
import java.util.Vector;

class TimePair {
  long from;
  long to;
  TimePair(long from, long to){
    this.from = from;
    this.to = to;    
  }
}


class LoadThroughputAdhoc implements Scheduler {
  static NinfLog dbg = new NinfLog("LoadThroughputAdhoc");
  int counter = 0;
  Hashtable pairTable = new Hashtable();

  TimePair[] pairs;

  double calcThroughput(ServerInformation serverInfo){
    double throughput = serverInfo.throughput.throughput;
    Vector timePairs = (Vector)pairTable.get(serverInfo.serverIndex);
    if (timePairs == null)
      return throughput;
    long now = System.currentTimeMillis();
    long when = serverInfo.throughput.when;
    Vector tmpV = new Vector();

    for (int i = 0; i < timePairs.size(); i++){
      TimePair tmp = (TimePair)timePairs.elementAt(i);
      dbg.log("from, to, when, now = " + tmp.from +"," + tmp.to +"," +when+ "," +now);
      if (now > tmp.to || tmp.from < when)
	continue;
      tmpV.addElement(tmp);
    }
    pairTable.put(serverInfo.serverIndex, tmpV);
    return throughput / (tmpV.size() + 1);
  }

  void updateTable(ServerInformation serverInfo, TimePair pair){
    Vector timePairs = (Vector)pairTable.get(serverInfo.serverIndex);
    if (timePairs == null){
      timePairs = new Vector();
      pairTable.put(serverInfo.serverIndex, timePairs);
    }
    timePairs.addElement(pair);
  }

  double predict(CallInformation info, ServerInformation serverInfo, int n){
    double average = serverInfo.load.loadAverage;
    double performance = 
      ((NinfServerStruct)(serverInfo.serverIndex.server)).serverChar.performance * 1000.0;
    double throughput = calcThroughput(serverInfo);
    dbg.log("calc'ed throughput = " + throughput);
    long order = info.order;
    long communication = info.foreSize +info.backSize;

    long span = (long )((info.foreSize / throughput) * 1000);
    long now = System.currentTimeMillis();
    pairs[n] = new TimePair(now, now + span);

    double calc = (order / (performance * (1.0 /(average + 1.0))));
    double comm = (communication /throughput);
    double sum = calc + comm;
    dbg.log(serverInfo.serverIndex + " : calc = " + calc + ", comm = " + comm + ": sum = " + sum);
    return sum;
  }

  void updateLoad(ServerInformation tmp){
    tmp.load.loadAverage += 1.0;
  }

  public ServerIndex schedule(CallInformation info, ServerInformation serverInfos[],
    DirectoryService service, StringBuffer sb){
    int n = 0;
    double min = Double.MAX_VALUE;

    dbg.println(info);
    double predictedTime[] = new double[serverInfos.length];
    pairs = new TimePair[serverInfos.length];

    if (serverInfos.length == 0)
      return null;
    for (int i = 0; i < serverInfos.length; i++){
      predictedTime[i] = predict(info, serverInfos[i], i);
      if (predictedTime[i] < min){
	min = predictedTime[i];
	n = i;
      }
      dbg.log(serverInfos[i] + " predicted: " + predictedTime[i]);
    }
    dbg.log("select " + n + " th Server: "+ serverInfos[n]);

    updateTable(serverInfos[n], pairs[n]);

    ServerInformation tmp = serverInfos[n];
    updateLoad(tmp);
    return tmp.serverIndex;
  }
}
