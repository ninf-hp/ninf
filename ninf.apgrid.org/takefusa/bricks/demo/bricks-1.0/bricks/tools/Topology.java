package bricks.tools;
import bricks.util.*;
import java.io.*;
import java.util.*;

/**
 * Topology generates various topologies.<BR>
 **/
public class Topology {

    /**
     * USAGE string 
     **/
    protected String usage() {
	return "java bricks.tool.Topology [options]\n" + argUsage();
    }

    protected String argUsage() {
	return 
	    "\t-nd <num>           [number of local area domains]\n" +
	    "\t-nn <min:max>       [number of nodes on each LAN]\n" +
	    "\t-lt <min:man>       [LAN throughput]\n" +
	    "\t-wt <min:man>       [WAN throughput]\n" +
	    "\t-r  <client:server> [ratio of client and server]\n" +
	    "\t(-cp <min:man>)     [client performance]\n" +
	    "\t-sp <min:man>       [server performance]\n" +
	    "\t(-cl <load>)        [client load average]\n" +
	    "\t(-sl <load>)        [server load average]\n" +
	    "\t-cr s:min:max:r:min:max:e:min:max:i:min:max [client requests]\n" +
	    "\t(-n  <num>)         [number of generating topology: default=1]\n" +
	    "\t(-traceprefix <prefix>)[prefix of network trace files: default->null]\n" +
	    "\t(-outprefix <prefix>)[prefix of topology files: default->stdout]\n" + 
	    "\t(-bricks)           [output Bricks format: default->XML]\n" +
	    "\t(-scheduler <name>) [specify scheduler name: default=<scheduler>]\n" + 
	    "\t(-comment)          [output comments of configuration files]\n" +
	    "\t(-mix)              [mix client and server nodes]\n" +
	    "\t(-seed <int>)       [seed for Random() in this class]\n" +
	    "\t(-npsize <double>)  [data size for network probe: default=100.0]\n" +
	    "\t(-itnp <double>)    [interval of network probes: default=60.0]\n" +
	    "\t(-itsp <double>)    [interval of server probes: default=60.0]\n" +
	    "\t(-sodsize <num>)    [size of others data for servers: default=50.0]\n" +
	    "\t(-fallback <num>)   [fallback server]\n" +
	    "\t(-predictor <share:<int>|->) [specify predictor name]\n" +
	    "\t(-deadline)         [deadline scheduler]\n" +
	    "\t(-sinterval <double>)[if -deadline then specify rescheduling interval]\n" +
	    "\t(-roundmonitor)   [RoundNetworkMonitor]\n" +
	    "\t(-dfact <Sequence>) [deadline factor sequence]\n" +
	    "\t  [<Sequence> should be in Constant(<double>), ExponentRandom(<double>), or <UniformRandom(<double min>,<double max>)]\n";
    }

    protected String fgnExecutable = null;

    protected int numArgs = 16;

    protected int numDomains;
    protected int minLANNodes, maxLANNodes;
    protected double minLANThru, maxLANThru;
    protected double minWANThru, maxWANThru;
    protected double ratioOfClients, ratioOfServers;
    protected double minClientPerf, maxClientPerf;
    protected double minServerPerf, maxServerPerf;
    protected double aveClientLoad;
    protected double aveServerLoad = -1.0;
    /* client request */
    protected double sendMin, sendMax;
    protected double receiveMin, receiveMax;
    protected double instructionMin, instructionMax;
    protected double invokeMin, invokeMax;
    protected double invokeAve;

    protected int numTopologies = 1;
    protected String outFilePrefix = "";
    protected String prefix = "";
    protected String format = "XML";
    protected String scheduler = "<scheduler>";
    protected boolean comment = false;
    protected boolean mix = false;

    protected boolean roundMonitor = false;
    protected double intervalNetworkProbes = 60.0;
    protected double intervalServerProbes = 60.0;
    protected double sizeNetworkProbe = 100.0;

