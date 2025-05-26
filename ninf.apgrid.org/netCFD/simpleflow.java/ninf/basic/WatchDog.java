package ninf.basic;

public class WatchDog implements Runnable{

  int interval;
  Checkable check;
  Informable inform;
  boolean forever;

  public WatchDog(Checkable check, Informable inform, 
		  int interval, boolean forever){
    this.check = check;
    this.inform = inform;
    this.interval = interval;
    this.forever = forever;
    (new Thread(this)).start();
  }

  public void run(){
    while (true){
      try {
	Thread.currentThread().sleep(interval);
      } catch (InterruptedException e){}
      if (check.check()){
	inform.inform();
	if (!forever)
	  break;
      }
    }
  }
}


