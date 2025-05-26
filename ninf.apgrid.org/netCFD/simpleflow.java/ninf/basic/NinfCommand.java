package ninf.basic;
import java.io.*;

public class NinfCommand{
  public String command;
  public String args[];

  public NinfCommand(String command, String args[]){
    this.command = command;
    this.args = args;
  }
  public NinfCommand(String command){
    this.command = command;
  }
  public NinfCommand(String command, String arg1){
    this.command = command;
    this.args = new String[1];
    this.args[0] = arg1;
  }
  public NinfCommand(String command, String arg1, String arg2){
    this.command = command;
    this.args = new String[2];
    this.args[0] = arg1;
    this.args[1] = arg2;
  }
  public NinfCommand(String command, String arg1, String arg2, String arg3){
    this.command = command;
    this.args = new String[3];
    this.args[0] = arg1;
    this.args[1] = arg2;
    this.args[2] = arg3;
  }
  public NinfCommand(String command, String arg1, String arg2, String arg3, String arg4){
    this.command = command;
    this.args = new String[4];
    this.args[0] = arg1;
    this.args[1] = arg2;
    this.args[2] = arg3;
    this.args[3] = arg4;
  }


  public int argc(){
    if (args == null)
      return 0;
    return args.length;
  }

  public String makeString(){
    String tmp = command;
    if (args != null){
      for (int i = 0; i < args.length; i++){
	tmp += " " + args[i];
      }
    }
    return tmp;
  }

  public boolean is(String str){
    return command.equalsIgnoreCase(str);
  }

  public void send(PrintStream os){
    os.println(makeString());
  }

  public String toString(){
    return makeString();
  }
  
}
