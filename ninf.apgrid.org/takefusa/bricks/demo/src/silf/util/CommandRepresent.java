package silf.util;

public class CommandRepresent {
  public String str;
  public int arglen;
  public CommandRepresent(String str, int arglen){
    this.str = str;
    this.arglen = arglen;
  }

  public boolean isMatch(String[] strs){
    if (strs.length != arglen + 1)
      return false;
    return str.equalsIgnoreCase(strs[0]);
  }
  public boolean isFirstMatch(String str){
    return this.str.equalsIgnoreCase(str);
  }

}
