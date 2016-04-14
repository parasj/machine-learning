import java.io.IOException;
import java.util.ArrayList;

import shared.DataSet;
import shared.DataSetWriter;
import shared.filt.IndependentComponentAnalysis;
import shared.filt.InsignificantComponentAnalysis;
import shared.filt.PrincipalComponentAnalysis;
import shared.filt.RandomizedProjectionFilter;
import shared.filt.ReversibleFilter;
import shared.reader.ArffDataSetReader;

/**
 * Not in use any more, but kept around for the filtering code as a reference
 * @author chronon
 *
 */
public class DataSetWorker {
	// static variables
	private static final String reducedDir = "/Users/Jonathan/Documents/College Work/Senior/CS 4641/Assignment 3/data/reduced/";
	private static final String clustReducedDir = "/Users/Jonathan/Documents/College Work/Senior/CS 4641/Assignment 3/data/creduced/";
	private static final String[] reduced = {"_pca.csv", "_ica.csv", "_insig.csv", "_rp.csv"};
	private static final String[] clustered = {"_kmeans", "_emax"};
	
	// the array of DataSets corresponding to the mountain of nnets we need to train
	private DataSet clean;

	private String setName;
	private final int toKeep = 4;
	
	
	public DataSetWorker(String setName, String file) throws Exception {
		this.setName = setName;
		this.clean = (new ArffDataSetReader(file)).read();
	}
	
	public void reduce() {
    	// Add all the filters we need
		ArrayList<Tuple<ReversibleFilter,String>> filters = new ArrayList<Tuple<ReversibleFilter,String>>();
        filters.add(new Tuple<ReversibleFilter, String>(new PrincipalComponentAnalysis(clean, toKeep), "_pca.csv"));
        filters.add(new Tuple<ReversibleFilter, String>(new IndependentComponentAnalysis(clean, toKeep), "_ica.csv"));
        filters.add(new Tuple<ReversibleFilter, String>(new RandomizedProjectionFilter(toKeep, clean.get(0).size()), "_rp.csv"));
        filters.add(new Tuple<ReversibleFilter, String>(new InsignificantComponentAnalysis(clean, toKeep), "_insig.csv"));
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