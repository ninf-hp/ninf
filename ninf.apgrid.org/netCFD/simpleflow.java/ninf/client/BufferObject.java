package ninf.client;
import ninf.basic.*;

public class BufferObject{
  public byte data[];
  public int sizes[];
  public URLResource rsc;

  public BufferObject(byte data[], int sizes[]){
    this.data = data;
    this.sizes = sizes;
  }
  public BufferObject(int size){
    this.data = new byte[size];
    this.sizes = null;
  }
  public BufferObject(int size, int sizes[]){
    this.data = new byte[size];
    this.sizes = sizes;
  }
  public BufferObject(byte data[]){
    this.data = data;
    this.sizes = null;
  }
  public BufferObject(){
    this.data = new byte[0];
    this.sizes = null;
  }
  public BufferObject(String str) throws NinfException{
    this.rsc = new URLResource(str);
  }

  public boolean hasResource(){
    return (rsc != null);
  }
}
