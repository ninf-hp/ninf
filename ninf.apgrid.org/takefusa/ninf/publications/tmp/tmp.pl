#!/usr/local/bin/perl

while (<>) {
  s/.htm"/.html"/g;
  print;
}
