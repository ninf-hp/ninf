package ninf.calc.plus;
import ninf.client.*;
import ninf.basic.*;

public class NinfQ extends NinfClient {
    public NinfQ() throws NinfException {
    }
    public NinfQ(String serverHost, int port) throws NinfException {
	super(serverHost, port);
    }
    public NinfQ(String serverHost) throws NinfException {
	super(serverHost, 3000); //<================================
    }

    public NinfStub[] query(String key) throws NinfException {
	NinfStub[] result;
	this.connectServer();
	result = con.query(key);
	this.disconnect();
	return result;
    }
}

