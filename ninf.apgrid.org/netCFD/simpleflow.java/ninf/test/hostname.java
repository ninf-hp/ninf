import java.io.*;
import java.net.*;


class hostname{
  public static void main(String arg[]){
    try {
      InetAddress localaddrs[] = InetAddress.getAllByName(arg[0]);
      for (int i = 0; i < localaddrs.length; i++)
	System.out.println(localaddrs[i]);
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}
