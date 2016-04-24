package assignment2;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;
import func.nn.activation.*;

import shared.DataSet;
import shared.DataSetDescription;
import shared.reader.CSVDataSetReader;
import shared.reader.DataSetReader;

import java.util.*;

import java.io.*;
import java.net.URL;
import java.text.*;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying credit applications as either 
 * success or failure. 
 *
 * @author Adam Acosta
 * @version 1.0
 */
public class CrxTest {

    private static Instance[] trainInstances = initializeInstances("tictactoe_training.csv", "tictactoe_training_labels.csv");
    private static Instance[] testInstances = initializeInstances("tictactoe_test.csv", "tictactoe_test_labels.csv");

//	private static Instance[] trainInstances = initializeInstances("train.csv", "train_labels.txt");
//	private static Instance[] testInstances = initializeInstances("test.txt", "test_labels.txt");

    //private static int inputLayer = 10, hiddenLayer = 2, outputLayer = 2;
    private static int inputLayer = 10, hiddenLayer = 7, outputLayer = 1;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(trainInstances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    private static Instance[] initializeInstances(String dataFile, String labelFile) {

        //DataSetReader dsr = new CSVDataSetReader(new File("").getAbsolutePath() + "/src/opt/test/" + dataFile);
        //DataSetReader lsr = new CSVDataSetReader(new File("").getAbsolutePath() + "/src/opt/test/" + labelFile);
    	
    	URL d_path = CrxTest.class.getResource(dataFile);
    	File df = new File(d_path.getFile());
    	URL l_path = CrxTest.class.getResource(dataFile);
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

            for(int i = 0; i < instances.length; i++) {
                instances[i].setLabel(new Instance(labels[i].getData().get(0)));
            	//instances[i].setLabel(new Instance(labels[i].getData()));
            }

            return instances;
        } catch (Exception e) {
            System.out.println("Failed to read input file");
            return null;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
    	//String results = "";
       // int it = args.length > 0 ? Integer.parseInt(args[0]): 1000;
    	int it = 500;
    	PrintWriter rhcOut = new PrintWriter("RHC_train_error.dat");
    	PrintWriter saOut = new PrintWriter("SA_train_error.dat");
    	PrintWriter gaOut = new PrintWriter("GA_train_error.dat");
    	PrintWriter rhcOutTest = new PrintWriter("RHC_test_error.dat");
    	PrintWriter saOutTest = new PrintWriter("SA_test_error.dat");
    	PrintWriter gaOutTest = new PrintWriter("GA_test_error.dat");
    	PrintWriter rhcOutTime = new PrintWriter("RHC_train_time.dat");
    	PrintWriter saOutTime = new PrintWriter("SA_train_time.dat");
    	PrintWriter gaOutTime = new PrintWriter("GA_train_time.dat");
    	PrintWriter rhcOutTestTime = new PrintWriter("RHC_test_time.dat");
    	PrintWriter saOutTestTime = new PrintWriter("SA_test_time.dat");
    	PrintWriter gaOutTestTime = new PrintWriter("GA_test_time.dat");
    	
    	while(it<=6000){
        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer},
                new LogisticSigmoid());
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }

          oa[0] = new RandomizedHillClimbing(nnop[0]);
          oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
          oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);
        
       
        for(int i = 0; i < oa.length; i++) {

            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            for(int j = 0; j < it; j++) {
                oa[i].train();
            }
            end = System.nanoTime();

            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            start = System.nanoTime();
            for(int j = 0; j < trainInstances.length; j++) {
                networks[i].setInputValues(trainInstances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(trainInstances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime = end - start;

            results +=  oaNames[i] + "," + it + "," + df.format(incorrect / (correct + incorrect)) + ","; 
            
            if (i == 0){
            	rhcOut.println(it + "\t" + df.format(incorrect / (correct + incorrect)));
            }
            if (i == 1){
            	saOut.println(it + "\t" + df.format(incorrect / (correct + incorrect)));
            }
            if (i == 2){
            	gaOut.println(it + "\t" + df.format(incorrect / (correct + incorrect)));
            }

            correct = 0;
            incorrect = 0;
            start = System.nanoTime();
            for(int j = 0; j < testInstances.length; j++) {
                networks[i].setInputValues(testInstances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(testInstances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime += end - start;
            testingTime /= 2.0;
            testingTime /= Math.pow(10, 9);

            results += df.format(incorrect / (correct + incorrect)) + ","
                     + df.format(trainingTime) + "," + df.format(testingTime) + "\n";
            
            if (i == 0){
            	rhcOutTest.println(it + "\t" + df.format(incorrect / (correct + incorrect)));
            	rhcOutTestTime.println(it + "\t" + df.format(testingTime));
            	rhcOutTime.println(it + "\t" + df.format(trainingTime));
            }
            if (i == 1){
            	saOutTest.println(it + "\t" + df.format(incorrect / (correct + incorrect)));
            	saOutTestTime.println(it + "\t" + df.format(testingTime));
            	saOutTime.println(it + "\t" + df.format(trainingTime));
            }
            if (i == 2){
            	gaOutTest.println(it + "\t" + df.format(incorrect / (correct + incorrect)));
            	gaOutTestTime.println(it + "\t" + df.format(testingTime));
            	gaOutTime.println(it + "\t" + df.format(trainingTime));
            }

        }

        System.out.print(results);
        it += 500;
        //System.out.println(it);
        }
    	rhcOut.close();
    	saOut.close();
    	gaOut.close();
    	rhcOutTest.close();
    	saOutTest.close();
    	gaOutTest.close();
    	rhcOutTime.close();
    	saOutTime.close();
    	gaOutTime.close();
    	rhcOutTestTime.close();
    	saOutTestTime.close();
    	gaOutTestTime.close();
        
    }

}