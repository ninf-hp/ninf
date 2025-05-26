package bricks.scheduling;
import bricks.util.*;
import java.util.*;
import java.io.*;

public class NormalServerMonitor extends ServerMonitor implements SchedulingUnit {

    public NormalServerMonitor(
	String keyOfResourceDB,	Sequence interprobingTime,
	double trackingTime
    ) {
	this.keyOfResourceDB = keyOfResourceDB;
	this.interprobingTime = interprobingTime;
	this.trackingTime = trackingTime;
    }

/************************* needed method *************************/
    // override
    public String getName() {
	return "NormalServerMonitor";
    }
}
