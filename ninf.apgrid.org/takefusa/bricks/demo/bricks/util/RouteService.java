package bricks.util;
import bricks.environment.*;
import bricks.scheduling.*;
import java.util.*;

public class RouteService {

    Hashtable routes = new Hashtable();

    public RouteService () {
    }

    public int size() {
	return routes.size();
    }

    public void initClientRoutes(Enumeration clients) {

	while (clients.hasMoreElements()) {

	    ClientNode client = (ClientNode)clients.nextElement();
	    Hashtable table = client.getRoutes();
	    Enumeration keys = table.keys();

	    while (keys.hasMoreElements()) {
		Node node = (Node)keys.nextElement();
		Vector route = (Vector)table.get(node);
		NodePair pair = new NodePair(client, node);
		routes.put(pair, route);
		//SimulationDebug.println("put " + node + "'s route initClientRoutes");
		//SimulationDebug.println("pair = " + pair + ", route = " + route);
	    }
	}
    }

    public void initServerRoutes(Enumeration servers) {

	while (servers.hasMoreElements()) {

	    ServerNode server = (ServerNode)servers.nextElement();
	    Hashtable table = server.getRoutes();
	    Enumeration keys = table.keys();
	    
	    SimulationDebug.println("RouteService.initServerRoute: " + server);
	    while (keys.hasMoreElements()) {
		Node node = (Node)keys.nextElement();
		SimulationDebug.println("RouteService.initServerRoute: " + node);
		Vector route = (Vector)table.get(node);
		NodePair pair = new NodePair(server, node);
		routes.put(pair, route);
		//SimulationDebug.println("put " + node + "'s route initServerRoutes");
		//SimulationDebug.println("pair = " + pair + ", route = " + route);
	    }
	}
	SimulationDebug.println("RouteService.initServerRoute: FINISH");
    }

    /*
    public void init(Enumeration e) {

	while (e.hasMoreElements()) {
	    ClientNode client = (ClientNode)e.nextElement();

	    // from ClientNode to ServerNode
	    Hashtable table = client.getCtoSRouteTable();
	    SimulationDebug.println("RouteService: table.size() = " + table.size());
	    Enumeration keys = table.keys();
	    while (keys.hasMoreElements()) {
		ServerNode server = (ServerNode)keys.nextElement();
		Vector vectorOfRoute = (Vector)table.get(server);
		vectorOfRoute.removeElement(client);
		vectorOfRoute.removeElement(server);

		NodePair pair = new NodePair(client, server);
		putIntoRoutes(pair, vectorOfRoute);
		//routes.put(pair, vectorOfRoute);
		SimulationDebug.print("RouteService: pair = " + pair);
		SimulationDebug.println(client + " - " + server + " : " + vectorOfRoute);
	    }
	    SimulationDebug.println("RouteService: [finish from ClientNode to ServerNode]");

	    // from ServerNode to ClientNode
	    table = client.getStoCRouteTable();
	    keys = table.keys();
	    while (keys.hasMoreElements()) {
		ServerNode server = (ServerNode)keys.nextElement();
		Vector vectorOfRoute = (Vector)table.get(server);

		vectorOfRoute.removeElement(client);
		vectorOfRoute.removeElement(server);

		NodePair pair = new NodePair(server, client);
		putIntoRoutes(pair, vectorOfRoute);
		//routes.put(pair, vectorOfRoute);
		SimulationDebug.print("RouteService: pair = " + pair);
		SimulationDebug.println(server + " - " + client + " : " + vectorOfRoute);
	    }
	    SimulationDebug.println("RouteService: [finish from ServerNode to ClientNode]");
	}
    }
    */

    /******************** public method ********************/
    public Vector get(NodePair pair) {
	return (Vector)((Vector)routes.get(pair)).clone();
    }

    public Enumeration keys() {
	return routes.keys();
    }

    public String toString() {
	String str = "RouteService: ";
	Enumeration e = keys();
	while (e.hasMoreElements()) {
	    NodePair pair = (NodePair)e.nextElement();
	    str += pair + "{" + routes.get(pair) + "}, ";
	}
	return str;
    }

    /*
    protected void putIntoRoutes(NodePair pair, Vector v) {
	if (routes.containsKey(pair)) {
	    Vector data = (Vector)routes.get(pair);
	    data.addElement(v);
	} else {
	    Vector data = new Vector();
	    data.addElement(v);
	    routes.put(pair, data);
	}
    }
    */
}
