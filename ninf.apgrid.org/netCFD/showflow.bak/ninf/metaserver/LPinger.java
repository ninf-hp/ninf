package ninf.metaserver;
import ninf.basic.*;
import ninf.client.*;
import ninf.common.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;
import java.net.*;

class LPinger implements Runnable{
  int interval;               /* interval in second */
  NinfServerHolder target;
  LoadInformation load;
  static NinfLog dbg = new NinfLog("LPinger");

  LPinger(NinfServerHolder target, int interval){
    this.target = target;
    this.interval = interval;
  }

  void ping(){
    new Thread(new LPingerBody(target, interval)).start();
  }

  public void run(){
    while (true){
      ping();
      try {
	Thread.sleep(interval * 1000);
      } catch (InterruptedException e){
      }
    }
  }

  public static void main(String args[]){
    NinfLog.verbose();
    //    NinfLog.log();
    NinfServerHolder tmp = 
      new NinfServerHolder(args[0], new Integer(args[1]).intValue());
    new Thread(new LPinger(tmp, 1)).start();
  }
}
