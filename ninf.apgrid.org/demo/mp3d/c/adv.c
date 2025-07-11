/* FOR EM-C */
/*
||
|| adv.U
||
|| This file contains routines that are used to take the
|| solution through one time step.  Some of the required 
|| reservoir code is in resadv.u.  Adapted from a CRAY 2
|| code, this code has been radically modified to take
|| advantage of the ANL macro set programming model.
|| Reservoir update is also handled in this module.
||
*/

/* include all mp3d data defintions */
#include "common.h"

/*
|| BOUNDARY :  Takes passed molecules and performs the
|| required modifications to its position and velocity vector based
|| on the boundary condition applicable to the current cell.
|| Boundry conditions at a particular cell are represented as
|| equations of a plane in that cell in a surface normal
|| representation.  Boundry temperature and absorbtion coeffecient
|| are also stored for handling diffuse (non-specular) boundary
|| conditions.
*/

static int local_BC,                         /* boundary collision counter */
           local_num_collisions;             /* molecule collision counter */

static void boundary(bn, Part)
int bn;                 /* boundary number */
struct particle *Part;  /* molecule */
{
  int i,local_next;
  float d2,temp;
  struct bcond *BCond = &Bconds[bn];
  struct ares *RPart;

  ++local_BC;
  if (bn < COND_OFFSET)  /* Solid wall case */ {
    d2 = 2*(BCond->bc_A*Part->x 
          + BCond->bc_B*Part->y 
          + BCond->bc_C*Part->z 
          + BCond->bc_D);
    if (d2 < 0.0) {
      Part->x -= d2 * BCond->bc_A;
      Part->y -= d2 * BCond->bc_B;
      Part->z -= d2 * BCond->bc_C;
      Part->where = &Cells[CELL(Part->y)][CELL(Part->z)][CELL(Part->x)];
      d2 = 2*(BCond->bc_A * Part->u 
            + BCond->bc_B * Part->v
            + BCond->bc_C * Part->w);
      temp = BCond->bc_temp*(1.0 - BCond->bc_coef);
      Part->u = (Part->u - d2*BCond->bc_A)
        * BCond->bc_coef + BCond->bc_A*temp;
      Part->v = (Part->v - d2*BCond->bc_B)
        * BCond->bc_coef + BCond->bc_B*temp;
      Part->w = (Part->w - d2*BCond->bc_C)
        * BCond->bc_coef + BCond->bc_C*temp;
    }

  } else /* entrance or exit condition */ {
    bn -= COND_OFFSET;
    switch (bn) {
      case 1:  /* exit */
#ifdef PARALLEL
        LOCK(g_next_res_lock);
#endif
	local_next = g_next_res++ % num_res;
	g_exit_num_flow++;
#ifdef PARALLEL
        UNLOCK(g_next_res_lock)
#endif
        RPart = &Ares[local_next];
        Part->x = 2.0 + RPart->xr*fluid_speed;
        Part->y = 2.0 + RPart->yr*space_heightm4;
        Part->z = 2.0 + RPart->zr*space_widthm4;
        Part->where = &Cells[CELL(Part->y)][CELL(Part->z)][CELL(Part->x)];
        Part->u = RPart->ur + fluid_speed;
        Part->v = RPart->vr;
        Part->w = RPart->wr;
        Part->r = RPart->rr;
        Part->s = RPart->sr;
        break;

      case 2:  /* entrance */
#ifdef PARALLEL
        LOCK(g_next_res_lock);
#endif
	local_next = g_next_res++ % num_res;
#ifdef PARALLEL
        UNLOCK(g_next_res_lock)
#endif
        RPart = &Ares[local_next];
        Part->x = 2.0 + RPart->xr*fluid_speed;
        Part->y = 2.0 + RPart->yr*space_heightm4;
        Part->z = 2.0 + RPart->zr*space_widthm4;
        Part->where = &Cells[CELL(Part->y)][CELL(Part->z)][CELL(Part->x)];
        Part->u = RPart->ur + fluid_speed;
        Part->v = RPart->vr;
        Part->w = RPart->wr;
        Part->r = RPart->rr;
        Part->s = RPart->sr;
        break;

      default:
#ifdef PARALLEL
        LOCK(g_output)
#endif
          printf("ERROR: Unknown Condition = %d.\n", bn);
#ifdef PARALLEL
        UNLOCK(g_output)
        MAIN_END(-1)
#endif
        exit(-1);
    } /* switch */
  } /* if condition */
} /* boundary() */

