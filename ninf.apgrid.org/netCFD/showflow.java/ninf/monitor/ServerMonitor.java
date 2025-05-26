package ninf.monitor;
import java.awt.*;
import ninf.basic.*;
import ninf.metaserver.NinfServerHolder;
import ninf.common.*;


class ServerMonitor implements Runnable {
  static final int LOADBUFFERSIZE = 1000;

  ServerMonitorPanel panel;
  NinfServerHolder holder;
  double loads[];
  int current = 0;
  int sleepTime = 3;

  ServerMonitor(ServerMonitorPanel panel, NinfServerHolder holder){
    this.panel = panel;
    this.holder = holder;
    loads = new double[LOADBUFFERSIZE];
  }

  double getVal(int i){
    int index = (current % loads.length) -1 - i;
    while (index < 0)
      index += loads.length;
    return loads[index];

  }

  void tick(){
    loads[current++ % loads.length] = holder.load.loadAverage;
    panel.repaint();
  }

  public void run(){
    for (;;){
      try {
	tick();
	Thread.sleep(sleepTime * 1000);
      } catch (InterruptedException e){
	System.out.println(e);
      }
    }
  }
}
