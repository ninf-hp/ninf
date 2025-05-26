package bricks.util;
import bricks.environment.*;
import bricks.scheduling.*;
import java.util.*;
import java.io.*;

public class SimulationSet {

    public RouteService routeService = new RouteService();

    public LogicalPacket logicalPacket = new LogicalPacket();
    protected Hashtable hashtableOfObj = new Hashtable();
    protected Hashtable hashtableOfNetworkMonitor = new Hashtable();
    protected Hashtable hashtableOfServerMonitor = new Hashtable();
    protected Hashtable hashtableOfResourceDB = new Hashtable();
    protected Hashtable hashtableOfMetaPredictor = new Hashtable();
    protected Hashtable hashtableOfScheduler = new Hashtable();
    protected Vector serverList = new Vector();
    protected Vector clientList = new Vector();
    protected Vector networkList = new Vector();
    protected Hashtable forwardNodeList = new Hashtable();

    public PrintWriter requestedDataLogWriter;
    public PrintWriter serverLogWriter;
    public PrintWriter networkLogWriter;
    public PrintWriter serverMonitorLogWriter;
    public PrintWriter networkMonitorLogWriter;

    public SimulationSet(OutputStream osOfRequestedDataLog) {
	requestedDataLogWriter = BricksUtil.getWriter(osOfRequestedDataLog);
    }

    // for debug
    public SimulationSet(InputStream isOfSimulationSet) {
	initOfDebug(isOfSimulationSet);
    }

/************************* public method *************************/
    public void setNetworkLog(OutputStream osOfNetworkLog) {
	networkLogWriter = BricksUtil.getWriter(osOfNetworkLog);
    }

    public void setServerLog(OutputStream osOfServerLog) {
	serverLogWriter = BricksUtil.getWriter(osOfServerLog);
    }

    public void setNetworkMonitorLog(OutputStream osOfNetworkMonitorLog) {
	networkMonitorLogWriter = BricksUtil.getWriter(osOfNetworkMonitorLog);
    }

    public void setServerMonitorLog(OutputStream osOfServerMonitorLog) {
	serverMonitorLogWriter = BricksUtil.getWriter(osOfServerMonitorLog);
    }

    public void init(InputStream isOfSimulationSet) throws BricksParseException {
	try {
	    SimulationDebug.println("initComponents() in SimulationSet.init()");
	    initComponents(isOfSimulationSet);
	} catch (BricksParseException e) {
	    throw e;
	}

	SimulationDebug.println("initLinks() in SimulationSet.init()");
	//System.out.println(toStringOfHashtableOfObj());
	initLinks();

	/* initRouteService */
	SimulationDebug.println("init routes in SimulationSet.init()");
	SimulationDebug.println("client...");
	routeService.initClientRoutes(clients());
	SimulationDebug.println("server...");
	routeService.initServerRoutes(servers());

	SimulationDebug.println("init ResourceDBs in SimulationSet.init()");
	initResourceDBs();

	SimulationDebug.println("init Monitors in SimulationSet.init()");
	initMonitors();

	SimulationDebug.println("init Schedulers in SimulationSet.init()");
	initSchedulers();
	//System.out.println(toInitString());
    }

    public void finish() {
	Enumeration e = hashtableOfResourceDB.elements();
	while (e.hasMoreElements()) {
	    ResourceDB db = (ResourceDB)e.nextElement();
	    db.finish();
	}
	requestedDataLogWriter.close();
	if (serverMonitorLogWriter != null)
	    serverMonitorLogWriter.close();
	if (networkMonitorLogWriter != null)
	    networkMonitorLogWriter.close();
	if (serverLogWriter != null)
	    serverLogWriter.close();
	if (networkLogWriter != null)
	    networkLogWriter.close();
    }

    public void register(ForwardNode node) {
	forwardNodeList.put(node.key, node);
    }

    public void register(String key, ClientNode node) {
	hashtableOfObj.put(key, node);
	clientList.addElement(node);
    }

    public void register(String key, NetworkNode node) {
	hashtableOfObj.put(key, node);
	networkList.addElement(node);
    }

    public void register(String key, ServerNode node) {
	hashtableOfObj.put(key, node);
	serverList.addElement(node);
    }

    public void register(String key, NetworkMonitor networkMonitor) {
	hashtableOfObj.put(key, networkMonitor);
	hashtableOfNetworkMonitor.put(key, networkMonitor);
    }

    public void register(String key, ServerMonitor serverMonitor) {
	hashtableOfObj.put(key, serverMonitor);
	hashtableOfServerMonitor.put(key, serverMonitor);
    }

    public void register(String key, Scheduler scheduler) {
	hashtableOfScheduler.put(key, scheduler);
    }

