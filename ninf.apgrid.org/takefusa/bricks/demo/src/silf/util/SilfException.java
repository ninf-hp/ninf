package silf.util;

public class SilfException extends Exception{
  public String str;
  public SilfException(){
    super();
  }
  public SilfException(String str){
    super();
    this.str = str;
  }
  public void printStackTrace(){
    if (str != null)
      System.out.println(str);
    super.printStackTrace();
  }
  public String toString(){
    if (str != null)
      return "SilfException:" + str;
    return super.toString();
  }
}
