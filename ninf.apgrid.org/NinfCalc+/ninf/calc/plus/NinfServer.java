package ninf.calc.plus;
import ninf.client.*;
import ninf.basic.*;
import java.util.*;

class NinfServer {
    protected Dictionary stubOfEntry = new Hashtable();
    public String hostname;
    NinfServer(String serverHost, int port) {
	hostname = serverHost;
	try {
	    NinfQ nq = new NinfQ(serverHost, port);
	    NinfStub[] operators = nq.query("");
	    for (int i = 0; i < operators.length; i++) {
		NinfStub stub = operators[i];
		stubOfEntry.put(stub.entry_name, stub);
	    }
	} catch (NinfException e) {
	    e.printStackTrace();
	}
	
    }
    NinfStub getStubOf(String entryName) {
	return (NinfStub)stubOfEntry.get(entryName);
    }
    Enumeration stubs() {
	return stubOfEntry.elements();
    }
}