    protected double sOthersSize = 50.0;

    protected boolean seedIs = false;
    protected int seed;
    protected boolean seedFGNIs = false;
    protected int seedFGN;

    protected Sequence LANThru;
    protected Sequence WANThru;
    protected Sequence serverPerf;
    
    /* deadline scheduling */
    boolean deadline = false;
    protected double optimisticFactor = Double.NEGATIVE_INFINITY;
    protected String deadlineFactor = null;
    protected double schedulingInterval = -1.0;

    protected int numFallback = -1;
    protected String predictor = null;
    protected double predictLoadUnit = -1.0;

    public Topology (String[] args) {
	parseArgs(args);
    }

    public void generates() {
	
	Sequence LANNodes = new UniformRandom(
	    minLANNodes, maxLANNodes, rand()
	);

	for (int k = 0; k < numTopologies; k++) {

	    String output = "";

	    /* Allocate clients and servers into domains */
	    /* 
	       Vector domains: elements = Hashtables
	       => Hashtable(key, Hashtable): key = node name
	          => Hashtable(key, value): key = {type, LANs, LANr}
	    */
	    Vector domains = new Vector(numDomains);

	    if (mix) { /* Mix client and server nodes */

		for (int i = 0; i < numDomains; i++) {

		    int numLANNodes = (int)Math.rint(LANNodes.nextDouble());
		    Hashtable nodes = new Hashtable(numLANNodes);
		    domains.addElement(nodes);

		    for (int j = 0; j < numLANNodes; j++) {

			/* client */
			if (randD() <= ratioOfClients) { 
			    Hashtable tmp = new Hashtable();
			    tmp.put("type", "client");
			    nodes.put("c_" + j + ".d_" + i, tmp);
			
			} else { /* server */
			    Hashtable tmp = new Hashtable();
			    tmp.put("type", "server");
			    nodes.put("s_" + j + ".d_" + i, tmp);
			}
		    }
		}

	    } else { /* Separate client and server nodes */

		int numClientDomain = (int)Math.rint(
					   numDomains * ratioOfClients);

		/* client */
		for (int i = 0; i < numClientDomain; i++) {

		    int numLANNodes = (int)Math.rint(LANNodes.nextDouble());
		    Hashtable nodes = new Hashtable(numLANNodes);
		    domains.addElement(nodes);

		    for (int j = 0; j < numLANNodes; j++) {
			Hashtable tmp = new Hashtable();
			tmp.put("type", "client");
			nodes.put("c_" + j + ".d_" + i, tmp);
		    }
		}

		/* server */
		for (int i = numClientDomain; i < numDomains; i++) {

		    int numLANNodes = (int)Math.rint(LANNodes.nextDouble());
		    Hashtable nodes = new Hashtable(numLANNodes);
		    domains.addElement(nodes);

		    for (int j = 0; j < numLANNodes; j++) {
			Hashtable tmp = new Hashtable();
			tmp.put("type", "server");
			nodes.put("s_" + j + ".d_" + i, tmp);
		    }
		}
	    }
	    
	    /* generate formats */
	    if (format.equals("bricks")) {
		output = generatesBricksFormat(domains, output, k);
	    } else { /* XML */
		output =  generatesXMLFormat(domains, output, k);
	    }

	    if ((numTopologies == 1) && (outFilePrefix.equals(""))) {
		/* stdout */
		System.out.println(output);

	    } else { /* file out */
		try {
		    FileOutputStream fos = new FileOutputStream(
			outFilePrefix + "_" + k
		    );
		    PrintWriter pw = BricksUtil.getWriter(fos);
		    pw.println(output);
		    pw.close();

		} catch (IOException e) {
		    e.printStackTrace();
		    throw new RuntimeException();
		}
	    }
	}
	return;
    }

