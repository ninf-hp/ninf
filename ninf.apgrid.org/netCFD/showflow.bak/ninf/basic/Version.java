package ninf.basic;

public class Version{
  static final int major = 2;
  static final int minor = 21;
  static int major(){
    return major;
  }
  static int minor(){
    return minor;
  }
  public String toString(){
    return "Ver. " + major + "." + minor;
  }
}
