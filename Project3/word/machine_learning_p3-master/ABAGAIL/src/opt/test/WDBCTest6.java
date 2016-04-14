package opt.test;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying breast cancer masses as malignant
 * or benign (M = 0, B = 1)
 *
 * @author Hannah Lau, Daniel Castro (edited)
 * @version 1.1
 */
public class WDBCTest6 {
    // Define the dataset, attributes, and instances.
    private static String dataFileSource = "src/opt/test/wdbc.txt";
    private static int numAttributes = 30;
    private static int numInstances = 569;

    private static int iterationsForSA = 10;

    private static Instance[] instances = initializeInstances();

    private static int inputLayer = numAttributes, hiddenLayer = 5, outputLayer = 1, trainingIterations = 4000;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(instances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[iterationsForSA];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[iterationsForSA];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[iterationsForSA];
    private static String[] oaNames = new String[iterationsForSA];
    private static String results = "Method,Correctly Classified, Incorrectly Classified, Training Time, Testing Time";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) {
        int interval = 5;
        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
            oaNames[i] = "GA" + interval;
            oa[i] = new StandardGeneticAlgorithm(200, interval, 10, nnop[i]);
            interval += 5;
        }

        for(int i = 0; i < oa.length; i++) {
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            train(oa[i], networks[i], oaNames[i]); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            start = System.nanoTime();
            for(int j = 0; j < instances.length; j++) {
                networks[i].setInputValues(instances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(instances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);

            results +=  "\n" + oaNames[i] + "," + correct + "," + incorrect + ","
                        + df.format(trainingTime) + "," + df.format(testingTime);
        }

        System.out.println(results);
    }

    private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        for(int i = 0; i < trainingIterations; i++) {
            oa.train();

            double error = 0;
            for(int j = 0; j < instances.length; j++) {
                network.setInputValues(instances[j].getData());
                network.run();

                Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);
            }
        }
    }

    private static Instance[] initializeInstances() {

        double[][][] attributes = new double[numInstances][][];

        try {
            // M = 0, B = 1
            BufferedReader br = new BufferedReader(new FileReader(new File(dataFileSource)));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[numAttributes];
                attributes[i][1] = new double[1];

                for(int j = 0; j < numAttributes; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // dataset is already formatted to have 0 or 1 for classification.
            instances[i].setLabel(new Instance(attributes[i][1][0]));

        }

        return instances;
    }
}
