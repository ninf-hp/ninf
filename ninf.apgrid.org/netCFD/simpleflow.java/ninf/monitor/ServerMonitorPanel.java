package ninf.monitor;
import java.awt.*;
import ninf.basic.*;
import ninf.metaserver.NinfServerHolder;
import ninf.common.*;

public class ServerMonitorPanel extends Panel {
  Frame frame;
  Label label;
  LoadGraph graph;
  ServerMonitor serverMonitor;
  NinfServerHolder holder;
  Font font = new Font("Helvetica", Font.BOLD, 12);

  String labelString(){
    if (holder.serverChar != null)
      return holder.struct.host + ":" + holder.struct.port + "  " + 
	holder.serverChar.toText();
    else
      return holder.struct.host + ":" + holder.struct.port;
  }
  
  void initPanel(){
    label = new Label(labelString());
    graph = new LoadGraph(serverMonitor);
    add("North", label);
    add("Center", graph);
  }

  public ServerMonitorPanel(NinfServerHolder holder){
    this.setLayout(new BorderLayout());
    this.holder = holder;
    this.setFont(font);
    serverMonitor = new ServerMonitor(this, holder);
    initPanel();
    (new Thread(serverMonitor)).start();
  }

  public void show(){
    frame = new Frame(labelString());
    frame.add("Center", this);
    frame.resize(600, 200);
    frame.show();
  }

  public void dispose(){
    frame.dispose();
  }

  public void repaint(){
    graph.repaint();
  }
}
