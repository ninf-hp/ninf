#include "common.h"
#include "framebuffer.h"

#define DISPLAY_TIME_STEP 1

int display_time_step;
#define DISPLAY_X_CELL	20
#define DISPLAY_Y_CELL  20
#define DISPLAY_X_BASE	100
#define DISPLAY_Y_BASE	600

#define BACKGROUND_COLOR 0
#define GRID_COLOR 0xFF
/* #define PART_COLOR (-1)*/
#define PART_COLOR (0xa0)
#define PART_TRACE_COLOR (0xc0)

/* 0x00, 0x20, 0x40, 0x60, 0x80, 0xa0, 0xc0, 0xe0 */
#define SOLID_CELL_COLOR (0x60)
#define EXIT_CELL_COLOR (0x40)
#define ENTRANCE_CELL_COLOR (0xc0)
#define UNKNOWN_CELL_COLOR (0)

#define PERSPECTIVE_PROJECTION

display_init()
{
    printf("cell x=%d, y=%d, z=%d\n",num_xcell,num_ycell,num_zcell);
    display_time_step = -1;
    fb_init(0);
/*    display_boundary_cond(); 
    do_display();*/
}

#define GRID_SIZE  25.0

/*
display_boundary_cond()
{
    int x,y,z;
    struct acell *cell;
    int color;
    int space_value;
    
#ifdef not
    for(z = 0; z < num_zcell; z++)
      for(y = 0; y < num_ycell; y++)
	for(x = 0; x < num_xcell; x++){
	    fb3d_cube(x*GRID_SIZE,y*GRID_SIZE,z*GRID_SIZE,
		      GRID_SIZE,GRID_COLOR);
	}
#endif
    for(z = (num_zcell -3); z >= 2 ; z--)
      for(y = 0; y < num_ycell; y++)
	for(x = 0; x < num_xcell; x++){
	    cell = &Cells[y][z][x];
	    if(cell->space >= 0) continue;
	    space_value = -cell->space;
	    if(space_value < COND_OFFSET)
	      color = SOLID_CELL_COLOR;
	    else if(space_value == (COND_OFFSET +1))
	      color = EXIT_CELL_COLOR;
	    else if(space_value == (COND_OFFSET + 2))
	      color = ENTRANCE_CELL_COLOR;
	    else color = UNKNOWN_CELL_COLOR;

	    fb3d_cube(x*GRID_SIZE+1,y*GRID_SIZE+1,z*GRID_SIZE+1,
		      GRID_SIZE-1,color);
	}
    fb_flush();
}
*/

display_cell_grid()
{
    int x,x_min,x_max;
    int y,y_min,y_max;

    x_min = DISPLAY_X_BASE;
    x_max = DISPLAY_X_BASE+num_xcell*DISPLAY_X_CELL;
    y_max = DISPLAY_Y_BASE;
    y_min = DISPLAY_Y_BASE-num_ycell*DISPLAY_Y_CELL;
    fb_clear(x_min,y_min,x_max+1,y_max+1,BACKGROUND_COLOR);
    for(x = 0; x <= num_xcell; x++){
	fb_line(x_min+x*DISPLAY_X_CELL,y_min,
		x_min+x*DISPLAY_X_CELL,y_max,GRID_COLOR);
    }
    for(y = 0; y <= num_ycell; y++){
	fb_line(x_min,y_min+y*DISPLAY_Y_CELL,
		x_max,y_min+y*DISPLAY_Y_CELL,GRID_COLOR);
    }
}


