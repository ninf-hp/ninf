package ninf.calc.plus;
import java.awt.*;
import java.net.*;

public class NinfCalcApplet extends java.applet.Applet {
    public static final String serverHostParamName = "server_host";
    public static final String serverPortParamName = "server_port";
    public static final String getSizeServerHostParamName = "get_size_server_host";
    public static final String getSizeServerPortParamName = "get_size_server_port";
    public static final String paradiseURLParamName = "paradise_url";
    
    public void init() {
	String defaultServerHost = getCodeBase().getHost();
	if (defaultServerHost == null || defaultServerHost.equals("")) {
	    defaultServerHost = "hpc.etl.go.jp";
	}
	try {
	    NinfCalc.serverHost = getParameter(serverHostParamName);
	    NinfCalc.serverPort = Integer.parseInt(getParameter(serverPortParamName));
	    NinfCalc.getSizeServerHost = getParameter(getSizeServerHostParamName);
	    NinfCalc.getSizeServerPort = Integer.parseInt(getParameter(getSizeServerPortParamName));
	    NinfCalc.paradiseURLString = getParameter(paradiseURLParamName);

System.out.println("getsizeserverhost = " + NinfCalc.getSizeServerHost);
System.out.println("serverhost = " + NinfCalc.serverHost);


	} catch (Exception e) {
System.out.println(e.toString());
	    return;
	}
	new NinfCalc(this, defaultServerHost).show();
    }
}
