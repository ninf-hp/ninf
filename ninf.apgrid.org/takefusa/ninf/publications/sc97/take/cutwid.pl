#!/usr/local/bin/perl

while (<>) {
  s/WIDTH="[^>]*"//g;
  print;
}
