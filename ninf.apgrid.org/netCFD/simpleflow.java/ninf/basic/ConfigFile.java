package ninf.basic;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;


public class ConfigFile{

  /* read config file and make hashtable.
     CommandString -> Vector of String[];
     when it encounter unaccepted command,
     notifies it to stderr.
   */

  protected NinfLog dbg = new NinfLog(this);
  Hashtable contents;

  public Vector getContent(String command){
    return (Vector)contents.get(command);
  }



  public String[] getOneContent(String command){
    Vector tmp = getContent(command);
    if (tmp == null)
      return new String[0];
    if (tmp.size() > 1)
      dbg.println("Duplicated " + command + " definition, use arbitary one");
    return (String[])tmp.elementAt(0);
  }

  public String getOneArg(String command){
    String strs[] = getOneContent(command);
    if (strs == null || strs.length == 0)
      return null;
    return strs[0];
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

  public ConfigFile(String fileName, String acceptList[])throws IOException{
    contents = new Hashtable();
    readConfiguration(fileName, acceptList);
  }

  boolean included(String strs[], String s){
    for (int i = 0; i < strs.length; i++){
      if (strs[i].equalsIgnoreCase(s))
	return true;
    }
    return false;
  }

  void addItem(String command, Vector arg){
    Vector vec = (Vector)contents.get(command);
    if (vec == null){
      vec = new Vector();
      contents.put(command, vec);
    }
    String argArray[] = new String[arg.size()-1];
    for (int i = 0; i < arg.size() - 1; i++)
      argArray[i] = (String)arg.elementAt(i+1);
    vec.addElement(argArray);
  }


  void readConfiguration(String fileName, String acceptList[])throws IOException{ 
    DataInputStream f;
    f = new DataInputStream(new FileInputStream(fileName));
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
	if (included(acceptList,first))
	  addItem(first, v);
	else
	  dbg.println("Unknown Command: "+ first + ", in file " + fileName);
      }
    } catch (Exception e){
      throw new IOException();
    }
  }


}
