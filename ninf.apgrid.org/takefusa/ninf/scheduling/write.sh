#!/bin/csh
foreach i (RoundRobin LoadOnly LoadOnlyL LoadThroughput LoadThroughputL LoadThroughputT LoadThroughputLT)
	ruby write.rb ${i}Scheduler >! $i.html
end
