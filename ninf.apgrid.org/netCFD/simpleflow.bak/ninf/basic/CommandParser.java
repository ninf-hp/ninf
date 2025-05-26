package ninf.basic;

import java.util.Vector;
import java.util.StringTokenizer;
import java.io.*;

public class CommandParser{
  CommandRepresent acceptList[];
  NinfLog dbg = new NinfLog(this);

  public CommandParser(CommandRepresent acceptList[]){
    this.acceptList = acceptList;
  }

  public CommandParser(CommandRepresent acceptOne){
    this.acceptList =  new CommandRepresent[1];
    this.acceptList[0] = acceptOne;
  }

  Vector tokenize(String s) {  
    Vector v = null;
    StringTokenizer st = new StringTokenizer(s);
    while (st.hasMoreTokens()) {
      if (v == null) v = new Vector();
      v.addElement(st.nextToken());
    }
    return v;
  }
  
  int included(CommandRepresent accepts[], String s, int len){
    boolean commandMatch = false;
    for (int i = 0; i < accepts.length; i++){
      if (accepts[i].str.equalsIgnoreCase(s)) {
	commandMatch = true;
	if (accepts[i].arglen == len)	 
	  return 0;    /* match */
      }
    }
    if (commandMatch)
      return 1;      /* command match, but arglen does not match */
    else
      return 2;      /* does not match */
  }

  public NinfCommand readCommand(DataInputStream is) throws NinfException {
    String line;
    try {
      line = is.readLine();
      dbg.println("readed:" + line);
      if (line == null)
	throw new NinfIOException();
    } catch (IOException ie) {throw new NinfIOException(ie);}
    return readCommand(line);
  }

  public NinfCommand readCommand(String s) throws NinfException {
    Vector v = tokenize(s);
    if (v.size() > 0){
      String first = (String)(v.elementAt(0));
      int match = included(acceptList, first, v.size() - 1);
      if (match == 0){
	String argArray[] = new String[v.size()-1];
	for (int i = 0; i < v.size() - 1; i++)
	  argArray[i] = (String)v.elementAt(i+1);

	return new NinfCommand(first, argArray);
      } else if (match == 1){
	throw new CommandParseException("Argument length mismatch: " + s);
      } else {
	throw new CommandParseException("no command matches: " + s);
      }
    } else {
      return null;
    }
  }

}
