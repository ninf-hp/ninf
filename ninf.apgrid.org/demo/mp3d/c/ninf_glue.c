#include "common.h"

int 
         bc_count,     /* number of boundary conditions defined */
         num_xcell,    /* X dimension of active space, in cells */
         num_ycell,    /* Y dimension of active space, in cells */
         num_zcell;    /* Z dimension of active space, in cells */

void display_init();
void l_do_display(int *, struct s_particle *);

l_mp3dx(int l_num_xcell, int l_num_ycell, int l_num_zcell, int l_bc_cnt, int * tmpint, float * tmpdata)
{
  num_xcell = l_num_xcell;
  num_ycell = l_num_ycell;
  num_zcell = l_num_zcell;
  Ninf_call("mp3d/mp3dx", l_num_xcell, l_num_ycell, l_num_zcell, l_bc_cnt, 
	    MAX_BCNT, tmpint, tmpdata, MAX_MOL, display_init, l_do_display);
  Ninf_perror("mp3dx");
}

int g_num_mol;
