package bricks.scheduling;
import bricks.util.*;
import java.util.*;
import java.io.*;

public class NormalNetworkMonitor extends NetworkMonitor implements SchedulingUnit {

    public NormalNetworkMonitor(
	String keyOfResourceDB,	Sequence interprobingTime,
	Sequence probeDataSize
    ) {
	this.keyOfResourceDB = keyOfResourceDB;
	this.interprobingTime = interprobingTime;
	this.probeDataSize = probeDataSize;
    }

/************************* needed method *************************/
    // override
    public String getName() {
	return "NormalNetworkMonitor";
    }
}
