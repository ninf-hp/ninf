#
# Sequential for Benchmarking
#
# for frame buffer lib, test program for sun4
#----------------------------------------------------------------------
include $(NINF_DIR)/template
CC = gcc
XDIR = /fs/local/X11R6


$(NINFLIB): .force
	(cd $(NINF_DIR)/NinfLib; make)
$(CLIENTLIB): .force
	(cd $(NINF_DIR)/NinfClient; make)
#dummy
.force: 
#----------------------------------------------------------------------
TARGET = backend
DISPLAY = front
#CCFLAGS = -O2 -DLOCKING
#CCFLAGS = -O2
CFLAGS = -g -I$(XDIR)/include/
LDXFLAGS = -L$(XDIR)/lib -R$(XDIR)/lib -lm -lX11 -lsocket -lnsl

DISPLAYOBJS = display.o xm_fb.o fb_line.o fb_circle.o em_mkpkt.o 

BACKOBJS = mp3dx.o adv.o setup.o  rand.o  

TEST = testall
OBJS = $(BACKOBJS) $(DISPLAYOBJS) glue.o main.o

ninf_main.o: main.c
	$(CC) $(CFLAGS) -DNINF -o ninf_main.o -c main.c

$(TEST): $(OBJS) 
	$(CC) $(OBJS) -o $(TEST) $(LDXFLAGS)


$(TARGET): $(BACKOBJS) $(LIB) mp3d.idl
	$(NINF_GEN) mp3d.idl
	NINF_DIR=$(NINF_DIR) make -f mp3d.mak


$(DISPLAY): $(DISPLAYOBJS) ninf_glue.o ninf_main.o $(LIB)
	$(CC) $(DISPLAYOBJS) ninf_glue.o ninf_main.o -o $(DISPLAY)  $(NINF_LDFLAGS) $(LDXFLAGS) 

all: $(TEST) $(DISPLAY) $(TARGET)

clean: 
	\rm *.o _stub* $(TEST) $(DISPLAY) *.mak

#  for NINFY


