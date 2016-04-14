package data.extraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public class DataExtracter {
	
	private Random rand;

	public DataExtracter(Random rand) {
		this.rand = rand;
	}
	
	/**
	 * Precondition: numTrainingDatapoints + numTestDatapoints <= numDatapointsInInput
	 * @param input
	 * @param numDatapointsInInput
	 * @param numTrainingDatapoints
	 * @param numTestDatapoints
	 * @return
	 * @throws IOException 
	 */
	public SimulationData extractNumericData(BufferedReader scan, int numDatapointsInInput, int numTrainingDatapoints, int numTestDatapoints) throws IOException {
		List<Integer> trainingIndices = generateRandomIndices(numDatapointsInInput, numTrainingDatapoints);
		List<Integer> testingIndices = generateRandomIndices(numDatapointsInInput, numTestDatapoints);
		
		Collection<String> trainingData = new ArrayList<>(numTrainingDatapoints);
		Collection<String> testData = new ArrayList<>(numTestDatapoints);
		int iTraining = 0;
		int iTesting = 0;
		int currentIndex = 0;
		boolean hasReadAllData = false;
		while (!hasReadAllData) {
			int trainingIndex = iTraining < trainingIndices.size() ? trainingIndices.get(iTraining) : Integer.MAX_VALUE;
			int testingIndex = iTesting < testingIndices.size() ? testingIndices.get(iTesting) : Integer.MAX_VALUE;
			int nextIndex = Math.min(trainingIndex, testingIndex);
			
			while (currentIndex < nextIndex) {
				scan.readLine();
				currentIndex++;
			}
			
			String data = scan.readLine();
			currentIndex++;
			
			if (trainingIndex <= testingIndex) {
				trainingData.add(data);
				iTraining++;
			}
			
			if (testingIndex <= trainingIndex) {
				testData.add(data);
				iTesting++;
			}
			
			hasReadAllData = iTraining == trainingIndices.size() && iTesting == testingIndices.size();
		}
		
		return new SimulationData(trainingData, testData);
	}
	
	private List<Integer> generateRandomIndices(int numDatapointsInInput, int numDatapoints) {
		List<Integer> indices = new ArrayList<Integer>(numDatapoints);
		
		double partitionRange = numDatapointsInInput / numDatapoints;
		for (int i = 0; i < numDatapoints; i++) {
			int smallestIndex = (int) (i * partitionRange);
			indices.add(smallestIndex + rand.nextInt((int) partitionRange));
		}
		
		return indices;
	}
}
