package ninf.metaserver;
import ninf.basic.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;

class MetaServerConfig extends ConfigFile{

  static String accepted[] = {"port", "myhostname", "log", "scheduler", "loadInterval", "loadlog", "schedulelog"};

  MetaServerConfig(String fileName) throws IOException{
    super(fileName, accepted);
  }

  int port(){
    String strs[] = getOneContent("port");
    if (strs == null || strs.length == 0)
      return 0;
    return Integer.valueOf(strs[0]).intValue();
  }

  int loadInterval(){
    String strs[] = getOneContent("loadInterval");
    if (strs == null || strs.length == 0)
      return 0;
    return Integer.valueOf(strs[0]).intValue();
  }

  String scheduler(){
    String str = getOneArg("scheduler");    
    if (str == null)
      return null;
    return str;
  }

  String myhostname(){
    return getOneArg("myhostname");
  }
}
