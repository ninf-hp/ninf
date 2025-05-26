package ninf.calc.plus;
import java.awt.*;
import java.net.*;
import java.util.*;
import ninf.client.*;
import ninf.basic.*;

public class NinfCalcControlPanel extends Panel {
    protected NinfCalcStack stack;

    protected Button pushData = new Button("Push Data");
    protected TextField url = new TextField(80);
    protected Button pushOperator = new Button("Push Operator");
    protected Choice serverChoice = new Choice();
    protected Choice operatorChoice = new Choice();
    protected NinfServer server;
    protected Button pop = new Button("Pop");
    
    public NinfCalcControlPanel(NinfCalcStack stack, String defaultServer) {
	this.stack = stack;

        setLayout(new BorderLayout());
        Panel controller = new Panel();
	controller.setLayout(new GridLayout(0, 1));

	Panel p = new Panel();
	p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(pushData);
        p.add(new Label("URL:"));
        p.add(url);
        controller.add(p);

	p = new Panel();
	p.setLayout(new FlowLayout(FlowLayout.LEFT));
	serverChoice.addItem(defaultServer);
	p.add(pushOperator);
	p.add(new Label("NinfServer:"));
	p.add(serverChoice);
	initOperatorChoice();
	p.add(new Label("Operator:"));
	p.add(operatorChoice);
	controller.add(p);

	p = new Panel();
	p.setLayout(new FlowLayout(FlowLayout.LEFT));
	pop = new Button("Pop");
	p.add(pop);
	controller.add(p);

	add("North", controller);
    }

    protected void initOperatorChoice() {
	server = new NinfServer(serverChoice.getSelectedItem(), NinfCalc.serverPort);
	Enumeration e = server.stubs();
	while (e.hasMoreElements()) {
	    NinfStub stub = (NinfStub)e.nextElement();
	    operatorChoice.addItem(stub.entry_name);
	}
    }

    public boolean action(Event ev, Object o) {
	if (ev.target == pushData) {
	    doPushData();
	} else if (ev.target == pushOperator) {
	    doPushOperator();
	} else if (ev.target == pop) {
	    doPop();
	}
	return false;
    }

    protected void doPushData() {
	Frame f = getFrame();
	try {
	    URL url = new URL(this.url.getText());
	    f.setCursor(Frame.WAIT_CURSOR);
	    new DoPushData(f, stack, url).start();
	} catch (MalformedURLException e) {
	    String message = "MalformedURL: " + url;
	    stack.push(new ErrorElement(message));
	}
    }
    protected Frame getFrame() {
	Container parent;
	parent = getParent();
	while (!(parent instanceof Frame) && parent != null) {
	    parent = parent.getParent();
	}
	return (Frame)parent;
    }
	
    protected void doPushOperator() {
	String operatorName = operatorChoice.getSelectedItem();
	NinfStub stub = server.getStubOf(operatorName);
	if (stack.isValidOperator(stub)) {
	    stack.push(new NinfCalcOperator(stack, server.hostname, stub));
	} else {
	    String message = "Invalid Operator: ";
	    stack.push(new ErrorElement(message));
	}
    }
    protected void doPop() {
	try {
	    stack.pop();
	} catch (EmptyStackException e) {
	    //-------
	}
    }

    public void setDataURL(String url) {
	this.url.setText(url);
    }
}
