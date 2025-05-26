package bricks.tools;
import bricks.util.*;
import java.io.*;
import java.util.*;

/**
 * BricksConfiguration generates Bricks configuration files 
 * for the Bricks simulations.<BR>
 **/

public class BricksConfiguration extends Topology {

    protected String usage() {
	return 
	    "java bricks.tool.BricksConfiguration [options]\n" + 
	    super.argUsage();
    }

    public BricksConfiguration (String[] args) {
	super(args);
    }

    protected String generatesBricksFormat(
	Vector domains, String output, int no
    ) {
	/* print packet declaration */
	if (comment) {
	    output = printPacketComment(output);
	}
	output = printPacket(output);

	/* print SchedulingUnit declaration */
	if (comment) {
	    output = printSchedulingUnitComment(output);
	}
	output = printSchedulingUnit(output);

	return super.generatesBricksFormat(domains, output, no);
    }

    /*-------------------- protected method --------------------*/
    protected void parseArgs(String[] args) {
	super.parseArgs(args);
	format = "bricks";
	return;
    }

    protected String printPacketComment(String output) {
	return output + "\n# ----- Packet declaration -----\n" + 
	    "# Packet [packet transmission(true/false)] [default packet size]\n";
    }

    protected String printPacket(String output) {
	return output + "\npacket true 10.0\n";
    }

    protected String printSchedulingUnitComment(String output) {
	return output + "\n" + 
	    "# ----- ResourceDB declaration -----\n" + 
	    "# ResourceDB [name] [ResourceDB(networkHistory, serverHistory)]\n" + 
	    "# ----- NetworkMonitor declaration -----\n" + 
	    "# NetworkMonitor [name] \\\n" + 
	    "# [NetworkMontor([ResourceDB name], [interval of probing], [probe data size])]\n" + 
	    "# ----- ServerMonitor declaration -----\n" + 
	    "# ServerMonitor [name] \\\n" + 
	    "# [ServerMontor([ResourceDB name], [interval of probing], [trackingTime])]\n" + 
	    "# ----- Precictor declaration -----\n" + 
	    "# Predictor [name] ([ResourceDB name], [Predictor] ...)\n" + 
	    "# if no Predictor, the simulator selects \n" + 
	    "# NonNetworkPredictor and NonServerPredictor\n" + 
	    "# ----- Scheduler declaration -----\n" + 
	    "# Scheduler [name] [Scheduler([Predictor name]([argument] ..))]\n";
    }

    protected String printSchedulingUnit(String output) {

	String str = output + "\n" + 
	    "ResourceDB db NormalResourceDB(1000, 1000)\n" + 
	    "ServerMonitor sm NormalServerMonitor(db, Constant(" +
	    intervalServerProbes + "), 60)\n";

	if (predictor != null) {
	    if (predictor.equalsIgnoreCase("share"))
		str += "Predictor pr (db, SharedHistoryServerPredictor(" +
		    predictLoadUnit + "))\n";
	    else
		BricksUtil.abort("-prefictor " + predictor + 
				 " is wrong argument.\n" + usage());
	} else {
	    str += "Predictor pr (db)\n";
	}

	if (roundMonitor) {
	    str += "NetworkMonitor nm RoundNetworkMonitor(" + 
		"db, Constant(" + intervalNetworkProbes + 
		"), Constant(" + sizeNetworkProbe + "))\n";
	} else {
	    str += "NetworkMonitor nm NormalNetworkMonitor(" + 
		"db, Constant(" + intervalNetworkProbes + 
		"), Constant(" + sizeNetworkProbe + "))\n";
	}

	if (deadline) {
	    if (schedulingInterval > 0)
		str += "Scheduler " + scheduler + 
		    " DeadlineLoadThroughputScheduler(pr, " + 
		    optimisticFactor + ", " + schedulingInterval + ")\n";

	} else {
	    str += "Scheduler " + scheduler + 
		" LoadThroughputScheduler(pr)\n";
	}
	return str;
    }

    /*-------------------- main --------------------*/
    /**
     * The entry 
     */
    public static void main(String[] args) {

	BricksConfiguration bc = new BricksConfiguration(args);
	bc.generates();
    }
}
