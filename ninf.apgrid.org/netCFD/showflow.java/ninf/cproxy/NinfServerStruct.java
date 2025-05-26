package ninf.cproxy;
import ninf.basic.*;
import ninf.common.*;
import java.io.PrintStream;

class NinfServerStruct extends ninf.client.NinfServerStruct{
  CommunicationInformation throughput;
  CommunicationInformation latency;

  NinfServerStruct(String str) {
    super(str);
  }

  NinfServerStruct(String h, int p) {
    super(h,p);
  }

  public NinfServerStruct(String h, String port) {
    super(h,port);
  }

  NinfServerStruct(NinfPacketInputStream is) throws NinfException {
    super(is);
  }

  void writeOut(PrintStream ps){
    ps.println(this.toCommand());
    ps.println(this.throughput.toCommand());
    ps.println(this.latency.toCommand());
  }
  
}
