/* modified version for xm_fb.c on sun4 */
#include <stdio.h>

typedef unsigned int uns;

typedef struct emx_word {
    int d;	/* data */
    uns t:6; 	/* tag */
} word_t; 

