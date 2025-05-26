package ninf.calc.plus;
import java.awt.*;
import java.util.*;
import ninf.basic.*;
import ninf.client.*;

public class Invoker extends Thread {
    NinfCalcOperator operator;
    NinfCalcStack stack;
    Thread blinker;
    NinfClient nc;
    
    public Invoker(
	NinfClient nc, NinfCalcOperator operator, NinfCalcStack stack, Thread blinker
    ) {
	this.operator = operator;
	this.stack = stack;
	this.blinker = blinker;
	this.nc = nc;
    }
    public void run() {
System.out.println("Invoke: " + operator.stub.entry_name + ": " + operator.ninfCallArgs);
	try {
	    nc.call(operator.stub.entry_name, operator.ninfCallArgs);
	} catch (NinfException e) {
e.printStackTrace();
	    blinker.stop();
	    return;
	}
	blinker.stop();
	synchronized(stack) {
	    int offset = stack.offsetOf(operator);
System.out.println(offset);
	    stack.pop(offset);
	    for (int i = 0; i < operator.numOfConsumes; i++) {
		stack.pop(offset);
	    }
	    Enumeration e = operator.results.elements();
	    while (e.hasMoreElements()) {
		stack.push((NinfCalcElement)e.nextElement(), offset);
	    }
	    stack.view.reflesh();
	}
    }
}
