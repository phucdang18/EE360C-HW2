#define _CRT_SECURE_NO_DEPRECATE
#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>

double ** parseMatrix(int *row, int *col, FILE* fp) {
	char c[2];
	c[1] = '\0';
	char* num = NULL;
	int i, j;

	while ((c[0] = fgetc(fp)) == ' ') {};
	num = (char*)calloc(1, 100 * sizeof(char));
	while (c[0] != ' ' && c[0] != EOF && c[0] != '\n' && c[0] != NULL) {
		strcat(num, c);
		c[0] = fgetc(fp);
	}
	*row = atoi(num);
	free(num);

	while ((c[0] = fgetc(fp)) == ' ') {};
	num = (char*)calloc(1, 100 * sizeof(char));
	while (c[0] != ' ' && c[0] != EOF && c[0] != '\n' && c[0] != NULL) {
		strcat(num, c);
		c[0] = fgetc(fp);
	}
	*col = atoi(num);
	free(num);

	double ** matrix = (double **)calloc(1, *row * sizeof(double *));
	for (i = 0; i < *row; i++) {
		matrix[i] = (double *)calloc(1, *col * sizeof(double));
	}

	i = 0;
	j = 0;

	for (i = 0; i < *row; i++) {
		for (j = 0; j < *col;) {
			c[0] = fgetc(fp);
			if (c[0] != ' ' && c[0] != EOF && c[0] != '\n' && c[0] != NULL) {
				num = (char*)calloc(1, 100 * sizeof(char));
				while (c[0] != ' ' && c[0] != EOF && c[0] != '\n') {
					strcat(num, c);
					c[0] = fgetc(fp);
				}
				matrix[i][j] = strtod(num, NULL);
				free(num);
				j++;
			}

		}
	}

	return matrix;
}


void MatrixMult(char file1[], char file2[], int T)
{
	FILE* fp;
	int *row1, *col1, *row2, *col2;
	int size = 0, i, j;
	double **m1 = NULL;
	double **m2 = NULL;
	double ** result = NULL;
	char *line = NULL;

	row1 = (int*) calloc(1, sizeof(int));
	col1 = (int*) calloc(1, sizeof(int));
	row2 = (int*) calloc(1, sizeof(int));
	col2 = (int*) calloc(1, sizeof(int));

	fp = fopen(file1, "r");
	m1 = parseMatrix(row1, col1, fp);
	fclose(fp);
	fp = fopen(file2, "r");
	m2 = parseMatrix(row2, col2, fp);

	result = (double **)calloc(1, *row1 * sizeof(double *));
	for (i = 0; i < *row1; i++) {
		result[i] = (double *)calloc(1, *col2 * sizeof(double));
	}


	double st = omp_get_wtime();
#pragma omp parallel num_threads(T)
	{
		int i, j, k;
		double sum;
		for (i = omp_get_thread_num(); i < *row1; i += T) {
			for (j = 0; j < *col2; j++) {
				sum = 0;
				for (k = 0; k < *row2; k++) {
					sum += m1[i][k] * m2[k][j];
				}
				result[i][j] = sum;
			}
		}
	}

	double en = omp_get_wtime();
	//printf("Time %lf\n", en - st);
	//printf("Thread %d\n", T);

	printf("%d %d\n", *row1, *col2);
	for (i = 0; i < *row1; i++) {
		for (j = 0; j < *col2; j++) {
			printf("%lf", result[i][j]);
			if (j + 1 < *col2) {
				printf(" ");
			}
		}
		if (i + 1 < *row1) {
			printf("\n");
		}
	}

	//while (1) {}


}

void main(int argc, char *argv[])
{
	char *file1, *file2;
	file1 = argv[1];
	file2 = argv[2];
	int T = atoi(argv[3]);
	MatrixMult(file1, file2, T);
}