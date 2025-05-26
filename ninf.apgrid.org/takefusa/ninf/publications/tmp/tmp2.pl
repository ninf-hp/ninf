#!/usr/local/bin/perl

while (<>) {
  s/WIDTH=800 HEIGHT=600/WIDTH=400 HEIGHT=300/g;
  print;
}
