package ninf.monitor;
import java.awt.*;
import ninf.basic.*;
import ninf.common.*;

public class MonitorPanel extends Panel {
  Frame frame;
  TextField host, port;
  TextArea logWindow;
  StubPanel stubPanel;
  Button connect;
  Button disconnect;
  Choice schedulerChoice;
  Monitor monitor;
  Font font = new Font("Helvetica", Font.BOLD, 12);

  String schedulers[] = {
    "", "SimpleScheduler",
    "RoundRobinScheduler",
    "LoadOnlyScheduler",       "LoadOnlyLScheduler",       
    "LoadThroughputScheduler", "LoadThroughputLScheduler",
    "LoadThroughputTScheduler","LoadThroughputLTScheduler"
  };

  void initSchedulerChoice(){
    schedulerChoice = new Choice();
    for (int i = 0; i < schedulers.length; i++){
      schedulerChoice.addItem(schedulers[i]);
    }
  }
  
  public MonitorPanel(String hostName, String portName){
    monitor = new Monitor(this);
    this.setFont(font);
    Panel buttonPanel = new Panel();
    connect = new Button("Connect");
    buttonPanel.add(connect);
    disconnect = new Button("Disconnect");
    buttonPanel.add(disconnect);
    initSchedulerChoice();

    Panel textPanel = new Panel();
    if (hostName == null)
      host = new TextField("", 20);
    else
      host = new TextField(hostName, 20);
    if (portName == null)
      port = new TextField("", 20);
    else
      port = new TextField(portName, 20);
    
    textPanel.setLayout(new GridLayout(4,1));
    { 
      Panel tmpPanel = new Panel();
      tmpPanel.add(new Label("Host"));
      tmpPanel.add(host);
      textPanel.add(tmpPanel);
    }
    { 
      Panel tmpPanel = new Panel();
      tmpPanel.add(new Label("Port"));
      tmpPanel.add(port);
      textPanel.add(tmpPanel);
    }
    textPanel.add(buttonPanel);
    { 
      Panel tmpPanel = new Panel();
      tmpPanel.add(schedulerChoice);
      textPanel.add(tmpPanel);
    }
    logWindow = new TextArea(80, 25);

    Panel tmpPanel0 = new Panel();
    tmpPanel0.setLayout(new BorderLayout());
    tmpPanel0.add("West", textPanel);
    tmpPanel0.add("Center", logWindow);
    
    stubPanel = new StubPanel(monitor);


    // this.setLayout(new GridLayout(2,1));
    // this.add(tmpPanel0);
    // this.add(stubPanel);
    
    double ratio[] = {0.4, 0.6};
    this.setLayout(new RatioLayout(RatioLayout.VERTICAL, ratio));
    this.add(tmpPanel0);
    this.add(stubPanel);
  }  

  /** Method to display self on a Window */
  public void show(){
    frame = new Frame("Scheduling Module Monitor");
    frame.add("Center", this);
    frame.resize(600, 400);
    frame.show();
  }

  public void dispose(){
    disconnect();
    if (frame != null)
      frame.dispose();
    frame = null;
  }

  void correctInfo(){
    String s = monitor.getScheduler();
    if (s != null)
      schedulerChoice.select(s);
  }

  void connect(){
    print("connecting ...");
    if (monitor.connect(host.getText(), port.getText())){
      correctInfo();
      println("  connected");
    } else
      println("  refused");	
  }
  void disconnect(){
      println("disconnect");
      schedulerChoice.select("");
      monitor.disconnect();
      repaint();
  }

  public boolean action(Event evt, Object arg){
    if (evt.target == connect){         /* accept button pressed */
      connect();
      return true;
    } else if (evt.target == disconnect){  
      disconnect();
      return true;
    } else if (evt.target == schedulerChoice){  
      println("select "+arg);
      if (!(monitor.selectScheduler((String)arg))){
	schedulerChoice.select("");
	println("selection failed "+arg);
      }
      return true;
    } else {
      return false;
    }
  }

  public void print(Object o){
    logWindow.appendText(""+ o);
  }
  public void println(Object o){
    logWindow.appendText(""+ o + "\n");
  }

}

