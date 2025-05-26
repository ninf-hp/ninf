package ninf.calc.plus;
import java.util.*;
import ninf.client.*;
import ninf.basic.*;

public class NinfCalcStack {
    Stack elements = new Stack();
    NinfCalcStackPanel view;
    
    public void push(NinfCalcElement o) {
	elements.push(o);
	view.push(o);
    }
    public void push(NinfCalcElement o, int offset) {
	int len = elements.size();
	if (len -offset < 0) {
	    throw new EmptyStackException();
	}
	elements.insertElementAt(o, len -offset);
	view.push(o, offset);
    }
    
    public NinfCalcElement pop() {
	try {
	    NinfCalcElement tmp = (NinfCalcElement)elements.pop();
	    view.pop();
	    return tmp;
	} catch (EmptyStackException e) {
	    throw e;
	}
    }
    public NinfCalcElement pop(int offset) {
	int len = elements.size();
	if (len -1 -offset < 0) {
	    throw new EmptyStackException();
	}
	try {
	
	    NinfCalcElement tmp = (NinfCalcElement)elements.elementAt(len -1 -offset);
	    elements.removeElementAt(len -1 -offset);
	    view.pop(offset);
	    return tmp;
	} catch (EmptyStackException e) {
	    throw e;
	}
    }
    
    public NinfCalcElement peek() {
	return (NinfCalcElement)elements.peek();
    }
    public NinfCalcElement peek(int offset) {
	int len = elements.size();
	if (len -1 -offset < 0) {
	    throw new EmptyStackException();
	}
	return (NinfCalcElement)elements.elementAt(len -1 -offset);
    }

    public int offsetOf(NinfCalcElement elm) {
	int offset = -1;
	synchronized (elements) {
	    int size = elements.size();
	    offset = size - 1 - elements.indexOf(elm);
	}
	return offset;
    }

    public void setView(NinfCalcStackPanel view) {
	this.view = view;
    }

    public boolean isValidOperator(NinfStub stub) {
	int offset = 0;
	try {
	    for (int i = 0; i < stub.params.length; i++) {
		NinfParamDesc desc = stub.params[i];
		if (desc.IS_IN_MODE()) {
		    if (desc.ndim == 0) continue;
		    int[] d = this.peek(offset++).dimension;
		    if (d.length != desc.ndim) return false;
		    for (int j = 0; j < d.length; j++) {
			//-------
		    }
		} else {
		}
	    }
	} catch (EmptyStackException e) {
	    return false;
	}
	return true;
    }

}
