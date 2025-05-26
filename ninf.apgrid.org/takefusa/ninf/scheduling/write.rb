# ruby
# write html file for scheculing results
# Standard calc version
# for ep & linpack problems

if (ARGV.size < 1)
    print "ruby logReader.rb [scheduler]"
    exit
end

@s = ARGV.shift
if @s =~ "RoundRobinScheduler"
    @t = "RoundRobin"
elsif @s =~ "LoadOnlyScheduler"
    @t = "Load"
elsif @s =~ "LoadOnlyLScheduler"
    @t = "Load (Predict Load)"
elsif @s =~ "LoadThroughputScheduler"
    @t = "Load + Thoughput"
elsif @s =~ "LoadThroughputLScheduler"
    @t = "Load + Thoughput (Predict Load)"
elsif @s =~ "LoadThroughputTScheduler"
    @t = "Load + Thoughput (Predict Throughput)"
elsif @s =~ "LoadThroughputLTScheduler"
    @t = "Load + Thoughput (Predict Load, Thooughput)"
else
    print "ERROR: input\n"
    exit
end

print "<html>\n<head>\n"
print "<title>Experimental Results with Ninf MetaServer - ", @t, "</title>\n"
print "</head>\n\n"
print "<body bgcolor=\"FFFFFF\">\n"
print "<basefont size = 3>\n\n"
print "<h2>Experimental Results with Ninf MetaServer</h2>\n"
print "<h3>", @t, "</h3>\n\n"

print "<applet name=\"Load\" code=\"ptplot.Plot\" width=700 height=100
	archive=\"ptplot/ptplot.zip\">\n"
print "<param name=\"pxgraphargs\" value=\"-t 'Ninf_call' -0 J90 -1 UltraSPAEC -nl -m sample/", @s, "_3051_client.plt sample/", @s, "_3052_client.plt\">\n"
print "<param name=\"background\" value=\"ffffff\">\n</applet>\n<br>\n\n"

print "<applet name=\"Load\" code=\"ptplot.Plot\" width=700 height=200
	archive=\"ptplot/ptplot.zip\">\n"
print "<param name=\"pxgraphargs\" value=\"-t Load -0 J90 -1 UltraSPAEC -m sample/", @s, "_3051_load.plt sample/", @s, "_3052_load.plt\">\n"
print "<param name=\"background\" value=\"ffffff\">\n</applet>\n<br>\n\n"

print "<applet name=\"Thru\" code=\"ptplot.Plot\" width=700 height=200
	archive=\"ptplot/ptplot.zip\">\n"
print "<param name=\"pxgraphargs\" value=\"-t Throughput -x 'Elapsed Time' -y Byte/sec -0 J90 -1 UltraSPARC -m sample/", @s, "_3051_thru.plt sample/", @s, "_3052_thru.plt\">\n"
print "<param name=\"background\" value=\"ffffff\">\n</applet>\n\n"

print "<p>\n<font size=2>\n"
print "<a href=\"http://www.sap.is.ocha.ac.jp/~takefusa/\">\n"
#print "<img src=\"home.gif\" border=\"0\" width=\"32\">\n"
print "http://www.sap.is.ocha.ac.jp/~takefusa/</a>\n"
print "</font>\n</body>\n</html>\n"