    protected String generatesBricksFormat(
	Vector domains, String output, int no
    ) {

	if (comment) {
	    output = printGridComment(output);
	}

	/* print Client, Server, ForwardNode, and Network */
	output = printGridComponents(domains, output, no);

	/* print Link between Grid components */
	output = printLinks(domains, output);

	return output;
    }

    /* not implemented yet !!!!! */
    public String generatesXMLFormat(Vector domains, String output, int no) {

	return new String(
	    numDomains + ", " + 
	    minLANNodes + ", " + maxLANNodes + ", " + 
	    minLANThru + ", " + maxLANThru + ", " + 
	    minWANThru + ", " + maxWANThru + ", " + 
	    ratioOfClients + ", " + ratioOfServers + ", " + 
	    minClientPerf + ", " + maxClientPerf + ", " + 
	    minServerPerf + ", " + maxServerPerf + ", " + 
	    aveClientLoad + ", " + 
	    aveServerLoad
	    );
    }

    /*-------------------- protected method --------------------*/
    protected void parseArgs(String[] args) {

	if (args.length < 1)
	    BricksUtil.abort(usage());

	boolean[] argsCheck = new boolean[numArgs];
	for (int i = 0; i < argsCheck.length; i++)
	    argsCheck[i] = false;

	StringTokenizer st;
	int i = 0;
	while (i < args.length) {

	    //debug
	    //System.err.println("arg = " + args[i]);

	    if (args[i].equalsIgnoreCase("-nd")) {
		argsCheck[0] = true;
		this.numDomains = BricksUtil.getInt(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-nn")) {
		argsCheck[1] = true;
		st = new StringTokenizer(args[++i]);
		this.minLANNodes = BricksUtil.getInt(st, ":");
		this.maxLANNodes = BricksUtil.getInt(st, ":");

	    } else if (args[i].equalsIgnoreCase("-lt")) {
		argsCheck[2] = true;
		st = new StringTokenizer(args[++i]);
		this.minLANThru = BricksUtil.getDouble(st, ":");
		this.maxLANThru = BricksUtil.getDouble(st, ":");

	    } else if (args[i].equalsIgnoreCase("-wt")) {
		argsCheck[3] = true;
		st = new StringTokenizer(args[++i]);
		this.minWANThru = BricksUtil.getDouble(st, ":");
		this.maxWANThru = BricksUtil.getDouble(st, ":");

	    } else if (args[i].equalsIgnoreCase("-r")) {
		argsCheck[4] = true;
		st = new StringTokenizer(args[++i]);
		double client = BricksUtil.getDouble(st, ":");
		double server = BricksUtil.getDouble(st, ":");
		this.ratioOfClients = client / (client + server);
		this.ratioOfServers = server / (client + server);

	    } else if (args[i].equalsIgnoreCase("-cr")) {
		argsCheck[5] = true;
		st = new StringTokenizer(args[++i]);
		BricksUtil.skipNextToken(st, ":"); // s
		this.sendMin = BricksUtil.getDouble(st, ":");
		this.sendMax = BricksUtil.getDouble(st, ":");
		BricksUtil.skipNextToken(st, ":"); // r
		this.receiveMin = BricksUtil.getDouble(st, ":");
		this.receiveMax = BricksUtil.getDouble(st, ":");
		BricksUtil.skipNextToken(st, ":"); // e
		this.instructionMin = BricksUtil.getDouble(st, ":");
		this.instructionMax = BricksUtil.getDouble(st, ":");
		BricksUtil.skipNextToken(st, ":"); // i
		this.invokeAve = BricksUtil.getDouble(st, ":");
		//this.invokeMin = BricksUtil.getDouble(st, ":");
		//this.invokeMax = BricksUtil.getDouble(st, ":");

	    } else if (args[i].equalsIgnoreCase("-sp")) {
		argsCheck[6] = true;
		st = new StringTokenizer(args[++i]);
		this.minServerPerf = BricksUtil.getDouble(st, ":");
		this.maxServerPerf = BricksUtil.getDouble(st, ":");

	    } else if (args[i].equalsIgnoreCase("-fgn")) {
		argsCheck[7] = true;
		fgnExecutable = args[++i];

	    } else if (args[i].equalsIgnoreCase("-cl")) {
		this.aveClientLoad = BricksUtil.getDouble(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-sl")) {
		this.aveServerLoad = BricksUtil.getDouble(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-n")) {
		argsCheck[9] = true;
		this.numTopologies = BricksUtil.getInt(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-traceprefix")) {
		this.prefix = args[++i];

	    } else if (args[i].equalsIgnoreCase("-bricks")) {
		this.format = "bricks";

	    } else if (args[i].equalsIgnoreCase("-scheduler")) {
		this.scheduler = args[++i];

	    } else if (args[i].equalsIgnoreCase("-comment")) {
		this.comment = true;

	    } else if (args[i].equalsIgnoreCase("-mix")) {
		this.mix = true;

	    } else if (args[i].equalsIgnoreCase("-cp")) {
		st = new StringTokenizer(args[++i]);
		this.minClientPerf = BricksUtil.getDouble(st, ":");
		this.maxClientPerf = BricksUtil.getDouble(st, ":");

	    } else if (args[i].equalsIgnoreCase("-outprefix")) {
		argsCheck[10] = true;
		outFilePrefix = args[++i];

	    } else if (args[i].equalsIgnoreCase("-deadline")) {
		deadline = true;

	    } else if (args[i].equalsIgnoreCase("-ofact")) {
		this.optimisticFactor = BricksUtil.getDouble(args[++i]);
		if ((optimisticFactor <= 0.0) || (optimisticFactor > 1.0))
		    BricksUtil.abort("ofact has to be in the range (0.0, 1.0]\n"+usage());

	    } else if (args[i].equalsIgnoreCase("-sinterval")) {
		this.schedulingInterval = BricksUtil.getDouble(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-dfact")) {
		this.deadlineFactor = args[++i];
		//if (deadlineFactor < 1.0)
		//BricksUtil.abort("dfact has to be 1.0 or more\n"+usage());

	    } else if (args[i].equalsIgnoreCase("-roundmonitor")) {
		this.roundMonitor = true;

	    } else if (args[i].equalsIgnoreCase("-seed")) {
		seed = BricksUtil.getInt(args[++i]);
		seedIs = true;

	    } else if (args[i].equalsIgnoreCase("-seed_fgn")) {
		seedFGN = BricksUtil.getInt(args[++i]);
		seedFGNIs = true;

	    } else if (args[i].equalsIgnoreCase("-npsize")) {
		this.sizeNetworkProbe = BricksUtil.getDouble(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-itnp")) {
		this.intervalNetworkProbes = BricksUtil.getDouble(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-itsp")) {
		this.intervalServerProbes = BricksUtil.getDouble(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-sodsize")) {
		this.sOthersSize = BricksUtil.getDouble(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-fallback")) {
		numFallback = BricksUtil.getInt(args[++i]);

	    } else if (args[i].equalsIgnoreCase("-predictor")) {
		st = new StringTokenizer(args[++i]);
		predictor = st.nextToken(":");
		predictLoadUnit = BricksUtil.getDouble(st, ":");

	    } else {
		BricksUtil.abort(args[i] + " is not in the options.\n" + usage());
		return;
	    }
	    i++;
	}

	/* check required options */
	for (i = 0; i <= 6; i++) {
	    if (argsCheck[i] == false)
		BricksUtil.abort(usage());
	}
	/* check prefix if specified the "-n" option */
	if ((argsCheck[9] == true) && (argsCheck[10] == false)) {
	    BricksUtil.abort("You have to specify prefix of topology files\n" + usage());
	}
	/* check deadline */
	if (deadline) {
	    if ((optimisticFactor < 0.0) || (deadlineFactor == null))
		BricksUtil.abort("-ofact and -dfact (-sinterval) have to be specified.\n"
		      + usage());
	}
	if (deadline) {
	    if ((schedulingInterval < 0.0) || (deadlineFactor == null))
		BricksUtil.abort("-ofact and -dfact (-sinterval) have to be specified.\n"
		      + usage());
	}

	/* generate random sequences */
	if (seedIs)
	    random = new Random(seed);
	else 
	    random = new Random();

	if (seedFGNIs)
	    randomFGN = new Random(seedFGN);
	else
	    randomFGN = new Random();

	LANThru = new UniformRandom(minLANThru, maxLANThru, rand());
	WANThru = new UniformRandom(minWANThru, maxWANThru, rand());
	serverPerf = new UniformRandom(minServerPerf, maxServerPerf, rand());

	return;
    }

    protected String printGridComment(String output) {

	/* Nodes */
	output += "\n" +
	    "# ---------- Client ----------\n" +
	    "# Client [name] [Scheduler name] \\\n" +
	    "#   RequestData([data size for send], [data size for receive], \\\n" + 
	    "#              [num of instruction], [interval of invoking])\n" +
	    "# ---------- Server ----------\n" +
	    "# Server [name] [Queue([performance] [argument] ..)]\\\n" + 
	    "#  [OthersData([num of instructions], [intararrival time])]\n" + 
	    "# ---------- Node ----------\n" +
	    "# Node [name]\n";
	
	/* Network */
	output += 
	    "# ---------- Network ----------\n" +
	    "# Network [name] [Queue([throughput] [argument] ..) \\\n" + 
	    "#  OthersData([data size], [intararrival time])\n";
	
	/* Link */
	output +=
	    "# ---------- Link ----------\n" +
	    "# Link [source Node]-[destination Node], ..\n" +
	    "# If from [client] to [network] or [server] to [network] then\n" +
	    "# Link [source Node]-[destination Node]:<RamdomSeries>\n";
	return output;
    }

    /** generates Grid component configuration and FGN files */
    protected String printGridComponents(
	Vector domains, String output, int no
    ) {

	Enumeration e = domains.elements();
	while (e.hasMoreElements()) {

	    Hashtable nodes = (Hashtable)e.nextElement();
	    Enumeration eNodes = nodes.keys();

	    /* Client or Server */
	    output +="\n";
	    while (eNodes.hasMoreElements()) {

		String key = (String)eNodes.nextElement();
		String type = ((String)((Hashtable)nodes.get(key)).get("type"));
		if (type.equals("client")) { 
		    if (deadline) {
			output +=
			    "client " + key + " " + scheduler + " " + 
			    "Requests(\\\n" +
			    "  UniformRandom(" +
			    sendMin + ", " + sendMax + ", " + rand() +
			    "), UniformRandom(" +
			    receiveMin + ", " + receiveMax + ", " + rand() +
			    "),\\\n  UniformRandom(" +
			    instructionMin + ", " + instructionMax + ", " + rand()+
			    "), ExponentRandom(" + invokeAve + ", " + rand() + 
			    "), ";
			if (deadlineFactor.startsWith("Constant")) {
			    output += deadlineFactor + ")\n";
			    double tmp = rand();
			} else { //ExponentRandom or UniformRandom
			    output += deadlineFactor + ", " + rand() + "))\n";
			}
		    } else {
			output +=
			    "client " + key + " " + scheduler + " " + 
			    "Requests(\\\n" +
			    "  UniformRandom(" +
			    sendMin + ", " + sendMax + ", " + rand() +
			    "), UniformRandom(" +
			    receiveMin + ", " + receiveMax + ", " + rand() +
			    "),\\\n  UniformRandom(" +
			    instructionMin + ", " + instructionMax + ", " + rand() +
			    "), ExponentRandom(" + invokeAve + ", " + rand() + 
			    "), ";
			if (deadlineFactor.startsWith("Constant")) {
			    output += deadlineFactor + ")\n";
			    double tmp = rand();
			} else { //ExponentRandom or UniformRandom
			    output += deadlineFactor + ", " + rand() + "))\n";
			}
		    }

		} else { /* server */
		    if (aveServerLoad < 0.0) {
			if (numFallback > 0)
			    output += 
				"fallbackserver(" + numFallback + ") " + 
				key + " QueueFCFS(Constant(" + 
				Format.format(serverPerf.nextDouble(), 3) +
				"), OthersData(Constant(0.0), Infinity())\n";
			else
			    output += 
				"server " + key + " QueueFCFS(Constant(" + 
				Format.format(serverPerf.nextDouble(), 3) +
				"), OthersData(Constant(0.0), Infinity())\n";
		    } else {
			double perf = serverPerf.nextDouble();
			double arrival = sOthersSize / perf / aveServerLoad;
			if (numFallback > 0)
			    output += 
				"fallbackserver(" + numFallback + ") " + 
				key + " QueueFCFS(Constant(" + 
				Format.format(perf, 3) +
				"), OthersData(Constant(" + sOthersSize + "), " + 
				"ExponentRandom(" + arrival + ", " + rand() + ")\n";
			else
			    output += 
				"server " + key + " QueueFCFS(Constant(" + 
				Format.format(perf, 3) +
				"), OthersData(Constant(" + sOthersSize + "), " + 
				"ExponentRandom(" + arrival + ", " + rand() + ")\n";
		    }
		}
	    }
	    
	    /* Network - LAN */
	    output += "\n";
	    eNodes = nodes.keys();

	    while (eNodes.hasMoreElements()) {
		String key = (String)eNodes.nextElement();
		((Hashtable)nodes.get(key)).put("LANs", "LANs." + key);
		((Hashtable)nodes.get(key)).put("LANr", "LANr." + key);
		
		output += 
		    "Network LANs." + key + " QueueFCFS(\\\n" + 
		    "  InterpolationThroughput(SplineOrLinear, " + 
		    numPoints + ", " + 
		    prefix + "_" + no + "_LANs." + key + ")\\\n" + 
		    "  OthersData(Constant(0.0), Infinity()))\n" +
		    "Network LANr." + key + " QueueFCFS(\\\n" + 
		    "  InterpolationThroughput(SplineOrLinear, " + 
		    numPoints + ", " +
		    prefix + "_" + no + "_LANr." + key + ")\\\n" + 
		    "  OthersData(Constant(0.0), Infinity()))\n";

		/* generates trace file */
		if (fgnExecutable != null)
		    generatesFGNTrace("LAN", LANThru, key, no);
	    }

	    /* Network - WAN */
	    int index = domains.indexOf(nodes);
	    output += "\n" +
		"Network " + "WANs.d_" + index + " QueueFCFS( \\\n" + 
		"  InterpolationThroughput(SplineOrLinear, " + numPoints + 
		", " + prefix + "_" + no + "_WANs.d_" + index + ")\\\n" +
		"  OthersData(Constant(0.0), Infinity()))\n" +
		"Network " + "WANr.d_" + index + " QueueFCFS( \\\n" + 
		"  InterpolationThroughput(SplineOrLinear, " + numPoints + 
		", " + prefix + "_" + no + "_WANr.d_" + index + ")\\\n" +
		"  OthersData(Constant(0.0), Infinity()))\n";

	    /* generates trace file */
	    if (fgnExecutable != null)
		generatesFGNTrace("WAN", WANThru, "d_" + index, no);
	}

	/* Forward node descriptioin */
	output += "\n";
	for (int i = 0; i < numDomains; i++) {
	    output += "Node dl_" + i + "\n";
	}
	output += "Node dw\n";

	return output;
    }

    protected String printLinks(Vector domains, String output) {

	/* Link: Client/Server <-> LAN, LAN <-> ForwareNode */
	output += "\n";
	Enumeration e = domains.elements();
	while (e.hasMoreElements()) {

	    Hashtable nodes = (Hashtable)e.nextElement();
	    Enumeration eNodes = nodes.keys();

	    while (eNodes.hasMoreElements()) {

		String key = (String)eNodes.nextElement();
		Hashtable h = (Hashtable)nodes.get(key);

		/* Link: client/server - LAN */
		output += 
		    "Link " + key + "-" + (String)h.get("LANs") + 
		    ":ExponentRandom(" + rand() + "), " +
		    (String)h.get("LANr") + "-" + key + "\n";

		/* Link: LAN - ForwardNode */
		output += 
		    "Link " + (String)h.get("LANs") +
		    "-dl_" + domains.indexOf(nodes) + ", " + 
		    "dl_" + domains.indexOf(nodes) + "-" + 
		    (String)h.get("LANr") + "\n";
	    }
	}
	
	/* Link: ForwardNode <-> WAN, WAN <-> ForwardNode */
	output += "\n";
	for (int i = 0; i < numDomains; i++) {
		output += 
		    "Link dl_" + i + "-WANs.d_" + i + 
		    ", WANr.d_" + i + "-dl_" + i + ", " +
		    "WANs.d_" + i + "-dw" + 
		    ", dw-WANr.d_" + i + "\n";
	}

	return output;
    }

    /**
     * for FGN (fft_fgn)
     **/
    protected int numPoints = 4096;
    //protected double duration = 86400.0;
    protected double duration = 172800.0;

    protected void generatesFGNTrace(
        String network, Sequence thruSequence, String index, int no
    ) {

	/* for FFT FGN */
	String[] fgn = new String[9];
	fgn[0] = fgnExecutable;
	fgn[1] = "-s";
	fgn[3] = "-M";
	fgn[5] = "-V";
	fgn[7] = "-n";
	fgn[8] = (new Double(numPoints)).toString();
	
	/* generate trace file */
	double thru = thruSequence.nextDouble();
	fgn[4] = (new Double(thru)).toString();
	fgn[6] = (new Double(Math.pow(thru * 0.1, 2))).toString();
	try {
	    Runtime r = Runtime.getRuntime();
	    fgn[2] = (new Double(randFGN())).toString();
	    Process p = r.exec(fgn);
	    DataInputStream dis = new DataInputStream(p.getInputStream());
	    FileOutputStream fos = new FileOutputStream(
	        prefix + "_" + no + "_" + network + "s." + index
	    );
	    outTrace(dis, fos);
	    
	    fgn[2] = (new Double(randFGN())).toString();
	    p = r.exec(fgn);
	    dis = new DataInputStream(p.getInputStream());
	    fos = new FileOutputStream(
		prefix + "_" + no + "_" + network + "r." + index
	    );
	    outTrace(dis, fos);

	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.exit(3);
	}
    }

    protected void outTrace(InputStream is, OutputStream os) {

	double stride = duration / (numPoints - 1);
	double timestamp = 0.0;

	PrintWriter pw = BricksUtil.getWriter(os);
	BufferedReader br = new BufferedReader(new InputStreamReader(is));

	String data = null;
	try {
	    while ((data = br.readLine()) != null) {
		pw.print(Format.format(timestamp, 3) + "\t");
		pw.println(data);
		timestamp = timestamp + stride;
	    } 
	    pw.close();

	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(3);
	}
    }

    /**
     * Random Sequences for the generation
     **/
    protected Random random;
    protected Random randomFGN;

    protected int rand() {
	return Math.abs(random.nextInt());
    }

    protected double randD() {
	return Math.abs(random.nextDouble());
    }

    protected int randFGN() {
	return Math.abs(randomFGN.nextInt());
    }

    /**
     * The entry 
     */
    public static void main(String[] args) {

	Topology tg = new Topology(args);
	tg.generates();
    }
}
