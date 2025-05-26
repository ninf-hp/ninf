package ninf.calc.plus;
import java.net.*;

public abstract class RemoteOperand extends NinfCalcOperand {
    URL url;
    
    public RemoteOperand(URL url) {
        this.url = url;
    }

    public Object toNinfCallArg() {
        return url.toString();
    }
}
