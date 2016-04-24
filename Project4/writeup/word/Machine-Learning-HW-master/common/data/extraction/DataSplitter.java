package data.extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class DataSplitter {
	
	public void splitData(String datapath, String trainingOutputPath, String testingOutputPath) throws FileNotFoundException {
		File dataFile = new File(datapath);
		int numDatapoints = countDatapoints(dataFile);
		List<Integer> allIndices = new ArrayList<>();
		
		for (int i = 0; i < numDatapoints; i++) {
			allIndices.add(i);
		}
		
		Collections.shuffle(allIndices);

		int middle = (numDatapoints + 1) / 2;
		List<Integer> trainingIndices = allIndices.subList(0, middle);
		trainingIndices.sort(Integer::compare);
		
		try (
			Scanner inputScanner = new Scanner(dataFile);
			PrintStream trainingPrinter = new PrintStream(trainingOutputPath);
			PrintStream testingPrinter = new PrintStream(testingOutputPath);
		) {
			int iTraining = 0;
			for (int i = 0; i < numDatapoints; i++) {
				String datapoint = inputScanner.nextLine();
				if (iTraining < trainingIndices.size() && i == trainingIndices.get(iTraining)) {
					iTraining++;
					trainingPrinter.println(datapoint);
				} else {
					testingPrinter.println(datapoint);
				}
			}
		}
	}
	
	private int countDatapoints(File dataFile) throws FileNotFoundException {
		try (Scanner scan = new Scanner(dataFile)) {
			int count = 0;
			while (scan.hasNext()) {
				scan.nextLine();
				count++;
			}

			return count;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		DataSplitter splitter = new DataSplitter();
		splitter.splitData("abalone/reorganized.csv", "abalone/training.csv", "abalone/testing.csv");
	}
}
