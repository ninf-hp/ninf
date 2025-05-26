package bricks.environment;
import bricks.util.*;
import java.util.*;

public class NetworkCreator extends ComponentCreator {

    // for bricks.tools.ShowUsage
    public NetworkCreator(){}

    public NetworkCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "Network <String key> <Queue queue> " +
	    "OthersData(<Sequence dataSize>, <Sqeuence interarrivalTime>)";
    }

    public void create(String str) throws BricksParseException {
	//System.out.println("create NetworkNode...");
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // network
	    String key = st.nextToken(" \t(),");
	    Queue queue = (Queue)subComponentFactory.create(st);

	    tmp = st.nextToken(" \t(),"); // OthersData
	    Sequence dataSize = 
		(Sequence)subComponentFactory.create(
		    st, (owner.logicalPacket).packetSize
		);

	    Sequence interarrivalTime = 
		(Sequence)subComponentFactory.create(st);

	    NetworkNode node = null;
	    if (owner.networkLogWriter != null) {
		node = new NetworkNode(
		    owner, key, queue, dataSize,
		    interarrivalTime, owner.networkLogWriter
		);
	    } else {
		node = new NetworkNode(
		    owner, key, queue, dataSize, interarrivalTime
		);
	    }
	    owner.register(key, node);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
