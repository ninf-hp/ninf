#!/bin/csh
foreach i (sld011 sld022 sld001 sld012 sld023 sld002 sld013 sld024 sld003 sld014 sld025 sld004 sld015 sld026 sld005 sld016 sld027 sld006 sld017 sld007 sld018  sld008 sld019 sld009 sld020 sld010 sld021 )
  mv $i.html $i.htm
  tmp2.pl $i.htm > $i.html
end

