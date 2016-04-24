package data.extraction;
import java.io.PrintWriter;
import java.util.Collection;


public class SimulationData {
	
	private Collection<String> trainingData;
	private Collection<String> testData;

	public SimulationData(Collection<String> trainingData, Collection<String> testData) {
		this.trainingData = trainingData;
		this.testData = testData;
	}
	
	public void writeTrainingData(PrintWriter writer) {
		writeData(trainingData, writer);
	}
	
	public void writeTestData(PrintWriter writer) {
		writeData(testData, writer);
	}
	
	private void writeData(Collection<String> data, PrintWriter writer) {
		for (String datapoint : data) {
			writer.println(datapoint);
		}
	}
}
