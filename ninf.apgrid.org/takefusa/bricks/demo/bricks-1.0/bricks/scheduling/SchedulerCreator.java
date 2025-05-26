package bricks.scheduling;
import bricks.util.*;
import java.util.*;

public class SchedulerCreator extends ComponentCreator {

    SchedulingUnitFactory schedulingUnitFactory;

    public SchedulerCreator(
	SimulationSet owner, SchedulingUnitFactory schedulingUnitFactory,
	SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.schedulingUnitFactory = schedulingUnitFactory;
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "Scheduler <String key> <Scheduler scheduler>";
    }

    public void create(String str) throws BricksParseException {
	//System.out.println("create Scheduler...");
	try {
	    StringTokenizer st = new StringTokenizer(str);
	    String tmp = st.nextToken(" \t(),"); // scheduler

	    String key = st.nextToken(" \t(),");
	    Scheduler scheduler =
		(Scheduler) schedulingUnitFactory.create(st);
	    scheduler.key = key;
	    SimulationDebug.println(
		"SchedulerCreator : key = " + scheduler + " : " + scheduler.key
	    );
	    owner.register(key, scheduler);

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}
