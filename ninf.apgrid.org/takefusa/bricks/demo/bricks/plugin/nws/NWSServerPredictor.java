package bricks.plugin.nws;
import bricks.environment.*;
import bricks.scheduling.*;
import bricks.util.*;
import nws.api.*;

public class NWSServerPredictor extends ServerPredictor implements Predictor {

    private NWSAdapter adapter = new NWSAdapter();
    private int displaySize = 5;

    public NWSServerPredictor(MetaPredictor owner, String nwsForecaster) {
	adapter.setNwsForecaster(nwsForecaster);
	init(owner);
    }

/************************* needed method *************************/
    public String getName() {
	return "NWSServerPredictor";
    }

    public ServerInfo getServerInfo(
	double currentTime, ServerNode serverNode, ServerInfo serverInfo
    ) {
	ForecastCollection[] forecast = adapter.extract(
	    adapter.availableCpuExperiment, serverNode.toString(), displaySize
	);

	int minIndex = adapter.getMinErrorIndex(forecast);
	ServerInfo returnInfo = (ServerInfo)serverInfo.clone();
	returnInfo.availableCpu = 
	    (forecast[forecast.length-1].forecasts[minIndex]).forecast;
	/* debug
	for (int i = 0 ; i < forecast.length ; i++) {
	    System.out.println(
		"NWSServerPredictor: " + adapter.availableCpuExperiment +
		": " + "(" + i + ") " + adapter.toString(forecast[i])
	    );
	}
	System.out.println("minIndex = " + minIndex);
	System.out.println("");
	*/
	return returnInfo;
    }
}

