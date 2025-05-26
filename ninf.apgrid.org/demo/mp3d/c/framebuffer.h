/* #include <thd/em_built_in.h> */
/* for X-Window emulation */
#define fb_drawpoint(x,y,d) em_mkpkt(((y)*1024+(x)+0x40000)<<2, d, 0x6a)
#define fb_init(mode)       em_mkpkt(0, (mode), 0x6b)
#define fb_rflush(x,y,w,h)  \
	    em_mkpkt(0,(w),0x66), \
	    em_mkpkt(4,(h),0x66), \
	    em_mkpkt(4, (y)*1024+(x)+0x40000, 0x6b)
#define fb_querypointer_root()   em_get(8, 0x6b);
#define fb_querypointer_window() em_get(12, 0x6b);
#define fb_flush()          fb_rflush(0,0,1024,768)
#define fb_cflush(x,y)      fb_rflush(x,y,12,24)

/* for FrameBuffer Board */
#define FBPE 0x22400000
#define vb_drawpoint(x,y,d) em_mkpkt(FBPE | (((y)*1024+(x)+0x40000)<<2), d, 0x24)

#define WHITE 0xff

