package silf.rjava;

import silf.util.*;
import java.io.*;
import java.net.*;

/** 
  usage: java silf.rjava.RJavaClient -h HOST -p PORT -c CLASSNAME -shell RSH_COMMAND
 */

public class RJavaClient {
  public static Logger dbg = new Logger("RJavaClient");  
  public static final String defaultHost = "localhost";
  public static final int defaultPort = 3333;
  String host;
  int port;

  RJavaClientCore core;

  public RJavaClient(String host, int port){
    this.host = host;
    this.port = port;
  }

  private void start(String args[]) {
    try {
      Socket s = new Socket(host, port);
      System.out.println("connected");
      core = new RJavaClientCore(s.getInputStream(), s.getOutputStream());
      core.start(args);
    } catch (IOException e){
      dbg.printStack(e);
    }
  }


  public static void main(String args[]){
    RJavaClient client = new RJavaClient(defaultHost, defaultPort);
    client.start(args);
  }
  

}
