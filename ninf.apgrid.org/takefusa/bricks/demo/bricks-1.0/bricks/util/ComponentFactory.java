package bricks.util;
import bricks.environment.*;
import bricks.scheduling.*;
import java.util.Hashtable;
import java.lang.reflect.*;

public class ComponentFactory {
    protected SimulationSet owner;
    protected Hashtable hashtableOfCreator = new Hashtable();
    protected SubComponentFactory subComponentFactory;
    protected SchedulingUnitFactory schedulingUnitFactory;

    public ComponentFactory(SimulationSet owner) {
	this.owner = owner;
	subComponentFactory = new SubComponentFactory(owner);
	schedulingUnitFactory = new SchedulingUnitFactory(owner, subComponentFactory);
	init();
    }

    public void create(String key, String information) throws BricksParseException {
	//String key = str.toLowerCase();
	SimulationDebug.println("ComponentFactory : key = " + key);
	ComponentCreator componentCreator = 
	    (ComponentCreator)hashtableOfCreator.get(key.toLowerCase());
	try {
	    if (componentCreator == null) {
		componentCreator = (ComponentCreator)Class.forName(key + "Creator").newInstance();
		componentCreator.set(owner, subComponentFactory);
		hashtableOfCreator.put(key.toLowerCase(), componentCreator);
	    }
	    componentCreator.create(information);

	}  catch (BricksParseException e) {
	    throw e;

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	} catch (InstantiationException e) {
	    e.printStackTrace();
	    throw new BricksParseException(noEntryError(key));

	}
    }

/************************* protected method *************************/
    protected String noEntryError(String key) {
	return this + ": no creator entry for key [" + key + "]";
    }

    // init hashtableOfCreator
    protected void init() {
	// ClientNode
	ComponentCreator creator = new ClientCreator(
	    owner, subComponentFactory
	);
	hashtableOfCreator.put("client", creator);

	// EPClientNode
	creator = new EPClientCreator(owner, subComponentFactory);
	hashtableOfCreator.put("epclient", creator);

	// ServerNode
	creator = new ServerCreator(owner, subComponentFactory);
	hashtableOfCreator.put("server", creator);

	// FallbackServerNode
	creator = new FallbackServerCreator(owner, subComponentFactory);
	hashtableOfCreator.put("fallbackserver", creator);

	// NetworkNode
	creator = new NetworkCreator(owner, subComponentFactory);
	hashtableOfCreator.put("network", creator);

	// ForwardNode
	creator = new ForwardNodeCreator(owner, subComponentFactory);
	hashtableOfCreator.put("node", creator);

	// Link
	creator = new LinkCreator(owner, subComponentFactory);
	hashtableOfCreator.put("link", creator);

	// Scheduler
	creator = new SchedulerCreator(
	    owner, schedulingUnitFactory, subComponentFactory
	);
	hashtableOfCreator.put("scheduler", creator);

	// NetworkMonitor
	creator = new NetworkMonitorCreator(
	    owner, schedulingUnitFactory, subComponentFactory
	);
	hashtableOfCreator.put("networkmonitor", creator);

	// ServerMonitor
	creator = new ServerMonitorCreator(
	    owner, schedulingUnitFactory, subComponentFactory
	);
	hashtableOfCreator.put("servermonitor", creator);

	// ResourceDB
	creator = new ResourceDBCreator(
	    owner, schedulingUnitFactory, subComponentFactory
	);
	hashtableOfCreator.put("resourcedb", creator);

	// MetaPredictor
	creator = new MetaPredictorCreator(owner, subComponentFactory);
	hashtableOfCreator.put("predictor", creator);
    }
}
