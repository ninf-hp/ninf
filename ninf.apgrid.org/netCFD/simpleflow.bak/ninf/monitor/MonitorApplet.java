package ninf.monitor;
import java.awt.*;
import java.applet.*;

public class MonitorApplet extends Applet{
  MonitorPanel panel;
  String serverName = "";
  String portName = "";
  Button start;

  public void init() {
    setFont(new Font("Helvetica", Font.BOLD, 24));

    if (getDocumentBase() != null)
      serverName = getDocumentBase().getHost();
    String at = getParameter("port");
    if (at != null)
      portName = at;
    start = new Button("start");
    add(start);
  }

  public boolean action(Event evt, Object arg){
    if (evt.target == start){         /* accept button pressed */
      launch();
      return true;
    }
    return false;
  }
  void launch(){
    panel = new MonitorPanel(serverName, portName);
    panel.show();
  }

  public void start() {
  }

  public void stop() {
    panel.dispose();
  }

}