/*
|| ADD TO FREE STREAM :  Adds molecules to entrance to build
|| up to the already calculated free stream density.
*/
static void add_to_free_stream() {
  int i,             /* subcript for dist loop */
      local_next,
      local_num_mol;
  struct ares *RPart;
  struct particle *Part;

  /* Must synchronize here to let master calc num to add */
#ifdef PARALLEL
  BARRIER(g_end_sync, number_of_processors)
#endif

  /* Add entrance molecules up to free flow number */
  /* as calculated in master advance               */
  for (i = I; i < g_max_sub; i+= number_of_processors) { 
#ifdef PARALLEL
    LOCK(g_num_mol_lock)
#endif
      local_num_mol = g_num_mol++;
#ifdef PARALLEL
    UNLOCK(g_num_mol_lock)
#endif

    if (local_num_mol < MAX_MOL) {
#ifdef PARALLEL
      LOCK(g_next_res_lock)
#endif
        local_next = g_next_res++ % num_res;
#ifdef PARALLEL
      UNLOCK(g_next_res_lock)
#endif
      Part = &Particles[local_num_mol];
      RPart = &Ares[local_next];
      Part->x = 2.0 + RPart->xr * fluid_speed;
      Part->y = 2.0 + RPart->yr * space_heightm4;
      Part->z = 2.0 + RPart->zr * space_widthm4;
      Part->where = &Cells[CELL(Part->y)][CELL(Part->z)][CELL(Part->x)];
      Part->u = RPart->ur + fluid_speed;
      Part->v = RPart->vr;
      Part->w = RPart->wr;
      Part->r = RPart->rr;
      Part->s = RPart->sr;

    } else {
#ifdef PARALLEL
      LOCK(g_output)
#endif
        printf("ERROR: Out of molecule space.\n");
#ifdef PARALLEL
      UNLOCK(g_output)
      MAIN_END(-1)
#endif
      exit(-1);
    }
  }
#ifdef PARALLEL
  BARRIER(g_end_sync, number_of_processors)
#endif
} /* add_to_free_stream() */

/*
|| RESET :  This routine prepares the space for the next time-step. 
|| Only positive numbers are zeroed in the space, as negative ones
|| represent boundarys.
*/
static void reset() {
  int i,      j,      k,      iter,      loop_max,      nz;
  struct acell *ACell;

  /* reset space */
  nz = num_zcell - 4;
  loop_max = (num_ycell - 4)*nz;
  for (iter = I; iter < loop_max; iter += number_of_processors) {
    j = 2 + iter/nz;
    k = 2 + iter%nz;
    for (i = 0; i < num_xcell; i++) {
      ACell = &Cells[j][k][i];
      ACell->avg_prob = 0.99*ACell->avg_prob 
                      + 0.01*g_coll_prob
                      * (float) ACell->cell_pop;
      ACell->cell_pop = 0;
      if (ACell->space > 0)
        ACell->space = 0;
    }
  } 

  /* Reset space lying partially under boundary conditions */
  for (i = I; i < bc_count; i+= number_of_processors) {
    Bc_space[i] = 0;
  }
#ifdef PARALLEL
  BARRIER(g_end_sync, number_of_processors)
#endif
} /* reset() */

/*
|| COLLIDE:  Collides the passed molecules in a collision of
|| statistically random outcome.
*/
static void collide(Part1, Part2)
struct particle *Part1,
                *Part2;
{
  int *pm;    /* pointer into mix table */
  float *ps,  /* pointer into sign table */
        r_mean,
        rel[5],
        s_mean,
        temp,
        u_mean,
        v_mean,
        w_mean;

  /* Calculate relative and mean velocities */
  rel[0]  = (Part1->u - Part2->u)*0.5;
  u_mean = Part1->u - rel[0];

  rel[1]  = (Part1->v - Part2->v)*0.5;
  v_mean = Part1->v - rel[1];

  rel[2]  = (Part1->w - Part2->w)*0.5;
  w_mean = Part1->w - rel[2];

  rel[3]  = (Part1->r - Part2->r)*0.5;
  r_mean = Part1->r - rel[3];

  rel[4]  = (Part1->s - Part2->s)*0.5;
  s_mean = Part1->s - rel[4];

  /* Lookup random collision outcome */
  pm = Mix_table[random() % MIXS_POSSIBLE];
  ps = Sign_table[random() % SIGNS_POSSIBLE];

  /* perform collision */
  temp = (*ps) * rel[*pm];
  Part1->u = u_mean + temp;
  Part2->u = u_mean - temp;
  ps++;
  pm++;
  temp = (*ps) * rel[*pm];
  Part1->v = v_mean + temp;
  Part2->v = v_mean - temp;
  ps++;
  pm++;
  temp = (*ps) * rel[*pm];
  Part1->w = w_mean + temp;
  Part2->w = w_mean - temp;
  ps++;
  pm++;
  temp = (*ps) * rel[*pm];
  Part1->r = r_mean + temp;
  Part2->r = r_mean - temp;
  ps++;
  pm++;
  temp = (*ps) * rel[*pm];
  Part1->s = s_mean + temp;
  Part2->s = s_mean - temp;

  ++local_num_collisions;
} /* collide() */

