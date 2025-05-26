#!/bin/csh
foreach i ( img013.htm img026.htm note010.htm note023.htm img001.htm img014.htm img027.htm note011.htm note024.htm img002.htm img015.htm img028.htm note012.htm note025.htm img003.htm img016.htm navbtn.htm note013.htm note026.htm img004.htm img017.htm note001.htm note014.htm note027.htm img005.htm img018.htm note002.htm note015.htm note028.htm img006.htm img019.htm note003.htm note016.htm outlinec.htm img007.htm img020.htm note004.htm note017.htm outlinee.htm img008.htm img021.htm note005.htm note018.htm ppframe.htm img009.htm img022.htm note006.htm note019.htm sizebtn.htm img010.htm img023.htm note007.htm note020.htm tmp.sh img011.htm img024.htm note008.htm note021.htm img012.htm img025.htm note009.htm note022.htm)
	ruby grepGIF.rb $i > ../$i
end
