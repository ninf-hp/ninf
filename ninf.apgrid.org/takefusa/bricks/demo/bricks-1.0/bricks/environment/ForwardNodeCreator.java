package bricks.environment;
import bricks.util.*;
import java.util.*;

public class ForwardNodeCreator extends ComponentCreator {

    // for bricks.tools.ShowUsage
    public ForwardNodeCreator() {}

    public ForwardNodeCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "Node <String key>";
    }

    public void create(String str) throws BricksParseException {
	SimulationDebug.println("create ForwardNode...");
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // ForwardNode
	    String key = st.nextToken(" \t(),");

	    if (SimulationDebug.mode > 0) {
		ForwardNode forwardNode = 
		    new ForwardNode(owner, key, owner.requestedDataLogWriter);
		owner.register(forwardNode);
	    } else {
		ForwardNode forwardNode = new ForwardNode(owner, key);
		owner.register(forwardNode);
	    }
	    
	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}
