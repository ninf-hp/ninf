#include "xm.h"

em_mkpkt(addr,data,pt_trc)
     int addr,data,pt_trc;
{
    word_t addr_w,data_w;
    
    addr_w.t = pt_trc;
    addr_w.d = addr;
    data_w.t = 0;
    data_w.d = data;

    if((pt_trc & 0x40) &&
       (addr_w.t == 0x26) || (addr_w.t == 0x2a) || (addr_w.t == 0x2b)) {
        emulate_framebuffer(addr_w,data_w);
        return;
    } else {
	fprintf(stderr,"only frame buffer packets are simulated\n");
	exit(1);
    }
}

