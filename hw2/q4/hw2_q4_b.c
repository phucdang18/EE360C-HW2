#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

double MonteCarlo(int s)
{
	double inCircle = 0;
	double outCircle = 0;
	int count = 0;
	int R = 32767;

#pragma omp parallel num_threads(10) shared(inCircle, outCircle, R, count)
	{
		int i;
		for (i = omp_get_thread_num(); i < s; i += 10) {
			int x = (rand() % (2*R)) - R;
			int y = (rand() % (2*R)) - R;

			if (((x*x) + (y*y)) < (R*R)) {
#pragma omp critical
				{
					inCircle = inCircle + 1;
					count++;

				}
			}
			else {
#pragma omp critical
				{
					outCircle = outCircle + 1;
					count++;

				}
			}

		}

	}

	return (inCircle / outCircle);

}

void main()
{
double pi;
//pi=MonteCarlo(100000000);
pi = MonteCarlo(10000000000000);
printf("Value of pi is: %lf\n",pi);
}



