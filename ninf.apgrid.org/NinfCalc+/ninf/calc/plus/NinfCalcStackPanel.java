package ninf.calc.plus;
import java.awt.*;
import java.util.*;

public class NinfCalcStackPanel extends Panel {
    NinfCalcStack model;
    Stack elementPanels = new Stack();
    ElementViewFactory factory = new SimpleElementViewFactory();
    
    public NinfCalcStackPanel(NinfCalcStack model) {
	this.model = model;
	setLayout(new StackLayout(2));
    }

    public void push(NinfCalcElement o) {
	NinfCalcElementView ep = factory.create(o);
	elementPanels.push(ep);
	add((Panel)ep);
	layout();
	ep.init();
	layout();
    }
    public void push(NinfCalcElement o, int offset) {
	NinfCalcElementView ep = factory.create(o);
    	int len = elementPanels.size();
	if (len -offset < 0) {
	    throw new EmptyStackException();
	}
	elementPanels.insertElementAt(ep, len -offset);
	add((Panel)ep);
	layout();
	ep.init();
//	layout();
    }
    
    public void pop() {
	Panel p = (Panel)elementPanels.pop();
	remove(p);
	layout();
    }
    public void pop(int offset) {
	int len = elementPanels.size();
	if (len -1 -offset < 0) {
	    throw new EmptyStackException();
	}
	Panel p = (Panel)elementPanels.elementAt(len -1 -offset);
	elementPanels.removeElementAt(len -1 -offset);
	remove(p);
    }

    public void reflesh() {
	removeAll();
	for (int i = 0; i < elementPanels.size(); i++) {
	    add((NinfCalcElementPanel)elementPanels.elementAt(i));
	}
	layout();
    }
}
