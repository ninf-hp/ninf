package ninf.basic;

public class CommandParseException extends NinfException{
  public CommandParseException(String s){
    super("unknown command: " + s);
  }
}
