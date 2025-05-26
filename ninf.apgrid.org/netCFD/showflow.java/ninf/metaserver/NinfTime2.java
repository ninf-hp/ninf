package ninf.metaserver;
import java.io.*;
import java.net.*;
import java.util.Vector;

class NinfTime2 {
  static int port = 3000;
  static String server = "";

  static String[] parseArg(String arg[]){
    Vector tmpV = new Vector();
    int index = 0;
    for (int i = 0; i < arg.length; i++){
      if (arg[i].equalsIgnoreCase("-port"))
	port = Integer.valueOf(arg[++i]).intValue();
      else if (arg[i].equalsIgnoreCase("-server"))
	server =arg[++i];
      else 
	tmpV.addElement(arg[i]);
    }
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }

  static void usage(){
    System.err.println("Usage: java NinfTime2 [-port PORT] [-server HOST]");
    System.exit(2);
  }

  public static void main(String args[]) throws Exception{
    args = parseArg(args);
    if (args.length > 0)
      usage();
    Socket s = new Socket(server, port);
    PrintStream os = new PrintStream(s.getOutputStream());
    DataInputStream is = new DataInputStream(s.getInputStream());
    os.println("time");
    String tmp = is.readLine();
    System.out.println(tmp);
  }
}