    public void register(String key, MetaPredictor metaPredictor) {
	hashtableOfMetaPredictor.put(key, metaPredictor);
    }

    public void register(String key, ResourceDB db) {
	hashtableOfResourceDB.put(key, db);
    }

    public Node getNode(String key) {

	//SimulationDebug.println("SimulationSet: getNode( " + key + " )");
	//SimulationDebug.println(hashtableOfObj);

	Node node = (Node)hashtableOfObj.get(key);
	if (node == null) {
	    SimulationDebug.println(forwardNodeList);
	    node = (Node)forwardNodeList.get(key);
	}
	//SimulationDebug.println("SimulationSet: return " + node);
	return node;
    }

    public NetworkMonitor getNetworkMonitor(String key) {
	return (NetworkMonitor)hashtableOfNetworkMonitor.get(key);
    }

    public ServerMonitor getServerMonitor(String key) {
	return (ServerMonitor)hashtableOfServerMonitor.get(key);
    }

    public Scheduler getScheduler(String key) {
	return (Scheduler)hashtableOfScheduler.get(key);
    }

    public ResourceDB getResourceDB(String key) {
	return (ResourceDB)hashtableOfResourceDB.get(key);
    }

    public MetaPredictor getMetaPredictor(String key) {
	return (MetaPredictor)hashtableOfMetaPredictor.get(key);
    }

    public int numNodes() {
	return hashtableOfObj.size() - hashtableOfNetworkMonitor.size()
	    - hashtableOfServerMonitor.size() + forwardNodeList.size();
    }

    public int numServers() {
	return serverList.size();
    }

    public int numObjs() {
	return hashtableOfObj.size();
    }

    public Enumeration objs() {
	return hashtableOfObj.elements();
    }

    public Enumeration networks() {
	return networkList.elements();
    }

    public Enumeration servers() {
	return serverList.elements();
    }

    public Enumeration clients() {
	return clientList.elements();
    }

    public Enumeration forwardNodes() {
	return forwardNodeList.elements();
    }

    public Enumeration networkMonitors() {
	return hashtableOfNetworkMonitor.elements();
    }

    public Enumeration serverMonitors() {
	return hashtableOfServerMonitor.elements();
    }

    public Enumeration schedulers() {
	return hashtableOfScheduler.elements();
    }

    public Enumeration metaPredictors() {
	return hashtableOfMetaPredictor.elements();
    }

    public Enumeration resourceDBs() {
	return hashtableOfResourceDB.elements();
    }

    public Vector getServerList() {
	return (Vector)serverList.clone();
    }

    public ServerNode getServerNode(int index) {
	ServerNode sn = (ServerNode)serverList.elementAt(index);
	return sn;
    }

    /******************** for RouteService ********************/
    //public void put(Node sourceNode, Node destiantionNode, Vector v) {;}

    /** getRoute returns clone of the route */
    public Vector getRoute(Node sourceNode, Node destinationNode) {
	return routeService.get(new NodePair(sourceNode, destinationNode));
    }

    /** getRoute returns clone of the route */
    public Vector getRoute(NodePair pair) {
	return routeService.get(pair);
    }

    public int numRoutes() {
	return routeService.size();
    }

    public Enumeration routes() {
	return routeService.keys();
    }

    public String toOriginalString(double currentTime) {
	String str = "[SimulationSet] Num of Node = " + numNodes() + "\n";
	Enumeration e = clients();
	while (e.hasMoreElements()) {
	    Node node = (Node)e.nextElement();
	    str += node.toOriginalString(currentTime);
	}
	e = servers();
	while (e.hasMoreElements()) {
	    Node node = (Node)e.nextElement();
	    str += node.toOriginalString(currentTime);
	}
	e = networks();
	while (e.hasMoreElements()) {
	    Node node = (Node)e.nextElement();
	    str += node.toOriginalString(currentTime);
	}
	return str;
    }

/************************* private method *************************/
    private void initComponents(InputStream inputStreamOfSimulationSet) 
	 throws BricksParseException 
    {

	ComponentFactory componentFactory = new ComponentFactory(this);
	BufferedReader br = new BufferedReader(
	    new InputStreamReader(inputStreamOfSimulationSet)
	);

	String line = null;
	String head = null;
	// packet line
	try {
	    while ((line = br.readLine()) != null) {
		StringTokenizer st = new StringTokenizer(line);
		if (!st.hasMoreElements())
		    continue;
		head = st.nextToken(" \t()");
		if(!comment(head))
		    break;
	    }
	    line = readOneInfomation(line, br);
	    //serverLogWriter.println(line);
	    logicalPacket.set(head, line);


	    // create component
	    while ((line = br.readLine()) != null) {
		StringTokenizer st = new StringTokenizer(line);
		if (!st.hasMoreElements())
		    continue;
		head = st.nextToken(" \t()");
		if(comment(head))
		    continue;
		line = readOneInfomation(line, br);

		SimulationDebug.println("first word is [" + head + "]");
		SimulationDebug.println("first line is [" + line + "]");

		//serverLogWriter.println(line);
		componentFactory.create(head, line);
	    }

	} catch (IOException e) {
	    throw new BricksParseException("line: " + line);

	} catch (BricksParseException e) {
	    e.addMessage("line: " + line);
	    throw e;
	}
    }

