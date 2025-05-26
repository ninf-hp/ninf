package bricks.util;
import java.util.*;

public class InfinityCreator extends SubComponentCreator {

    public String usage() {
	return "Infinity()";
    }

    public SubComponent create(StringTokenizer st) throws BricksParseException {
	return new Infinity();
    }
}