l_do_display(int * g_num, struct s_particle * s_Particles)
{
    int pn;
    struct s_particle *Part;
    int x,y,z;
    float v;

    g_num_mol = *g_num;

    display_time_step++;
    if((display_time_step & DISPLAY_TIME_STEP) != 0) return;

    printf("time_step = %d, g_num_mol = %d\n",
	   display_time_step,g_num_mol);
    display_cell_grid();
    for(pn = 0; pn < g_num_mol; pn++){
	Part = &s_Particles[pn];
	/* printf("%d=(%f,%f,%f)\n",pn,Part->x,Part->y,Part->z); */
	x = (int)(Part->x * DISPLAY_X_CELL);
	y = (int)(Part->y * DISPLAY_Y_CELL);
	v = Part->v*Part->v+Part->u*Part->u+Part->w*Part->w;
	if(v > 1.0e-2)
	  fb_drawpoint(x+DISPLAY_X_BASE,DISPLAY_Y_BASE-y,PART_TRACE_COLOR);
	else
	  fb_drawpoint(x+DISPLAY_X_BASE,DISPLAY_Y_BASE-y,PART_COLOR);
    }

    /* special particle trace */
    Part = &s_Particles[0];
    x = (int)(Part->x * DISPLAY_X_CELL);
    y = (int)(Part->y * DISPLAY_Y_CELL);
    fb_circle(x+DISPLAY_X_BASE,DISPLAY_Y_BASE-y,2,PART_TRACE_COLOR);
    
    /* fb_flush(); */
    { 
	int x_min,x_max,y_max,y_min;
	x_min = DISPLAY_X_BASE;
	x_max = DISPLAY_X_BASE+num_xcell*DISPLAY_X_CELL;
	y_max = DISPLAY_Y_BASE;
	y_min = DISPLAY_Y_BASE-num_ycell*DISPLAY_Y_CELL;
	fb_rflush(x_min, y_min, x_max-x_min+1, y_max-y_min+1);
    }
}

fb_clear(x1,y1,x2,y2,color)
{
    int x,y;

    for(x = x1; x < x2; x++)
      for(y = y1; y < y2; y++)
	fb_drawpoint(x,y,color);
}

/* 
 * 3D display lib
 */

#ifdef PERSPECTIVE_PROJECTION
#define VIEW_X (500.0)
#define VIEW_Y (500.0)
#define VIEW_Z (-500.0)
#define VIEW_D (500.0)
#define WINDOW_VIEW_X 0
#define WINDOW_VIEW_Y 200
#define MAX_X 1024
#define MAX_Y 768

fb3d_line(x1,y1,z1,x2,y2,z2,color)
     float x1,y1,z1,x2,y2,z2;
     int color;
{
    int x11,y11,x22,y22;

    x11 = (x1 - VIEW_X)*VIEW_D/(z1-VIEW_Z);
    y11 = (y1 - VIEW_Y)*VIEW_D/(z1-VIEW_Z);
    x22 = (x2 - VIEW_X)*VIEW_D/(z2-VIEW_Z);
    y22 = (y2 - VIEW_Y)*VIEW_D/(z2-VIEW_Z);

    fb_line(x11+WINDOW_VIEW_X,WINDOW_VIEW_Y-y11,
	    x22+WINDOW_VIEW_X,WINDOW_VIEW_Y-y22,color);
}
#endif

#ifdef  PARALLEL_PROJECTION
#define WINDOW_VIEW_X 400
#define WINDOW_VIEW_Y 700
#define Z_DEPTH_FACTOR 0.4

fb3d_line(x1,y1,z1,x2,y2,z2,color)
     float x1,y1,z1,x2,y2,z2;
     int color;
{
    int x11,y11,x22,y22;

    x11 = x1 + z1*Z_DEPTH_FACTOR;
    y11 = y1 + z1*Z_DEPTH_FACTOR;
    x22 = x2 + z2*Z_DEPTH_FACTOR;
    y22 = y2 + z2*Z_DEPTH_FACTOR;

    fb_line(x11+WINDOW_VIEW_X,WINDOW_VIEW_Y-y11,
	    x22+WINDOW_VIEW_X,WINDOW_VIEW_Y-y22,color);
}
#endif


fb3d_cube(x,y,z,d,color)
     float x,y,z,d;
{
    fb3d_line(x,y,z,x+d,y,z,color);
    fb3d_line(x+d,y,z,x+d,y+d,z,color);
    fb3d_line(x+d,y+d,z,x,y+d,z,color);
    fb3d_line(x,y+d,z,x,y,z,color);

    fb3d_line(x,y,z,x,y,z+d,color);
    fb3d_line(x+d,y,z,x+d,y,z+d,color);
    fb3d_line(x+d,y+d,z,x+d,y+d,z+d,color);
    fb3d_line(x,y+d,z,x,y+d,z+d,color);

    fb3d_line(x,y,z+d,x+d,y,z+d,color);
    fb3d_line(x+d,y,z+d,x+d,y+d,z+d,color);
    fb3d_line(x+d,y+d,z+d,x,y+d,z+d,color);
    fb3d_line(x,y+d,z+d,x,y,z+d,color);
}
