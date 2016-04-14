/*import java.io.IOException;
import java.util.ArrayList;*/
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*import dist.AbstractConditionalDistribution;

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
import shared.reader.ArffDataSetReader;*/
import shared.DataSet;
import shared.DataSetWriter;
import shared.filt.IndependentComponentAnalysis;
import shared.filt.InsignificantComponentAnalysis;
import shared.filt.LabelSelectFilter;
import shared.filt.LabelSplitFilter;
import shared.filt.PrincipalComponentAnalysis;
import shared.filt.RandomizedProjectionFilter;
import shared.filt.ReversibleFilter;
import shared.reader.ArffDataSetReader;
//import shared.reader.CSVDataSetReader;
import shared.reader.CSVDataSetReader;

public class ProjectRunner {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String reducedDir = "data/reduced/";
		//String clustReducedDir = "data/clustered-reduced/";
		String[] reduced = {"_pca", "_ica", "_insig", "_rp"};
		String[] clustered = {"_kmeans", "_emax"};
		String[] setNames = {"hd"};
		int iterations = 4000;
		
		// numbers for the ThreadPoolExecutor
		int minThreads = 1;
		int maxThreads = 4;
		long keepAlive = 10;
		double epsilon = 0.5;
		
		LinkedBlockingQueue<Runnable> q = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(minThreads, maxThreads, keepAlive, TimeUnit.SECONDS, q);
		
		// run the clean sets for comparison
		DataSet clean = (new CSVDataSetReader("data/abalone.csv")).read();
		//WEKA's clustering adds the cluster num as the final attribute
		// so the second to last attribute is now the label
		LabelSplitFilter filt = new LabelSplitFilter();
		filt.filter(clean);
		for (int i = 1; i < 6; i++) {
			DataSetWorker dsw = new DataSetWorker(clean, "abalone", i);
			dsw.reduce();
		}
		/*tpe.submit(new NeuralNetTrainer(
				iterations, 
				clean, 
				"", 
				"abalone", epsilon));
		
		DataSet hdclean = (new ArffDataSetReader("data/heart_disease.arff")).read();
		filt.filter(hdclean);
		tpe.submit(new NeuralNetTrainer(
				iterations, 
				hdclean, 
				"", 
				"hd", epsilon));
		
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
							setName,
							epsilon));
					// pull in the reduced->clustered data sets
					for (String clusterer : clustered) {
						DataSet a = (new ArffDataSetReader(clustReducedDir+setName+clusterer+reducer+".arff")).read();
						lsl = new LabelSelectFilter(a.get(0).size()-2);
						lsl.filter(a);
						tpe.submit(new NeuralNetTrainer(
								iterations, 
								a,
								clusterer+reducer, 
								setName,
								epsilon));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}*/
		tpe.shutdown();
		while (!tpe.awaitTermination(60, TimeUnit.MINUTES)) {
			  System.out.println(".");
		}
		System.out.println("Done\n========");
		
	}
	
	/**
	 * Not in use any more, but kept around for the filtering code as a reference
	 * @author chronon
	 *
	 */
	private static class DataSetWorker {
		// static variables
		private static final String reducedDir = "data/reduced/";
		private static final String clustReducedDir = "data/creduced/";
		private static final String[] reduced = {"_pca.csv", "_ica.csv", "_insig.csv", "_rp.csv"};
		private static final String[] clustered = {"_kmeans", "_emax"};
		
		private DataSet clean;

		private String setName;
		private int toKeep = 5;
		
		
		public DataSetWorker(DataSet clean, String name, int keep) {
			this.clean = clean;
			this.setName = name;
			this.toKeep = keep;
		}
		
		public void reduce() {
	    	// Add all the filters we need
			ArrayList<Tuple<ReversibleFilter,String>> filters = new ArrayList<Tuple<ReversibleFilter,String>>();
	        filters.add(new Tuple<ReversibleFilter, String>(new PrincipalComponentAnalysis(clean, toKeep), "_pca"+toKeep+".csv"));
	        filters.add(new Tuple<ReversibleFilter, String>(new IndependentComponentAnalysis(clean, toKeep), "_ica"+toKeep+".csv"));
	        filters.add(new Tuple<ReversibleFilter, String>(new RandomizedProjectionFilter(toKeep, clean.get(0).size()), "_rp"+toKeep+".csv"));
	        filters.add(new Tuple<ReversibleFilter, String>(new InsignificantComponentAnalysis(clean, toKeep), "_insig"+toKeep+".csv"));
			for (Tuple<ReversibleFilter, String> tup : filters) {
				ReversibleFilter filter = tup.fst();
				String ext = tup.snd();
				
				filter.filter(clean);
				DataSetWriter wr = new DataSetWriter(clean, reducedDir+setName+ext);
				try {
					wr.write();
				} catch (IOException e) {
					e.printStackTrace();
				}
				filter.reverse(clean);
			}
		}

		
	}
	
	public static class Tuple<X, Y> { 
		  private final X fst; 
		  private final Y snd; 
		  public Tuple(X x, Y y) { 
		    this.fst = x; 
		    this.snd = y; 
		  } 
		  
		  public X fst() {
			  return this.fst;
		  }
		  
		  public Y snd() {
			  return this.snd;
		  }
		}
}
