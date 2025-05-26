import ninf.client.*;
import ninf.basic.*;
import java.io.*;

class testQ extends NinfClient{
  public testQ() throws IOException{}

  public NinfStub[] query(String key) throws IOException {
    NinfStub tmp[];

    this.connectServer();
    tmp = con.query(key);
    this.disconnect();
    return tmp;
  }

  public static void main(String args[]){
    args = parseArg(args);
    try {
      testQ q = new testQ();
      NinfStub stubs[] = q.query(args[0]);
      
      for (int i = 0; i < stubs.length; i++)
	System.out.println(i +". "+ stubs[i].module_name + ":" + stubs[i].entry_name);
    
    } catch (IOException e){}
  }


}
