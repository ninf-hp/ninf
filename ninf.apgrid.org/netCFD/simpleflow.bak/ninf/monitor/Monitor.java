package ninf.monitor;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import ninf.metaserver.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;

public class Monitor{
  MetaServerReference server;
  MetaServerConnection command;
  MetaServerConnection inform;
  MetaServerConnection logConnection;

  MonitorPanel panel;
  MonitorReceive receive;
  
  Hashtable holders;   // struct -> holder
  Hashtable smPanels;   // struct -> serverMonitorPanel
  Hashtable functionTable;  /* key = FunctionName, val = FunctionStruct */

  FunctionPinger functionPinger;
  Thread functionPingerThread;
  Thread forwarder;

  public Monitor(MonitorPanel panel){
    this.panel = panel;
    holders = new Hashtable();
    smPanels = new Hashtable();
    functionTable = new Hashtable(); 
  }

  void disconnect(){
    if (command != null){
      command.close();
      command = null;
    }
    if (inform != null){
      inform.close();
      inform = null;
    }
    if (logConnection != null){
      logConnection.close();
      logConnection = null;
    }

    Enumeration enum = smPanels.elements();
    while (enum.hasMoreElements()){
      ServerMonitorPanel smPanel = (ServerMonitorPanel)(enum.nextElement());
      smPanel.dispose();
    }
    smPanels = new Hashtable();
    holders = new Hashtable();

    if  (functionPingerThread != null){
      functionPingerThread.stop();
      if (functionPinger != null)
	functionPinger.disconnect();
      functionPingerThread = null;
    }
    if  (forwarder != null){
      forwarder.stop();
      forwarder = null;
    }
    updateFunctionTable(new Hashtable());

  }

  boolean connect(String host, String port){
    try {
      if (command != null){
	panel.print(" already connected.. ");
	return false;
      }
      server = new MetaServerReference(host, port);
      command = server.connect();
      inform = server.connect();
      inform.os.println("inform");
      receive = new MonitorReceive(inform.is, panel, this);
      (new Thread(receive)).start();

      logConnection = server.connect();
      logConnection.os.println("getLog");
      
      forwarder = new Thread(new ForwardLine(panel, logConnection.is));
      forwarder.start();

      functionPinger = new FunctionPinger(this);
      functionPingerThread = new Thread(functionPinger);
      functionPingerThread.start();

    } catch (NinfIOException e){
      //      panel.println(e);
      return false;
    }
    return true;
  }

  String getScheduler(){
    command.os.println("getScheduler");
    String tmp;
    try {
      tmp = command.is.readLine();
    } catch (IOException e){
      return null;
    }
    return trimPackage(tmp);
  }

  boolean selectScheduler(String str){
    if (command == null){
      panel.println("Connect, First.");
      return false;
    } else if (str.equals("")){
      return false;
    } else {
      command.os.println("scheduler "+ str);
      String tmp;
      try {
	tmp = command.is.readLine();
      } catch (IOException e){
	panel.print(e);
	return false;
      }
      if (tmp == null)
	return false;
      panel.println(tmp);
      if (tmp.equals("+OK"))
	return true;
      else
	return false;
    }
  }

  ServerCharacter getServerCharacter(NinfServerStruct struct)
                throws NinfException {
    command.os.println("getServerCharacter");
    struct.toCommand().send(command.os);
    ServerCharacter sChar = new ServerCharacter(command.is);
    return sChar;
  }

  void serverLoad(NinfServerStruct struct, LoadInformation load) {
    NinfServerHolder holder = (NinfServerHolder)holders.get(struct);
    if (holder == null){
      holder = new NinfServerHolder(struct, load);
      try {
	holder.serverChar = getServerCharacter(struct);
      } catch (NinfException e){
      }
      ServerMonitorPanel panel = new ServerMonitorPanel(holder);
      panel.show();
      smPanels.put(struct, panel);
      holders.put(struct, holder);
    }
    holder.load = load;
  }

  void updateFunctionTable(Hashtable newtable){
    functionTable = newtable;
    panel.stubPanel.makeList();
  }

  /******** other **********/

  String trimPackage(String str){
    StringTokenizer st = new StringTokenizer(str, ".");
    String tmp = "";
    while (st.hasMoreTokens())
      tmp = st.nextToken();
    return tmp;
  }

  /********    MAIN     ********/
  public static void main(String args[]){
    (new MonitorPanel("hpc", "3050")).show();
  }
  
}