static void move_single(pn)
int pn;  /* molecule to move */
{
  float u,
        v,
        w;
  int bc_val,
      space_value;
  struct acell *ACell;
  struct particle *Part;

  Part = &Particles[pn];
  ACell = Part->where;

  /* Do sample from previous time-step */
  u = Part->u;
  v = Part->v;
  w = Part->w;
#ifdef LOCKING
  LOCK(ACell->sample_lock)
#endif
    ACell->U += u;
    ACell->V += v;
    ACell->W += w;
    ACell->Usqr += u*u;
    ACell->Vsqr += v*v;
    ACell->Wsqr += w*w;
#ifdef LOCKING
  UNLOCK(ACell->sample_lock)
#endif

  /* Move them x=x+u */
  Part->x += u;
  Part->y += v;
  Part->z += w;
  ACell = &Cells[CELL(Part->y)][CELL(Part->z)][CELL(Part->x)];
  Part->where = ACell;

  /* boundary interaction if required */
#ifdef LOCKING
  LOCK(ACell->space_lock)
#endif
    space_value = ACell->space;
    if (space_value < 0) {
#ifdef LOCKING
    UNLOCK(ACell->space_lock)
#endif
    boundary(-space_value, Part);
    ACell = Part->where;
#ifdef LOCKING
    LOCK(ACell->space_lock)
#endif
    }

    /* Update cell populations */
    ACell->cell_pop++;
    ACell->cum_cell_pop++;

    /* Do collision pairing */
    space_value = ACell->space;
    if (space_value == 0) {
      ACell->space = pn;

    } else {
      if (space_value > 0) {
        ACell->space = 0;
        if (RANDOM <= ACell->avg_prob)
          collide(&Particles[space_value], Part);

      } else { /* must use boundary space */
        if (-space_value < COND_OFFSET) {   /* PATCH */
          bc_val = Bc_space[-space_value];
          if (bc_val != 0) {
            if (RANDOM <= ACell->avg_prob)
              collide(&Particles[bc_val], Part);
          } else
            Bc_space[-space_value] = pn;
        }
      }
    }
#ifdef LOCKING
  UNLOCK(ACell->space_lock)
#endif
} /* move_single() */

/*
|| MOVE :  Move Molecules through one time-step, doing
|| boundary interactions collisions as required.  A processor
|| be it master or slave is assigned to work on one molecule
|| at a time via a distributed loop.
*/
static void move() {
  int j,
      limit,
      pn,
      step;
  struct particle *Part;

  local_num_collisions = 0;
  local_BC = 0;

  step = number_of_processors*clump;
  limit = clump*(g_num_mol/clump);
  for (j = I*clump; j < limit; j += step)
    for (pn = j; pn < j + clump; pn++)
      move_single(pn);

  if (I == number_of_processors - 1)
    for (pn = limit; pn < g_num_mol; pn++) 
      move_single(pn);

#ifdef PARALLEL
  LOCK(g_collisions_lock)
#endif
    g_num_collisions += local_num_collisions;
    g_total_collisions += local_num_collisions;
    g_BC += local_BC;
#ifdef PARALLEL
  UNLOCK(g_collisions_lock)
  BARRIER(g_end_sync, number_of_processors)
#endif
} /* move() */

/*
|| RESERVOIR MOVE :  Moves mollecules in the reservoir through 
|| a single time-step and performs cyclic boundary conditions.
*/
static void reservoir_move() {
  int pn,
      space_value;
  struct ares *RPart;

  for (pn = I; pn < num_res; pn += number_of_processors) {

    /* Move them and keep in unit cube by employing 
       cyclic boundary conditions */
    RPart = &Ares[pn];
    RPart->xr += RPart->ur + 2.0;
    RPart->xr -= (int) RPart->xr;

    RPart->yr += RPart->vr + 2.0;
    RPart->yr -= (int) RPart->yr;

    RPart->zr += RPart->wr + 2.0;
    RPart->zr -= (int) RPart->zr;
  }
#ifdef PARALLEL
  BARRIER(g_end_sync, number_of_processors)
#endif
} /* reservoir_move() */

