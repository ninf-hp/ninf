package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class ResourceDBCreator extends ComponentCreator {

    SchedulingUnitFactory schedulingUnitFactory;

    // for bricks.tools.ShowUsage
    public ResourceDBCreator(){}

    public ResourceDBCreator(
	SimulationSet owner, SchedulingUnitFactory schedulingUnitFactory,
	SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.schedulingUnitFactory = schedulingUnitFactory;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "ResourceDB <String key> <ResourceDB resourceDB>";
    }

    public void create(String str) throws BricksParseException {
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // resourcedb
	    String key = st.nextToken(" \t(),"); // key of resourcedb

	    ResourceDB db = (ResourceDB) schedulingUnitFactory.create(st);
	    db.key = key;
	    SimulationDebug.println(
		"ResourceDBCreator : key = " + db + " : " + db.key
	    );
	    owner.register(key, db);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
