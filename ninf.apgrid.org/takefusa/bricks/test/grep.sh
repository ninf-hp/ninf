#!/bin/csh

set old = "serverInfo"
set new = "hostInfo"

mkdir old
mv environment old
mv scheduling old
mv tools old
mv util old
mv plugin old
mkdir environment scheduling tools util plugin
mkdir plugin/nws
foreach i (environment scheduling tools util plugin/nws)
    cp -r old/${i}/CVS ${i}
end

echo "environment"
foreach i (ClientCreator Client EPClientCreator EPClient FallbackServerCreator FallbackServer ForwardNodeCreator ForwardNode HostCreator Host LinkCreator NetworkCreator Network Node NodeWithQueue Packets SendBuffer ServerCreator Server ServerTrace TerminalNode)
    ruby grep.rb ${old} ${new} old/environment/${i}.java > environment/${i}.java
end


echo "scheduling"
foreach i (DeadlineLoadThroughputSchedulerCreator DeadlineLoadThroughputScheduler EPRandomSchedulerCreator EPRandomScheduler EPRoundRobinSchedulerCreator EPRoundRobinScheduler EPSelfSchedulerCreator EPSelfScheduler LoadCorrectionServerPredictorCreator LoadCorrectionServerPredictor LoadIntensiveSchedulerCreator LoadIntensiveScheduler LoadThroughputSchedulerCreator LoadThroughputScheduler MetaPredictorCreator MetaPredictor NaiveNetworkPredictorCreator NaiveNetworkPredictor NaiveServerPredictorCreator NaiveServerPredictor NetworkMonitorCreator NetworkMonitor NetworkPrediction NetworkPredictor NonNetworkPredictorCreator NonNetworkPredictor NonServerPredictorCreator NonServerPredictor NormalNetworkMonitorCreator NormalNetworkMonitor NormalResourceDBCreator NormalResourceDB NormalServerMonitorCreator NormalServerMonitor PredictorCreator PredictorFactory Predictor RandomSchedulerCreator RandomScheduler ResourceDBCreator ResourceDB RoundNetworkMonitorCreator RoundNetworkMonitor RoundRobinSchedulerCreator RoundRobinScheduler SchedulerCreator Scheduler SchedulingUnitCreator SchedulingUnitFactory SchedulingUnit ServerMonitorCreator ServerMonitor ServerPrediction ServerPredictor)
   ruby grep.rb ${old} ${new} old/scheduling/${i}.java > scheduling/${i}.java
end

echo "util"
foreach i (BricksException BricksNotScheduledException BricksParseException BricksUtil ComponentCreator ComponentFactory ConstantCreator Constant Creator Data EPRequestedData EPTask ExponentRandomCreator ExponentRandom Format FormatString HyperexponentRandomCreator HyperexponentRandom InfinityCreator Infinity InterpolationCpuCreator InterpolationCpu InterpolationInterarrivalTimeCreator InterpolationInterarrivalTime Interpolation InterpolationThroughputCreator InterpolationThroughput Linear LogicalPacket NetworkInfo NodePair NonInterpolation Obj OthersDataOfNetwork OthersDataOfServer PacketData Point ProbeData QueueFCFSCreator QueueFCFSFiniteBufferCreator QueueFCFSFiniteBuffer QueueFCFS Queue QueueTimeSharingCreator QueueTimeSharing RequestedData RingBuffer RouteService Sequence HostInfo Session SimulationDebug SimulationSet Spline SplineOrLinear StaticNetworkInfo StaticHostInfo SubComponentCreator SubComponentFactory SubComponent TraceFileCreator TraceFile TrafficData UniformRandomCreator UniformRandom)
    ruby grep.rb ${old} ${new} old/util/${i}.java > util/${i}.java
end


echo "tools"
foreach i (BricksConfiguration ShowUsage Topology)
    ruby grep.rb ${old} ${new} old/tools/${i}.java > tools/${i}.java
end


echo "plugin/nws/"
foreach i (NWSAdapter NWSNetworkPredictorCreator NWSNetworkPredictor NWSResourceDBCreator NWSResourceDB NWSServerPredictorCreator NWSServerPredictor)
    ruby grep.rb ${old} ${new} old/plugin/nws/${i}.java > plugin/nws/${i}.java
end
