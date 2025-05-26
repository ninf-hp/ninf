/*
 *   LINPACK test program (Fortran)
 *
 *   % linpackf-local  <matrix size> <log file name>
 *   % linpackf-remote <matrix size> <log file name>
 *   % linpackf-ninf   <matrix size> <log file name>
 */

#ifdef NINF
#define linpack(a, lda, n, ipiv, b, info) \
Ninf_call("linpackf",a, lda, n, ipiv, b, &info)  
#endif

#ifdef REMOTE
#define linpack(a, lda, n, ipiv, b, info) \
Ninf_local_exec("/usr/users/atakefu/linpack/sol_stub/_stub_linpackf",a, lda, n, ipiv, b, &info)
#endif

#ifdef LOCAL
#define linpack(a, lda, n, ipiv, b, info) \
linpack_(a, &lda, &n, ipiv, b, &info)
#endif

#define   TIMES   10
#define   FILE_OUT

#ifdef   FILE_OUT
#define   FILE_NAME   "/usr/users/atakefu/linpack/client/RES/%s.log"
#endif


#include <stdio.h>
#include <math.h>
#include <sys/time.h>
#include <malloc.h>

int    gettimeofday( struct timeval*, void* );
void   manage();
double laptime();
double ops;

main(argc, argv)
int  argc;
char *argv[];
{
    double *a, *b;
    int    n,lda,info;  
    int    *ipiv;
    double norma;
    long   t;
    char   *ctime;
    struct timeval st, fn, md;
    double mytime[TIMES][4];   /* 0 : dgefa
				  1 : dgesl
				  2 : total
				  3 : FLOPS */
    double res[3];   /* 0 : max of FLOPS
			1 : average
			2 : standard */
    double time0;    /* empty time */
    int    i,j;

#ifdef   FILE_OUT
    FILE * fp;
    char   out_file_name[50];

    if(argc < 3){
      printf("linpackf-<...> <matrix size> <log file name>\n");
      exit(0);
    }
#else
    if(argc < 2){
      printf("linpackf-<...> <matrix size> <log file name>\n");
      exit(0);
    }
#endif

    n = atoi(argv[1]);
    lda = n;
    ops = (2.0e0*((double)n*n*n))/3.0 + 2.0*((double)n*n);

#ifdef   FILE_OUT
    sprintf(out_file_name, FILE_NAME, argv[2]);

    /* file open */
    if((fp = fopen(out_file_name , "a+")) == NULL){
      printf("can't open %s\n", out_file_name);
      exit(-1);
    }
#endif

    time(&t);
    ctime = asctime(localtime(&t));

#ifdef   FILE_OUT
    fprintf(fp, "Ninf LINPACK     %s",ctime);
#ifdef NINF
    fprintf(fp, "Ninf Execution..\n");
#elif REMOTE
    fprintf(fp, "Remote Execution..\n");
#else
    fprintf(fp, "Local Execution..\n");
#endif
    fprintf(fp, "Matrix size: %d by %d\n",n,n);
    fclose (fp);
#else
    printf("Ninf LINPACK     %s",ctime);
#ifdef NINF
    printf("Ninf Execution..\n");
#elif   REMOTE
    printf("Remote Execution..\n");
#else
    printf("Local Execution..\n");
#endif
    printf("Matrix size: %d by %d\n",n,n);
#endif

    a = (double*) malloc(sizeof(double) * lda * n);
    b = (double*) malloc(sizeof(double) * lda );
    ipiv = (int*) malloc(sizeof(int) * lda );	

    gettimeofday(&st, 0);
    gettimeofday(&fn, 0);
    time0 = laptime(st, fn);

    /* start linpack calculation */
    for (i = 0 ; i < TIMES ; i++){
      matgen(a, lda, n, b, &norma);

      gettimeofday( &st, 0 );
      linpack(a, lda, n, ipiv, b, info); 
      gettimeofday( &fn, 0 );

      mytime[i][2] = laptime( st, fn ) - time0;
      sleep (5);
    }

    free(a);
    free(b);
    free(ipiv);

    manage(mytime, res);

#ifdef   FILE_OUT
    /* file open */
    if((fp = fopen(out_file_name , "a+")) == NULL){
      printf("can't open %s\n", out_file_name);
      exit(-1);
    }

/*    fprintf(fp, "      dgefa      dgesl      total       kflops\n");*/
    fprintf(fp, "      total       kflops\n");
    for(i = 0 ; i < TIMES ; i++)
/*      fprintf(fp, "%11.2f%11.2f%11.2f%11.0f\n",
	      (double)mytime[i][0],
	      (double)mytime[i][1],
	      (double)mytime[i][2],
	      (double)mytime[i][3]); */
      fprintf(fp, "%11.2f%11.0f%\n",
	      (double)mytime[i][2],
	      (double)mytime[i][3]);
    fprintf(fp, "MAX performance : %11.0f\n", res[0]);
    fprintf(fp, "average         : %11.2f\n", res[1]);
    fprintf(fp, "standard        : %11.2f\n\n", res[2]);

    fclose (fp);
#else
/*    printf("      dgefa      dgesl      total       kflops\n");*/
    printf("      total       kflops\n");
    for(i = 0 ; i < TIMES ; i++)
/*      printf("%11.2f%11.2f%11.2f%11.0f\n",
	      (double)mytime[i][0],
	      (double)mytime[i][1],
	      (double)mytime[i][2],
	      (double)mytime[i][3]); */
      printf("%11.2f%11.0f%\n",
	      (double)mytime[i][2],
	      (double)mytime[i][3]);
    printf("MAX performance : %11.0f\n", res[0]);
    printf("average         : %11.2f\n", res[1]);
    printf("standard        : %11.2f\n\n", res[2]);
#endif

exit(0);
}
     

/*
 *  functions
 */
void manage(mytime, res)
double mytime[TIMES][4];   /* INOUT */
double res[3];             /* OUT */
{
  double sum = 0.0;
  int    i, j;

  res[0] = 0.0;

  for(i = 0 ; i < TIMES ; i++){
    mytime[i][3] = ops / (1.0e3 * mytime[i][2]);
    sum += mytime[i][3];
    if(res[0] < mytime[i][3])
      res[0] = mytime[i][3];   /* max */
  }
  res[1] = sum / TIMES;   /* average */

  sum = 0.0;
  for(i = 0 ; i < TIMES ; i++)
    sum += (mytime[i][3] - res[1]) * (mytime[i][3] - res[1]);
  res[2] = sqrt(sum / TIMES);   /* standard */
}


double laptime( st, fn )
struct timeval st;
struct timeval fn;
{
    return fn.tv_sec - st.tv_sec + (fn.tv_usec - st.tv_usec) * 1.0e-06;
}


matgen(a,lda,n,b,norma)
double a[],b[],*norma;
int lda, n;
{
	int init, i, j;

	init = 1325;
	*norma = 0.0;
	for (j = 0; j < n; j++) {
		for (i = 0; i < n; i++) {
			init = 3125*init % 65536;
			a[lda*j+i] = (init - 32768.0)/16384.0;
			*norma = (a[lda*j+i] > *norma) ? a[lda*j+i] : *norma;
		}
	}
	for (i = 0; i < n; i++) {
          b[i] = 0.0;
	}
	for (j = 0; j < n; j++) {
		for (i = 0; i < n; i++) {
			b[i] = b[i] + a[lda*j+i];
		}
	}
}

