package bricks.tools;
import bricks.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * ShowUsage shows usage of a specified class.<BR>
 **/
public class ShowUsage {

    /**
     * USAGE string 
     **/
    public static String usage() {
	return "java bricks.tool.ShowUsage [class name]\n";
    }

    public static void showUsage(String[] args) {

	for (int i = 0; i < args.length; i++) {

	    try {
		Creator creator = (Creator)Class.forName(args[i] + "Creator").newInstance();
		System.out.println(creator.usage());

	    } catch (ClassNotFoundException e) {
		
		try {
		    showScriptUsage(args[i]);
		} catch (BricksException be) {
		      System.err.println("ClassNotFoundException: " + args[i] + "Creator");
		}
		//e.printStackTrace();

	    } catch (InstantiationException e) {
		System.err.println("InstantiationException: " + args[i] + "Creator");
		//e.printStackTrace();

	    } catch (IllegalAccessException e) {
		System.err.println("IllegalAccessException: " + args[i] + "Creator");
		//e.printStackTrace();
	    }
	}
    }

    protected static void showScriptUsage(String arg) throws BricksException {

	String key = arg.toLowerCase();
	try {
	    if (key.equalsIgnoreCase("bricks")) {
		System.out.println("# Bricks configuration files consist of the following lines.");
		System.out.println("Packet");
		System.out.println("ResourceDB(s)");
		System.out.println("NetworkMonitor(s)");
		System.out.println("ServerMonitor(s)");
		System.out.println("Predictor(s)");
		System.out.println("Scheduler(s)");
		System.out.println("Server(s)");
		System.out.println("Client(s)");
		System.out.println("Network(s)");
		System.out.println("Link(s)");
		System.out.println("# Details of each line are shown by the following command:");
		System.out.println("# java bricks.tools.ShowUsage [word]");

	    } else if (key.equalsIgnoreCase("packet")) {
		System.out.println("Packet <double logicalPacketSize>");

	    } else if (key.equalsIgnoreCase("predictor")) {
		System.out.println("Predictor <String key> <Predictor predictor1> <Predictor predictor2> ...");

	    } else if (key.equalsIgnoreCase("scheduler")) {
		System.out.println("Scheduler <String key> <Scheduler scheduler>");

	    } else if (key.equalsIgnoreCase("resourcedb")) {
		System.out.println(((Creator)Class.forName("bricks.scheduling.ResourceDBCreator").newInstance()).usage());

	    } else if (key.equalsIgnoreCase("networkmonitor")) {
		System.out.println(((Creator)Class.forName("bricks.scheduling.NetworkMonitorCreator").newInstance()).usage());

	    } else if (key.equalsIgnoreCase("servermonitor")) {
		System.out.println(((Creator)Class.forName("bricks.scheduling.ServerMonitorCreator").newInstance()).usage());

	    } else if (key.equalsIgnoreCase("client")) {
		System.out.println(((Creator)Class.forName("bricks.environment.ClientCreator").newInstance()).usage());

	    } else if (key.equalsIgnoreCase("server")) {
		System.out.println(((Creator)Class.forName("bricks.environment.ServerCreator").newInstance()).usage());

	    } else if (key.equalsIgnoreCase("network")) {
		System.out.println(((Creator)Class.forName("bricks.environment.ServerCreator").newInstance()).usage());

	    } else if (key.equalsIgnoreCase("link")) {
		System.out.println(((Creator)Class.forName("bricks.environment.LinkCreator").newInstance()).usage());

	    } else {
		throw new BricksException();
	    }
	} catch (ClassNotFoundException e) {
	    throw new BricksException();
	} catch (InstantiationException e) {
	    throw new BricksException();
	} catch (IllegalAccessException e) {
	    throw new BricksException();
	}
    }

    /**
     * The entry 
     */
    public static void main(String[] args) {

	if (args.length < 1)
	    BricksUtil.abort(ShowUsage.usage());

	ShowUsage.showUsage(args);
    }
}



