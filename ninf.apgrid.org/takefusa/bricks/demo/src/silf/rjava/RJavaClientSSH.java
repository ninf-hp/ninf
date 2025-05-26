package silf.rjava;

import silf.util.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

/** 
  usage: java silf.rjava.RJavaClientSSH -host HOST -shell RSH_COMMAND -cp RCP_COMMAND
  -java REMOTE_JAVA_COMMAND -jar RJAVA_SERVER_JARFILE  class_name args
 */

public class RJavaClientSSH {
  public static Logger dbg = new Logger("RJavaClientSSH");  
  public static final String defaultHost = "localhost";
  public static final String REMOTE_JAR_PATH = "/tmp/rjava.jar";

  String RSH_COMMAND = "ssh";
  String RCP_COMMAND = "scp";
  String JAR_FILE    = null;
  String JAVA_COMMAND = "java";

  String SERVER_PROG_NAME = "silf.rjava.RJavaServerSSH";
  String SERVER_PROG_JAR  = "-cp " + REMOTE_JAR_PATH;

  String host;
  int port;

  RJavaClientCore core;

  public RJavaClientSSH(String host){
    this.host = host;
  }

  private void start(String args[]) {
    args = parseArg(args);

    try {
      Runtime runtime = Runtime.getRuntime();

      if (JAR_FILE != null){
	String []com = {RCP_COMMAND, JAR_FILE, host+":"+REMOTE_JAR_PATH};

	Process pcp = runtime.exec(com);
	(new Thread(new RedirectRunnable(pcp.getErrorStream(), System.err))).start();
	boolean flag = true;
	while (flag){
	  try {
	    pcp.waitFor();
	    flag = false;
	  } catch (InterruptedException e){}
	}
      }

      String [] command = new String[3];
      command[0] = RSH_COMMAND;
      command[1] = host;
      command[2] = JAVA_COMMAND + " " + 
	(JAR_FILE ==  null? "": SERVER_PROG_JAR) + " " +SERVER_PROG_NAME;

      Process proc = runtime.exec(command);

      (new Thread(new RedirectRunnable(proc.getErrorStream(), System.err))).start();
      
      core = new RJavaClientCore(proc.getInputStream(), proc.getOutputStream());
      core.start(args);
    } catch (IOException e){
      dbg.printStack(e);
    }
  }


  public static void main(String args[]){
    RJavaClientSSH client = new RJavaClientSSH(defaultHost);
    client.start(args);
  }
  
  static void usage(){
   String s= " usage: java silf.rjava.RJavaClientSSH  \n" +
             "     -host HOST   -shell RSH_COMMAND -cp RCP_COMMAND\n" + 
             "     -java REMOTE_JAVA_COMMAND -jar RJAVA_SERVER_JARFILE\n" + 
             "     CLASSNAME [ARGS]";
   System.err.println(s);
   System.exit(3);
  }

  String[] parseArg(String [] args){
    Vector tmpV = new Vector();
    int i;
    for (i = 0; i < args.length; i++){
      if (args[i].charAt(0) != '-')
	break;
      if (args[i].equalsIgnoreCase("-host")){
	host = args[++i];
      } else if (args[i].equalsIgnoreCase("-shell")){
	RSH_COMMAND = args[++i];
      } else if (args[i].equalsIgnoreCase("-cp")){
	RCP_COMMAND = args[++i];
      } else if (args[i].equalsIgnoreCase("-java")){
	JAVA_COMMAND = args[++i];
      } else if (args[i].equalsIgnoreCase("-jar")){
	JAR_FILE = args[++i];
      } else {
	usage();
      }
    }

    for (; i < args.length; i++){
      tmpV.addElement(args[i]);      
    }

    String tmp[] = new String[tmpV.size()];
    for (i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }


}



