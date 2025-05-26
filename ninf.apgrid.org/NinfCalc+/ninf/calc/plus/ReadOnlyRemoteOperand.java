package ninf.calc.plus;
import java.net.*;
import java.io.*;
import ninf.client.*;
import ninf.basic.*;

public class ReadOnlyRemoteOperand extends RemoteOperand {
    public ReadOnlyRemoteOperand(URL url)
	throws NinfException, FileNotFoundException
    {
	super(url);
	detectSize();
    }

    protected void detectSize() throws NinfException, FileNotFoundException {
System.out.println("getsizeserverhost = " + NinfCalc.getSizeServerHost);

	NinfClient nc = new NinfClient(NinfCalc.getSizeServerHost, NinfCalc.getSizeServerPort);
	int[] x = new int[1];
	int[] y = new int[1];
	nc.callWith("getSize", url.toString(), x, y);
	dimension = new int[2];
	dimension[0] = x[0];
	dimension[1] = y[0];
System.out.println("" + x[0] + "," + y[0]);
	if (x[0] == -1 || y[0] == -1) {
	    throw new FileNotFoundException(url.toString());
	}
    }
    
    public boolean available() {
    	return true;
    }
}
