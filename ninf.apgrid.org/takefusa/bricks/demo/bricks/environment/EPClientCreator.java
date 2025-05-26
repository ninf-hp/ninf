package bricks.environment;
import bricks.util.*;
import java.util.*;

public class EPClientCreator extends ComponentCreator {

    // for bricks.tools.ShowUsage
    public EPClientCreator() {}

    public EPClientCreator(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "EPClient <String key> <String keyOfScheduler> " +
	    "Requests(<Sequence numTasks>, " +
	    "<Sequence dataSizeForSend>, <Sequence dataSizeForRecv>" +
	    ", <Sequence numInstructions>, <Sqeuence interarrivalTime>" +
	    "(, <Sequence deadlineFactor>))";
    }

    public void create(String str) throws BricksParseException {
	//System.out.println("create ClientNode...");
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // client

	    String key = st.nextToken(" \t(),");
	    //String keyOfScheduler = (st.nextToken(" \t")).toLowerCase();
	    String keyOfScheduler = st.nextToken(" \t");

	    tmp = st.nextToken(" \t(),"); // Requests
	    
	    Sequence numTasks = 
		(Sequence)subComponentFactory.create(st);
	    Sequence dataSizeForSend = 
		(Sequence)subComponentFactory.create(st);
	    Sequence dataSizeForReceive = 
		(Sequence)subComponentFactory.create(st);
	    Sequence numInstructions =
		(Sequence)subComponentFactory.create(st);
	    Sequence throwingInterval = 
		(Sequence)subComponentFactory.create(st);

	    if (st.hasMoreElements()) {
		Sequence deadlineFactor = 
		    (Sequence)subComponentFactory.create(st);

		EPClientNode clientNode = new EPClientNode(
		    owner, key, keyOfScheduler, numTasks, 
		    dataSizeForSend, dataSizeForReceive,
		    numInstructions, throwingInterval,
		    deadlineFactor
		);
		owner.register(key, clientNode);
	    } else {
		EPClientNode clientNode = new EPClientNode(
		    owner, key, keyOfScheduler, numTasks, 
		    dataSizeForSend, dataSizeForReceive,
		    numInstructions, throwingInterval
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
