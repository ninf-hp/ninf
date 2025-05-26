package ninf.basic;
import java.util.Hashtable;
import java.util.Enumeration;

public class Set {
  Hashtable table = new Hashtable();

  public void add(Object o){
    if (table.get(o) != null)
      return;
    table.put(o, o);
  }

  public void remove(Object o){
    table.remove(o);
  }

  public Object get(Object o){
    Object tmp;
    if ((tmp = table.get(o)) != null)
      return tmp;
    return null;
  }

  public Enumeration elements(){
    return table.elements();
  }

  
  public Set select(BooleanFunction func){
    Set tmp = new Set();
    Enumeration enum = table.elements();
    while (enum.hasMoreElements()){
      Object o = enum.nextElement();
      if (func.eval(o))
	tmp.add(o);
    }
    return tmp;
  }

  public int size(){
    return table.size();
  }

  public Object match(BooleanFunction func){
    Enumeration enum = table.elements();
    while (enum.hasMoreElements()){
      Object o = enum.nextElement();
      if (func.eval(o))
	return o;
    }
    return null;
  }

  public void doEach(VoidFunction func){
    Enumeration enum = table.elements();
    while (enum.hasMoreElements()){
      Object o = enum.nextElement();
      func.eval(o);
    }
  }
}

