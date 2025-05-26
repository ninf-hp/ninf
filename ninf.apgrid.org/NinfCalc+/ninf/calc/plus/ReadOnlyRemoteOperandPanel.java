package ninf.calc.plus;
import java.awt.*;

public class ReadOnlyRemoteOperandPanel extends RemoteOperandPanel {
    public ReadOnlyRemoteOperandPanel(ReadOnlyRemoteOperand model) {
	super(model);
    }
    public void init() {
	setBackground(Color.white);
	super.init();
    }

}
