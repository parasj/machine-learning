/**
 * 
 */
package optproblems.nn;

import java.util.ArrayList;

import optproblems.Statistics;

/**
 * MyNNTest.java
 * 
 * @author Karthik
 *
 */
public class RHCNeuralNetwork {

	static int NUM_OF_SIMULATIONS = 100;
	
	/**
	 * main.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		ArrayList<Double> times = new ArrayList<Double>();
		ArrayList<Double> maxValues = new ArrayList<Double>();
		
		BreastCancerNN nn = new BreastCancerNN();
		BreastCancerNN.TESTS = 30;
		int iterations = 100;
		
		for (int sims = 0; sims <= NUM_OF_SIMULATIONS - 1; sims++) {
			System.out.println("Simulation " + sims + "...");
			
			double minNeighborhood = -1.5;
			double maxNeighborhood = 1.5;
			
			// Keep track of the current maximum
			double[] curMaxX = randomWeights(minNeighborhood, maxNeighborhood);
			double curMaxEval = nn.fitness(curMaxX);

			// Start timer
			long start = System.nanoTime();

			// Convergence rate
			//int width = maxNeighborhood - minNeighborhood;
			//double convergenceRate = 0.99;

			for (int i = 0; i <= iterations - 1; i++) {
				double[] nextX = randomWeights(minNeighborhood, maxNeighborhood);
				double nextEval = nn.fitness(curMaxX);
				if (nextEval > curMaxEval) {
					curMaxX = nextX;
					curMaxEval = nextEval;
					//System.out.println("f(" + curMaxX + ") = " + curMaxEval
					//		+ "; search space: [" + (curMaxX - width / 2)
					//		+ ", " + (curMaxX + width / 2) + "]");
				}
				
				// Shrink the neighborhood
				//minNeighborhood = curMaxX - width / 2;
				//maxNeighborhood = curMaxX + width / 2;

				// Shrink the width
				//width = (int) (width * convergenceRate);
			}

			// End timer
			long end = System.nanoTime();

			// Print out results
			//System.out.println("----");
			//System.out.println("f(" + curMaxX + ") = " + curMaxEval);
			//System.out.println("Time taken: " + (end - start) / Math.pow(10, 9) + " seconds.");
			
			times.add((end - start) / Math.pow(10, 9));
			maxValues.add(curMaxEval * 1.0);
		}
		Statistics.printStats(times, maxValues);
		Statistics.histogramPrint(maxValues, 10, true);
	}
	
	/**
	 * randomInRange
	 * 
	 * @param minWeight
	 * @param maxWeight
	 * @return
	 */
	private static double[] randomWeights(double minWeight, double maxWeight) {
		double[] ret = new double[992];
		for (int i = 0; i <= ret.length - 1; i++)
			ret[i] = minWeight + Math.random() * (maxWeight - minWeight);
		return ret;
	}
}
