package bricks.util;
import java.util.*;

public class QueueFCFSFiniteBufferCreator extends SubComponentCreator {

    private SubComponentFactory subComponentFactory;

    // for bricks.tools.ShowUsage
    public QueueFCFSFiniteBufferCreator(){}

    public QueueFCFSFiniteBufferCreator(SubComponentFactory subComponentFactory) {
	this.subComponentFactory = subComponentFactory;
    }

    public String usage() {
	return "QueueFCFSFiniteBuffer(" +
	    "<Sequence throughput>, <int queueLength>)";
    }

    public SubComponent create(StringTokenizer st) 
	throws BricksParseException 
    {
	try {
	    Sequence throughput = (Sequence)subComponentFactory.create(st);
	    int bufferMax = Integer.valueOf(st.nextToken(" \t,()")).intValue();
	    QueueFCFSFiniteBuffer queueFCFSFiniteBuffer = 
		new QueueFCFSFiniteBuffer(throughput, bufferMax);
	    queueFCFSFiniteBuffer.setMaxThroughput();
	    return queueFCFSFiniteBuffer;

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (BricksParseException e) {
	    e.addMessage(usage());
	    throw e;
	}

    }
}

