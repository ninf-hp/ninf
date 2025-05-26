package ninf.monitor;
import java.awt.*;
import ninf.basic.*;
import ninf.metaserver.NinfServerHolder;
import ninf.common.*;

public class LoadGraph extends Canvas {
  ServerMonitor serverMonitor;

  Color lowColor = Color.yellow;
  Color highColor = Color.black;

  LoadGraph(ServerMonitor serverMonitor){
    this.serverMonitor = serverMonitor;
  }

  Graphics sub_g;
  Image sub_im;
  public void update(Graphics g) {
    if (sub_g == null){
      sub_im = this.createImage(size().width, size().height);
      sub_g = sub_im.getGraphics();
    }
    paint(sub_g);
    g.drawImage(sub_im, 0, 0, this);
  }

  public void paint(Graphics g) {
    g.setColor(highColor);
    g.fillRect(0, 0, size().width, size().height);
    g.setColor(lowColor);
    for (int i = 0; i < size().width; i++){
      double val = serverMonitor.getVal(i);
      int y = size().height;
      int off = (int)((1 - val) * y);
      int rest =  y - off;
      g.fillRect(size().width - i -1, off, 1, rest);
    }

  }  


}

