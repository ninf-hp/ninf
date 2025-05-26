package ninf.basic;

public class MetaServerReference {
  String host;
  int port;
  public MetaServerReference(String host, int port){
    this.host = host;
    this.port = port;
  }
  public MetaServerReference(String host, String port){
    this.host = host;
    this.port = (new Integer(port)).intValue();
  }

  public MetaServerConnection connect() throws NinfIOException{
    try {
      return new MetaServerConnection(this);
    } catch(NinfException e){
      System.out.println("Ninf Exception occured");
    }
    return null;
  }

  public String toString(){
    return host + ":" + port;
  }
}
