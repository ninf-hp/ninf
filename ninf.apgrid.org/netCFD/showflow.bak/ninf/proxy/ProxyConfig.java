package ninf.proxy;
import ninf.basic.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;

class ProxyConfig extends ConfigFile{

  static String accepted[] = {"metaserver", "server", "port", "myhostname", "log"};

  ProxyConfig(String fileName) throws IOException{
    super(fileName, accepted);
  }

  int port(){
    String strs[] = getOneContent("port");
    if (strs == null || strs.length == 0)
      return 0;
    return Integer.valueOf(strs[0]).intValue();
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
