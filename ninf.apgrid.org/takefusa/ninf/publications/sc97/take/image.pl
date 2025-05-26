#!/usr/local/bin/perl

while (<>) {
  s/="image/="IMAGE\/IMAGE/g;
  s/\.gif"/\.GIF"/g;
  print;
}
