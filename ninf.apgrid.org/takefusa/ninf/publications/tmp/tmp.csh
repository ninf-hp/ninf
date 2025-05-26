#!/bin/csh
foreach i (sld001 sld011 sld021 tsld006 tsld016 sld002 sld012 sld022 tsld007 tsld017 sld003 sld013 sld023 tsld008 tsld018 sld004 sld014 sld024 tsld009 tsld019 sld005 sld015 sld025 tsld010 tsld020 sld006 sld016 tsld001 tsld011 tsld021 sld007 sld017 tsld002 tsld012 tsld022 sld008 sld018 tsld003 tsld013 tsld023 sld009 sld019 tsld004 tsld014 tsld024 sld010 sld020 tsld005 tsld015 tsld025 )
  tmp.pl $i.htm > $i.html
end

foreach i (img001 img006 img011 img016 img021 img002 img007 img012 img017 img022 img003 img008 img013 img018 img023 img004 img009 img014 img019 img024 img005 img010 img015 img020 img025 )
  mv $i.gif $i.GIF
end

