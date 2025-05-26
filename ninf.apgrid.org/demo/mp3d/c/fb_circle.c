/* #include <thd/framebuffer.h> */
#include "framebuffer.h"

fb_circle(x1, y1, r, color)
int x1, y1, r, color;
{
    int x, y, s;

    x = r; y = 0; s = r;
    while (x >= y) {
	fb_drawpoint(x1+x,y1+y,color);
	fb_drawpoint(x1+x,y1-y,color);
	fb_drawpoint(x1-x,y1+y,color);
	fb_drawpoint(x1-x,y1-y,color);
	fb_drawpoint(x1+y,y1+x,color);
	fb_drawpoint(x1+y,y1-x,color);
	fb_drawpoint(x1-y,y1+x,color);
	fb_drawpoint(x1-y,y1-x,color);
	s = s - y*2 - 1; y = y + 1;
	if (s < 0) {
	    s = s + (x-1)*2; x = x - 1;
	}
    }
}
