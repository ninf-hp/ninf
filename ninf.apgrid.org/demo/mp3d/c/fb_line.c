/* #include <thd/framebuffer.h> */
#include "framebuffer.h"

fb_line(x1, y1, x2, y2, color)
int x1, y1, x2, y2, color;
{
    int signdx, signdy, adx, ady;
    int len, e, e1, e2;
    int t, i;

    if (x1 == x2) {        /* vertical line */
	if (y1 > y2) {
	    t = y2; y2 = y1; y1 = t;
	}
	for (i=y1; i<=y2; i++)
	    fb_drawpoint(x1,i,color);
	return;
    } else if (y1 == y2) { /* horizontal line */
	if (x1 > x2) {
	    t = x2; x2 = x1; x1 = t;
	}
	for (i=x1; i<x2; i++)
	    fb_drawpoint(i,y1,color);
	return;
    }                      /* slope line */
    signdx = 1;
    if ((adx = x2 - x1) < 0) {
	adx = -adx;
	signdx = -1;
    }
    signdy = 1;
    if ((ady = y2 - y1) < 0) {
	ady = -ady;
	signdy = -1;
    }
    if (adx > ady) {
	len = adx; 
	e1 = ady << 1;
	e2 = - (adx << 1);
	e = - adx;			
	--len;
	while (len >= 1) {
	    fb_drawpoint(x1,y1,color);
	    x1 += signdx; e += e1; 
	    if (e >= 0) { y1 += signdy; e += e2; }
	    len --;
	}
    } else {
	len = ady; 
	e1 = adx << 1;
	e2 = - (ady << 1);
	e = - ady;			
	--len;
	while (len >= 1) {
	    fb_drawpoint(x1,y1,color);
	    y1 += signdy; e += e1; 
	    if (e >= 0) { x1 += signdx; e += e2; }
	    len --;
	}
    }
    fb_drawpoint(x1,y1,color);
}

