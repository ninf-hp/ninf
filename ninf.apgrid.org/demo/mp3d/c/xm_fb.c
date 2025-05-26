/* $Id: xm_fb.c,v 1.1 1998/03/23 02:50:16 nakada Exp $ */
#include "xm.h"
#include <X11/Xlib.h>
#include <X11/Xutil.h>
Display *d;
Window r, w, root, chile;
GC gc;
XImage *ximage;
Colormap cmap, cmap0;
int fb_mode;
unsigned char *frame_buffer;
int rflush_arg[2];
float tbl3[40] = {1.0, 0.99, 0.98, 0.97, 0.96, 0.95, 0.94, 0.93, 0.92, 0.91, 0.90, 0.89, 0.88, 0.87, 0.86, 0.85, 0.84, 0.83, 0.82, 0.81, 0.80, 
0.78, 0.76, 0.74, 0.72, 0.70, 0.68, 0.66, 0.64, 0.62, 0.60, 0.58, 0.56, 0.54, 0.52, 0.50, 0.48, 0.46, 0.44, 0.00};

emulate_framebuffer(addr_w,data_w)
word_t addr_w,data_w;
{
    if (addr_w.t == 0x2a) {
	if ((addr_w.d < 0x100000) || (addr_w.d >= 0x400000)) {
	    printf("illegal address on frame buffer %08x\n", addr_w.d);
	} else if (fb_mode)
	    frame_buffer[((addr_w.d-0x100000) >> 2)&0xfffff] = data_w.d;
	else
	    frame_buffer[((addr_w.d-0x100000) >> 2)&0xfffff] = 
		((data_w.d>>16)&0xc0)+((data_w.d>>10)&0x38)+((data_w.d>>5)&0x7);
    } else if (addr_w.t == 0x26) {
	if (addr_w.d < 8) {
	    rflush_arg[addr_w.d>>2] = data_w.d;
	} else {
	    printf("argments number must be less than 2 but %d\n", addr_w.d);
	}
    } else if (addr_w.t == 0x2b)
	control_frame_buffer_window(addr_w.d>>2, data_w);
}

control_frame_buffer_window(addr, data_w)
int addr;
word_t data_w;
{
    int data;
    unsigned long red, green, blue, pixel;
    int i, j;
    int rx, ry, wx, wy;
    unsigned int mask;
    word_t retdata;

    data = data_w.d;
    switch (addr) {
    case 0:        /* fb_init() */
	if (frame_buffer) {
	    printf("ignore rebuild window\n");
	    return;
	}
	frame_buffer = (unsigned char *)calloc(1024*768, sizeof(char));
	d = XOpenDisplay(NULL);
	r = RootWindow(d, 0);
	gc = XCreateGC(d, r, 0, 0);
	ximage = XCreateImage(d, DefaultVisual(d, 0), DefaultDepth(d, 0), 
			      ZPixmap, 0, frame_buffer, 1024, 768, 8, 0);
	w = XCreateSimpleWindow(d, r, 0, 0, 1024, 768, 2, 0, 1);
	XSelectInput(d, w, ExposureMask | ButtonPressMask);
	XMapWindow(d, w);
	cmap = XCreateColormap(d, r, DefaultVisual(d, 0), AllocAll);
	cmap0 = XDefaultColormap(d, 0);
	fb_mode = data;
	switch (data) {
	case 0:
	case 1: setRGBcolormap(); break;
	case 2: setHVCcolormap(); break;
	case 3: setGRAYcolormap(); break;
	}
	XInstallColormap(d, cmap);
	XPutImage(d, w, gc, ximage, 0, 0, 0, 0, 1024, 768);
	XFlush(d);
	break;
    case 1:        /* fb_rflush(x, y, width, height) */
	/* XInstallColormap(d, cmap); */
	data =  data - 0x40000;
	i = data % 1024; j = data / 1024;
	XPutImage(d, w, gc, ximage, i, j, i, j, rflush_arg[0], rflush_arg[1]);
	XFlush(d);
	break;
    case 2:	/* fb_querypointer_root() */
	XQueryPointer(d, w, &root, &chile, &rx, &ry, &wx, &wy, &mask);
	retdata.t = 0; retdata.d = ((mask>>8)<<24) + (ry<<12) + rx;
	send_pkt(data_w, retdata, 0);
	break;
    case 3:	/* fb_querypointer_window() */
	XQueryPointer(d, w, &root, &chile, &rx, &ry, &wx, &wy, &mask);
	retdata.t = 0; retdata.d = ((mask>>8)<<24) + (wy<<12) + wx;
	send_pkt(data_w, retdata, 0);
	break;
    }
}

setHVCcolormap()
{
    int i;
    XColor color1;

    for (i=0; i<16; i++) {
	color1.pixel = i;
	XQueryColor(d, cmap0, &color1);
	XStoreColor(d, cmap, &color1);
    }
    for (i=0; i<40; i++) {
	color1.red = 0xffff;
	color1.green = 0xffff*tbl3[39-i];
	color1.blue = 0;
	color1.pixel = i+16;
	color1.flags = DoRed | DoGreen | DoBlue;
	XStoreColor(d, cmap, &color1);
	color1.red = 0xffff*tbl3[i];
	color1.green = 0xffff;
	color1.blue = 0;
	color1.pixel = i+56;
	color1.flags = DoRed | DoGreen | DoBlue;
	XStoreColor(d, cmap, &color1);
	color1.red = 0;
	color1.green = 0xffff;
	color1.blue = 0xffff*tbl3[39-i];
	color1.pixel = i+96;
	color1.flags = DoRed | DoGreen | DoBlue;
	XStoreColor(d, cmap, &color1);
	color1.red = 0;
	color1.green = 0xffff*tbl3[i];
	color1.blue = 0xffff;
	color1.pixel = i+136;
	color1.flags = DoRed | DoGreen | DoBlue;
	XStoreColor(d, cmap, &color1);
	color1.red = 0xffff*tbl3[39-i];
	color1.green = 0;
	color1.blue = 0xffff;
	color1.pixel = i+176;
	color1.flags = DoRed | DoGreen | DoBlue;
	XStoreColor(d, cmap, &color1);
	color1.red = 0xffff;
	color1.green = 0;
	color1.blue = 0xffff*tbl3[i];
	color1.pixel = i+216;
	color1.flags = DoRed | DoGreen | DoBlue;
	XStoreColor(d, cmap, &color1);
    }
}

setRGBcolormap()
{
    int red, green, blue;
    XColor color1;

    for (blue = 0; blue < 4; blue ++)
	for (green = 0; green < 8; green++)
	    for (red = 0; red < 8; red++) {
		color1.red   = red*0x2492;
		color1.green = green*0x2492;
		color1.blue  = blue*0x5555;
		color1.pixel = blue*64+green*8+red;
		color1.flags = DoRed | DoGreen | DoBlue;
		XStoreColor(d, cmap, &color1);
	    }
}

setGRAYcolormap()
{
    int i;
    XColor color1;

    for (i=0; i<256; i++) {
	color1.red = i*0x101;
	color1.green = i*0x101;
	color1.blue = i*0x101;
	color1.pixel = i;
	color1.flags = DoRed | DoGreen | DoBlue;
	XStoreColor(d, cmap, &color1);
    }
}
