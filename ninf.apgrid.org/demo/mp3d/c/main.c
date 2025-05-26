#include <stdio.h>
#include "common.h"

/* Local variables: */
char geometry_file_name[FILENAME_LENGTH] = "test.geom";

float tmpdata[MAX_BCNT * 6];
int tmpint[MAX_BCNT * 3];
float * tmpdatap;
int * tmpintp;
static int l_num_xcell, l_num_ycell, l_num_zcell, l_bc_cnt;

main(argc, argv)
int argc;
char ** argv;
{
#ifdef NINF
  argc = Ninf_parse_arg(argc, argv);
#endif
  if (get_geom(geometry_file_name)) {
    l_mp3dx(l_num_xcell, l_num_ycell, l_num_zcell, l_bc_cnt, tmpint, tmpdata);
  } else {
    fflush(stdout);
    /* perror("ERROR"); */
    fprintf(stderr, "ERROR: Could not read geometry file (%s).\n",
      geometry_file_name);
    exit(-1);
  }
}
/*
||
|| GET GEOM :  Reads the specified geometry input file and calculates
|| required parameters such as BC cell area ratios.
||
*/
int get_geom(geom_filename)
char geom_filename[];
{
  int i, cx, cy, cz;
  float f1, f2, f3, f4, f5, f6;
  FILE *fd;

  fd = fopen(geom_filename, "r");
  if (fd != NULL) {

    fscanf(fd, "%d", &l_num_xcell);
    if (l_num_xcell < 1) {
      fflush(stdout);
      fprintf(stderr, "ERROR: x dimension too small in geometry. (%d) (%s)\n",
             l_num_xcell, geom_filename);
      exit(-1);
    } else if (l_num_xcell > MAX_XCELL) {
      fflush(stdout);
      fprintf(stderr, "ERROR: x dimension too large in geometry. (%d) (%s)\n",
             l_num_xcell, geom_filename);
      exit(-1);
    }

    fscanf(fd, "%d", &l_num_ycell);
    if (l_num_ycell < 5) {
      fflush(stdout);
      fprintf(stderr, "ERROR: y dimension too small in geometry. (%d) (%s)\n",
             l_num_ycell, geom_filename);
      exit(-1);
    }

    fscanf(fd, "%d", &l_num_zcell);
    if (l_num_zcell < 5) {
      fflush(stdout);
      fprintf(stderr, "ERROR: z dimension too small in geometry. (%d) (%s)\n",
             l_num_zcell, geom_filename);
      exit(-1);

    } else if (l_num_zcell > MAX_ZCELL) {
      fflush(stdout);
      fprintf(stderr, "ERROR: z dimension too large in geometry. (%d) (%s)\n",
             l_num_zcell, geom_filename);
      exit(-1);
    }


    fscanf(fd, "%d", &l_bc_cnt);
    l_bc_cnt += 9;   /* for the 8 edges of the active space, plus [0] not used */

#ifdef not
    Bconds = (struct bcond *) calloc(l_bc_cnt, sizeof(struct bcond));
    if (Bconds <= 0) {
      fflush(stdout);
      fprintf(stderr, "ERROR: Alloc error for boundary conditions.\n");
      exit(-1);
    }
#else
    if(l_bc_cnt > MAX_BCNT){ 
	fflush(stdout);
	fprintf(stderr, "ERROR: l_bc_cnt too large in geometry. (%d) (%s)\n",
		l_bc_cnt, geom_filename);
	exit(-1);
    }
#endif

    tmpintp = &(tmpint[0]);
    tmpdatap = &(tmpdata[0]);
    for (bc_count = 9; bc_count < l_bc_cnt; bc_count++) {
      fscanf(fd, "%d %d %d", &cx, &cy, &cz);
      *tmpintp++ = cx; *tmpintp++ = cy; *tmpintp++ = cz; 
      
      fscanf(fd, "%f %f %f %f %f %f", &f1, &f2, &f3, &f4, &f5, &f6);
      *tmpdatap++ = f1; *tmpdatap++ = f2; *tmpdatap++ = f3; 
      *tmpdatap++ = f4; *tmpdatap++ = f5; *tmpdatap++ = f6; 
    }      
    fclose(fd);
    return(TRUE);

  } else
    return(FALSE);
} /* get_geom() */
