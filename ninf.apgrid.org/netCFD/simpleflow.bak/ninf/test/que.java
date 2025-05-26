
import java.util.Vector;
import java.lang.Math;
import java.io.DataInputStream;

class enque implements Runnable{
  String str;
  que q;
  enque(String str, que q){ this.str = str; this.q = q;}
  public void run(){
    System.out.println("do: "+str + "," + Thread.currentThread());
    String tmp = q.doIt(str);
    System.out.println("done: " + tmp);
    System.out.flush();
  }
}


public class que implements Runnable{ 
  Vector threads = new Vector();

  int index = 0;

  private void enque(Thread t){
    System.out.println("Enque :"+ t);
    threads.addElement(t);
  }

  private Thread get(){
    if (threads.size() > index){
      Thread tmp = (Thread)threads.elementAt(index);
      return tmp;
    } else 
      return null;
  }

  public synchronized void rm(){
    if (threads.size() > index)
      threads.removeElementAt(index);
  }

  private void resetIndex(){
    index += 1000;
  }

  boolean condition(){
      double r = Math.random();
      System.out.println(Thread.currentThread() + " got:"+r);
      if (r < 0.3)
	return true;
      return false;
  }

  public String doIt(String a){
    if (condition())
      return "end:" + a;
    System.out.println(Thread.currentThread() +  ": failed:"+a);
    enque(Thread.currentThread());
    Thread.currentThread().suspend();
    System.out.println(Thread.currentThread() +  ": resumed");
    while(true){
      if (condition()){
	rm();
	goAgain();
//	new Thread(this).start();
	break;
      }
      index++;
      new Thread(this).start();

      Thread.currentThread().suspend();
    }
    return ("end:" + a);
  }

  private boolean flag = true;

  public synchronized void testIt(){
    while (!flag){
      System.out.println("waiting for notify");
      try { wait();} catch(Exception e){}
    }
    flag = false;
    System.out.println(threads + ":>" + "index ="+ index);
    run();
  }

  private synchronized void goAgain(){
    index = 0;
    flag = true;
    System.out.println(Thread.currentThread() + ": notify");
    notify();
  }
  

  public synchronized void run(){
    Thread tmp;
    System.out.println(Thread.currentThread() + ": testing");
    if (index >= threads.size()){
      goAgain();
    } else {
      if ((tmp = get()) != null){
	System.out.println(Thread.currentThread() + ": testing target: at " +index + ":"+ tmp);
	tmp.resume();
      }
    }
  }


  public static void main(String args[]) throws Exception{
    que tmp = new que();
    (new Thread(new enque("a", tmp))).start();
    (new Thread(new enque("b", tmp))).start();
    (new Thread(new enque("c", tmp))).start();
    (new Thread(new enque("d", tmp))).start();
    (new Thread(new enque("e", tmp))).start();

    (new Thread(new tester(tmp))).start();
  }
  
}

class tester implements Runnable{
  que q;
  public tester(que q){this.q = q;}
  public void run(){
    Thread.currentThread().setPriority(3);
    DataInputStream is = new DataInputStream(System.in);
    while (true){
      try{ is.readLine(); } catch(Exception e){}
      q.testIt();
      System.out.println(Thread.currentThread() + ": testIt ended");
    }
  }
  
}
