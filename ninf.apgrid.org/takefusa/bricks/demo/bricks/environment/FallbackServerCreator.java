package bricks.environment;
import bricks.util.*;
import java.util.*;

public class FallbackServerCreator extends ComponentCreator {

    // for bricks.tools.ShowUsage
    public FallbackServerCreator() {}

    public FallbackServerCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "FallbackServer(<int numFallback>) <String key> <Queue queue> " +
	    "OthersData(<Sequence numInstructions>, " +
	    "<Sqeuence interarrivalTime>) (<int traceBufferSize>)";
    }

    public void create(String str) throws BricksParseException {
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // fallbackserver
	    int numFallback = 
		Integer.valueOf(st.nextToken(" \t(),")).intValue();
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
		ServerNode serverNode = new FallbackServerNode(
		    owner, key, queue, numInstructions, 
		    interarrivalTime, numFallback, traceBufferSize
	        );
		owner.register(key, serverNode);
		
	    } else {
		ServerNode serverNode = new FallbackServerNode(
		    owner, key, queue, numInstructions, 
		    interarrivalTime, numFallback
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
