package ninf.client;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;
import ninf.basic.*;


public class URLResource{
  public String host;
  public String port;
  public String path;

  public URLResource(String str) throws NinfErrorException{
    try {
      URL url = new URL(str);
      if (url.getProtocol().equals("http")){
	host = url.getHost();
	int tmpPort = url.getPort();
	if (tmpPort < 0)
	  port = "80";
	else
	  port = ""+tmpPort;
	path = url.getFile();
      } else if (url.getProtocol().equals("file")){
	host = "";
	port = "";
	path = url.getFile();
      } else {
	throw new NinfErrorException(NinfError.MALFORMED_URL);
      }
    } catch (MalformedURLException e){
      throw new NinfErrorException(NinfError.MALFORMED_URL);
    }
  }

  public void writeTo(NinfPacketOutputStream ostream) throws NinfIOException{
    ostream.writeString(host);
    ostream.writeString(port);
    ostream.writeString(path);
  }

  public URLResource(NinfPacketInputStream istream) throws NinfException{
    host = istream.readString();
    port = istream.readString();
    path = istream.readString();
  }
}
