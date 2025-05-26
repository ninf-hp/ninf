package bricks.util;
import bricks.environment.*;

public abstract class Obj {

    public double nextEventTime = 0.0;
    protected SimulationSet owner;
    public String key;

    public abstract void updateNextEvent(double currentTime);
    public abstract void processEvent(double currentTime);
    public abstract void initSession(double currentTime);
    public abstract String toInitString();
    public int id;
    protected static int serialNumber = 0;

/************************* public method *************************/
    public String toString() {
	return key;
    }

    public int hashCode () {
	return id;
    }

    public boolean equals(Object o) {
	if (!(o instanceof Node))
	    return false;
	//if (id == ((Node)o).id)
	if (key.compareTo(((Node)o).key) == 0)
	    return true;
	return false;
    }

/************************* protected method *************************/
    protected void init() {
	id = serialNumber++;
    }

    /************************* test *************************/

    public static void main(String[] argv) {

	ClientNode c1 = new ClientNode("c1");
	System.out.println("new ClientNode c1 : " + c1);
	ClientNode c2 = new ClientNode("c2");
	System.out.println("new ClientNode c2 : " + c2);
	ClientNode c3 = (ClientNode)((ClientNode)c1).clone();

	c1.nextEventTime = 0.1;
	c3.nextEventTime = 0.3;

	System.out.println("comparison: c1(" + c1 + ") & c2(" + c2 + ")");
	if (c1.equals(c2))
	    System.out.println("c1 == c2");
	else
	    System.out.println("c1 != c2");

	System.out.println("comparison: c1(" + c1 + ") & c3(" + c3 + ")");
	if (c1.equals(c3))
	    System.out.println("c1 == c3");
	else
	    System.out.println("c1 != c3");
	System.out.println("c1.nextEventTime = " + c1.nextEventTime);
	System.out.println("c3.nextEventTime = " + c3.nextEventTime);
    }
}
