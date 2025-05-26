package bricks.util;
import java.util.*;

public class QueueFCFSCreator extends SubComponentCreator {

    private SubComponentFactory subComponentFactory;

    // for bricks.tools.ShowUsage
    public QueueFCFSCreator(){}

    public QueueFCFSCreator(SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "QueueFCFS(<Sequence throughput>)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    Sequence throughput = (Sequence)subComponentFactory.create(st);
	    QueueFCFS queueFCFS = new QueueFCFS(throughput);
	    queueFCFS.setMaxThroughput();
	    return queueFCFS;

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}
    }
}

