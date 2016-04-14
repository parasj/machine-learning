package Matlab;
import java.util.Arrays;

import math.Vector;


/**
 * 
 * @author Tom Pepels
 */
public class FastICA {

    private static double[] meanValues;
    private static double[] eigenValues;
    private static double[][] vectorsZeroMean;
    private static double[][] covarianceMatrix;
    private static double[][] E;
    private static double[][] resVectors;
    private static double[][] whiteningMatrix;
    private static double[][] dewhiteningMatrix;
    private static double[][] whitenedVectors;
    private static double[][] B;
    private static int iterationLimit = 10;

    /**
     * Finds a certain number of indepentent components of the input signal using fastica
     * @param input the input signals
     * @param maxIterations maximum number of iterations
     * @param epsilon level of accuracy
     * @param noComponents the number of components this method should return
     * @return the independent components of the signal
     */
    public static double[][] fastICA(double[][] input, int maxIterations, double epsilon, int noComponents) {
        System.out.println("start da whitening");
    	whitening(input);
        int m = Matrix.getNumOfRows(whitenedVectors);
        int n = Matrix.getNumOfColumns(whitenedVectors);
        if (m > noComponents) {
            noComponents = m;
        }
        System.out.println("step1");
        B = Matrix.random(noComponents, m);
        System.out.println("step2");
        B = Matrix.mult(
                powerSymmMatrix(Matrix.square(B), -0.5),
                B);

        for (int k = 1; k < maxIterations; k++) {               // Steps 2 - 4
        	System.out.println("iteration "+k);
            double[][] oldB = Matrix.clone(B);

            for (int c = 0; c < noComponents; c++) {
                //Step 1
                double[] prevW = Matrix.getVecOfRow(oldB, c);
                
                double[] firstPart = new double[m];

                for (int j = 0; j < n; j++) {
                	System.out.println("j "+j);
                    //First part of the equation
                    double one = Vector.dot(prevW, Matrix.getVecOfCol(whitenedVectors, j));
                    one = Math.pow(one, 3);
                    double[] two = Vector.scale(one, Matrix.getVecOfCol(whitenedVectors, j));
                    firstPart = Vector.add(firstPart, two);
                    //

                }

                firstPart = Vector.scale((1.0 / (double) n), firstPart);

                //Second part of the equation
                double[] secondPart = Vector.scale(-3, prevW);
                double[] w = Vector.add(firstPart, secondPart);
                //

                // End of step 2

                // write new vector to the matrix
                for (int j = 0; j < m; ++j) {
                    B[c][j] = w[j];
                }
            }

            // symmetric decorrelation by orthonormalisation
            B = Matrix.mult(
                    powerSymmMatrix(Matrix.square(B), -0.5),
                    B);

            double matrixDelta = deltaMatrices(B, oldB);
            System.out.println("Matrix delta: " + matrixDelta);
            if (matrixDelta < epsilon) {
                System.out.println("Converged after " + k + " iterations.");
                break;
            }
        }
        double[][] sepMatrix = Matrix.mult(B, whiteningMatrix);
        return Matrix.mult(sepMatrix, input);
    }

    /**
     * Finds the major component of the input signal using fastica
     * 
     * @param input the input signals
     * @param maxIterations maximum number of iterations
     * @param epsilon level of accuracy
     * @return the major component
     */
    public static double[] fastICA(double[][] input, int maxIterations, double epsilon) {
        whitening(input);

        int m = Matrix.getNumOfRows(whitenedVectors);
        int n = Matrix.getNumOfColumns(whitenedVectors);

        double[] w = Vector.random(m);                              //Step 1
        w = Vector.normalize(w);

        for (int k = 1; k < maxIterations; k++) {               // Steps 2 - 4

            double[] prevW = Arrays.copyOf(w, w.length);

            double[] firstPart = new double[m];
            for (int j = 0; j < n; j++) {

                //First part of the equation
                double one = Vector.dot(prevW, Matrix.getVecOfCol(whitenedVectors, j));
                one = Math.pow(one, 3);
                double[] two = Vector.scale(one, Matrix.getVecOfCol(whitenedVectors, j));
                firstPart = Vector.add(firstPart, two);
                //
            }

            firstPart = Vector.scale((1.0 / (double) n), firstPart);

            //Second part of the equation
            double[] secondPart = Vector.scale(-3, prevW);
            //
            w = Vector.add(firstPart, secondPart);              // End of step 2
            w = Vector.normalize(w);                            // Step 3

            double dotTest = Math.abs(Vector.dot(w, prevW));
            System.out.println("Dot test: " + dotTest);
            if (dotTest >= (1 - epsilon)) {                     // Step 4
                System.out.println("Converged after " + k + " iterations.");
                break;
            }
        }

        double[][] wTransposed = new double[m][1];

        for (int i = 0; i < m; i++) {
            wTransposed[i][0] = w[i];
        }


        return Matrix.mult(wTransposed, whitenedVectors)[0];
    }

