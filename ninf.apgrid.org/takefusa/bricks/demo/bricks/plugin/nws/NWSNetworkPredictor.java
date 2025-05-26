package bricks.plugin.nws;
import bricks.environment.*;
import bricks.scheduling.*;
import bricks.util.*;
import nws.api.*;

public class NWSNetworkPredictor extends NetworkPredictor implements Predictor {

    private NWSAdapter adapter = new NWSAdapter();
    private int displaySize = 5;
    
    public NWSNetworkPredictor(MetaPredictor owner, String nwsForecaster) {
	adapter.setNwsForecaster(nwsForecaster);
	init(owner);
    }

/************************* needed method *************************/
    public String getName() {
	return "NWSNetworkPredictor";
    }

    public NetworkInfo getNetworkInfo(
	double currentTime, Node sourceNode, Node destinationNode,
	NetworkInfo networkInfo
    ) {
	ForecastCollection[] forecastBandwidth = adapter.extract(
	    adapter.bandwidthTcpExperiment,
	    sourceNode.toString(), destinationNode.toString(), displaySize
	);
	ForecastCollection[] forecastLatency = adapter.extract(
	    adapter.latencyTcpExperiment,
	    sourceNode.toString(), destinationNode.toString(), displaySize
	);

	int minIndex = adapter.getMinErrorIndex(forecastBandwidth);
	NetworkInfo returnInfo = (NetworkInfo)networkInfo.clone();
	returnInfo.throughput = 
	    (forecastBandwidth[forecastBandwidth.length-1].forecasts[minIndex]).forecast;
	returnInfo.latency = 
	    (forecastLatency[forecastBandwidth.length-1].forecasts[minIndex]).forecast;

	/* debug
	for (int i = 0 ; i < forecastBandwidth.length ; i++) {
	    System.out.println(
		"NWSNetworkPredictor: " + adapter.bandwidthTcpExperiment +
		": " + "(" + i + ") " + adapter.toString(forecastBandwidth[i])
	    );
	}
	System.out.println("minIndex = " + minIndex);
	System.out.println("");
	*/
	return returnInfo;
    }
}
