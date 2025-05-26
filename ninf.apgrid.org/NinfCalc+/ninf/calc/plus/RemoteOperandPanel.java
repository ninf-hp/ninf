package ninf.calc.plus;
import java.awt.*;

public abstract class RemoteOperandPanel extends NinfCalcOperandPanel {
    public RemoteOperandPanel(RemoteOperand model) {
	super(model);
    }

    protected Button viewButton;
    protected TextField urlLabel;

    public void init() {
	super.init();
	RemoteOperand model = (RemoteOperand)this.model;
	setLayout(new FlowLayout(FlowLayout.LEFT));
	StringBuffer s = new StringBuffer();
	s.append("[");
	int i = 0;
	while (true) {
	    s.append("" + model.dimension[i++]);
	    if (i == model.dimension.length) break;
	    s.append(",");
	}
	s.append("] ");
	s.append(model.url.toString());
	viewButton = new Button("View");
	add("West", viewButton);
	urlLabel = new TextField(s.toString(), 60);
	urlLabel.setEditable(false);
	add("Center", urlLabel);
	layout();
    }
    public boolean action(Event ev, Object o) {
	if (ev.target == viewButton) {
	    if (NinfCalc.applet != null) {
		NinfCalc.applet.getAppletContext().showDocument(
		    ((RemoteOperand)model).url
		);
	    }
	}
	return false;
    }

}
