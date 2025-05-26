package bricks.plugin.nws;
import bricks.util.*;
import nws.support.*;
import nws.api.*;

public class NWSAdapter {

    public static final String bandwidthTcpExperiment = "bandwidth_tcp";
    public static final String latencyTcpExperiment   = "latency_tcp";
    public static final String availableCpuExperiment = "available_cpu";
    public static final String currentCpuExperiment   = "current_cpu";

    private String nwsMemoryServer;
    private String nwsForecaster;

    public void printNwsServers() {
	System.out.println("nws_memory = " + nwsMemoryServer);
	System.out.println("nws_forecast = " + nwsForecaster);
    }

    public void setNwsMemoryServer(String nwsMemoryServer) {
	this.nwsMemoryServer = nwsMemoryServer;
    }

    public void setNwsForecaster(String nwsForecaster) {
	this.nwsForecaster = nwsForecaster;
    }

    /************************* insert *************************/
    public void insert(
	String experimentName, String server,
	double probeTime, double loadAverage
    ) {
	ExperimentSpec experiment = new ExperimentSpec();
	experiment.sourceMachine = server;
	insert(experimentName, experiment, probeTime, loadAverage);
    }

    public void insert(
	String experimentName, String sourceNode, String destinationNode,
	double probeTime, double throughput
    ) {
	ExperimentSpec experiment = new ExperimentSpec();
	experiment.sourceMachine = sourceNode;
	experiment.destinationMachine = destinationNode;
	insert(experimentName, experiment, probeTime, throughput);
    }

    public void insert(
	String experimentName, ExperimentSpec experiment,
	double probeTime, double measuredValue
    ) {
	SimulationDebug.println("NWSAdapter: insert()");

	HostSpec memServer = new HostSpec(nwsMemoryServer);
	NWSClient client = new NWSClient();
	client.ipLookupInvalidate();

	setExperimentName(experiment, experimentName);
	try {
	    if (memServer != null)
		client.setMemServer(memServer);
	    else
		client.useDefaultMemServer();
	} catch (NWSException e){
	    NWSUtil.abort(e.toString());
	}

	Measurement[] measurements = new Measurement[1];
	measurements[0] = new Measurement(probeTime, measuredValue);

	//for (int i = 0; i < measurements.length; i++)
	//dbg.println(measurements[i]);
	
	try {
	    client.putMeasurements(experiment, measurements);
	} catch (NWSException e){
	    e.printStackTrace();
	    NWSUtil.abort("Attempt to store experiments failed\n%s\n", e);
	}
    }

    /************************* extract *************************/
    public ForecastCollection[] extract(
	String experimentName, String server, int displaySize
    ){
	ExperimentSpec experiment = new ExperimentSpec();
	experiment.sourceMachine = server;
	
	return extract(experimentName, experiment, displaySize);
    }
    
    public ForecastCollection[] extract(
	String experimentName, String sourceNode, String destinationNode,
	int displaySize
    ){
	ExperimentSpec experiment = new ExperimentSpec();
	experiment.sourceMachine = sourceNode;
	experiment.destinationMachine = destinationNode;
	
	return extract(experimentName, experiment, displaySize);
    }
    
    public ForecastCollection[] extract(
	String experimentName, ExperimentSpec experiment, int displaySize
    ){
	SimulationDebug.println("NWSAdapter: extract()");

	//HostSpec nameServer = null;
	NWSClient client = new NWSClient();
	client.ipLookupInvalidate();
	
	HostSpec forecasterSpec = new HostSpec(nwsForecaster);
	try {
	    client.setForecaster(forecasterSpec);
	} catch (NWSException e){
	    NWSUtil.abort("Unable to contact forecaster %s\n", forecasterSpec);
	}
	try {
	    HostSpec nsSpec 
		= client.getNameServer(new HostCookie(forecasterSpec));
	    client.setNameServer(nsSpec);
	} catch (NWSException e){
	    System.err.print(
		"Unable to determine name server, using default\n"
	    );
	    try {
		client.useDefaultNameServer();
	    } catch (NWSException e2){
		NWSUtil.abort("Failed to use even default Server \n");
	    }
	}
	setExperimentName(experiment, experimentName);

	ForecastCollection[] forecasts = null;
	try {
	    forecasts = client.getForecasts(experiment, displaySize);
	} catch (NWSException e) {
	    NWSUtil.abort("Error: %s", e.str);
	}
	return forecasts;
    }

    /************************* other methods *************************/
    public String toString(ForecastCollection forecast) {
	boolean displayForecast[] = {true, true};
	return forecast.toString(
	    Measurement.MEASUREMENT_FORMAT,
	    Forecast.FORECAST_FORMAT, displayForecast
	);
    }

    public int getMinErrorIndex(ForecastCollection[] forecast) {

	double[] score = new double[forecast[0].FORECASTTYPECOUNT];
	for (int i = 0 ; i < forecast[0].FORECASTTYPECOUNT ; i++)
	    score[i] = 0.0;
	for (int i = 0 ; i < forecast.length ; i++) {
	    for (int j = 0 ; j < forecast[0].FORECASTTYPECOUNT ; j++)
		score[j] += (forecast[i].forecasts[j]).error;
	}

	int minIndex = 0;
	for (int i = 1 ; i < forecast[0].FORECASTTYPECOUNT ; i++) {
	    if (score[minIndex] > score[i])
		minIndex = i;
	}
	return minIndex;
    }

    private void setExperimentName(
	ExperimentSpec experiment, String experimentName
    ) {
	if (experimentName.equalsIgnoreCase(bandwidthTcpExperiment)) {
	    experiment.experimentName = 
		ExperimentSpec.DEFAULT_BANDWIDTH_EXPERIMENT;
	} else if (experimentName.equalsIgnoreCase(latencyTcpExperiment)) {
	    experiment.experimentName = 
		ExperimentSpec.DEFAULT_LATENCY_EXPERIMENT;
	} else if (experimentName.equalsIgnoreCase(availableCpuExperiment)) {
	    experiment.experimentName = 
		ExperimentSpec.DEFAULT_AVAILABLE_CPU_EXPERIMENT;
	} else if (experimentName.equalsIgnoreCase(currentCpuExperiment)) {
	    experiment.experimentName = 
		ExperimentSpec.DEFAULT_CURRENT_CPU_EXPERIMENT;
	} else {
	    experiment.experimentName = experimentName;
	}
    }
}
