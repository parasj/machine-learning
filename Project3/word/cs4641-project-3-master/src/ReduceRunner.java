import java.io.IOException;

public class ReduceRunner {
	public static void main(String[] args) throws Exception {
		System.out.println(new java.io.File(".").getCanonicalPath());
		DataSetWorker worker = new DataSetWorker("car", "/Users/Jonathan/Documents/College Work/Senior/CS 4641/Assignment 3/data/car.arff");
		worker.reduce();
	}
}
