#include <stdio.h>


main(int argc, char *argv[])
{

 printf("%<html%>\n");

 printf("%<frameset rows=\"25%%,77%%\"%>\n");
 printf("M- M-  %<frame src=\"exa%s.html\"%>\n",argv[1]);
 printf("M-    %<frameset cols=\"30%%,70%%\"%>\n");
 printf("M- M-  %<frame src=\"generate%s.html\"%>\n",argv[1]);
 printf("M- M-  %<frame src=\"info%s.html\" NAME=\"display\"%>\n",argv[1]);
 printf("M-    %</frameset%>\n");
 printf("%</frameset%>\n");

 printf("%</html%>\n");
}
