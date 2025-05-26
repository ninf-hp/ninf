package ninf.monitor;
import java.io.*;


class ForwardLine implements Runnable {
  MonitorPanel panel;
  DataInputStream is;
  
  ForwardLine(MonitorPanel panel, DataInputStream is){
    this.panel = panel;
    this.is = is;
  }
  
  public void run(){
    while (true){
      try {
	String line = is.readLine();
	if (line == null)
	  break;
	panel.println(line);
      } catch (IOException e) {
	break;
      }
    }
  }
}
