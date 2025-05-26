package bricks.util;
import java.util.*;

public class SubComponentFactory {
    protected SimulationSet owner;
    protected Hashtable hashtableOfCreator = new Hashtable();

    public SubComponentFactory(SimulationSet owner) {
	this.owner = owner;
	init();
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	String key = null;
	try {
	    key = st.nextToken(" \t(),");
	    SubComponentCreator creator = 
		(SubComponentCreator)hashtableOfCreator.get(key.toLowerCase());
	    
	    SimulationDebug.println("SubComponentFactory: key = " + key);
	    SimulationDebug.println(
		"SubComponentFactory: Creator = " + creator
	    );

	    SubComponent subComponent = null;
	    if (creator == null) {
		creator = (SubComponentCreator)Class.forName(key + "Creator").newInstance();
		hashtableOfCreator.put(key.toLowerCase(), creator);
	    }
	    subComponent = creator.create(st);
	    return subComponent;

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (InstantiationException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	}  catch (BricksParseException e) {
	    throw e;
	}
    }

    // for DataSize of OthersData for NetworkNode
    public SubComponent create(StringTokenizer st, double mean) 
	throws BricksParseException 
    {
	String key = null;
	try {
	    key = (st.nextToken(" \t(),")).toLowerCase();
	    //System.out.println("SubComponentFactory : key = " + key);
	    SubComponentCreator creator = 
		(SubComponentCreator)hashtableOfCreator.get(key);

	    //System.out.println("SubComponentFactory_N: key = " + key);
	    //System.out.println("SubComponentFactory_N: Creator = "+creator);

	    return creator.create(st, mean);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (BricksParseException e) {
	    throw e;
	}
    }

    // for interarrivalTimeofPackets information of LinkCreator
    public SubComponent create(double mean, StringTokenizer st) 
	throws BricksParseException 
    {
	String key = null;
	try {
	    key = (st.nextToken(": \t(),")).toLowerCase();
	    //System.out.println("SubComponentFactory : key = " + key);

	    SubComponentCreator creator = 
		(SubComponentCreator)hashtableOfCreator.get(key);

	    //System.out.println("SubComponentFactory_N: key = " + key);
	    //System.out.println("SubComponentFactory_N: Creator = "+creator);

	    return creator.create(mean, st);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (NullPointerException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (BricksParseException e) {
	    throw e;
	}
    }

/************************* protected method *************************/
    protected String noEntryError(String key) {
	return this.toString() + ": no creator entry for key [" + key + "]";
    }

    // init Hashtable
    protected void init() {
	// Sequence
	SubComponentCreator creator = new ExponentRandomCreator();
	hashtableOfCreator.put("exponentrandom", creator);

	creator = new HyperexponentRandomCreator();
	hashtableOfCreator.put("hyperexponentrandom", creator);

	creator = new ConstantCreator();
	hashtableOfCreator.put("constant", creator);

	creator = new UniformRandomCreator();
	hashtableOfCreator.put("uniformrandom", creator);

	creator = new InfinityCreator();
	hashtableOfCreator.put("infinity", creator);

	creator = new InterpolationThroughputCreator();
	hashtableOfCreator.put("interpolationthroughput", creator);

	creator = new InterpolationInterarrivalTimeCreator();
	hashtableOfCreator.put("interpolationinterarrivaltime", creator);

	creator = new InterpolationCpuCreator();
	hashtableOfCreator.put("interpolationcpu", creator);

	creator = new TraceFileCreator();
	hashtableOfCreator.put("tracefile", creator);

	// Queue
	creator = new QueueFCFSCreator(this);
	hashtableOfCreator.put("queuefcfs", creator);

	creator = new QueueFCFSFiniteBufferCreator(this);
	hashtableOfCreator.put("queuefcfsfinitebuffer", creator);
    }
}
