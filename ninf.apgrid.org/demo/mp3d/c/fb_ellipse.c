#include <thd/framebuffer.h>

fb_ellipse(x1, y1, rx, ry, color)
int x1, y1, rx, ry, color;
{
    int x, y, s, x0, y0;

    if (rx > ry) {
	x = s = rx; y = 0;
	while (x >= y) {
	    x0 = x * ry / rx;
	    y0 = y * ry / rx;
	    fb_drawpoint(x1+x,y1+y0,color);
	    fb_drawpoint(x1+x,y1-y0,color);
	    fb_drawpoint(x1-x,y1+y0,color);
	    fb_drawpoint(x1-x,y1-y0,color);
	    fb_drawpoint(x1+y,y1+x0,color);
	    fb_drawpoint(x1+y,y1-x0,color);
	    fb_drawpoint(x1-y,y1+x0,color);
	    fb_drawpoint(x1-y,y1-x0,color);
	    s = s - y*2 - 1; y = y + 1;
	    if (s < 0) {
		s = s + (x-1)*2; x = x - 1;
	    }
	}
    } else {
	x = s = ry; y = 0;
	while (x >= y) {
	    x0 = x * rx / ry;
	    y0 = y * rx / ry;
	    fb_drawpoint(x1+x0,y1+y,color);
	    fb_drawpoint(x1+x0,y1-y,color);
	    fb_drawpoint(x1-x0,y1+y,color);
	    fb_drawpoint(x1-x0,y1-y,color);
	    fb_drawpoint(x1+y0,y1+x,color);
	    fb_drawpoint(x1+y0,y1-x,color);
	    fb_drawpoint(x1-y0,y1+x,color);
	    fb_drawpoint(x1-y0,y1-x,color);
	    s = s - y*2 - 1; y = y + 1;
	    if (s < 0) {
		s = s + (x-1)*2; x = x - 1;
	    }
	}
    }
}
