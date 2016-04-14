import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dist.AbstractConditionalDistribution;

import func.FunctionApproximater;
import func.KMeansClusterer;
import func.NeuralNetworkClassifier;
import shared.DataSet;
import shared.DataSetWriter;
import shared.filt.IndependentComponentAnalysis;
import shared.filt.InsignificantComponentAnalysis;
import shared.filt.LabelSplitFilter;
import shared.filt.PrincipalComponentAnalysis;
import shared.filt.RandomizedProjectionFilter;
import shared.filt.ReversibleFilter;
import shared.reader.ArffDataSetReader;
import shared.DataSet;
import shared.filt.LabelSelectFilter;
import shared.reader.ArffDataSetReader;
import shared.reader.CSVDataSetReader;
import shared.reader.CSVDataSetReader;

public class ProjectRunner {
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		String reducedDir = "data/reduced/";
		String clustReducedDir = "data/clustered-reduced/";
		String[] reduced = {"_pca", "_ica", "_insig", "_rp"};
		String[] clustered = {"_kmeans", "_emax"};
		String[] setNames = {"abalone", "hd"};
		int iterations = 8000;
		
		// numbers for the ThreadPoolExecutor
		int minThreads = 1;
		int maxThreads = 4;
		long keepAlive = 10;
		
		LinkedBlockingQueue<Runnable> q = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(minThreads, maxThreads, keepAlive, TimeUnit.SECONDS, q);
		int numTasks = 0;
		for (String setName : setNames) {
			for (String reducer : reduced) {
				try {
					// pull in the reduced data set
					DataSet d = (new CSVDataSetReader(reducedDir+setName+reducer+".csv")).read();
					//WEKA's clustering adds the cluster num as the final attribute
					// so the second to last attribute is now the label
					LabelSelectFilter lsl = new LabelSelectFilter(d.get(0).size()-2);
					lsl.filter(d);
					tpe.submit(new NeuralNetTrainer(
							iterations, 
							d, 
							reducer, 
							setName));
					numTasks++;
					// pull in the reduced->clustered data sets
					for (String clusterer : clustered) {
						DataSet a = (new ArffDataSetReader(clustReducedDir+setName+clusterer+reducer+".arff")).read();
						lsl.filter(a);
						tpe.submit(new NeuralNetTrainer(
								iterations, 
								a,
								clusterer+reducer, 
								setName));
						numTasks++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		int i = 0;
		while (tpe.getCompletedTaskCount() < numTasks) {
			//System.out.println("Fuck java");
			i++;
		}
		tpe.shutdown();
		while (!tpe.awaitTermination(60, TimeUnit.SECONDS)) {
			  System.out.println(".");
		}
		System.out.println("Done\n========");

		System.out.println(i);

		
	}
	
}
