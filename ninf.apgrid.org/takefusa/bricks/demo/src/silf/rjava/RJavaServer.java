package silf.rjava;

import silf.util.*;
import java.io.*;
import java.net.*;

public class RJavaServer  {
  public static Logger dbg = new Logger("RJavaServer");  
  public static final int defaultPort = 3333;
  int port;

  RJavaServerCore core;

  RJavaServer(int port){
    this.port = port;
  }

  private void start() {
    try {
      ServerSocket ss = new ServerSocket(port);
      Socket s = ss.accept();

      System.err.println("accepted");

      core = new RJavaServerCore(s.getInputStream(), s.getOutputStream());
      core.start();
    } catch (IOException e){
      dbg.printStack(e);
    }
  }

  public static void main(String args[]){
    RJavaServer server  = new RJavaServer(defaultPort);
    server.start();
  }

}

