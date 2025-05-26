import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;

public class Chartest{

  public static void main(String args[]) throws Exception{
    NinfServerStruct server = new NinfServerStruct(args[0], new Integer(args[1]).intValue());
    System.out.println(server.connect().getServerCharacter());
  }
}
