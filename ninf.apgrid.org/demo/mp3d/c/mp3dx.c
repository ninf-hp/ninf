#define TEST_N_STEP 100 /* 10 /* 50 */
#define TEST_N_MOL 3000  	/* RES_RATIO*2 < N_MOL < MAX_MOL */
#define N_PE	1
#define G_MALLOC	malloc

/*
||
|| mp3d.U
||
|| Use : This is a general purpose program that uses the Stanford
|| particle method to examine rarefied flows over objects in a simulated
|| wind tunnel.  Geometry is defined by a cad program. This version
|| uses the ANL macro set to exploit shared memory parallel computer
|| architectures.
||
|| Date : February, 1988.
|| Prog : Jeff McDonald
||        Stanford University.
||
|| Date : Aug. 1993
|| Modified for EM-C/EM-4 by M. SATO
||
*/

/* Macro for variable declarations */
/* Include files */

#include <stdio.h>
#include <fcntl.h>
#include <math.h>

#include "common.h"

/* 
 * Define the Globally-shared data structures. 
 */

/* Particle Properties - positions and velocites */
struct particle *Particles;

/* Instant and cumulative cell populations and physical space map. */
plane *Cells;

/* Thermalizer reservoir variables */
struct ares *Ares;

/* Boundary Conditions Temporary Space */
int *Bc_space;

/* Globally-shared scalars */
int g_open_cells,
     g_next_res,               /* next reservoir variable for thermalization */
     g_num_mol,                /* number of active molecules */
     g_take_steps,              /* number of steps to take */
     g_time_step,
     g_cum_steps,               /* cumulative steps taken */
     g_num_collisions,          /* molecule collisions in this iteration */
     g_total_collisions,        /* total molecule collisions */
     g_BC,                      /* number of boundary collisions */
     g_res_offset,           /* offset between collided reservoir molecules */
     g_max_sub;                 /* general purpose max subscript */
 float g_coll_prob,             /* collision probability */
       g_free_pop,
       g_num_flow,
       g_tot_flt_input;
 long g_exit_num_flow,
      g_tot_input;

float space_heightm4,        /* height of active space - 4 */
           space_lengthm4,        /* length of active space - 4 */
           space_widthm4,         /* width of active space - 4 */
           snd_speed,             /* speed of sound */
           fluid_speed,           /* speed of the free stream */
           thermal_factor,
           gama,
           upstream_mfp;          /* mean free path of free stream */
int I,            /* processor index */
         bc_count,     /* number of boundary conditions defined */
         clump,        /* clumping factor for move loop */
         num_res,      /* number of reservoir molecules */
         num_xcell,    /* X dimension of active space, in cells */
         num_ycell,    /* Y dimension of active space, in cells */
         num_zcell,    /* Z dimension of active space, in cells */
         number_of_processors; 

/* Collision Tables. */
int Mix_table[MIXS_POSSIBLE][MIX_DOF];
float Sign_table[SIGNS_POSSIBLE][MIX_DOF];

/* Boundary Conditions Lookup Tables */
#ifdef TEST
struct bcond  Bconds[MAX_BCNT];
#else
struct bcond  *Bconds;
#endif


static void test_command();

/*
||  MAIN PROGRAM.  The begining and the end.
*/
int mp3dx(int l_num_xcell, int l_num_ycell, int l_num_zcell, int l_bc_cnt, int * tmpint, float * tmpdata)
{
  copy_data(l_num_xcell, l_num_ycell, l_num_zcell, l_bc_cnt, tmpint, tmpdata);

  fill_mix_table();
  fill_sign_table();
  prepare_multi();

  g_num_mol = TEST_N_MOL;
  number_of_processors = N_PE; 

  init_borders();
  init_variables();
  fill_space();
  fill_reservoir();

  /* test main for command = r,50,q,e */
  test_command();
  /* exit(0); */
} /* main() */

