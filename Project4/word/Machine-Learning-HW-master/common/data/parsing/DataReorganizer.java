package data.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataReorganizer {

	/**
	 * 
	 * @param filename
	 * @param categoryIndex negative means from the end
	 * @param converter Converts category labels into binary labels; may be null if the labels are already binary
	 * @throws FileNotFoundException
	 * @throws CategoryNotBinaryException 
	 */
	public void reorganize(String inputFilename, int categoryIndex, BinaryClassConverter converter, String outputFilename) throws FileNotFoundException, CategoryNotBinaryException {
		File inputFile = new File(inputFilename);
		FeatureMapper[] featureMappers = learn(inputFile);
		
		categoryIndex = categoryIndex >= 0 ? categoryIndex : featureMappers.length + categoryIndex;
		if (converter != null) {
			featureMappers[categoryIndex] = new CategoryMapper(converter);
		} else if (!featureMappers[categoryIndex].isBinary()) {
			throw new CategoryNotBinaryException(featureMappers[categoryIndex]);
		}
		
		int sizeSum = 0;
		for (FeatureMapper feature : featureMappers) {
			int size = feature.size();
			sizeSum += size;
			System.out.println(size);
		}
		System.out.println("total: " + sizeSum);
		
		try (
			PrintWriter writer = new PrintWriter(outputFilename);
			Scanner scan = new Scanner(inputFile);
		) {
			while (scan.hasNextLine()) {
				String datapoint = scan.nextLine();
				reorganize(writer, datapoint, featureMappers, categoryIndex);
				writer.println();
			}
		}
	}
	
	/**
	 * 
	 * @param writer
	 * @param datapoint
	 * @param featureMappers
	 * @param categoryIndex The index of the category feature; featureMappers[categoryIndex] must be binary
	 * @throws CategoryNotBinaryException when the category feature cannot recognize a value
	 */
	private void reorganize(PrintWriter writer, String datapoint, FeatureMapper[] featureMappers, int categoryIndex) throws CategoryNotBinaryException {
		List<String> parsedData = parseLine(datapoint);
		try {
			double category = featureMappers[categoryIndex].map(parsedData.get(categoryIndex))[0];
			writer.print(category);
		} catch (IndexOutOfBoundsException ioobe) {
			throw new RuntimeException("Offending line: " + datapoint, ioobe);
		} catch (UnlearnedValueException e) {
			throw new CategoryNotBinaryException(featureMappers[categoryIndex]);
		}
		
		for (int i = 0; i < parsedData.size(); i++) {
			if (i != categoryIndex) {
				try {
					double[] values = featureMappers[i].map(parsedData.get(i));
					for (double value : values) {
						writer.print(',');
						writer.print(value);
					}
				} catch (UnlearnedValueException e) {
					// could also just ignore this datapoint
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private FeatureMapper[] learn(File file) throws FileNotFoundException {
		try (Scanner scan = new Scanner(file)) {
			List<String> features = parseLine(scan.nextLine());
			FeatureLearner[] featureLearners = new FeatureLearner[features.size()];

			for (int i = 0; i < features.size(); i++) {
				String value = features.get(i);
				featureLearners[i] = createAppropriateLearner(value);
				featureLearners[i].learn(value);
			}
			
			while (scan.hasNextLine()) {
				features = parseLine(scan.nextLine());

				for (int i = 0; i < features.size(); i++) {
					String value = features.get(i);
					featureLearners[i].learn(value);
				}
			}
			
			FeatureMapper[] mappers = new FeatureMapper[featureLearners.length];
			for (int i = 0; i < featureLearners.length; i++) {
				mappers[i] = featureLearners[i].createMapper();
			}
			
			return mappers;
		}
	}
	
	private FeatureLearner createAppropriateLearner(String exampleOfFeature) {
		boolean isNumeric;
		
		try {
			Double.parseDouble(exampleOfFeature);
			isNumeric = true;
		} catch (NumberFormatException nfe) {
			isNumeric = false;
		}
		
		if (isNumeric) {
			return new NumericFeatureLearner();
		} else {
			return new LabelFeatureLearner();
		}
	}
	
	private List<String> parseLine(String line) {
		try (Scanner scan = new Scanner(line.replace(" ", ""))) {
			scan.useDelimiter(",");
			List<String> ret = new ArrayList<>();
			
			while(scan.hasNext()) {
				ret.add(scan.next());
			}
			
			return ret;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, CategoryNotBinaryException {
		DataReorganizer reorganizer = new DataReorganizer();
		reorganizer.reorganize("abalone/abalone.data", -1, new SplitBinaryConverter(10), "abalone/reorganized.csv");
	}
}
