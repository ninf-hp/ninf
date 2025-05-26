package ninf.client;
import java.util.StringTokenizer;
import ninf.basic.NinfPacketOutputStream;
import ninf.basic.NinfIOException;

public class URLResource{
  public String host;
  public String port;
  public String path;

  public URLResource(String str){
    StringTokenizer st = new StringTokenizer(str.substring(7,str.length()));
    String tmp = st.nextToken("/");
System.out.println(tmp);
   StringTokenizer st2 = new StringTokenizer(tmp);
    host = st2.nextToken(":");
    if (st2.hasMoreTokens())
      port = st2.nextToken("/");
    else
      port = "80";
    path = st.nextToken(" ");
    path = path.substring(1,path.length());
  }
  public void writeTo(NinfPacketOutputStream ostream) throws NinfIOException{
    ostream.writeString(host);
    ostream.writeString(port);
    ostream.writeString(path);
  }
}
