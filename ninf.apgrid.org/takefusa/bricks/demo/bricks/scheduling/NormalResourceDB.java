package bricks.scheduling;
import bricks.util.*;

public class NormalResourceDB extends ResourceDB implements SchedulingUnit {

    public NormalResourceDB() {;}

    public NormalResourceDB(int numNetworkHistory, int numServerHistory) {
	this.numNetworkHistory = numNetworkHistory;
	this.numServerHistory = numServerHistory;
    }

/************************* needed method *************************/
    public String getName() {
	return "NormalResourceDB";
    }
}
