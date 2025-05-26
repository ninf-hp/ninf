package ninf.calc.plus;
import java.net.*;

public class WritableRemoteOperand extends RemoteOperand {
    boolean available = false;

    public WritableRemoteOperand(URL url) {
    	super(url);
    }

    public boolean available() {
    	return available;
    }
}
