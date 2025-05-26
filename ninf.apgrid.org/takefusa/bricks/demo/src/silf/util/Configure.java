package silf.util;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;


public class Configure{

  /* read config file and make hashtable.
     CommandString -> Vector of String[];
     when it encounter unaccepted command,
     notifies it to stderr.
   */
  protected Logger dbg = new Logger(this);
  CommandRepresent[] acceptCommands;
  CommandRepresent[] acceptOptions;
  Hashtable contents;
  Hashtable options;

  public Configure(String args[],
		   CommandRepresent[] acceptCommands,
		   CommandRepresent[] acceptOptions) throws SilfException{
    contents = new Hashtable();
    options = new Hashtable();
    this.acceptCommands = acceptCommands;
    this.acceptOptions = acceptOptions;
    args = parseArg(args);
    String filename = getFileName(args);
    if (filename != null)
      readConfiguration(filename);
  }

  String getFileName(String args[]) throws SilfException{
    if (args.length < 1){
      dbg.log("No configuration file specified.");
      return null;
    }
    return args[0];
  }

  public Vector getContent(String command){
    return (Vector)contents.get(command.toLowerCase());
  }

  public String[] getOneContent(String command){
    Vector tmp = getContent(command);
    if (tmp == null)
      return new String[0];
    if (tmp.size() > 1)
      dbg.log("Duplicated " + command + " definition, use arbitary one");
    return (String[])tmp.elementAt(0);
  }

  public int getPositiveNumber(String command){
    Vector tmp = getContent(command);
    if (tmp == null)
      return -1;
    if (tmp.size() > 1)
      dbg.log("Duplicated " + command + " definition, use arbitary one");
    return Integer.valueOf(((String[])tmp.elementAt(0))[0]).intValue();
  }

  public double getPositiveDouble(String command){
    Vector tmp = getContent(command);
    if (tmp == null)
      return -1;
    if (tmp.size() > 1)
      dbg.log("Duplicated " + command + " definition, use arbitary one");
    return Double.valueOf(((String[])tmp.elementAt(0))[0]).doubleValue();
  }

  public String getOneArg(String command){
    String strs[] = getOneContent(command);
    if (strs == null || strs.length == 0)
      return null;
    return strs[0];
  }

  public String[] getOptionArg(String optname){
    return (String [])options.get(optname);
  }
  public String getFirstOptionArg(String optname){
    String[] tmp;
    if ((tmp = (String [])options.get(optname)) != null)
      return tmp[0];
    return null;
  }

  public boolean getBoolean(String optname, boolean initial)
    throws ConfigureException{
    String tmp = getOneArg(optname);
    if (tmp == null)
       return initial;
    if (tmp.equalsIgnoreCase("yes") || tmp.equalsIgnoreCase("on") ||
	tmp.equalsIgnoreCase("true"))
       return true;
    if (tmp.equalsIgnoreCase("no") || tmp.equalsIgnoreCase("off") ||
	tmp.equalsIgnoreCase("false"))
       return false;
    throw new ConfigureException("Entry '"+ optname+ "' have wrong value");
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

  boolean included(CommandRepresent[] commands, String[] strs){
    for (int i = 0; i < commands.length; i++){
      if (commands[i].isMatch(strs))
	return true;
    }
    return false;
  }

  boolean included(CommandRepresent[] commands, Vector tmpV){
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return included(commands, tmp);
  }

  void addItem(String command, Vector arg){
    Vector vec = (Vector)contents.get(command.toLowerCase());
    if (vec == null){
      vec = new Vector();
      contents.put(command.toLowerCase(), vec);
    }
    String argArray[] = new String[arg.size()-1];
    for (int i = 0; i < arg.size() - 1; i++)
      argArray[i] = (String)arg.elementAt(i+1);
    vec.addElement(argArray);
  }

  public void readConfiguration(String fileName)throws ConfigureException{ 
    DataInputStream f;
    try {
      f = new DataInputStream(new FileInputStream(fileName));
    } catch (IOException e){
      dbg.log("Failed to open the configure file " + fileName);      
      throw new ConfigureException();
    }

    String s;
    try {
      while (f.available() != 0) {
	s = f.readLine();
	if (s.length() == 0) continue;    // skip empty line
	if (s.charAt(0) == '#') continue; // skip comment line
	Vector v = tokenize(s);
	if (v == null || v.size() == 0) continue;
	int tn = v.size();
	String first = (String)(v.elementAt(0));
	if (included(acceptCommands,v))
	  addItem(first, v);
	else{
	  dbg.log("Unknown Command: "+ first + ", in file " + fileName);
	  throw new ConfigureException();
	}
      }
    } catch (IOException e){
      dbg.log("IO Error occurred while reading the configuration file " 
	      + fileName) ;
      throw new ConfigureException();
    }
  }

  int getOneOption(String args[], int index) throws SilfException {
    CommandRepresent[] commands = acceptOptions;
    for (int i = 0; i < commands.length; i++){
      if (commands[i].isFirstMatch(args[index])){
	String[] largs = new String[commands[i].arglen];
	try {
	  for (int j = 0; j < commands[i].arglen; j++)
	    largs[j] = args[index +j];
	} catch (ArrayIndexOutOfBoundsException e){
	  dbg.log("index over run while parsing argument " 
			     + args[index]);
	  throw new ConfigureException();
	}
	String optionString = args[index].toLowerCase();
	options.put(optionString, largs);
	return index + commands[i].arglen;
      }
    }
    dbg.log("Unknown option: "+ args[index] + " . Ignored");
    return index;
  }

  public String[] parseArg(String args[]) throws SilfException{
    Vector tmpV = new Vector();
    int index = 0;
    for (int i = 0; i < args.length; i++){
      if (args[i].startsWith("-"))
	i = getOneOption(args, i);
      else 
	tmpV.addElement(args[i]);
    }
    String tmp[] = new String[tmpV.size()];
    for (int i = 0; i < tmpV.size(); i++)
      tmp[i] = (String)(tmpV.elementAt(i));
    return tmp;
  }



  /**************  Configure  ****************/

  public void configureLog(){
    if (getOneArg("log") != null){
      try {
	Logger.changeOutput(getOneArg("log"));
      } catch (java.io.IOException ie){
	dbg.log("cannot open log file: "+ getOneArg("log"));
      }
    }
  }

  protected void configureDebug(){
    if (options.get("-debug") != null)
      Logger.verbose();
  }
  protected void configureQuiet(){
    if (options.get("-quiet") != null)
      Logger.verbose();
  }


  public void configure() throws SilfException{
    configureDebug();
    configureQuiet();
  }

  public PrintStream openLogStream(String entryName) throws SilfException{
    String tmp = getOneArg(entryName);
    if (tmp != null){
      try {
	return new PrintStream(new FileOutputStream(tmp));
      } catch(IOException e) {
	dbg.log("Can't open log file for " + "entryName: " + entryName);
	throw new ConfigureException();
      }
    } else {
      return null;
    }
  }

}
