package ninf.metaserver;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;

public class ServerInformation{
  public ServerIndex serverIndex;
  public   CommunicationInformation throughput;
  public   CommunicationInformation latency;
  public   LoadInformation load;
  public   ServerCharacter serverChar;

  ServerInformation(ServerIndex serverIndex, CommunicationInformation throughput, 
		    CommunicationInformation latency, LoadInformation load,
		    ServerCharacter serverChar){
    this.serverIndex = serverIndex;
    this.throughput = throughput;
    this.latency = latency;
    this.load = load;
    this.serverChar = serverChar;
  }

  public String toString(){
    return serverIndex + ", throughput = " + throughput.throughput
      + ", latency = " + latency.rtt + ", " + load + ", " + serverChar;
  }

}
