package ninf.metaserver;
import ninf.basic.*;
import ninf.client.*;
import ninf.common.*;

class LPingerBody implements Runnable{
  int interval;               /* interval in second */
  NinfServerHolder target;
  LoadInformation load;
  static NinfLog dbg = new NinfLog("LPinger");

  LPingerBody(NinfServerHolder target, int interval){
    this.target = target;
    this.interval = interval;
  }

  public void run(){
    try {
      NinfServerConnection con = target.struct.connect();
      load = con.getLoad();
      target.setLoad(load);
      con.close();
    } catch (NinfException e){
      System.err.println(e);
    }
    if (MetaServerRoot.loadLogStream != null)
      MetaServerRoot.loadLogStream.println(FormatString.format(
		"%.2lf %s %s", 
		new Double(System.currentTimeMillis() / 1000.0),
		target,
		load.logString()));
  }  

}
