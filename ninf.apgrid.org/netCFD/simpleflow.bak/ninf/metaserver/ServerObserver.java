package ninf.metaserver;
import java.util.Observer;
import java.util.Observable;
import java.io.*;
import ninf.basic.Set;
import ninf.basic.VoidFunction;

class ServerObserver implements Observer {
  PrintStream ps;
  Set servers = new Set();
  
  ServerObserver(PrintStream ps){
    this.ps = ps;
  }


  public void update(Observable obs, Object o){
    print((NinfServerHolder)obs);
  }
  
  synchronized void print(NinfServerHolder holder){
    ps.println("loadupdate");
    holder.toCommand().send(ps);
    holder.load.toCommand().send(ps);
  }


  void removeSelf(){
    servers.doEach(new RemoveThis(this));
  }

  void addObservable(NinfServerHolder obs){
    servers.add(obs);
    obs.addObserver(this);
  }
  

  class RemoveThis implements VoidFunction{
    ServerObserver obs;
    RemoveThis(ServerObserver obs){
      this.obs = obs;
    }
    public void eval(Object o){
      ((NinfServerHolder)o).deleteObserver(obs);
    }
  }

}
