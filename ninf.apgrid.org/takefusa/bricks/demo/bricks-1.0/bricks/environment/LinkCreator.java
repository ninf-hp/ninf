package bricks.environment;
import bricks.util.*;
import java.util.*;

public class LinkCreator extends ComponentCreator {

    // for bricks.tools.ShowUsage
    public LinkCreator(){}

    public LinkCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "Link <String keyOfSourceNode>-<String keyOfDestinationNode>\n"+
	    "if SourceNode == Client / Server then\n" +
	    "  Link source-destination:ExponentRandom(<long seed>)";
    }

    public void create(String str) throws BricksParseException {
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),:"); // Link

	    while (st.hasMoreElements()) {

		String keyOfFromNode = st.nextToken(":-, \t()");
		String keyOfToNode = st.nextToken(":-, \t()");
		Node fromNode = (Node)owner.getNode(keyOfFromNode);
		Node toNode = (Node)owner.getNode(keyOfToNode);
		//System.out.println("fromKey " + keyOfFromNode + 
		//" toKey " + keyOfToNode);
		//System.out.println("from " + fromNode + " to " + toNode);
		//SimulationDebug.println("from " + fromNode + " to " + toNode);
		fromNode.putLinkedNode(toNode);

		// network link of PacketTrans
		if ((fromNode instanceof ClientNode) || 
		    (fromNode instanceof ServerNode)) {

		    double averageInterarrivalTime =
			(owner.logicalPacket).packetSize / 
			((NodeWithQueue)toNode).queue.maxThroughput;
		    SimulationDebug.println(
			"interarrival time of packets : " + 
			averageInterarrivalTime
		    );
		    Sequence ng = (Sequence)subComponentFactory.create(
					averageInterarrivalTime, st
				    );
		    fromNode.putInterarrivalTimeOfPackets(ng, toNode);
		    toNode.putInterarrivalTimeOfPackets(ng, toNode);
		}
	    }

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NullPointerException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
