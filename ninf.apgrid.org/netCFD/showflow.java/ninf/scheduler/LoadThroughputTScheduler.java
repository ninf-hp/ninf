package ninf.scheduler;
import ninf.metaserver.*;
import ninf.common.*;
import ninf.basic.*;
import java.util.Vector;
import java.util.Hashtable;

public class LoadThroughputTScheduler extends LoadThroughputScheduler {

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
}
