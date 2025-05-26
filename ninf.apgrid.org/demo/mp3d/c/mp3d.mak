# This file 'mp3d.mak' was created by /usr/users/nakada/ninf/ninf/solaris/bin/ninf_gen. Don't edit.

# Default setting

CC = gcc
NINF_COMPILER = $(CC)
NINF_LINKER = $(CC)

# Include template

include $(NINF_DIR)/lib/template

# CompileOptions:


# stub sources

NINF_STUB_SRC = _stub_mp3dx.c 

# stub programs

NINF_STUB_PROGRAM = _stub_mp3dx

all: $(NINF_STUB_PROGRAM)

_stub_mp3dx.o: _stub_mp3dx.c
	$(NINF_COMPILER) $(CFLAGS) $(NINF_CFLAGS)  -c _stub_mp3dx.c
_stub_mp3dx: _stub_mp3dx.o mp3dx.o adv.o setup.o rand.o -lm
	$(NINF_LINKER) $(CFLAGS)  -o _stub_mp3dx _stub_mp3dx.o $(LDFLAGS) $(NINF_STUB_LDFLAGS) mp3dx.o adv.o setup.o rand.o -lm $(LIBS)


clean:
	rm -f _stub_mp3dx.o _stub_mp3dx.c

veryclean: clean
	rm -f $(NINF_STUB_PROGRAM)
	rm -f mp3dx.o adv.o setup.o rand.o -lm


# END OF Makefile
