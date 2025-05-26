#!/usr/local/bin/perl

while (<>) {
  s/<FONT[^>]*>//g;
  s/<\/FONT>//g;
  print;
}
