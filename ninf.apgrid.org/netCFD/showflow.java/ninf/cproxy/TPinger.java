package ninf.cproxy;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;

class TPinger implements Runnable{
  static final int MAXIMUMSIZE = 10000000;
  static final int MINIMUMSIZE = 1000;
  int interval;               /* interval in second */
  int size;                   /* size in byte */
  int minimumSize, maximumSize;
  int psize;
  NinfServerStruct target;
  double throughput;
  static NinfLog dbg = new NinfLog("TPinger");
  double measureTime;
  PrintStream throughputLogStream;

  TPinger(NinfServerStruct target, int interval, int size){
    this(target, interval, size, 10.0);
  }

  TPinger(NinfServerStruct target, int interval, int size, double measureTime){
    this(target, interval, size, measureTime, null);
  }

  TPinger(NinfServerStruct target, int interval, int size, double measureTime, 
	  PrintStream throughputLogStream){
    this(target, interval, size, MINIMUMSIZE, MAXIMUMSIZE, measureTime, throughputLogStream);
  }

  TPinger(NinfServerStruct target, int interval, int size, int minimumSize, int maximumSize, 
	  double measureTime, PrintStream throughputLogStream){
    this.target = target;
    this.interval = interval;
    this.size = size;
    this.minimumSize = minimumSize;
    this.maximumSize = maximumSize;
    this.measureTime = measureTime;
    if (throughputLogStream != null)
      this.throughputLogStream = throughputLogStream;
    else 
      this.throughputLogStream = System.out;
  }

  void ping(){
    try {
      long rtt;      
      NinfServerConnection con = target.connect();
      rtt = con.getThroughput(size, 0);
      target.throughput = new CommunicationInformation(rtt, size, 0);
      rtt = con.getThroughput(0, 0);
      target.latency = new CommunicationInformation(rtt, 0, 0);
      con.close();
      throughput = target.throughput.throughput;
      psize = size;

      size = (int)(throughput * measureTime / 2);
      if (size > maximumSize)
	size = maximumSize;
      if (size < minimumSize)
	size = minimumSize;
      
      if (throughputLogStream != null)
	throughputLogStream.println(FormatString.format(
	    "%.2lf %30s %11.2lf", 
	    new Double(System.currentTimeMillis() / 1000.0),
	    target, 
	    new Double(target.throughput.throughput)));

    } catch (NinfException e){
      System.err.println(e);
    }
  }

  public void run(){
    while (true){
      ping();
      //      System.out.println("Throughput to " + target + ": " 
      //	  + throughput + "[byte/sec] at size = " + psize);
      try {
	Thread.sleep(interval * 1000);
      } catch (InterruptedException e){
      }
    }
  }

  void start(){
    new Thread(this).start();
  }

  public static void main(String args[]){
    NinfLog.verbose();
    //    NinfLog.log();
    NinfServerStruct tmp = new NinfServerStruct(args[0], new Integer(args[1]).intValue());
    new Thread(new TPinger(tmp, 5, 
			   new Integer(args[2]).intValue(),
			   new Integer(args[2]).intValue(),
			   new Integer(args[2]).intValue(),
			   new Double (args[3]).doubleValue(),
			   null)).start();
  }
}