    public static void whitening(double[][] input) {
        // Centering, substract the mean from the signal vectors
    	System.out.println("calcMeanValues");
        meanValues = calcMeanValues(input);
    	System.out.println("addVecToSet");
        vectorsZeroMean = Vector.addVecToSet(input, Vector.scale(-1.0, meanValues));
        // calculate the covariance matrix
    	System.out.println("covarianceMatrix scale");
        covarianceMatrix = Matrix.scale(Matrix.square(vectorsZeroMean), 1.0 / Matrix.getNumOfColumns(input));
        // calculate the eigenvalue decomposition
    	System.out.println("eigenvaluedecompositionsymm constructor");
        EigenValueDecompositionSymm eigenDeco = new EigenValueDecompositionSymm(covarianceMatrix);
    	System.out.println("geteigenvectors");
        E = eigenDeco.getEigenVectors();
        eigenValues = eigenDeco.getEigenValues();
        
        //Eigenvaluefilter
        /* System.out.println("Eigenvalues: ");
        for(int i = 0 ;i < eigenValues.length ; i++)
            System.out.println((i+1)+": "+eigenValues[i]);
        EigenValueFilter ev = new BelowEVFilter(0.01, false);
        ev.passEigenValues(eigenValues, E);
        eigenValues = ev.getEigenValues();
        System.out.println("New Eigenvalues: ");
        for(int i = 0 ;i < eigenValues.length ; i++)
            System.out.println((i+1)+": "+eigenValues[i]);
        E = ev.getEigenVectors(); */
        // Continue with new eigenvalues and vectors
        
        // calculate the resulting vectors
    	System.out.println("resVectors mult");
        resVectors = Matrix.mult(Matrix.transpose(E), vectorsZeroMean);

    	System.out.println("whiteningmatrix mult");
        whiteningMatrix =
                Matrix.mult(
                Matrix.diag(Vector.invVector(Matrix.sqrtVector(eigenValues))),
                Matrix.transpose(E));
    	System.out.println("dewhiteningmatrx mult");
        dewhiteningMatrix =
                Matrix.mult(E, Matrix.diag(Matrix.sqrtVector(eigenValues)));
        // the whitened vectors' correlation matrix equals unit matrix
        // which is demanded to perform the FastICA algorithm
    	System.out.println("whitenedvectors mult");
        whitenedVectors =
                Matrix.mult(whiteningMatrix, vectorsZeroMean);
    }

    /**
     * Calculates the mean vector from a set of vectors.
     * @param inVectors the set of vectors
     * @return the resulting mean vector
     */
    private static double[] calcMeanValues(double[][] inVectors) {
        int m = Matrix.getNumOfRows(inVectors);
        int n = Matrix.getNumOfColumns(inVectors);
        double[] mValues = Vector.newVector(m);
        for (int i = 0; i < m; ++i) {
            mValues[i] = 0.0;
            for (int j = 0; j < n; ++j) {
                mValues[i] += inVectors[i][j];
            }
            mValues[i] /= n;
        }
        return (mValues);
    }

    /**
     * Calculates a difference measure of two matrices
     * relative to their size.
     * @param mat1 the first matrix
     * @param mat2 the second matrix
     * @return the difference measure
     */
    private static double deltaMatrices(
            double[][] mat1,
            double[][] mat2) {
        double[][] test = Matrix.sub(mat1, mat2);
        double delta = 0.0;
        int m = Matrix.getNumOfRows(mat1);
        int n = Matrix.getNumOfColumns(mat1);
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                delta += Math.abs(test[i][j]);
            }
        }
        return (delta / (m * n));
    }

    /**
     * Calculates the power of a symmetric matrix.
     * @param inMatrix the symmetric matrix
     * @param power the power
     * @return the resulting matrix
     */
    private static double[][] powerSymmMatrix(
            double[][] inMatrix,
            double power) {
        EigenValueDecompositionSymm eigenDeco =
                new EigenValueDecompositionSymm(inMatrix);
        int m = Matrix.getNumOfRows(inMatrix);
        double[][] eigenVectors = eigenDeco.getEigenVectors();
        double[] eigenValues = eigenDeco.getEigenValues();
        for (int i = 0; i < m; ++i) {
            eigenValues[i] = Math.pow(eigenValues[i], power);
        }
        return (Matrix.mult(
                Matrix.mult(eigenVectors, Matrix.diag(eigenValues)),
                Matrix.transpose(eigenVectors)));
    }
}