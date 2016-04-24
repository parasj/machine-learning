package shared.test;

import java.io.File;
import java.net.URL;

import shared.DataSet;
import shared.Instance;
import shared.filt.IndependentComponentAnalysis;
import shared.reader.CSVDataSetReader;
import shared.reader.DataSetReader;
import util.linalg.Matrix;
import util.linalg.RectangularMatrix;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class IndepenentComponentAnalysisMyDataTest {
    
	 private static Instance[] trainInstances = initializeInstances("tictactoe_training.csv", "tictactoe_training_labels.csv");
	 //private static Instance[] testInstances = initializeInstances("tictactoe_test.csv", "tictactoe_test_labels.csv");
	
	private static Instance[] initializeInstances(String dataFile, String labelFile) {

        //DataSetReader dsr = new CSVDataSetReader(new File("").getAbsolutePath() + "/src/opt/test/" + dataFile);
        //DataSetReader lsr = new CSVDataSetReader(new File("").getAbsolutePath() + "/src/opt/test/" + labelFile);
    	
    	URL d_path = IndepenentComponentAnalysisMyDataTest.class.getResource(dataFile);
    	File df = new File(d_path.getFile());
    	URL l_path = IndepenentComponentAnalysisMyDataTest.class.getResource(dataFile);
    	File lf = new File(l_path.getFile());
    	
        DataSetReader dsr = new CSVDataSetReader(df.toString());
        DataSetReader lsr = new CSVDataSetReader(lf.toString());
        DataSet ds;
        DataSet labs;

        try {
            ds = dsr.read();
            labs = lsr.read();
            Instance[] instances = ds.getInstances();
            Instance[] labels = labs.getInstances();

//            for(int i = 0; i < instances.length; i++) {
//                instances[i].setLabel(new Instance(labels[i].getData().get(0)));
//            	//instances[i].setLabel(new Instance(labels[i].getData()));
//            }

            return instances;
        } catch (Exception e) {
            System.out.println("Failed to read input file");
            return null;
        }
    }
	
	
	
    /**sdf
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
//        Instance[] instances =  new Instance[100];
//        for (int i = 0; i < instances.length; i++) {
//            double[] data = new double[2];
//            data[0] = Math.sin(i/2.0);
//            data[1] = (Math.random() - .5)*2;
//            instances[i] = new Instance(data);
//        }
        DataSet set = new DataSet(trainInstances);
        System.out.println("Before randomizing");
        System.out.println(set);
//        Matrix projection = new RectangularMatrix(new double[][]{ {.6, .6}, {.4, .6}});
        Matrix projection = new RectangularMatrix(new double[][]{ {.1, .1}, {.1, .1}});

        for (int i = 0; i < set.size(); i++) {
            Instance instance = set.get(i);
            instance.setData(projection.times(instance.getData()));
        }
        System.out.println("Before ICA");
        System.out.println(set);
        IndependentComponentAnalysis filter = new IndependentComponentAnalysis(set, 1);
        filter.filter(set);
        System.out.println("After ICA");
        System.out.println(set);
          
    }

}