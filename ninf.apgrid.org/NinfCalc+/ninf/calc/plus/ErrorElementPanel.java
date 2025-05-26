package ninf.calc.plus;
import java.awt.*;

public class ErrorElementPanel extends NinfCalcElementPanel {
    public ErrorElementPanel(ErrorElement model) {
	super(model);
    }

    public void init() {
	super.init();
	setBackground(new Color(200, 0, 0));
	ErrorElement model = (ErrorElement)this.model;
	setLayout(new FlowLayout(FlowLayout.LEFT));
	add(new Label(model.message));
	layout();
    }
}
