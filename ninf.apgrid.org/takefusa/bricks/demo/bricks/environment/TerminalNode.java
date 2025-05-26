package bricks.environment;
import bricks.util.*;
import java.io.*;
import java.util.*;

/** 
 * TerminalNode.java
 * <pre>
 * TerminalNode represents the terminal that data are killed.
 * </pre>
 */
public class TerminalNode extends Node {

    public TerminalNode() {
	key = "terminal";
    }

/************************* needed method *************************/
    public String getName() {
	return "TerminalNode";
    }

    public String toOriginalString(double currentTime) {
	return "  [" + getName() + "]\n";
    }

    public String toInitString() {
	return "  [" + getName() + "]\n";
    }

    public void printLog(String log) {;}

    protected Data getNextData(double currenttime) {
	error("This is TerminalNode!!");
	return null;
    }

    public void updateNextEvent(double currentTime) {
	error("This is TerminalNode!!");
    }

    public void processEvent(double currentTime) {
	error("This is TerminalNode!!");
    }

    public void initSession(double currentTime) {
	error("This is TerminalNode!!");
    }

    public void schedule(double currentTime, Data data) {
	if (data instanceof ProbeData) {
	    ((ProbeData)data).killed(currentTime);
	}
    }
}