/*
|| RESERVOIR COLLIDE:  Performs collision of each reservoir molecule
|| on every time-step.  Uses different pairings for each
|| time-step to keep the whole system mixed.
*/
static void reservoir_collide() {
  float rel[5],
        u_mean,
        v_mean,
        w_mean,
        r_mean,
        s_mean,
        *ps,  /* pointer into sign table */
        temp;
  int *pm,  /* pointer into mix table */
      pn,
      pn1,
      pn2;
  struct ares *RPart1,
              *RPart2;

  for (pn = I; pn < num_res/2; pn += number_of_processors) {
    pn1 = 2*pn; /* even */
    pn2 = (pn1 + g_res_offset) % num_res; /* odd */
    RPart1 = &Ares[pn1];
    RPart2 = &Ares[pn2];

    /* Get mean and relative velocity */
    rel[0]  = (RPart1->ur - RPart2->ur)*0.5;
    u_mean = RPart1->ur - rel[0];

    rel[1]  = (RPart1->vr - RPart2->vr)*0.5;
    v_mean = RPart1->vr - rel[1];

    rel[2]  = (RPart1->wr - RPart2->wr)*0.5;
    w_mean = RPart1->wr - rel[2];

    rel[3]  = (RPart1->rr - RPart2->rr)*0.5;
    r_mean = RPart1->rr - rel[3];

    rel[4]  = (RPart1->sr - RPart2->sr)*0.5;
    s_mean = RPart1->sr - rel[4];

    /* Pick a random collision outcome */
    pm = Mix_table[random() % MIXS_POSSIBLE];
    ps = Sign_table[random() % SIGNS_POSSIBLE];

    /* Do the collision */
    RPart1->ur = u_mean + (temp = (*ps) * rel[*pm]);
    RPart2->ur = u_mean - temp;
    ps++;
    pm++;
    RPart1->vr = v_mean + (temp = (*ps) * rel[*pm]);
    RPart2->vr = v_mean - temp;
    ps++;
    pm++;
    RPart1->wr = w_mean + (temp = (*ps) * rel[*pm]);
    RPart2->wr = w_mean - temp;
    ps++;
    pm++;
    RPart1->rr = r_mean + (temp = (*ps) * rel[*pm]);
    RPart2->rr = r_mean - temp;
    ps++;
    pm++;
    RPart1->sr = s_mean + (temp = (*ps) * rel[*pm]);
    RPart2->sr = s_mean - temp;
  }
#ifdef PARALLEL
  BARRIER(g_end_sync, number_of_processors)
#endif
} /* reservoir_collide() */
  
/*
|| SLAVE ADVANCE SOLUTION :  Advances the solution take_steps 
|| time-steps for the molecules All Boundry conditions and 
|| collisions are performed.  This routine represents the code
|| executed by all slave processors (all but one).  MASTER ADVANCE
|| SOLUTION is the same execpt it also increments take_steps after
|| each steps.
*/
void slave_advance_solution() {
  while (g_take_steps>0) {
    reset();              /* reset the memory map of space */
    move();               /* move molecules one delta t and collide */
    add_to_free_stream(); /* add to free stream */
    reservoir_move();     /* move reservoir molecules */
    reservoir_collide();  /* collide reservoir molecules */
  }
} /* slave_advance_solution() */

/*
|| MASTER ADVANCE SOLUTION :  Advances the solution take_steps 
|| time-steps for the molecules All Boundry conditions and 
|| collisions are performed.  This routine represents the code
|| executed by the host  processor.  MASTER ADVANCE SOLUTION is 
|| the same as SLAVE ADVANCE SOLUTION except it also increments 
|| take_steps after each steps.  Remember explicit barriers are not
|| required as distributed loops in other routines imposed
|| barriers.  Master advance also does small amounts of work that
|| must not be duplicated by slaves.
*/
void master_advance_solution() {
int dum;  
#ifdef DISPLAY
    display_init(&dum); 
#endif
    
  while (g_take_steps>0) {

#ifdef DISPLAY
      do_display();
#endif
      
    /* Reset counters. */
    g_BC = 0;
    g_num_collisions = 0;

    reset();              /* reset map of space */
    g_take_steps--; /* safe to decrement number of steps */
    move();               /* move through delta t */

    /* How many new molecules should we add? */
    g_tot_flt_input += g_num_flow;
    g_tot_input = (long) (g_tot_flt_input);
    g_max_sub = g_tot_input - g_exit_num_flow;
    g_res_offset += 2;

    add_to_free_stream(); /* add new molecules */

    /* Adjust entrance counts */
    if (g_exit_num_flow < g_tot_input)
      g_exit_num_flow = g_tot_input;

    reservoir_move();     /* move reservoir molecules */
    reservoir_collide();  /* collide reservoir molecules */
  } /* while more steps */
} /* master_advance_solution() */


