package bricks.environment;
import bricks.util.*;
import java.util.*;

public class ServerCreator extends ComponentCreator {

    // for bricks.tools.ShowUsage
    public ServerCreator(){}

    public ServerCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "Server <String key> <Queue queue> " +
	    "OthersData(<Sequence numInstructions>, " +
	    "<Sqeuence interarrivalTime>) (<int traceBufferSize>)";
    }

    public void create(String str) throws BricksParseException {
	SimulationDebug.println("create ServerNode...");
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // server
	    String key = st.nextToken(" \t(),");
	    Queue queue = (Queue)subComponentFactory.create(st);

	    tmp = st.nextToken(" \t(),"); // OthersData
	    Sequence numInstructions = 
		(Sequence)subComponentFactory.create(st);
	    Sequence interarrivalTime = 
		(Sequence)subComponentFactory.create(st);

	    if (st.hasMoreElements()) {
		int traceBufferSize = 
		    Integer.valueOf(st.nextToken(" \t,()")).intValue();
		ServerNode serverNode = new ServerNode(
		    owner, key, queue, numInstructions, 
		    interarrivalTime, traceBufferSize
	        );
		owner.register(key, serverNode);
		
	    } else {
		ServerNode serverNode = new ServerNode(
		    owner, key, queue, numInstructions, interarrivalTime
	        );
		owner.register(key, serverNode);
	    }

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
