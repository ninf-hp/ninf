package ninf.metaserver;
import ninf.client.*;
import ninf.basic.*;

public class NinfTime extends NinfClient{

  public NinfTime() throws NinfIOException{
    super();
  }

  public void run() throws NinfException {
      this.connectServer();
      double ack = con.getTime();
      System.out.print(FormatString.format(
					   "%.2lf\n", new Double(ack)));
      this.disconnect();
  }

  public static void main(String args[]){
    try {
      NinfLog.quiet();
      args = parseArg(args);
      if (args.length > 1)
	usage();
      (new NinfTime()).run();
    } catch (NinfException e){}
  }
  static void usage(){
    System.err.println("Usage: java NinfTime [-port PORT] [-server HOST]");
    System.exit(2);
  }

}