    private String readOneInfomation(String line, BufferedReader br) 
	throws IOException 
    {
	String tmp;
	try {
	    while ((tmp = tailCheck(line)) != null) {
		String nextLine;
		while ((nextLine = br.readLine()) != null) {
		    StringTokenizer st = new StringTokenizer(nextLine);
		    if (!st.hasMoreElements()) {
			line = tmp;
			break;
		    }
		    if (comment(st.nextToken(" \t"))) {
			continue;
		    } else {
			line = tmp + " " + nextLine;
			break;
		    }
		}
		if (nextLine == null) {
		    line = tmp;
		    break;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    throw e;
	}
	return line;
    }

    private boolean comment(String str) {
	if (str.charAt(0) == '#') {
	    return true;
	} else {
	    return false;
	}
    }

    private String tailCheck(String str) {
	char[] line = str.toCharArray();

	int index = str.length() - 1;
	while (index > 0) {
	    Character c = new Character(line[index]);
	    if ((line[index] == ' ') || (line[index] == '\t')) {
		index--;
		continue;
	    }

	    if (line[index] == '\\') {
                return str.substring(0, index);
	    } else {
                return null;
	    }
        }
        return null;
    }

    private void initLinks() {

	SimulationDebug.println("routes from clients");
	Enumeration e = clients();
	while (e.hasMoreElements()) {
	    ClientNode node = (ClientNode)e.nextElement();
	    Vector list = new Vector();
	    node.tracePath(list);
	}
	SimulationDebug.println("routes from servers");
	e = servers();
	while (e.hasMoreElements()) {
	    ServerNode node = (ServerNode)e.nextElement();
	    Vector list = new Vector();
	    node.tracePath(list);
	}
    }

    private void initMonitors() {
	Enumeration e = networkMonitors();
	while (e.hasMoreElements()) {
	    NetworkMonitor networkMonitor = (NetworkMonitor)e.nextElement();
	    networkMonitor.init(this);
	}
	e = serverMonitors();
	while (e.hasMoreElements()) {
	    ServerMonitor serverMonitor = (ServerMonitor)e.nextElement();
	    //serverMonitor.init(this, serverList);
	    serverMonitor.init(this);
	}
    }

    private void initResourceDBs() {
	Enumeration e = resourceDBs();
	while (e.hasMoreElements()) {
	    ResourceDB resourceDB = (ResourceDB)e.nextElement();
	    resourceDB.init(this);
	}
    }

    private void initSchedulers() {
	Enumeration e = schedulers();
	while (e.hasMoreElements()) {
	    Scheduler scheduler = (Scheduler)e.nextElement();
	    scheduler.init(this);
	}
    }

    // debug
    private String toStringOfHashtableOfObj() {
	String str = "hashtableOfNodes : ";
	Enumeration e = objs();
	while (e.hasMoreElements()) {
	    Node node = (Node)e.nextElement();
	    str += node + ", ";
	}
	return str;
    }

    private String toInitString() {
	String str = "\n[SimulationSet] Num of Node = " + numNodes() + "\n";
	str += "  [packet] " + logicalPacket.packetSize + "\n";
	    //", " + interarrivalTimeOfPacket + "\n";
	Enumeration e = objs();
	while (e.hasMoreElements()) {
	    Obj obj = (Obj)e.nextElement();
	    str += obj.toInitString();
	}
	return str;
    }

    // for debug
    private void initOfDebug(InputStream isOfSimulationSet) {
	System.out.println("initComponents()...");
	try {
	    initComponents(isOfSimulationSet);
	} catch (BricksParseException e) {
	    BricksUtil.abort("debug error");
	}

	System.out.println(toStringOfHashtableOfObj());

	System.out.println("initLinks()...");
	initLinks();

	System.out.println("initScheduler()...");
	initSchedulers();

	System.out.println(toInitString());
    }

/****************************** debug ******************************/
    public static void main(String[] argv) {
	String fileName = "system2.dat";
	try {
	    FileInputStream fis = new FileInputStream(fileName);
	    SimulationSet simulationSet = new SimulationSet(fis);
	} catch (FileNotFoundException fnfe) {
	    fnfe.printStackTrace();
	}
    }
}
