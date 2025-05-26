package silf.rjava;

import silf.util.*;
import java.io.*;
import java.net.*;

public class RJavaServerSSH  {
  public static Logger dbg = new Logger("RJavaServerSSH");  
  RJavaServerCore core;

  RJavaServerSSH(){
  }

  private void start() {
    try {
      core = new RJavaServerCore(System.in, System.out);
      core.start();
    } catch (IOException e){
      dbg.printStack(e);
    }
  }

  public static void main(String args[]){
    RJavaServerSSH server  = new RJavaServerSSH();
    server.start();
  }

}

