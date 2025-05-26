package ninf.client;

import ninf.basic.*;

public class NinfExecInfo{
  public   double fore_time;
  public   double exec_time;
  public   double back_time;
  public   String hostname;
  public   int port;
  public   long start_time, end_time;
  public   double client_exec_time;
  public   int call_serial;

  public String toString(){
    return ""+ 
        client_exec_time + "  " +
        fore_time + "  " +
        exec_time + "  " +
        back_time + "  " +
        hostname + ":" + port + " " +
        call_serial;
  }
  void read_data(NinfPacketInputStream is) throws NinfException {
    fore_time = is.readDouble();
    exec_time = is.readDouble();
    back_time = is.readDouble();
    hostname = is.readString();
    port = is.readInt();
  }
  void calc_client_exec() {
    client_exec_time = (end_time - start_time) / 1000.0;
  }
  void start() {
    start_time = System.currentTimeMillis();
  }
  void end() {
    end_time = System.currentTimeMillis();
  }
}
