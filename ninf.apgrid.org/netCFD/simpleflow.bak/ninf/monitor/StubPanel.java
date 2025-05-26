package ninf.monitor;
import java.awt.*;
import ninf.basic.*;
import ninf.common.*;
import ninf.client.*;
import ninf.metaserver.*;
import java.util.Hashtable;
import java.util.Enumeration;



public class StubPanel extends Panel {
  Monitor monitor;

  Font font = new Font("Helvetica", Font.BOLD, 10);
  
  List stubList;
  TextArea description;
  NinfStub stubs[];

  StubPanel(Monitor monitor){
    this.monitor = monitor;
    this.setFont(font);
    this.setBackground(Color.white);

    double ratio[] = {0.35, 0.65};
    this.setLayout(new RatioLayout(RatioLayout.HORIZONTAL, ratio));
    stubList = new List();
    description = new TextArea(60, 25);
    description.setEditable(false);
    this.add(stubList);
    this.add(description);

  }

  public void makeList(){
    Hashtable table = monitor.functionTable;
    int num = table.size();   
    if (num < 0) return;
    stubs = new NinfStub[num];
    stubList.clear();
    Enumeration enum = table.keys();
    for (int i = 0; i < num; i++){
      FunctionName name = (FunctionName)enum.nextElement();
      stubs[i] = ((FunctionStruct)(table.get(name))).stub;
      stubList.addItem(name.toString());
    }    
  }

  public boolean action(Event evt, Object arg){
    if (evt.target == stubList){         /* List Item clicked */
      int selected = stubList.getSelectedIndex();
      if (selected < 0 || selected >= stubs.length) return true;
      description.setText(stubs[selected].toText());
      return true;
    }
    return false;  
  }


  


}
