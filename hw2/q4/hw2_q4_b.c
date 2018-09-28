#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

double MonteCarlo(int s)
{
	double inCircle = 0;
	double outCircle = 0;
	int R = 5000;

#pragma omp parallel num_threads(10) shared(inCircle, outCircle, R)
	{
		int i;
		for (i = omp_get_thread_num(); i < s; i += omp_get_num_threads()) {
			/* rand() returns a num between 0 - RAND_MAX. We subtract RAND_MAX/2 to get an even split of pos and neg nums */
			int x = (rand()%(2*R)) - R;
			int y = (rand()%(2*R)) - R;

			if (((x*x) + (y*y)) < (R*R)) {
#pragma omp critical
				{
					inCircle = inCircle + 1;

				}
			}
			else {
#pragma omp critical
				{
					outCircle = outCircle + 1;

				}
			}

		}

	}

	return ((inCircle / (inCircle + outCircle)) * 4);

}

void main()
{
	double pi;
	pi = MonteCarlo(100000000);
	printf("Value of pi is: %lf\n", pi);
}




