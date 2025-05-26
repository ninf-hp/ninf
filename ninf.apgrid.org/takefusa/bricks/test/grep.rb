# ruby
# grep.rb
#

if (ARGV.size < 1)
    print "ruby grep.rb [org. word] [chg. word] [file] > [new file]\n"
    exit
end

@org = ARGV.shift
@chg = ARGV.shift

while str = gets
    if str =~ @org
        str.gsub!(@org, @chg)
#    if str =~ /RequestData/
#        str.gsub!(/RequestData/, "RequestedData")
    end
    print str
end
