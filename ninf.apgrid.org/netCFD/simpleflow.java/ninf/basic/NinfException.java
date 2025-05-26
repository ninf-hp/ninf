package ninf.basic;

public class NinfException extends Exception{
  public String str;
  public NinfException(){
    super();
  }
  public NinfException(String str){
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
      return "NinfException:" + str;
    return super.toString();
  }
}
