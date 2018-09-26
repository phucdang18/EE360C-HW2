#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

double MonteCarlo(int s)
{
	double inCircle = 0;
	double outCircle = 0;
	int count = 0;
	int R = RAND_MAX/2;

#pragma omp parallel num_threads(10) shared(inCircle, outCircle, R, count)
	{
		int i;
		for (i = omp_get_thread_num(); i < s; i += 10) {
			/* rand() returns a num between 0 - RAND_MAX. We subtract RAND_MAX/2 to get an even split of pos and neg nums */
			int x = rand() - R;
			int y = rand() - R;

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
pi=MonteCarlo(100000000);
printf("Value of pi is: %lf\n",pi);
}



