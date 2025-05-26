package bricks.scheduling;
import bricks.util.*;
import java.util.StringTokenizer;

public abstract class SchedulingUnitCreator implements Creator {

    protected SubComponentFactory subComponentFactory;

    public abstract SchedulingUnit create(StringTokenizer st) 
	throws BricksParseException;
}
