package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class SchedulingUnitFactory {
    protected SimulationSet owner;
    protected SubComponentFactory subComponentFactory;
    protected Hashtable hashtableOfCreator = new Hashtable();

    public SchedulingUnitFactory(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
	init();
    }

    public SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException 
    {
	String key = null;
	try {
	    //key = (st.nextToken(" \t(),")).toLowerCase();
	    key = st.nextToken(" \t(),");
	    SchedulingUnitCreator creator = 
		(SchedulingUnitCreator)hashtableOfCreator.get(key.toLowerCase());
	    
	    SimulationDebug.println("SchedulingUnitFactory: key = " + key);
	    SimulationDebug.println(
		"SchedulingUnitFactory: Creator = " + creator
	    );

	    SchedulingUnit schedulingUnit = null;
	    if (creator == null) {
		creator = (SchedulingUnitCreator)Class.forName(key + "Creator").newInstance();
		hashtableOfCreator.put(key.toLowerCase(), creator);
	    }
	    schedulingUnit = creator.create(st);
	    return schedulingUnit;

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException();

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (InstantiationException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (BricksParseException e) {
	    throw e;
	}
    }

/************************* protected method *************************/
    protected String noEntryError(String key) {
	return this + ": no creator entry for key [" + key + "]";
    }

    // init Hashtable
    protected void init() {
	// Scheduler
	SchedulingUnitCreator creator = new RandomSchedulerCreator();
	hashtableOfCreator.put("randomscheduler", creator);

	creator = new RoundRobinSchedulerCreator();
	hashtableOfCreator.put("roundrobinscheduler", creator);

	creator = new LoadIntensiveSchedulerCreator();
	hashtableOfCreator.put("loadintensivescheduler", creator);

	creator = new LoadThroughputSchedulerCreator();
	hashtableOfCreator.put("loadthroughputscheduler", creator);

	creator = new DeadlineLoadThroughputSchedulerCreator();
	hashtableOfCreator.put("deadlineloadthroughputscheduler", creator);

	// EPScheduler
	creator = new EPRandomSchedulerCreator();
	hashtableOfCreator.put("eprandomscheduler", creator);

	EPRoundRobinSchedulerCreator epRoundRobinSchedulerCreator =
	    new EPRoundRobinSchedulerCreator();
	hashtableOfCreator.put("eproundrobinscheduler", epRoundRobinSchedulerCreator);

	creator = new EPSelfSchedulerCreator();
	hashtableOfCreator.put("epselfscheduler", creator);

	// Monitor
	creator = new RoundNetworkMonitorCreator(subComponentFactory);
	hashtableOfCreator.put("roundnetworkmonitor", creator);

	creator = new NormalNetworkMonitorCreator(subComponentFactory);
	hashtableOfCreator.put("normalnetworkmonitor", creator);

	creator = new NormalServerMonitorCreator(subComponentFactory);
	hashtableOfCreator.put("normalservermonitor", creator);

	// ResourceDB
	creator = new NormalResourceDBCreator(subComponentFactory);
	hashtableOfCreator.put("normalresourcedb", creator);

	//creator = new NWSResourceDBCreator(subComponentFactory);
	//hashtableOfCreator.put("nwsresourcedb", creator);
    }
}
