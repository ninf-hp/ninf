package ninf.calc.plus;
import java.awt.*;
import ninf.client.*;
import ninf.basic.*;

public class NinfCalcOperatorPanel extends NinfCalcElementPanel {
    Button invokeButton;

    public NinfCalcOperatorPanel(NinfCalcOperator model) {
	super(model);
    }
    public void init() {
	NinfCalcOperator model = (NinfCalcOperator)this.model;
	setLayout(new FlowLayout(FlowLayout.LEFT));
	setBackground(new Color(220, 220, 220));
	add(invokeButton = new Button("Invoke"));

	StringBuffer s = new StringBuffer();
	s.append("ninf://");
	s.append(model.server);
	s.append("/");
	s.append(model.stub.entry_name);
//	for (int i = 0; i < model.stub.params.length; i++) {
//	    s.append(model.stub.params[i].toString());
//	}
	
	add(new Label(s.toString()));
	layout();
    }

    Thread invoker;
    NinfClient nc;

    public boolean action(Event ev, Object o) {
	if (ev.target == invokeButton) {
	    if (invokeButton.getLabel().equals("Invoke")) {
		invokeButton.disable();
		Thread blinker = new Blinker(this);
		blinker.start();
		NinfCalcOperator model = (NinfCalcOperator)this.model;
		try {
		    nc = new NinfClient(model.server, NinfCalc.serverPort);
		} catch (NinfException e) {
		    //------------------------------------
		}
		invoker = new Invoker(nc, model, model.stack, blinker);
		invoker.start();
		invokeButton.setLabel("Stop");
		invokeButton.enable();
	    } else if (invokeButton.getLabel().equals("Stop")) {
		invokeButton.disable();
		nc.disconnect();
		invokeButton.setLabel("Invoke");
		invokeButton.enable();
	    }
	}
	return false;
    }

}
