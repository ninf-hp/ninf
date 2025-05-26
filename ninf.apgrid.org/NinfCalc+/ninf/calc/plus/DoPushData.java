package ninf.calc.plus;
import java.awt.*;
import java.net.*;
import java.io.*;
import ninf.basic.*;

class DoPushData extends Thread {
    Frame frame;
    NinfCalcStack stack;
    URL url;
    DoPushData(Frame frame, NinfCalcStack stack, URL url) {
	this.frame = frame;
	this.stack = stack;
	this.url = url;
    }
    public void run() {
	try {
	    stack.push(new ReadOnlyRemoteOperand(url));
	} catch (NinfException e) {
	    //----------------------------------------
	} catch (FileNotFoundException e) {
	    //----------------------------------------
	}
	frame.setCursor(Frame.DEFAULT_CURSOR);
    }
}
