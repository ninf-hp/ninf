# ruby
while $_ = gets
    if $_ =~ /\.GIF/ then
	$_.gsub!(/\.GIF/, "\\.gif")
    end
    print $_
end