/*
|| TEST COMMAND:  
*/
static void test_command()
{
    int i, j, k,   scale,    cz,      num_col,      temp_tot;
    unsigned int start_time,               end_time;

    /* R */
    r_command();

    /* step 50 */
    g_take_steps = TEST_N_STEP;
    g_time_step += g_take_steps;
    g_cum_steps += g_take_steps;
    printf("Working towards %d steps.\n", g_time_step);
    
#ifdef PARALLEL
    /* NumberOfProcessors-1 slaves to be started */
    for (I = 1; I < number_of_processors; I++)
      CREATE(slave_advance_solution)
#endif
#ifdef EMX
    start_time = em_utime();
#else
    start_time = 0;
#endif
    I = 0;
    
    /* Make master earn his keep */
    master_advance_solution();
    
#ifdef EMX
    end_time = em_utime();
#else
    end_time = 0;
#endif

#ifdef PARALLEL
    WAIT_FOR_END(number_of_processors - 1)
      CLOCK(end_time)
	fflush(stdout);
#endif
    fprintf(stderr, "Elapsed time for advance: %d msec\n",end_time-start_time);
    
    /* Q, print result */
    temp_tot = 0;
    for (i=2; i<4; i++)
      for (j=2; j<4; j++) {
          printf("Cum cell pop = %d %d %d %d\n",
		 Cells[j][2][i].cum_cell_pop, 
		 Cells[j][3][i].cum_cell_pop, 
		 Cells[j][4][i].cum_cell_pop,
		 Cells[j][5][i].cum_cell_pop);
          for (k=2; k<4; k++) 
            temp_tot += Cells[j][k][i].cell_pop;
      }
    printf("\n\n");
    for (i=2; i<4; i++)
      for (j=2; j<4; j++)
	printf("Cell pop = %d %d %d %d\n",
	       Cells[j][2][i].cell_pop, 
	       Cells[j][3][i].cell_pop, 
	       Cells[j][4][i].cell_pop,
	       Cells[j][5][i].cell_pop);
    printf("total in system = %d\n", temp_tot);
    /* END */
    r_command();
} 

r_command()
{
    printf("\nRun Status\n\n");
    printf("Number of molecules........%ld\n", g_num_mol);
    printf("Number of steps............%d\n", g_time_step);
    printf("Number of Avg. Steps.......%d\n", g_cum_steps);
    printf("Upstream MFP ..............%f\n", upstream_mfp);
    printf("Free Pop ..................%f\n", g_free_pop);
    printf("Gamma......................%f\n", gama);
    printf("Coll Prob .................%f\n", g_coll_prob);
    printf("Num collisions ............%d\n", g_num_collisions);
    printf("Total collisions...........%d\n", g_total_collisions);
    printf("Num BC ....................%d\n\n", g_BC);
    fflush(stdout);
}



/*
|| GLOBAL_ALLOC:  Allocates aligned global memory.
*/
int *global_alloc(bytes, name)
unsigned bytes;
char *name;
{
  unsigned temp;

  temp = (unsigned) G_MALLOC(bytes + ALIGNMENT - 1);
  if (temp == (unsigned)NULL) {
    fflush(stdout);
    fprintf(stderr, 
      "ERROR: unable to allocate global memory for %s. (%d bytes)\n", 
      name, bytes);
      exit(-1);
  }
  if (temp % ALIGNMENT) {
    temp += ALIGNMENT;
    temp -= temp % ALIGNMENT;
  }
  printf("g_alloc(0x%x): %d bytes(0x%x words) for %s\n",
	 temp,bytes,bytes/4,name); 
  return (int *)temp;
} /* global_alloc() */

/*
|| PREPARE MULTI :  Initializes all ANL constructs as well
|| as allocating global memory.
*/
static int prepare_multi() {

  /* set clump factor for move loop */
  for (clump = 1; (clump * sizeof(struct particle)) % ALIGNMENT; clump++);

  Particles = (struct particle *) 
       global_alloc(MAX_MOL*sizeof(struct particle), "Particles");
#ifdef PARALLEL
  /* Initialize locks, barriers and distributed loops */
  BARINIT(g_end_sync)
  LOCKINIT(g_output)
  LOCKINIT(g_collisions_lock)
  LOCKINIT(g_num_mol_lock)
  LOCKINIT(g_next_res_lock)
#endif
} /* prepare_multi() */


struct s_particle * s_Particles = (void *)0;

do_display(){
  int i; 
  if (s_Particles == (void *)0){
    s_Particles = (struct s_particle *) 
      global_alloc(MAX_MOL*sizeof(struct s_particle), "s_Particles");
  }

  for (i = 0; i < MAX_MOL; i++){
    s_Particles[i].x = Particles[i].x;
    s_Particles[i].y = Particles[i].y;
    s_Particles[i].z = Particles[i].z;
    s_Particles[i].u = Particles[i].u;
    s_Particles[i].v = Particles[i].v;
    s_Particles[i].w = Particles[i].w;
  }
printf("g_num_mol = %d\n", g_num_mol);
  l_do_display(&g_num_mol, s_Particles);
}
