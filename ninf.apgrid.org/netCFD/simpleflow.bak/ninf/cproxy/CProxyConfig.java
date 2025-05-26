package ninf.cproxy;
import ninf.basic.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;

class CProxyConfig extends ConfigFile{

  static String accepted[] = {"metaserver", "port", "myhostname", "log", "throughputSize", "MinimumSize", "MaxmumSize", "interval", "measureTime", "metaInterval", "throughputlog"};

  CProxyConfig(String fileName) throws IOException{
    super(fileName, accepted);
  }

  int port(){
    String strs[] = getOneContent("port");
    if (strs == null || strs.length == 0)
      return 0;
    return Integer.valueOf(strs[0]).intValue();
  }

  int throughputSize(){
    String strs[] = getOneContent("throughputSize");
    if (strs == null || strs.length == 0)
      return -1;
    return Integer.valueOf(strs[0]).intValue();
  }
  int minimumSize(){
    String strs[] = getOneContent("minimumSize");
    if (strs == null || strs.length == 0)
      return -1;
    return Integer.valueOf(strs[0]).intValue();
  }
  int maximumSize(){
    String strs[] = getOneContent("maximumSize");
    if (strs == null || strs.length == 0)
      return -1;
    return Integer.valueOf(strs[0]).intValue();
  }
  int interval(){
    String strs[] = getOneContent("interval");
    if (strs == null || strs.length == 0)
      return -1;
    return Integer.valueOf(strs[0]).intValue();
  }
  int metaInterval(){
    String strs[] = getOneContent("metaInterval");
    if (strs == null || strs.length == 0)
      return -1;
    return Integer.valueOf(strs[0]).intValue();
  }

  double measureTime(){
    String strs[] = getOneContent("measureTime");
    if (strs == null || strs.length == 0)
      return -1.0;
    return Double.valueOf(strs[0]).doubleValue();
  }

  String myhostname(){
    return getOneArg("myhostname");
  }

  MetaServerReference metaServer(){
    String strs[] = getOneContent("metaserver");
    if (strs == null || strs.length == 0)
      return null;
    return new MetaServerReference(strs[0], (new Integer(strs[1]).intValue()));
  }
  
}
