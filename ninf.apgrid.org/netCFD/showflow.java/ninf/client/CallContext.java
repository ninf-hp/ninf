package ninf.client;
import ninf.basic.*;

public class CallContext{
  public int iargs[];
  ArrayShape shapes[];
  public BufferObject buffers[];
  
  public CallContext(int n){
    shapes = new ArrayShape[n];
    iargs = new int[n];
    buffers = new BufferObject[n];
  }
}
