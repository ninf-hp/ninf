package bricks.environment;
import bricks.util.*;
import java.util.*;

public class ClientCreator extends ComponentCreator {

    // for bricks.tools.ShowUsage
    public ClientCreator() {}

    public ClientCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "Client <String key> <String keyOfScheduler> " +
	    "Requests(<Sequence dataSizeForSend>, <Sequence dataSizeForRecv>" +
	    ", <Sequence numInstructions>, <Sqeuence interarrivalTime>" +
	    "(, <Sequence deadlineFactor>)";
    }

    public void create(String str) throws BricksParseException {
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // client

	    String key = st.nextToken(" \t(),");
	    //String keyOfScheduler = (st.nextToken(" \t")).toLowerCase();
	    String keyOfScheduler = st.nextToken(" \t");

	    tmp = st.nextToken(" \t(),"); // Requests
	    
	    Sequence dataSizeForSend = 
		(Sequence)subComponentFactory.create(st);
	    Sequence dataSizeForReceive = 
		(Sequence)subComponentFactory.create(st);
	    Sequence numInstructions =
		(Sequence)subComponentFactory.create(st);
	    Sequence throwingInterval = 
		(Sequence)subComponentFactory.create(st);

	    if (st.hasMoreElements()) { // deadline jobs
		Sequence deadlineFactor = 
		    (Sequence)subComponentFactory.create(st);
		SimulationDebug.println("creates ClientNode with deadline");
		SimulationDebug.println("deadlineFactor = " + deadlineFactor);
		ClientNode clientNode = new ClientNode(
		    deadlineFactor,
		    owner, key, keyOfScheduler,	dataSizeForSend, 
		    dataSizeForReceive,	numInstructions, throwingInterval
		);
		owner.register(key, clientNode);

	    } else {
		SimulationDebug.println("creates ClientNode");
		ClientNode clientNode = new ClientNode(
		    owner, key, keyOfScheduler, dataSizeForSend, 
		    dataSizeForReceive,	numInstructions, throwingInterval
		);
		owner.register(key, clientNode);
	    }

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
