package ninf.basic;

import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import java.io.*;
import java.util.StringTokenizer;

public class FunctionName {
  static final CommandRepresent acceptCommands[] = {new CommandRepresent("funcname", 2)};
  static CommandParser parser = new CommandParser(acceptCommands);

  public String module_name;	/* module name */
  public String entry_name;	/* entry name */

  /**** INSTANCE CREATION ****/
  
  public FunctionName(String module_name, String entry_name){
    this.module_name = module_name;
    this.entry_name = entry_name;
  }

  public FunctionName(String fullName){
    StringTokenizer st = new StringTokenizer(fullName, "/");
    if (st.hasMoreTokens()){
      this.module_name = st.nextToken();
    } else {
      this.entry_name = fullName;
      return;
    }
    if (st.hasMoreTokens())
      this.entry_name = st.nextToken();
    else {
      this.entry_name = this.module_name;
      this.module_name = "";
    }
  }

  public FunctionName(DataInputStream is) throws NinfException{
    NinfCommand com = parser.readCommand(is);
    this.module_name = com.args[0];
    this.entry_name = com.args[1];
  }

  public NinfCommand toCommand(){
    return new NinfCommand("funcname", module_name, entry_name);
  }
  

  /***************** FUNCTIONS *******************/

  public boolean match(String str, int option){
    if (option == NinfPktHeader.PARTIAL){
      if (str.compareTo("") == 0)
	return true;
      int tmp = str.indexOf(entry_name);
      if (tmp >= 0)
	return true;
      else
	return false;
    } else { /* EXACT */
      return (str.compareTo(entry_name) == 0);
    }
  }


  /***************** OTHER FUNCTIONS *******************/
  public String toString() {
    return (module_name + "/" + entry_name);
  }
  public int hashCode(){
//    return module_name.hashCode() + entry_name.hashCode();
    return entry_name.hashCode();
  }
  public boolean equals(Object o){
    //    System.out.println(this + "  and  " + o + " are equal? :");
    if (!(o instanceof FunctionName))
      return false;
//    if (module_name.equals(((FunctionName)o).module_name) && 
//	 entry_name.equals(((FunctionName)o).module_name))
    //    System.out.println(entry_name.length() + "  and  " + ((FunctionName)o).entry_name.length() + " are equal? :");
    if (entry_name.equals(((FunctionName)o).entry_name))
      return true;

    return false;
  }



}
