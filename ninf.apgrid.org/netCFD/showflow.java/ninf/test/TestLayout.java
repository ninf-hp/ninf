import java.awt.*;

public class TestLayout extends Panel {
  Frame frame;
  public TestLayout() {
    double ratio[] = {0.1, 0.1, 0.1, 0.2, 0.1, 0.4};
    setLayout(new MyLayout(MyLayout.HORIZONTAL, ratio));
    add(new Button("1"));
    add(new Button("2"));
    add(new Button("3"));
    add(new Button("4"));
    add(new Button("5"));
    add(new Button("6"));
  }

  public void show(){
    frame = new Frame("Scheduling Module Monitor");
    frame.add("Center", this);
    frame.resize(400, 300);
    frame.show();
  }

  public static void main(String args[]){
    (new TestLayout()).show();
  }

}

